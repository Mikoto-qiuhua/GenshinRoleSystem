package org.qiuhua.genshinattributesbackpack.armsfollow;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinattributesbackpack.configfile.RoleCombinationConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.Map;

public class FollowControl {


    public static void addItemFollow(Player player){
        //获取玩家数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        //获取组合id
        String combinationId = data.getCombinationId();
        //获取武器信息
        ItemStack item = new ItemStack(Material.IRON_HELMET);
        Integer armsSlot = RoleCombinationConfig.getArmsSlot(combinationId);
        Map<Integer, ItemStack> map = data.getEquipmentMap();
        if(map.containsKey(armsSlot)){
            item = map.get(armsSlot);
        }
        //如果物品是空的 就删掉现在的盔甲架
        if(item.getType() == Material.IRON_HELMET){
            ArmorStandController.deSpawnArmorStand(player);
            return;
        }
        ArmorStandController.spawnArmorStand(player, item);
    }


}
