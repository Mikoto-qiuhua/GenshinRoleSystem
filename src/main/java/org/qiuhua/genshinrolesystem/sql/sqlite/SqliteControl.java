package org.qiuhua.genshinrolesystem.sql.sqlite;

import org.qiuhua.genshinrolesystem.Main;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SqliteControl {
    private static String dbPath;
    private static Connection connection;


    public static void loadSQLiteJDBC() {
        try {
            Class.forName("org.sqlite.JDBC");
            String var10000 = Main.getMainPlugin().getDataFolder().getAbsolutePath();
            dbPath = var10000 + File.separator + "Database.db";
        } catch (ClassNotFoundException var1) {
            Main.getMainPlugin().getLogger().severe("无法加载 SQLite JDBC 驱动程序");
            throw new RuntimeException(var1);
        }

        Main.getMainPlugin().getLogger().info("已加载 SQLite JDBC 驱动程序");
    }

    public static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException var1) {
            Main.getMainPlugin().getLogger().severe("无法连接到 SQLite 数据库");
            throw new RuntimeException(var1);
        }

        Main.getMainPlugin().getLogger().info("已连接到 SQLite 数据库");
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException var1) {
            Main.getMainPlugin().getLogger().severe("无法关闭 SQLite 数据库连接");
            throw new RuntimeException(var1);
        }

        Main.getMainPlugin().getLogger().info("已连关闭 SQLite 数据库连接");
    }

    public static void createTable(UUID uuid) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + getTableName(uuid) + " (slot INTEGER PRIMARY KEY,item TEXT);";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException var3) {
            Main.getMainPlugin().getLogger().severe("执行 CREATE TABLE 语句时出错");
            System.out.println(var3.getMessage());
            throw new RuntimeException(var3);
        }
    }

    public static void dropTable(UUID uuid) {
        String sql = "DROP TABLE IF EXISTS" + getTableName(uuid) + ";";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static void insert(UUID uuid, Integer slot, String item) {
        String sql = "INSERT INTO " + getTableName(uuid) + " (slot,item) VALUES(?,?);";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, slot);
            pst.setString(2, item);
            pst.executeUpdate();
        } catch (SQLException var5) {
            throw new RuntimeException(var5);
        }
    }

    public static Map<Integer, String> selectAll(UUID uuid) {
        String sql = "select * from " + getTableName(uuid) + ";";
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next()) {
                Integer slot = resultSet.getInt("slot");
                String item = resultSet.getString("item");
                map.put(slot, item);
            }

            return map;
        } catch (SQLException var7) {
            throw new RuntimeException(var7);
        }
    }

    public static String getTableName(UUID uuid) {
        return String.format("`%s`", uuid);
    }

    public static List<String> allTableName() {
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";
        List<String> list = new ArrayList();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next()) {
                String tableName = resultSet.getString(1);
                list.add(tableName);
            }

            return list;
        } catch (SQLException var5) {
            throw new RuntimeException(var5);
        }
    }
}
