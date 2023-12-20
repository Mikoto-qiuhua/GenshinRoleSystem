package org.qiuhua.genshinattributesbackpack.armsfollow;

import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.EntityTypes;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorStandController {


    //记录了全部武器漂浮盔甲架
    private static final Map<UUID, EntityInstance> allArmorStand = new HashMap<>();

    //
    private static BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), ArmorStandController::asyncUpdateArmorStand, 0L, 2L);

    //生成一个ady的盔甲架
    public static void spawnArmorStand(Player player,ItemStack item){
        UUID uuid = player.getUniqueId();
        EntityInstance entity = PlayerDataController.getPlayerData(player).getEntityArmorStand();
        //如果玩家没有自己的 就创建一个新的
        if(entity == null){
            Manager manager =  AdyController.getEntityManager();
            entity = manager.create(EntityTypes.ARMOR_STAND, player.getLocation());
            AdyArmorStand armorStand = (AdyArmorStand) entity;
            entity.setCustomName(player.getDisplayName() + "的武器跟随");
            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setEquipment(EquipmentSlot.HEAD, item);
            PlayerDataController.getPlayerData(player).setEntityArmorStand(entity);
        }
        AdyArmorStand armorStand = (AdyArmorStand) entity;
        armorStand.setEquipment(EquipmentSlot.HEAD, item);
        //添加进更新列表
        allArmorStand.put(player.getUniqueId(), entity);
        entity.respawn();
    }


    //移动一个ady盔甲架
    public static void tpArmorStand(Player player){
        EntityInstance entity = allArmorStand.get(player.getUniqueId());
        entity.teleport(player.getLocation());
    }

    //移动一个ady盔甲架
    public static void tpArmorStand(UUID uuid){
        EntityInstance entity = allArmorStand.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            entity.teleport(player.getLocation());
        }
    }

    //异步循环处理盔甲架位置
    public static void asyncUpdateArmorStand(){
        if(!allArmorStand.isEmpty()){
            for(UUID uuid : allArmorStand.keySet()){
                Player player = Bukkit.getPlayer(uuid);
                //如果这个玩家获取不到 就跳过他
                if(player == null){
                    continue;
                }
                tpArmorStand(uuid);
//                System.out.println(uuid);
            }
        }
    }

    //移除一个盔甲架 仅销毁数据包 并且移除更新列表
    public static void deSpawnArmorStand(Player player){
        UUID uuid = player.getUniqueId();
        EntityInstance entity = PlayerDataController.getPlayerData(player).getEntityArmorStand();
        if(entity != null){
            entity.despawn(true, true);
            PlayerDataController.getPlayerData(player).setEntityArmorStand(null);
        }
        allArmorStand.remove(uuid);
    }
}
