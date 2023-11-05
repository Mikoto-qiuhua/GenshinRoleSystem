package org.qiuhua.genshinattributesbackpack.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataController {

    //获取一个玩家数据  如果没有 则创建
    private static final Map<UUID, PlayerData> allMaster = new HashMap<>();
    public static PlayerData getPlayerData(Player player){
        UUID uuid = player.getUniqueId();
        if(!allMaster.containsKey(uuid)){
            allMaster.put(uuid, new PlayerData());
        }
        return allMaster.get(uuid);
    }


    public static PlayerData getPlayerData(UUID uuid){
        if(!allMaster.containsKey(uuid)){
            allMaster.put(uuid, new PlayerData());
        }
        return allMaster.get(uuid);
    }

    public static Boolean isPlayerData(UUID uuid){
        return allMaster.containsKey(uuid);
    }

    public static Map<UUID, PlayerData> getAllPlayerData(){
        return allMaster;
    }


    public static void removePlayerData(UUID uuid){
        allMaster.remove(uuid);
//        Bukkit.getLogger().info("已清除当前服务器玩家数据UUID => " + uuid);
    }




}
