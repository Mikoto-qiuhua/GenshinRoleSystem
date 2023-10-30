package org.qiuhua.genshinattributesbackpack.gui;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinattributesbackpack.combination.Switch;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.MessageConfig;
import org.qiuhua.genshinattributesbackpack.configfile.RoleCombinationConfig;
import org.qiuhua.genshinattributesbackpack.configfile.ToolConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryTool {

    //判断界面
    public static boolean isCustomizeInventoryHolder (InventoryHolder holder)
    {
        return holder instanceof CustomizeInventoryHolder;
    }
    public static boolean isCustomizeInventoryHolder (Inventory inventory)
    {
        return inventory.getHolder() instanceof CustomizeInventoryHolder;
    }

    /**
     * 如果返回true就代表这个是玩家物品栏
     * 不是上半部分
     * @param slot
     * @return
     */
    public static boolean isPlayerInventory(int slot){
        return slot < 0 || slot >= 54;
    }

    /**
     * 如果返回true就代表这里是空格子
     * @param itemStack
     * @return
     */
    public static boolean isAirOrNull (ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == (Material.AIR);
    }


    //点击的格子属于哪个组合
    public static String getCombinationId(Integer slot){
        //获取一级节点的组合列表
        Set<String> list = RoleCombinationConfig.getCombinationList();
        for(String key : list){
            //先看是不是圣遗物的格子
            ConfigurationSection artifactsSection = RoleCombinationConfig.getArtifactsSection(key);
            Set<String> artifactsList = artifactsSection.getKeys(false);
            for(String artifactsKey : artifactsList){
                if(slot == artifactsSection.getInt(artifactsKey + ".slot")){
                     return key;
                }
            }
            //看是不是角色格子
            if(slot == RoleCombinationConfig.getInt(key + ".Role.slot")){
                return key;
            }
            //看是不是武器格子
            if(slot == RoleCombinationConfig.getInt(key + ".Arms.slot")){
                return key;
            }
        }
        return "无组合";
    }

    //这个物品是否可以放入
    public static List<String> isItemPut(Integer slot, ItemStack item, Inventory inv){
        //获取一级节点的组合列表
        Set<String> list = RoleCombinationConfig.getCombinationList();
        List<String> isItemPutList = new ArrayList<>();
        for(String key : list){
            //先看是不是圣遗物的格子
            ConfigurationSection artifactsSection = RoleCombinationConfig.getArtifactsSection(key);
            Set<String> artifactsList = artifactsSection.getKeys(false);
            for(String artifactsKey : artifactsList){
                if(slot == artifactsSection.getInt(artifactsKey + ".slot")){
                    String condition = artifactsSection.getString(artifactsKey + ".condition");
                    isItemPutList.add(condition);
                    isItemPutList.add(isItemLore(item, condition).toString());
                    return isItemPutList;
                }
            }
            //看是不是角色格子
            if(slot == RoleCombinationConfig.getInt(key + ".Role.slot")){
                //条件不符提示
                String condition = RoleCombinationConfig.getString(key + ".Role.condition");
                //重复信息提示
                String repeatRole = MessageConfig.getString("repeatRole");
                Boolean isItemLore = isItemLore(item, condition);
                Boolean isRepeatRole = isRepeatRole(item, inv);
                //如果这个物品本身就不能放
                if(!isItemLore){
                    isItemPutList.add(condition);
                    isItemPutList.add("false");
                    return isItemPutList;
                }
                //如果是重复角色
                if(isRepeatRole){
                    isItemPutList.add(repeatRole);
                    isItemPutList.add("false");
                }
                isItemPutList.add(condition);
                isItemPutList.add("true");
                return isItemPutList;
            }
            //看是不是武器格子
            if(slot == RoleCombinationConfig.getInt(key + ".Arms.slot")){
                String condition = RoleCombinationConfig.getString(key + ".Arms.condition");
                //检查这个物品是否可以放入
                if(!isItemLore(item, condition)){
                    isItemPutList.add(condition);
                    isItemPutList.add("false");
                    return isItemPutList;
                }
                //检查武器是否匹配
                //获取当前武器类型
                String armsType = ToolConfig.getArmsType(item);
                //获取当前角色物品
                ItemStack a = inv.getItem(RoleCombinationConfig.getInt(key + ".Role.slot"));
                //拿到当前角色名称
                String roleId = "无角色";
                if(a != null){
                    roleId = getItemRole(a);
                }
                //检测武器类型是否匹配
                isItemPutList.add("武器不符");
                if(ToolConfig.isRoleArmsType(roleId, armsType)){
                    isItemPutList.add("true");
                }else{
                    isItemPutList.add("false");
                }
                return isItemPutList;
            }
        }
        return null;
    }

    //是否有角色重复
    public static Boolean isRepeatRole(ItemStack item, Inventory inv){
        String role = null;
        //获取角色名称
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Role.condition") + "(.*)");
        if (loreList != null) {
            for(String lore : loreList){
                lore = ToolConfig.removeColorCode(lore);
                Matcher matcher = pattern.matcher(lore);
                if(matcher.find()){
                   role = matcher.group(1);
                }
            }
        }
        ////////////////////////////////////////////
        //如果获取不到这个角色
        if(role == null){
            return false;
        }
        //获取其他角色槽位
        //获取全部角色槽位列表
        List<Integer> allRoleSlotList = RoleCombinationConfig.gerAllRoleSlotList();
        //如果是空的
        if(allRoleSlotList.isEmpty()){
            return false;
        }
//        System.out.println("11111111111" + role);
        for(Integer slot : allRoleSlotList){
            ItemStack aItem = inv.getItem(slot);
//            System.out.println("槽位" + slot);
            if(aItem != null && aItem.getType() != Material.AIR){
                String aRole = getRole(aItem);
//                System.out.println("11111111111" + aRole);
                if(aRole.equals(role)){
                    return true;
                }
            }
        }
        return false;
    }

    private static String getRole(ItemStack item) {
        String role = null;
        //获取角色名称
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Role.condition") + "(.*)");
        if (loreList != null) {
            for(String lore : loreList){
                lore = ToolConfig.removeColorCode(lore);
                Matcher matcher = pattern.matcher(lore);
                if(matcher.find()){
                    role = matcher.group(1);
                }
            }
        }
        return role;
    }



    //在物品上搜索lore
    public static Boolean isItemLore(ItemStack item, String str){
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        if(loreList == null){
            return false;
        }
        for(String lore : loreList){
            lore = ToolConfig.removeColorCode(lore);
            if(lore.equals(str)){
                return true;
            }
        }
        return false;
    }

    //判断是否放入了角色
    public static Boolean isRoleItem(String combinationId, Inventory inventory, int clickSlot){
        int slot = RoleCombinationConfig.getInt(combinationId + ".Role.slot");
        if(clickSlot == slot){
            return true;
        }
        ItemStack item = inventory.getItem(slot);
        if(item != null && item.getType() != Material.AIR){
            return true;
        }
        return false;
    }


    //禁止拖拽
    public static void banDragMenu(InventoryDragEvent event){
        List<Integer> list = new ArrayList<>(event.getRawSlots());
        int slot = Collections.min(list);
        if(slot <= 53){
            event.setCancelled(true);
        }
    }

    //获取物品上的角色信息
    public static String getItemRole(ItemStack item){
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Role.condition") + "(.*)");
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




