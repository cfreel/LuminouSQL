package org.luminousql;

public class ConfiguredDriver {
    String name;
    String path;
    String className;

    public ConfiguredDriver(String name, String serialized) {
        String[] parts = serialized.split(",");
        this.name = name;
        this.path = parts[0];
        this.className = parts[1];
    }

    public ConfiguredDriver(String name, String path, String className) {
        this.name = name;
        this.path = path;
        this.className = className;
    }

    @Override
    public String toString() {
        return name + "," + path + "," + className;
    }
}
