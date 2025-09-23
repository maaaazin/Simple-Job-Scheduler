package com.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Job {
    private String id;
    private String name;
    private Runnable task;
    private List<Job> dependencies = new ArrayList<>();
    private boolean completed = false;
    private Logger logger = LoggerUtil.getLogger();

    public Job(String name, Runnable task) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.task = task;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addDependency(Job job) {
        dependencies.add(job);
    }

    public List<Job> getDependencies() {
        return dependencies;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void execute() {
        logger.info("Job STARTED: " + name + " (ID: " + id + ")");
        try {
            task.run();
            this.completed = true;
            logger.info("Job COMPLETED: " + name + " (ID: " + id + ")");
        } catch (Exception e) {
            logger.severe("Job FAILED: " + name + " - Error: " + e.getMessage());
        }
    }
}
