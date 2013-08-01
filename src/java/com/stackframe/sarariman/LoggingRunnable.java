/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcculley
 */
public class LoggingRunnable implements Runnable {

    private final Runnable runnable;

    private final Logger logger;

    public LoggingRunnable(Runnable runnable, Logger logger) {
        this.runnable = runnable;
        this.logger = logger;
    }

    public void run() {
        try {
            runnable.run();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Runnable threw an exception", t);
        }
    }

}
