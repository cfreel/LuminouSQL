package org.luminousql;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Luminousql {
    public static Map<String,String> configMap;

    public static void main(String[] args) {
        if (args.length==0) {
            UIThread uiThread = new UIThread();
            Executor uiExe = Executors.newSingleThreadExecutor();
            uiExe.execute(uiThread);
        } else {
            processArgs(args);
        }
    }

    private static void processArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            Configuration.initConfig();
            if (!Configuration.passcodeExists()) {
                if (line.hasOption("passcode") || line.hasOption("alias")) {
                    System.err.println("Passcode must have previously been set interactively to use" +
                            " passcode or alias options.");
                    System.exit(-1);
                }
            }

            if (!line.hasOption("query")) {
                System.err.println("Must include a query to run.  Run with -help if needed.");
                System.exit(-1);
            }

            if (line.hasOption("passcode")) {
                String userPasscode = line.getOptionValue("passcode");
                try {
                    if (!Configuration.passcodeIsValid(userPasscode)) {
                        System.err.println("Passcode is incorrect.");
                        System.exit(-1);
                    }
                } catch (Throwable t) {
                    System.err.println("Failed on passcode verification:" + t.getMessage());
                    System.exit(-1);
                }
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar LuminouSQL.jar", options);
                System.exit(-1);
            }

            if (line.hasOption("alias")) {
                String query = line.getOptionValue("query");
                String alias = line.getOptionValue("alias");
                Configuration.loadAliases();
                List<String> colNames = new ArrayList<>();
                Configuration.queryResults = DatabaseDAO.runQuery(query, colNames, alias);
                writeResults(Configuration.queryResults, colNames);
                System.exit(0);
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("pc", "passcode", true, "the passcode that was previously set");
        options.addOption("a", "alias", true, "a previously defined alias to use");
        options.addOption("u", "user", true, "db user");
        options.addOption("p", "pass", true, "db password");
        options.addOption("d", "driver", true, "jdbc driver file");
        options.addOption("c", "class", true, "class for JDBC to use");
        options.addOption("q", "query", true, "the query to run");
        options.addOption("h", "help", true, "display help with valid options");
        return options;
    }

    private static void writeResults(List<List<String>> results, List<String> colNames) {
        System.out.println(String.join(",", colNames));
        for (List<String> row : results) {
            System.out.println(String.join(",", row));
        }
    }

}
