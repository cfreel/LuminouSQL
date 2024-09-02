package org.luminousql;

public class Alias {
    String name;
    String user;
    String pass;
    String driver;
    String connection;

    public Alias(String name, String serialized) {
        String[] parts = serialized.split(",");
        this.name = name;
        this.user = parts[0];
        try {
            this.pass = EncryptionUtil.performDecyption(parts[1]);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            this.pass = parts[1];
        }
        this.driver = parts[2];
        this.connection = parts[3];
        if (parts.length > 4) {
            for (int i=4; i< parts.length; i++) {
                this.connection += "," + parts[i];
            }
        }
    }

    public Alias(String name, String user, String pass, String driver, String connection) {
        this.name = name;
        this.user = user;
        this.pass = pass;
        this.driver = driver;
        this.connection = connection;
    }
}
