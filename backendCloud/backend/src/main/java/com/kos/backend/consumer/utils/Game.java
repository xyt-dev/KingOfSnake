package com.kos.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kos.backend.consumer.WebSocketServer;
import com.kos.backend.pojo.Record;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    @Getter
    private final Integer rows;
    @Getter
    private final Integer cols;
    @Getter
    private final Integer innerWallsCount;
    @Getter
    private final int[][] g;
    final private static int[] dx = {-1, 0, 1, 0};
    final private static int[] dy = {0, 1, 0, -1};

    @Getter
    private final Player playerA;
    @Getter
    private final Player playerB;

    private Integer nextStepA = null;
    private Integer nextStepB = null;
    // 以上存在读写冲突，需要加锁
    private final ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String gameResult = "D"; // D: 平局 |  A: 玩家A撞  |  B: 玩家B撞

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public Game(Integer rows, Integer cols, Integer innerWallsCount, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.g = new int[rows][cols];
        playerA = new Player(idA, rows - 2, 1, new ArrayList<>()); // a
        playerB = new Player(idB, 1, cols - 2, new ArrayList<>()); // b
    }

    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) {
            return true;
        }
        g[sx][sy] = 1;

        for (int i = 0; i < 4; ++ i) {
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if(checkConnectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0; // 恢复地图
                    return true;
                }
            }
        }

        g[sy][sx] = 0; // 恢复地图
        return false;
    }

    private boolean draw() { // 画地图
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.innerWallsCount / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows); // 0 ~ this.rows - 1
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue; // 不重复
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue; // 左下角和右上角

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; ++i) {
            if (draw()) {
                break;
            }
        }
    }

    private boolean nextStep() {
        try {
            Thread.sleep(200); // 防止发送过快前端动画未执行完
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 100; i ++ ) { // 10 秒内需要获取到输入
            try {
                Thread.sleep(100); // 最大操作延迟: 100ms
                lock.lock();
                try {
                    if(nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private boolean checkNotHit(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if(g[cell.getX()][cell.getY()] == 1) return false; // hit wall
        for(int i = 0; i < n - 1; ++ i) {
            if(cell.getX() == cellsA.get(i).getX() && cell.getY() == cellsA.get(i).getY()) return false; // hit itself
        }
        for(int i = 0; i < n - 1; ++ i) {
            if(cell.getX() == cellsB.get(i).getX() && cell.getY() == cellsB.get(i).getY()) return false; // hit B
        }
        return true;
    }

    private void judge() {
        List<Cell>  cellsA = playerA.getCell();
        List<Cell>  cellsB = playerB.getCell();
        boolean validA = checkNotHit(cellsA, cellsB);
        boolean validB = checkNotHit(cellsB, cellsA);
        if(!validA || !validB) {
            status = "finished";
            if(!validA && !validB) {
                gameResult = "D";
            } else if(!validA) {
                gameResult = "A";
            } else {
                gameResult = "B";
            }
        }
    }

    private void sendMessage2Both(String message) {
        // 防止在 get socket 时玩家正好掉线(computeIfPresent 是线程安全的判断)
        WebSocketServer.userSocketMap.computeIfPresent(playerA.getId(), (key, value) -> {
            value.sendMessage(message);
            return value;
        });
        WebSocketServer.userSocketMap.computeIfPresent(playerB.getId(), (key, value) -> {
            value.sendMessage(message);
            return value;
        });
    }

    private void sendMove() { // 向两个Client传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA = nextStepB = null; // 清空
            sendMessage2Both(resp.toJSONString());
        } finally {
            lock.unlock();
        }
    }

    private String getGameMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; ++ i) {
            for (int j = 0; j < cols; ++ j) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDataBase() {
        Record record = new Record(
            null,
            playerA.getId(),
            playerA.getSx(),
            playerA.getSy(),
            playerA.getStepsString(),
            playerB.getId(),
            playerB.getSx(),
            playerB.getSy(),
            playerB.getStepsString(),
            getGameMapString(),
            gameResult,
            new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("result", gameResult);
        sendMessage2Both(resp.toJSONString());
        saveToDataBase(); // 保存结果到数据库
    }

    @Override
    public void run() { // TODO 逻辑
        for(int i = 0; i < 2000; ++ i) {
            if(nextStep()) { // 是否获取到两条蛇的下一步操作
                judge();
                if(status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        gameResult = "D";
                    } else if (nextStepA == null) {
                        gameResult = "A";
                    } else {
                        gameResult = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
