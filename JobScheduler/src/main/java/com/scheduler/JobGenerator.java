package com.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JobGenerator {
    public static List<Job> generateJobs(int count) {
        List<Job> jobs = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String jobName = "Job-" + (i);
            Job job = new Job(jobName, () -> {
                try {
                    // Simylate soem random work time (0.5 - 2s)
                    Thread.sleep((long) (500 + Math.random() * 1500));
                    System.out.println("[Task] " + jobName + "finished work.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                }
            });
            jobs.add(job);
        }
        // Adding random dependencies
        Random rand = new Random();
        for (int i = 1; i < jobs.size(); i++) {
            jobs.get(i).addDependency(jobs.get(rand.nextInt(i)));
        }

        return jobs;
    }
}