package org.luminousql;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Luminousql {

    private static final String APP_NAME = "LuminouSQL";

    public static Map<String,String> configMap;


    public static void main(String[] args) {

        try {
            String homeDir = System.getProperty("user.home");
            Configuration.readConfig(homeDir + "/." + APP_NAME);
        } catch (IOException e) {
            Log.error(e);
            System.exit(-1);
        }

        UIThread uiThread = new UIThread();

        Executor uiExe = Executors.newSingleThreadExecutor();
        uiExe.execute(uiThread);
    }

}
