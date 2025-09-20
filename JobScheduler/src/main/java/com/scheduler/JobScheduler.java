package com.scheduler;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class JobScheduler {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private Logger logger = LoggerUtil.getLogger();

    public void scheduleJob(Job job) {
        executor.submit(() -> {
            try {
                // wait for dependencies first
                for (Job dep : job.getDependencies()) {
                    logger.info(job.getName() + "waiting for dependency: " + dep.getName());
                    dep.execute();
                }
                job.execute();
            } catch (Exception e) {
                logger.severe("Error Executing job: " + job.getName() + ": " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
        System.out.println("Scheduler shutting down...");
    }
}
