package org.qiuhua.genshinrolesystem.sql.mysql;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinrolesystem.Main;
import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.MessageConfig;
import org.qiuhua.genshinrolesystem.data.PlayerData;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;

import java.util.Map;
import java.util.UUID;

public class MysqlDataControl {



    //保存指定玩家数据
    public static void addPlayerData(UUID uuid) {
        PlayerData data = PlayerDataController.getAllPlayerData().get(uuid);

        if(data == null){
            Bukkit.getLogger().warning("UUID => " + uuid + " 数据异常 data为null");
            Bukkit.getLogger().warning("已跳过保存");
            return;
        }
        MysqlControl.isValid();
        MysqlControl.dropTable(uuid);
        MysqlControl.createTable(uuid);
        if (DefaultConfig.getBoolean("Debug")) {
            Bukkit.getLogger().info("======[GenshinRoleSystem]======");
            Bukkit.getLogger().info("本次动作 => 写入玩家数据");
            Bukkit.getLogger().info("UUID => " + uuid);
        }
        //保存装备
        Map<Integer, ItemStack> map = data.getEquipmentMap();
        if(!map.isEmpty()){
            for (Integer slot : map.keySet()) {
                String item = itemStackSave(map.get(slot));
                MysqlControl.addItemSlot(uuid, slot, item);
            }
        }
        //保存血量信息
        Map<String, Double> roleMap = data.getRoleHealthList();
            if (!roleMap.isEmpty()) {
                for (String s : roleMap.keySet()) {
                    Double h = roleMap.get(s);
                    MysqlControl.addRoleHealth(uuid, s, h);
                }
            }
        if (DefaultConfig.getBoolean("Debug")) {
            Bukkit.getLogger().info("========================================");
        }

    }


    //保存全部数据
    public static void addAllPlayerData() {
        MysqlControl.isValid();
        Map<UUID, PlayerData> allPlayerData = PlayerDataController.getAllPlayerData();
        for (UUID uuid : allPlayerData.keySet()) {
            addPlayerData(uuid);
        }

    }


    //加载一个指定玩家数据
    public static void loadPlayerData(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null){
            return;
        }
        MysqlControl.isValid();
        PlayerData data = PlayerDataController.getPlayerData(uuid);
        try {
            //创建表格 如果没有这个表 就会创建
            MysqlControl.createTable(uuid);
            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("======[GenshinRoleSystem]======");
                Bukkit.getLogger().info("本次动作 => 读取玩家数据");
            }
            Map<Integer, ItemStack> equipmentMap = data.getEquipmentMap();
            Map<Integer, String> sqlItemMap = MysqlControl.getItemSlot(uuid);

            for (Integer slot : sqlItemMap.keySet()) {
                String str = sqlItemMap.get(slot);
                ItemStack item = itemStackLoad(str);
                if (item != null) {
                    if (equipmentMap == null) {
                        Main.getMainPlugin().getLogger().severe("还原物品出现错误 玩家装备数据容器为Null");
                        return;
                    }

                    equipmentMap.put(slot, item);
                }
            }

            Map<String, Double> sqlRoleMap = MysqlControl.getRoleHealth(uuid);
            Map<String, Double> roleMap = data.getRoleHealthList();

            for (String role : sqlRoleMap.keySet()) {
                Double h = sqlRoleMap.get(role);
                roleMap.put(role, h);
            }

            if (DefaultConfig.getBoolean("Debug")) {
                Bukkit.getLogger().info("========================================");
            }

        } catch (Exception var9) {
            Main.getMainPlugin().getLogger().severe("读取数据失败 UUID -> " + uuid);
            throw new RuntimeException(var9);
        }
        Bukkit.getLogger().info("读取数据完成 UUID -> " + uuid);
    }

    public static String itemStackSave(ItemStack itemStack) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item", itemStack);
        return yml.saveToString();
    }

    public static ItemStack itemStackLoad(String str) {
        YamlConfiguration yml = new YamlConfiguration();
        if (str == null) {
            return null;
        } else {
            try {
                yml.loadFromString(str);
            } catch (InvalidConfigurationException var3) {
                Main.getMainPlugin().getLogger().severe("无法加载物品");
                throw new RuntimeException(var3);
            }

            return yml.getItemStack("item");
        }
    }




    //自动保存
    public static void autoSave() {
        int autoTime = DefaultConfig.getInteger("AutoSave");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                Main.getMainPlugin().getLogger().info(MessageConfig.getString("autoSave"));
                for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    UUID uuid = onlinePlayer.getUniqueId();
                    MysqlDataControl.addPlayerData(uuid);
                }
            }
        }, (autoTime * 60L) * 20L, (autoTime * 60L) * 20L);

    }
}
