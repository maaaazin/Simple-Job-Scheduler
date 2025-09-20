package com.scheduler;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoggerUtil {
    private static Logger logger = Logger.getLogger("JobSchedulerLogger");

    static {
        try {
            // Create a file handler (append mode true)
            FileHandler fileHandler = new FileHandler("jobs.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(true); // also print to console
            logger.setLevel(Level.INFO);

        } catch (IOException e) {
            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}