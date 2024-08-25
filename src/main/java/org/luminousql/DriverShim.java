package org.luminousql;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {
    private Driver driver;
    DriverShim(Driver d) {
        this.driver = d;
    }
    public boolean acceptsURL(String u) throws SQLException {
        return this.driver.acceptsURL(u);
    }
    public Connection connect(String u, Properties p) throws SQLException {
        return this.driver.connect(u, p);
    }
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }
    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return this.driver.getPropertyInfo(u, p);
    }
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public static Connection getConnection(ConfiguredDriver configuredDriver, Alias alias) throws Exception {
        URL u = new URL("file://" + configuredDriver.path);
        String classname = configuredDriver.className;
        URLClassLoader ucl = new URLClassLoader(new URL[] { u });
        Driver d = (Driver)Class.forName(classname, true, ucl).newInstance();
        DriverManager.registerDriver(new DriverShim(d));
        return DriverManager.getConnection(alias.connection, alias.user, alias.pass);
    }
}
