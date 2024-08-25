package org.luminousql;

public class Log {
    // Can add logging framework later if it seems necessary


    private static boolean traceEnabled = false;

    public static void info(String message) {
        System.out.println("INFO: " + message);
    }

    public static void error(String message) {
        System.err.println("ERROR: " + message);
    }

    public static void error(Throwable t) {
        System.err.println("ERROR: " + t.getMessage());
        t.printStackTrace();
    }

    public static void trace(String message) {
        if (traceEnabled) {
            System.out.println("TRACE: " + message);
        }
    }

    public static void enableTrace() {
        traceEnabled = true;
    }

    public static void disableTrace() {
        traceEnabled = false;
    }
}