package org.qiuhua.genshinattributesbackpack.sql.sqlite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.MessageConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SqliteDataControl {

    public static void addEquipmentData(UUID uuid, Map<Integer, ItemStack> map) {
        if (!map.isEmpty()) {
            for (Integer slot : map.keySet()) {
                String item = itemStackSave(map.get(slot));
                SqliteControl.insert(uuid, slot, item);
            }

        }
    }

    public static void addAllEquipmentData() {
        Map<UUID, PlayerData> allPlayerData = PlayerDataController.getAllPlayerData();

        try {
            SqliteControl.connect();
            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("======[GenshinAttributesBackpack]======");
                Bukkit.getLogger().info("本次动作 => 写入装备数据");
            }

            for (UUID uuid : allPlayerData.keySet()) {
                if (DefaultConfig.getBoolean("Debug")) {
                    Bukkit.getLogger().info("UUID => " + uuid);
                }

                SqliteControl.dropTable(uuid);
                SqliteControl.createTable(uuid);
                Map<Integer, ItemStack> map = PlayerDataController.getPlayerData(uuid).getEquipmentMap();
                addEquipmentData(uuid, map);
            }

            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("========================================");
            }
        } finally {
            SqliteControl.close();
        }

    }

    public static void loadEquipmentData(String strUuid) {
        UUID uuid = UUID.fromString(strUuid);
        PlayerData data = PlayerDataController.getPlayerData(uuid);
        Map<Integer, ItemStack> equipmentMap = data.getEquipmentMap();

        try {
            SqliteControl.createTable(uuid);
            Map<Integer, String> sqlMap = SqliteControl.selectAll(uuid);

            for (Integer slot : sqlMap.keySet()) {
                String str = sqlMap.get(slot);
                ItemStack item = itemStackLoad(str);
                if (equipmentMap == null) {
                    Main.getMainPlugin().getLogger().severe("还原物品出现错误 玩家装备数据容器为Null");
                    return;
                }

                equipmentMap.put(slot, item);
            }

        } catch (Exception var9) {
            throw new RuntimeException(var9);
        }
    }

    public static void loadEquipmentData(UUID uuid) {
        PlayerData data = PlayerDataController.getPlayerData(uuid);
        Map<Integer, ItemStack> equipmentMap = data.getEquipmentMap();

        try {
            SqliteControl.connect();
            SqliteControl.createTable(uuid);
            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("======[GenshinAttributesBackpack]======");
                Bukkit.getLogger().info("本次动作 => 还原装备数据");
                Bukkit.getLogger().info("UUID => " + uuid);
                Bukkit.getLogger().info("========================================");
            }

            Map<Integer, String> sqlMap = SqliteControl.selectAll(uuid);

            for (Integer slot : sqlMap.keySet()) {
                String str = sqlMap.get(slot);
                ItemStack item = itemStackLoad(str);
                if (equipmentMap == null) {
                    Main.getMainPlugin().getLogger().severe("还原物品出现错误 玩家装备数据容器为Null");
                    return;
                }

                equipmentMap.put(slot, item);
            }
        } finally {
            SqliteControl.close();
        }

    }

    public static void StarServerLoadData() {
        SqliteControl.connect();

        try {
            List<String> list = SqliteControl.allTableName();
            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("======[GenshinAttributesBackpack]======");
                Bukkit.getLogger().info("本次动作 => 还原装备数据");
            }

            String str;
            for(Iterator<String> var1 = list.iterator(); var1.hasNext(); loadEquipmentData(str)) {
                str = var1.next();
                if (DefaultConfig.getBoolean("Debug")) {
                    Bukkit.getLogger().info("UUID => " + str);
                }
            }

            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("========================================");
            }
        } finally {
            SqliteControl.close();
        }

    }

    public static String itemStackSave(ItemStack itemStack) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item", itemStack);
        return yml.saveToString();
    }

    public static ItemStack itemStackLoad(String str) {
        YamlConfiguration yml = new YamlConfiguration();

        try {
            yml.loadFromString(str);
        } catch (InvalidConfigurationException var3) {
            Main.getMainPlugin().getLogger().severe("无法加载物品");
            throw new RuntimeException(var3);
        }

        return yml.getItemStack("item");
    }

    public static void autoSave() {
        Integer autoTime = DefaultConfig.getInteger("AutoSave");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                Main.getMainPlugin().getLogger().info(MessageConfig.getString("autoSave"));
                SqliteDataControl.addAllEquipmentData();
            }
        }, 0L, (long) (autoTime * 60) * 20L);
    }
}
