package com.scheduler;

import java.util.List;

public class App {
    public static void main(String[] args) {
        JobScheduler scheduler = new JobScheduler();

        int n = 20; // no of jobs to be scheduled

        List<Job> jobs = JobGenerator.generateJobs(n);

        for (Job job: jobs) {
            scheduler.scheduleJob(job);
        }

        // give some time before shutdown
        try { 
            Thread.sleep(15000);
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        }
        scheduler.shutdown();
    }
}
