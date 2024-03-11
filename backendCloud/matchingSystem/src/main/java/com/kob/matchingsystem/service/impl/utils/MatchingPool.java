package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static final int SCALING_FACTOR = 200; // 等待因子

    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    private void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try {
            // 不重复才添加
            for(Player player : players) { // O(n)
                if(player.getUserId().equals(userId)) {
                    return;
                }
            }
            players.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players) { // O(n) + O(n)
                if(!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private boolean checkMatched(Player a, Player b) {
        // 匹配策略：a与b中等待时间最小值 * 等待因子 > |a.rating - b.rating|
        return Math.min(a.getWaitingTime(), b.getWaitingTime()) * SCALING_FACTOR > Math.abs(a.getRating() - b.getRating());
    }

    private void sendResult(Player a, Player b) { // 发送匹配结果 TODO
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user1_id", a.getUserId().toString());
        data.add("user1_bot_id", a.getBotId().toString());
        data.add("user2_id", b.getUserId().toString());
        data.add("user2_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() { // 尝试匹配所有玩家
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); ++ i) {
            if (used[i]) continue;
            for (int j = i + 1; j < players.size(); ++ j) {
                if (used[j]) continue;
                Player a = players.get(i);
                Player b = players.get(j);
                if (checkMatched(a, b)) {
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); ++ i) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;

        System.out.println("MatchingPools: " + players);
    }

    private void increaseWaitingTime() {
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1); // 已等待时间 +1
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000); // 每隔一秒匹配一次, sleep 节省 CPU
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
