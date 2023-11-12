package org.example.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class MonitorThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorThread.class);
    private final ThreadPoolExecutor executor;
    private final int seconds;
    private final List<Integer> rates = new ArrayList<>();
    private boolean run = true;
    private int initialCompletedTaskCount = 0;

    public MonitorThread(ThreadPoolExecutor executor, int delay) {
        this.executor = executor;
        this.seconds = delay;
    }

    public void shutdown() {
        this.run = false;
    }

    @Override
    public void run() {
        while (run) {
            long currentCompletedTaskCount = this.executor.getCompletedTaskCount();
            int tasksProcessedInInterval = (int) (currentCompletedTaskCount - initialCompletedTaskCount);
            initialCompletedTaskCount = (int) currentCompletedTaskCount;
            int currentRatePerSec = tasksProcessedInInterval / seconds;

            LOGGER.info(
                    "[{}/s}] [{}/{}] [{}] Active: {}, Completed: {}, Task: {}, Queue: {}, isShutdown: {}, isTerminated: {}",
                    currentRatePerSec,
                    this.executor.getPoolSize(),
                    this.executor.getCorePoolSize(),
                    this.executor.getMaximumPoolSize(),
                    this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(),
                    this.executor.getTaskCount(),
                    this.executor.getQueue().size(),
                    this.executor.isShutdown(),
                    this.executor.isTerminated());
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException: ", e);
                Thread.currentThread().interrupt();
            }
        }

    }

    public int getRateAverage() {
        return (int) rates.stream().mapToInt(a -> a).average().getAsDouble();
    }

}
