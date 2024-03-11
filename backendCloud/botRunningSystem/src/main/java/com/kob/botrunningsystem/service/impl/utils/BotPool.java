package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Queue<Bot> bots = new LinkedList<>();
    private static final long TIMEOUT = 5000; // ms

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {
        Consumer consumer = new Consumer();
        consumer.startTimeout(TIMEOUT, bot);
        // TODO
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await(); // 线程加入该条件的等待队列，并释放锁 (其实相当于先释放互斥锁，再等待一个信号量）
                } catch (InterruptedException e) {
                    lock.unlock();
                    e.printStackTrace();
                    break;
                }
            } else {
                Bot bot = bots.remove(); // 从队头取出一个 bot
                lock.unlock();
                // 消费者，编译执行代码(耗时操作放临界区外)
                consume(bot);
            }
        }
    }
}
