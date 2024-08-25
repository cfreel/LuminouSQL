package org.luminousql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDAO {

    // Database credentials
    static String url = "";
    static String user = "";
    static String password = "";

    public static List<String> getAllTableNames() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return getTableNames(connection);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<String> getColumnNames(String tableName) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return getTableColumns(connection, tableName);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<List<String>> getAllTableData(String tableName) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            List<String> colNames = getTableColumns(connection, tableName);
            return getTableData(connection, tableName, colNames);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<List<String>> runQuery(String query, List<String> colNames, String aliasName) {
        Alias alias = Configuration.getAliasFromName(aliasName);
        ConfiguredDriver configuredDriver = Configuration.getConfiguredDriverFromAlias(alias);
        try (Connection connection = DriverShim.getConnection(configuredDriver, alias)) {
            return queryTable(connection,query, colNames);
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<List<String>> runQuery(String query, List<String> colNames) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return queryTable(connection,query, colNames);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // Get a list of table names from the database
    private static List<String> getTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while (tables.next()) {
            tableNames.add(tables.getString("TABLE_NAME"));
        }
        return tableNames;
    }

    // Get a list of column names from a specific table
    private static List<String> getTableColumns(Connection connection, String tableName) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, tableName, "%");
        while (columns.next()) {
            columnNames.add(columns.getString("COLUMN_NAME"));
        }
        return columnNames;
    }

    private static List<List<String>> queryTable(Connection connection, String query, List<String> resultCols) throws SQLException{
        List<List<String>> tableData = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int numCols = rsmd.getColumnCount();
            for (int i=1; i<=numCols; i++) {
                resultCols.add(rsmd.getColumnName(i));
            }
            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i=1; i<=numCols; i++) {
                    row.add(resultSet.getString(i));
                }
                tableData.add(row);
            }
        }
        return tableData;
    }

    // Fetch data from a specific table
    private static List<List<String>> getTableData(Connection connection, String tableName, List<String> columnNames) throws SQLException {
        List<List<String>> tableData = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (String columnName : columnNames) {
                    row.add(resultSet.getString(columnName));
                }
                tableData.add(row);
            }
        }
        return tableData;
    }
}
