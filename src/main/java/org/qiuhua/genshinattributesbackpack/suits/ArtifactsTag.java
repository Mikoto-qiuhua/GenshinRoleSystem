package org.qiuhua.genshinattributesbackpack.suits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.RoleCombinationConfig;
import org.qiuhua.genshinattributesbackpack.configfile.ToolConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArtifactsTag {
    //提供组合id 获取装备信息添加全部tag
    public static void addTag(String combinationId, Player player){
        PlayerData data = PlayerDataController.getPlayerData(player);
        //获取对应物品槽位
        List<Integer> slotList = RoleCombinationConfig.getCombinationSlotList(combinationId);
        Map<Integer, ItemStack> equipment = data.getEquipmentMap();
        //清空在进行添加操作
        data.removeEquipmentTagAndNumber();
        for(Integer slot : slotList){
            ItemStack item = equipment.get(slot);
            if (item != null && item.getType() != Material.AIR){
                String tag = getTag(item);
                if(tag != null){
                    data.putEquipmentTagAndNumber(tag, 1);
                }
            }
        }
        if(DefaultConfig.getBoolean("Debug")){
            Bukkit.getLogger().info("======[GenshinAttributesBackpack]======");
            Bukkit.getLogger().info("本次动作 => 统计套装tag");
            for (Map.Entry<String, Integer> entry : data.getequipmentTagAndNumberMap().entrySet()) {
                String key = entry.getKey(); // 获取 key
                Integer value = entry.getValue(); // 获取 value
                Bukkit.getLogger().info(key + " => " + value);
            }
            Bukkit.getLogger().info("========================================");
        }

    }

    //单件物品获取标签
    public static String getTag(ItemStack item){
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Suits.condition") + "(.*)");
        if (loreList != null) {
            for(String lore : loreList){
                lore = ToolConfig.removeColorCode(lore);
                Matcher matcher = pattern.matcher(lore);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
        }
        return null;
    }
}
