package com.kob.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestBot implements com.kob.botrunningsystem.utils.BotInterface {
    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    // 方向：0上，1右，2下，3左
    private final int UP = 0;
    private final int RIGHT = 1;
    private final int DOWN = 2;
    private final int LEFT = 3;
    private final int rows = 21;
    private final int cols = 20;

    private boolean checkTailIncreasing(int step) { // 检验当前回合蛇是否变长
        if(step <= 10) return true;
        return step % 3 == 0;
    }

    private List<Cell> getCell(int sx, int sy, String steps) {
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for(int i = 1; i < steps.length() - 1; i ++) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!checkTailIncreasing(++ step)) { // 和步数对应
                res.remove(0);
            }
        }
        return res;
    }

    Integer countArea(int sx, int sy, int[][] map) {
        if(map[sx][sy] == 1) return 0;
        // 广搜求面积
        int area = 0;
        int [][] flag = new int[rows][cols];
        for(int i = 0; i < rows; i ++) {
            System.arraycopy(map[i], 0, flag[i], 0, cols);
        }
        Queue<Cell> q = new LinkedList<>();
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        q.add(new Cell(sx, sy));
        flag[sx][sy] = 1;
        while(!q.isEmpty()) {
            Cell c = q.poll(); // get first element
            for(int i = 0; i < 4; i ++) {
                int x = c.x + dx[i];
                int y = c.y + dy[i];
                if(flag[x][y] == 0) {
                    flag[x][y] = 1;
                    ++ area;
                    q.add(new Cell(x, y));
                }
            }
        }
        return area;
    }

    @Override
    public Integer nextMove(String input) {
        String[] strs = input.split("#");

        int[][] map = new int[rows][cols];
        for(int i = 0, k = 0 ; i < rows; i ++) {
            for(int j = 0; j < cols; j ++, k ++) {
                if(strs[0].charAt(k) == '1') {
                    map[i][j] = 1; // wall
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]);
        int aSy = Integer.parseInt(strs[2]);
        String aSteps = strs[3];
        int bSx = Integer.parseInt(strs[4]);
        int bSy = Integer.parseInt(strs[5]);
        String bSteps = strs[6];

        List<Cell> aCells = getCell(aSx, aSy, aSteps); // a 对应己方
        List<Cell> bCells = getCell(bSx, bSy, bSteps); // b 对应对手

        for(Cell c : aCells) map[c.x][c.y] = 1;
        for(Cell c : bCells) map[c.x][c.y] = 1;

        int opponentX = bCells.get(bCells.size() - 1).x;
        int opponentY = bCells.get(bCells.size() - 1).y;
        int selfX = aCells.get(aCells.size() - 1).x;
        int selfY = aCells.get(aCells.size() - 1).y;

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        int maxArea = 0;
        int direction = 0;
        for (int i = 0; i < 4; ++ i) {
            int x = selfX + dx[i];
            int y = selfY + dy[i];
            int area = countArea(x, y, map);
            if(area > maxArea) {
                maxArea = area;
                direction = i;
            }
        }

        return direction;
    }

}

