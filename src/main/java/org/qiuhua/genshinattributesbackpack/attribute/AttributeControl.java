package org.qiuhua.genshinattributesbackpack.attribute;

import com.skillw.attsystem.api.AttrAPI;
import com.skillw.attsystem.api.compiled.CompiledData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.RoleCombinationConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.*;

public class AttributeControl {




    //使用组合id添加对应的属性
    public static void addAttribute(String combinationId, Player player){
        Map<Integer, ItemStack> equipmentMap = PlayerDataController.getPlayerData(player).getEquipmentMap();
        List<Integer> combinationSlotList = RoleCombinationConfig.getCombinationSlotList(combinationId);
        List<ItemStack> itemList = new ArrayList<>();
        try {
            for(Integer slot : combinationSlotList){
                ItemStack item = equipmentMap.get(slot);
                if(item != null){
                    itemList.add(item);
                }
            }
            CompiledData attribute = AttrAPI.readItems(itemList, player, null);
            AttrAPI.addCompiledData(player, "GenshinAttributes", attribute);
        }finally {
            if(DefaultConfig.getBoolean("Debug")){
                Bukkit.getLogger().info("======[GenshinAttributesBackpack]======");
                Bukkit.getLogger().info("本次动作 => 添加属性");
                Bukkit.getLogger().info("事件玩家 => " + player.getDisplayName());
                Bukkit.getLogger().info("组合ID => " + combinationId);
                Bukkit.getLogger().info("涉及的Slot => " + combinationSlotList);
                Bukkit.getLogger().info("========================================");
            }
        }

    }

    //使用属性合集添加属性  套装用的
    public static void addAttributeSuits(List<String> list, Player player){
        AttrAPI.addCompiledData(player, "GenshinAttributes_Suits", list);
    }







}
