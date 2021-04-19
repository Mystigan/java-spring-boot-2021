package io.hari.democonnectors.mysql;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author hayadav
 * @create 4/19/2021
 */
@Slf4j
public class MySqlConnector {

    public static final String SCHEMA_NAME = "testdb";
    public static final String URL = "jdbc:mysql://localhost:3306/" + SCHEMA_NAME + "?useSSL=false";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "hariom";

    @Test
    public void testConnection() {
        final Connection connection = createConnection();
        closeConnection(connection);
    }

    @Test
    public void testTableName() {
        readAllTables();
        readTablesInsideSchema();
    }

    @Test
    public void testAllRowsInTable() {
        getAllRowsInTable();
    }

    @Test
    public void insertNewRow() {
        insertRowWithAllValues();
    }

    @Test
    public void insertNewRow2() {
        insertRowAutoGeneratedPK();
        insertMultipleRowsAutoGeneratedPKForLoop();
    }

    @Test
    public void testColumnMetadata() {
        getColumnInformation();
    }


    @SneakyThrows
    public Connection createConnection() {
        log.info("MySqlConnector.createConnection");
        final Connection connection = getConnection();
        final Statement connectionStatement = connection.createStatement();
        final ResultSet resultSet = connectionStatement.executeQuery("SELECT VERSION()");
        if (resultSet.next()) {
            System.out.println("resultSet.getString(1) = " + resultSet.getString(1));
        }
        resultSet.close();
        connectionStatement.close();
        return connection;
    }

    @SneakyThrows
    public void closeConnection(Connection connection) {
        log.info("MySqlConnector.closeConnection");
        connection.close();
    }

    @SneakyThrows
    public void readAllTables() {
        final Connection connection = getConnection();
        final DatabaseMetaData connectionMetaData = connection.getMetaData();
        String[] table = {"TABLE"};
        final ResultSet resultSet = connectionMetaData.getTables(null, null, null, table);//working
//        final ResultSet resultSet = connectionMetaData.getTables(null, null, "%", table);//working
        List<String> tableNames = new LinkedList<>();
        while (resultSet.next()) {
            final String tableName = resultSet.getString("TABLE_NAME");
            tableNames.add(tableName);
        }
        System.out.println("tableNames = " + tableNames);
        connection.close();
    }

    @SneakyThrows
    public void readTablesInsideSchema() {
        final Connection connection = getConnection();
        final Statement connectionStatement = connection.createStatement();
        final ResultSet resultSet = connectionStatement.executeQuery("show tables");
        while (resultSet.next()) {
            final String resultSetString = resultSet.getString(1);
            System.out.println("resultSetString = " + resultSetString);
        }
        connection.close();
    }

    @SneakyThrows
    public void getAllRowsInTable() {
        final Connection connection = getConnection();
        final Statement connectionStatement = connection.createStatement();
        final ResultSet resultSet = connectionStatement.executeQuery("select * from emp");
        while (resultSet.next()) {
            final int firstCol = resultSet.getInt(1);
            final String secondCol = resultSet.getString(2);
            System.out.println(firstCol + " " + secondCol);
        }
        connection.close();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    @SneakyThrows
    public void insertRowWithAllValues() {
        final String sql = "insert into emp(id, name) values(?, ?)";
        final Connection connection = getConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 4);
        preparedStatement.setString(2, "neha");
        final int executeUpdate = preparedStatement.executeUpdate();
        System.out.println("executeUpdate = " + executeUpdate);

    }

    @SneakyThrows
    public void insertRowAutoGeneratedPK() {
        final String sql = "insert into emp(name) values(?)";
        final Connection connection = getConnection();
//        final PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//working
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);//working
        preparedStatement.setString(1, "neha 2");
        final int executeUpdate = preparedStatement.executeUpdate();
        System.out.println("executeUpdate = " + executeUpdate);
        connection.close();
    }

    @SneakyThrows
    public void insertMultipleRowsAutoGeneratedPKForLoop() {
        final String sql = "insert into emp(name) values(?)";
        final Connection connection = getConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < 10; i++) {
            preparedStatement.setString(1, "neha-"+i);
            final int executeUpdate = preparedStatement.executeUpdate();
            System.out.println("executeUpdate = " + executeUpdate);
        }
        connection.close();
    }

    /**
     * https://dev.mysql.com/doc/refman/5.6/en/information-schema-columns-table.html
     *
     * SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
     *   FROM INFORMATION_SCHEMA.COLUMNS
     *   WHERE table_name = 'tbl_name'
     *   [AND table_schema = 'db_name']
     *   [AND column_name LIKE 'wild']
     */
    @SneakyThrows
    public void getColumnInformation() {
        final Connection connection = getConnection();
        final Statement connectionStatement = connection.createStatement();
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT " +
                "  FROM INFORMATION_SCHEMA.COLUMNS " +
                "  WHERE table_name = 'emp'";
        final ResultSet resultSet = connectionStatement.executeQuery(sql);
        while (resultSet.next()) {
            log.info("{} {} {} ", resultSet.getString(1), resultSet.getString(2),resultSet.getString(3));
        }
        connection.close();
    }

}