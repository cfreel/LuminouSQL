package org.luminousql;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Luminousql {
    public static Map<String,String> configMap;

    public static void main(String[] args) {
        UIThread uiThread = new UIThread();

        Executor uiExe = Executors.newSingleThreadExecutor();
        uiExe.execute(uiThread);
    }

}
