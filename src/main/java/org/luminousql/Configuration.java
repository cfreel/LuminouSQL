package org.luminousql;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    private static String configFilePath;
    private static final List<String> configFileText = new ArrayList<>(1000);
    private static final Map<String, String> configMap = new HashMap<>();
    // Map to store comments and their line numbers
    private static final Map<String,Integer> configLocationMap = new HashMap<>(1000);

    private static final String ENC_KEY_FIXED_PART = "EncKeyFixedPart";
    private static final String PASSCODE_VALIDATION_LABEL = "PassVal";
    private static final String PASSCODE_VALIDATION_TEXT = "TheQuickBrownFox";

    static List<ConfiguredDriver> configuredDrivers = new ArrayList<>();
    static List<Alias> aliases = new ArrayList<>();

    static Alias selectedAlias;
    static ConfiguredDriver selectedDriver;
    static String currentQuery;
    static List<String> columns;
    static List<List<String>> queryResults;
    static String fixedPortionEncKey;

    public static void readConfig(String configFile) throws IOException {
        configFilePath = configFile;

        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            int lineNumber = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                configFileText.add(line);

                int pos = line.indexOf('=');
                if (pos <0) {
                    throw new IllegalArgumentException("Invalid configuration format: " + line);
                }
                String key = line.substring(0, pos).trim();
                String value = line.substring(pos+1).trim();
                configMap.put(key, value);
                configLocationMap.put(key, lineNumber);
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            File file = new File(configFile);
            // Create the file if it doesn't exist
            if (file.createNewFile()) {
                System.out.println("File created: " + configFile);
            } else {
                System.out.println("File already exists (should have gotten here): " + configFile);
            }
        }
        loadDrivers();
        loadAliases();
    }

    public static boolean passcodeExists() {
        String encrypted = getConfig(PASSCODE_VALIDATION_LABEL);
        return encrypted != null;
    }

    public static boolean passcodeIsValid(String passcode) throws Exception {
        String encrypted = getConfig(PASSCODE_VALIDATION_LABEL);
        if (encrypted != null) {
            String encKeyFixedPart = getConfig(ENC_KEY_FIXED_PART);
            SecretKey key = EncryptionUtil.getKey(passcode, encKeyFixedPart);
            EncryptedStringBundle encBundle = new EncryptedStringBundle(encrypted);
            String decryptedFromPasscode = EncryptionUtil.decrypt(key, encBundle);
            return PASSCODE_VALIDATION_TEXT.equals(decryptedFromPasscode);
        }
        return false;
    }

    public static void setPasscodeInConfig(String passcode) throws Exception {
        // get a random key for the fixed part (acts a bit like a salt), intended for the first time only:
        String salt = EncryptionUtil.getSalt();
        addConfig(ENC_KEY_FIXED_PART, salt);
        SecretKey key = EncryptionUtil.getKey(passcode, salt);
        EncryptedStringBundle encryptedStringBundle = EncryptionUtil.encrypt(key, PASSCODE_VALIDATION_TEXT);
        addConfig(PASSCODE_VALIDATION_LABEL, encryptedStringBundle.toString());
        writeConfig();
    }

    public static void loadAliases() {
        String aliasNames = getConfig("Aliases");
        if (aliasNames != null && !aliasNames.trim().isEmpty()) {
            String[] names = aliasNames.split(",");
            for (String name : names) {
                String aliasConf = Configuration.getConfig(name);
                if (aliasConf != null && !aliasConf.trim().isEmpty()) {
                    Alias alias = new Alias(name, aliasConf);
                    aliases.add(alias);
                }
            }
        }
    }

    public static void loadDrivers() {
        String driverNames = getConfig("Drivers");
        if (driverNames != null && !driverNames.trim().isEmpty()) {
            String[] names = driverNames.split(",");
            for (String name : names) {
                String driverConf = Configuration.getConfig(name);
                if (driverConf != null && !driverConf.trim().isEmpty()) {
                    ConfiguredDriver cd = new ConfiguredDriver(name, driverConf);
                    configuredDrivers.add(cd);
                }
            }
        }
    }

    public static void updateDriver(ConfiguredDriver cd) {
        configuredDrivers.forEach(e->{e.path=cd.path; e.className=cd.className;});
        setConfig(cd.name, cd.path+","+cd.className);
        writeConfig();
    }

    public static void removeDriver(String driverName) {
        ConfiguredDriver cd = configuredDrivers.stream().
                filter(e->e.name.equals(driverName)).
                findFirst().
                orElse(null);
        if (cd==null) {
            System.err.println("Tried to remove non-existent driver.");
            return;
        }
        configuredDrivers.remove(cd);
        String driverNames = getConfig("Drivers");
        if (driverNames==null) {
            System.err.println("Tried to remove non-existent driver from list of driver names.");
            return;
        }
        int startPos = driverNames.indexOf(driverName);
        if (startPos>=0) {
            int endPos = driverNames.indexOf(',', startPos + 1);
            if (endPos>=0) {
                driverNames = driverNames.substring(0, startPos) + driverNames.substring(endPos);
            } else {
                int lastPos = startPos > 0 ? startPos - 1 : startPos;
                driverNames = driverNames.substring(0, lastPos);
            }
            setConfig("Drivers", driverNames);
        }
        deleteConfig(driverName);
        writeConfig();
    }

    public static void removeAlias(String aliasName) {
        Alias alias = aliases.stream().
                filter(e->e.name.equals(aliasName)).
                findFirst().
                orElse(null);
        if (alias==null) {
            System.err.println("Tried to remove non-existent alias.");
            return;
        }
        aliases.remove(alias);
        String aliasNames = getConfig("Aliases");
        if (aliasNames==null) {
            System.err.println("Tried to remove non-existent alias from list of alias names.");
            return;
        }
        int startPos = aliasNames.indexOf(aliasName);
        if (startPos>=0) {
            int endPos = aliasNames.indexOf(',', startPos + 1);
            if (endPos>=0) {
                aliasNames = aliasNames.substring(0, startPos) + aliasNames.substring(endPos);
            } else {
                int lastPos = startPos > 0 ? startPos - 1 : startPos;
                aliasNames = aliasNames.substring(0, lastPos);
            }
            setConfig("Aliases", aliasNames);
        }
        deleteConfig(aliasName);
        writeConfig();
    }

    public static void addDriver(ConfiguredDriver cd) {
        configuredDrivers.add(cd);
        String driverNames = getConfig("Drivers");
        if (driverNames==null) {
            addConfig("Drivers", cd.name);
        } else {
            setConfig("Drivers", driverNames + "," + cd.name);
        }
        addConfig(cd.name, cd.path + "," + cd.className);
        writeConfig();
    }

    public static void addAlias(Alias alias) {
        aliases.add(alias);
        String aliasNames = getConfig("Aliases");
        if (aliasNames==null) {
            addConfig("Aliases", alias.name);
        } else {
            setConfig("Aliases", aliasNames + "," + alias.name);
        }
        addConfig(alias.name, alias.user+","+alias.pass+","+alias.driver+","+alias.connection);
        writeConfig();
    }

    public static String getConfig(String key) {
        return configMap.get(key);
    }

    public static void setConfig(String key, String value) {
        Integer pos = configLocationMap.get(key);
        if (pos==null) {
            System.err.println("Attempting to update a config key that doesn't exist.");
            return;
        }
        configMap.put(key, value);
        configFileText.set(pos, key + "=" + value);
    }

    public static void addConfig(String key, String value) {
        configMap.put(key, value);
        int curLen = configFileText.size();
        configFileText.add(key+"="+value);
        configLocationMap.put(key, curLen);
    }

    public static void deleteConfig(String key) {
        configMap.remove(key);
        Integer pos = configLocationMap.get(key);
        if (pos >= 0) {
            configFileText.remove(pos.intValue());
            configLocationMap.remove(key);
        }
    }

    private static void writeConfig()  {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath))) {
            configFileText.forEach(e-> {
                try {
                    writer.write(e);
                    writer.newLine();
                } catch (IOException ex) {
                    System.err.println("Failed writing config file updates: " + ex);
                    throw new RuntimeException(ex);
                }
            });
        } catch (IOException ioe) {
            System.err.println("Failed writing config file updates: " + ioe);
        }
    }

    static Alias getAliasFromName(String aliasName) {
        return aliases.stream().
                filter(e->e.name.equals(aliasName)).
                findFirst().
                orElse(null);
    }

    static ConfiguredDriver getConfiguredDriverFromAlias(Alias alias) {
        ConfiguredDriver configuredDriver = configuredDrivers.stream().
                filter(e->e.name.equals(alias.driver)).
                findFirst().
                orElse(null);
        return configuredDriver;
    }

}
