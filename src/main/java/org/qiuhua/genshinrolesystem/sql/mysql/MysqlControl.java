package org.qiuhua.genshinrolesystem.sql.mysql;

import org.qiuhua.genshinrolesystem.Main;
import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MysqlControl {
    private static Connection connection;
    private static Statement statement;

    //检查连接是否还有效
    public static void isValid(){
        try {
            if (connection.isValid(5)) {
                Main.getMainPlugin().getLogger().info("Mysql 数据库连接正常");
            } else {
                Main.getMainPlugin().getLogger().info("Mysql 数据库连接失效  尝试重新连接");
                enableMySQL();
            }
        } catch (SQLException e) {
            Main.getMainPlugin().getLogger().info(e.getMessage());
        }


    }


    public static void enableMySQL() {
        String ip = DefaultConfig.getString("Sql.ip");
        String databaseName = DefaultConfig.getString("Sql.dataBaseName");
        String userName = DefaultConfig.getString("Sql.username");
        String userPassword = DefaultConfig.getString("Sql.password");
        int port = DefaultConfig.getInteger("Sql.port");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?autoReconnect=true", userName, userPassword);
            statement = connection.createStatement();
            Main.getMainPlugin().getLogger().info("已连接到 Mysql 数据库");
        } catch (SQLException var6) {
            Main.getMainPlugin().getLogger().severe("连接 Mysql 数据库失败");
            var6.printStackTrace();
        }

    }

    public static Connection getConnection() {
        return connection;
    }

    public static Statement getStatement() {
        return statement;
    }

    public static void shutdown() {
        try {
            connection.close();
            Main.getMainPlugin().getLogger().info("已连断开 Mysql 数据库");
        } catch (SQLException var1) {
            Main.getMainPlugin().getLogger().severe("断开 Mysql 数据库失败");
            throw new RuntimeException(var1);
        }
    }

    public static void createTable(UUID uuid) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + getTableName(uuid) + " (item TEXT, slot INTEGER, role VARCHAR(255), health DOUBLE);";
            statement.executeUpdate(sql);
        } catch (SQLException var2) {
            Main.getMainPlugin().getLogger().severe("执行 CREATE TABLE 语句时出错");
            System.out.println(var2.getMessage());
            throw new RuntimeException(var2);
        }
    }

    public static void dropTable(UUID uuid) {
        String sql = "DROP TABLE IF EXISTS" + getTableName(uuid) + ";";

        try {
            statement.executeUpdate(sql);
        } catch (SQLException var3) {
            Main.getMainPlugin().getLogger().severe("执行 DROP TABLE 语句时出错");
            System.out.println(var3.getMessage());
            throw new RuntimeException(var3);
        }
    }

    public static void addItemSlot(UUID uuid, Integer slot, String item) {
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

    public static void addRoleHealth(UUID uuid, String role, Double health) {
        String sql = "INSERT INTO " + getTableName(uuid) + " (role,health) VALUES(?,?);";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, role);
            pst.setDouble(2, health);
            pst.executeUpdate();
        } catch (SQLException var5) {
            throw new RuntimeException(var5);
        }
    }

    public static Map<Integer, String> getItemSlot(UUID uuid) {
        Map<Integer, String> map = new HashMap();
        String sql = "select * from " + getTableName(uuid) + ";";

        try {
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Integer slot = resultSet.getInt("slot");
                String item = resultSet.getString("item");
                map.put(slot, item);
            }

            return map;
        } catch (SQLException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static Map<String, Double> getRoleHealth(UUID uuid) {
        Map<String, Double> map = new HashMap();
        String sql = "select * from " + getTableName(uuid) + ";";

        try {
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String role = resultSet.getString("role");
                Double health = resultSet.getDouble("health");
                map.put(role, health);
            }

            return map;
        } catch (SQLException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static String getTableName(UUID uuid) {
        return String.format("`%s`", uuid);
    }
}