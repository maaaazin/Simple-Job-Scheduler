package com.scheduler;

import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.Set;

public class JobScheduler {
    int corePool = 1; // 
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(corePool);
    private Logger logger = LoggerUtil.getLogger();
    private final Set<String> completedJobIds = ConcurrentHashMap.newKeySet();
    private final Set<String> executingJobIds = ConcurrentHashMap.newKeySet();
    private final Object executionLock = new Object();

    public void scheduleJob(Job job) {
        executor.submit(() -> {
            try {
                // skip if job is already completed or currently execting
                if (job.isCompleted() || completedJobIds.contains(job.getId())
                        || executingJobIds.contains(job.getId())) {
                    logger.info("Job SKIPPED (already completed/executing): " + job.getName() + " (ID: " + job.getId()
                            + ")");
                    return;
                }

                // wait for dependencies first
                for (Job dep : job.getDependencies()) {
                    logger.info(job.getName() + " waiting for dependency: " + dep.getName());
                    executeDependencySafely(dep);
                }

                // Execute the main job safely
                executeJobSafely(job);

            } catch (Exception e) {
                logger.severe("Error Executing job: " + job.getName() + ": " + e.getMessage());
            }
        });
    }

    private void executeDependencySafely(Job dep) {
        synchronized (executionLock) {
            if (dep.isCompleted() || completedJobIds.contains(dep.getId()) || executingJobIds.contains(dep.getId())) {
                logger.info("Dependency SKIPPED (already completed/executing): " + dep.getName());
                return;
            }

            // mark as executing
            executingJobIds.add(dep.getId());
            logger.info("Dependency STARTING: " + dep.getName() + " (ID: " + dep.getId() + ")");
        }

        try {
            dep.execute();

            synchronized (executionLock) {
                if (dep.isCompleted()) {
                    completedJobIds.add(dep.getId());
                    logger.info("Dependency COMPLETED and tracked globally: " + dep.getName());
                }
                executingJobIds.remove(dep.getId());
            }
        } catch (Exception e) {
            synchronized (executionLock) {
                executingJobIds.remove(dep.getId());
            }
            throw e;
        }
    }

    private void executeJobSafely(Job job) {
        synchronized (executionLock) {
            if (job.isCompleted() || completedJobIds.contains(job.getId()) || executingJobIds.contains(job.getId())) {
                logger.info(
                        "Job SKIPPED (already completed/executing): " + job.getName() + " (ID: " + job.getId() + ")");
                return;
            }

            // mark as executing
            executingJobIds.add(job.getId());
            logger.info("Job STARTING: " + job.getName() + " (ID: " + job.getId() + ")");
        }

        try {
            job.execute();

            synchronized (executionLock) {
                if (job.isCompleted()) {
                    completedJobIds.add(job.getId());
                    logger.info("Job COMPLETED and tracked globally: " + job.getName());
                }
                executingJobIds.remove(job.getId());
            }
        } catch (Exception e) {
            synchronized (executionLock) {
                executingJobIds.remove(job.getId());
            }
            throw e;
        }
    }

    public void shutdown() {
        executor.shutdown();
        System.out.println("Scheduler shutting down...");
    }

    public void markJobCompleted(Job job) {
        synchronized (executionLock) {
            completedJobIds.add(job.getId());
            job.setCompleted(true);
            executingJobIds.remove(job.getId()); // remove from executing if it was there
        }
        logger.info("Job manually marked as completed: " + job.getName() + " (ID: " + job.getId() + ")");
    }

    public boolean isJobCompleted(Job job) {
        return job.isCompleted() || completedJobIds.contains(job.getId());
    }

    public boolean isJobExecuting(Job job) {
        return executingJobIds.contains(job.getId());
    }

    public int getCompletedJobCount() {
        return completedJobIds.size();
    }

    public int getExecutingJobCount() {
        return executingJobIds.size();
    }
}
