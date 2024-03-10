package org.qiuhua.genshinrolesystem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinrolesystem.armsfollow.ArmorStandController;
import org.qiuhua.genshinrolesystem.armsfollow.FollowControl;
import org.qiuhua.genshinrolesystem.armsfollow.LockingArms;
import org.qiuhua.genshinrolesystem.combination.Switch;
import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.MessageConfig;
import org.qiuhua.genshinrolesystem.configfile.RoleCombinationConfig;
import org.qiuhua.genshinrolesystem.configfile.ToolConfig;
import org.qiuhua.genshinrolesystem.data.PlayerData;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;
import org.qiuhua.genshinrolesystem.sql.mysql.MysqlDataControl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GuiControl {


    public static void open(Player player){
        String title = ToolConfig.getPapiString(player, DefaultConfig.getString("Gui.title"));
        Inventory inventory = Bukkit.createInventory(new CustomizeInventoryHolder(), 54, title);
        loadGuiItem(inventory, player);
        player.openInventory(inventory);
    }

    public static void click(InventoryClickEvent event){
        //点击的界面
        Inventory inventory = event.getInventory();
        //点击的玩家
        Player player = (Player) event.getWhoClicked();
        //点击的槽位id
        int clickSlot = event.getRawSlot();
        //点击是否是shift点击
        boolean isShiftClick = event.getClick().isShiftClick();
        //是否使用数字键移动
        if(event.getClick() == ClickType.NUMBER_KEY){
            event.setCancelled(true);
            return;
        }
        //是否是玩家物品栏
        if(InventoryTool.isPlayerInventory(clickSlot)){
            //如果shift点击
            if(isShiftClick){
                event.setCancelled(true);
                return;
            }
            return;
        }
        //当前点击的物品
        ItemStack currentClickItem = event.getCurrentItem();
        //光标上的物品
        ItemStack cursorItem = event.getCursor();
        //拿起物品还是放下物品
        String pickupOrDrop = "放下物品";
        //查看这次点击的格子属于哪个组合
        String combinationId = InventoryTool.getCombinationId(clickSlot);

        //如果光标上的是空气 那就是拿起物品
        if(cursorItem == null || cursorItem.getType() == Material.AIR){
            pickupOrDrop = "拿起物品";
            deBug(player, clickSlot, currentClickItem, cursorItem, pickupOrDrop, combinationId);
            return;
        }
        //查看这次点击的格子属于哪个组合
        if(combinationId.equals("无组合")){
            event.setCancelled(true);
            player.sendMessage(MessageConfig.getString("defaultPutFailed"));
            return;
        }

        //判断是否放入了角色信息
        if(!InventoryTool.isRoleItem(combinationId, inventory, clickSlot)){
            event.setCancelled(true);
            player.sendMessage(MessageConfig.getString("putArmsAnArtifactsCondition"));
            deBug(player, clickSlot, currentClickItem, cursorItem, pickupOrDrop, combinationId);
            return;
        }
        //光标上的物品是否可以放置
        //list第一个参数是条件信息  第二个参数是布尔值
        List<String> isItemPutList = InventoryTool.isItemPut(clickSlot, cursorItem, inventory);
        if(isItemPutList != null && isItemPutList.get(1).equals("false")){
            event.setCancelled(true);
            player.sendMessage(MessageConfig.getString("putFailed").replaceAll("<condition>", isItemPutList.get(0)));
            deBug(player, clickSlot, currentClickItem, cursorItem, pickupOrDrop, combinationId);
            return;
        }
        deBug(player, clickSlot, currentClickItem, cursorItem, pickupOrDrop, combinationId);
    }



    public static void deBug(Player player, int clickSlot, ItemStack currentClickItem, ItemStack cursorItem, String pickupOrDrop, String combinationId){
        if(!DefaultConfig.getBoolean("Debug")){
            return;
        }
        Bukkit.getLogger().info("======[GenshinRoleSystem]======");
        Bukkit.getLogger().info("本次动作 => " + pickupOrDrop);
        Bukkit.getLogger().info("事件玩家 => " + player.getDisplayName());
        Bukkit.getLogger().info("点击格子 => " + clickSlot);
        if(currentClickItem != null && currentClickItem.getType() != Material.AIR){
            Bukkit.getLogger().info("点击物品 => " + Objects.requireNonNull(currentClickItem.getItemMeta()).getDisplayName());
        }
        if(cursorItem != null && cursorItem.getType() != Material.AIR){
            Bukkit.getLogger().info("光标物品 => " + Objects.requireNonNull(cursorItem.getItemMeta()).getDisplayName());
        }
        Bukkit.getLogger().info("组合ID => " + combinationId);
        Bukkit.getLogger().info("========================================");
    }


    //保存界面上的物品
    public static void saveGuiItem(Inventory inventory, Player player){
        //获取玩家数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        data.removeEquipmentMap();
        for(int i = 0; i <= 53; i++){
            ItemStack item = inventory.getItem(i);
            if(item != null && item.getType() != Material.AIR){
                data.putEquipmentList(i, item);
            }
        }
    }

    //加载界面物品数据
    public static void loadGuiItem(Inventory inventory, Player player){
        //获取玩家数据
        PlayerData data = PlayerDataController.getAllPlayerData().get(player.getUniqueId());
        //如果玩家数据为null 那就尝试从数据库加载
        if(data == null){
            Bukkit.getLogger().warning("UUID => " + player.getUniqueId() + " 数据异常 data为null");
            Bukkit.getLogger().warning("尝试从数据库加载");
            MysqlDataControl.loadPlayerData(player.getUniqueId());
            //重新获取玩家数据
            data = PlayerDataController.getPlayerData(player);
        }
        //获取装备map
        Map<Integer, ItemStack> map = data.getEquipmentMap();
        for(Integer slot : map.keySet()){
            ItemStack item = map.get(slot);
            inventory.setItem(slot, item);
        }
    }


    //界面关闭时该做的
    public static void closeGui(Inventory inventory, Player player){
        //保存数据
        saveGuiItem(inventory, player);
        //获取数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        String combinationId;
        //检测玩家当前组合id是否需要重置成第一套
        if(data.getCombinationId() == null){
            combinationId = RoleCombinationConfig.getDefaultCombinationId();
        }else{
            combinationId = data.getCombinationId();
        }
        Switch.CombinationAttribute(combinationId, player);
        //判断是否需要取消武器显示
        if(LockingArms.cancelArmsFollow(player)){
            ArmorStandController.deSpawnArmorStand(player);
        }else {
            FollowControl.addItemFollow(player);
        }
    }





}

