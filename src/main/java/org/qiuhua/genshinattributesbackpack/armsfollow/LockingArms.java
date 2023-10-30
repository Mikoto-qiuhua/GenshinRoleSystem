package org.qiuhua.genshinattributesbackpack.armsfollow;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.RoleCombinationConfig;
import org.qiuhua.genshinattributesbackpack.configfile.ToolConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LockingArms {


    //创建一个占位武器
    public static ItemStack getPlaceholderArms(Player player){
        //将字符串转换为 Material 枚举类型
        Material itemType = Material.getMaterial(DefaultConfig.getString("ArmsSlot.item.type"));
        //检查物品类型是否有效
        if(itemType == null){
            itemType = Material.BARRIER;
        }
        //创建一个新物品堆
        ItemStack item = new ItemStack(itemType, 1);
        //获取物品属性
        ItemMeta itemMeta = item.getItemMeta();
        //设置物品名称
        if (itemMeta != null) {
            itemMeta.setDisplayName(DefaultConfig.getString("ArmsSlot.item.name"));
            //设置物品lore
            itemMeta.setLore(ToolConfig.getPapiList(player, DefaultConfig.getList("ArmsSlot.item.lore")));
            //设置物品模型数据
            if(DefaultConfig.getInteger("ArmsSlot.item.customModelData") != -1){
                itemMeta.setCustomModelData(DefaultConfig.getInteger("ArmsSlot.item.customModelData"));
            }
        }
        //物品属性设置回去
        item.setItemMeta(itemMeta);
        return item;
    }


    //获取一个组合内武器
    public static ItemStack getCombinationArms(Player player){
        //获取玩家数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        //拿到组合id
        String combinationId = data.getCombinationId();
        //拿到武器槽位
        Integer armsSlot = RoleCombinationConfig.getArmsSlot(combinationId);
        //拿到整个装备列表
        Map<Integer, ItemStack> equipmentMap = data.getEquipmentMap();
        //如果有武器的key
        if(equipmentMap.containsKey(armsSlot)){
            //复制一份武器
            ItemStack item = equipmentMap.get(armsSlot).clone();
            //获取物品meta
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            //检测是否添加额外lore
            if(DefaultConfig.getBoolean("ArmsSlot.item.loreAdd")){
                //获取物品meta
                lore = ToolConfig.getPapiList(player, DefaultConfig.getList("ArmsSlot.item.lore"));
            }
            if (itemMeta != null) {
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
            }
            item.setItemMeta(itemMeta);
            return item;
        }
        return getPlaceholderArms(player);
    }


    //添加武器槽
    public static void addArmsSlot(Player player){
        Integer armsSlot = DefaultConfig.getInteger("ArmsSlot.slot");
        ItemStack item = getCombinationArms(player);
        player.getInventory().setItem(armsSlot, item);
    }


    //判断手持槽位是否是武器槽位
    public static Boolean isArmsSlot(Player player){
        int armsSlot = DefaultConfig.getInteger("ArmsSlot.slot");
        int heldSlot = player.getInventory().getHeldItemSlot();
        if(armsSlot == heldSlot){
            return true;
        }
        return false;
    }


    //判断槽位是否相同
    public static Boolean isArmsSlot(int slot){
        int armsSlot = DefaultConfig.getInteger("ArmsSlot.slot");
        if(armsSlot == slot){
            return true;
        }
        return false;
    }

    //切换物品栏时判断是否切换到武器槽位
    public static void cancelArmsFollow(PlayerItemHeldEvent event){
        //切换后
        int newSlot = event.getNewSlot();
        //切换前
        int previousSlot = event.getPreviousSlot();
        Player player = event.getPlayer();
        //如果切换后的槽位是武器槽位 就取消武器悬浮
        if(isArmsSlot(newSlot)){
            ItemStack item = new ItemStack(Material.AIR);
            ArmorStandController.spawnArmorStand(player, item);
            return;
        }
        if(isArmsSlot(previousSlot)){
            FollowControl.addItemFollow(player);
            return;
        }

    }


    //判断物品是否相同
    public static boolean isArmsContrast(Player player, ItemStack item) {
        ItemStack aItem = getCombinationArms(player);
        return aItem.isSimilar(item);
    }


    //判断物品是否相同
    public static boolean isArmsContrast(Player player, InventoryClickEvent event) {
        if(event.getHotbarButton() == DefaultConfig.getInteger("ArmsSlot.slot")){
            return true;
        }
        ItemStack item = event.getCurrentItem();
        ItemStack aItem = getCombinationArms(player);
        return aItem.isSimilar(item);
    }


}



