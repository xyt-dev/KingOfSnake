package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("Adding player: {userId: " + userId + ", rating: " + rating + "}");
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("Removing player: {userId: " + userId  + "}");
        return null;
    }
}
