package org.qiuhua.genshinrolesystem.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinrolesystem.Main;
import org.qiuhua.genshinrolesystem.armsfollow.ArmorStandController;
import org.qiuhua.genshinrolesystem.armsfollow.FollowControl;
import org.qiuhua.genshinrolesystem.armsfollow.LockingArms;
import org.qiuhua.genshinrolesystem.combination.Switch;

import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.MessageConfig;
import org.qiuhua.genshinrolesystem.configfile.RoleCombinationConfig;
import org.qiuhua.genshinrolesystem.data.PlayerData;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;
import org.qiuhua.genshinrolesystem.fightingstate.FightingState;
import org.qiuhua.genshinrolesystem.mousecombo.Combo;
import org.qiuhua.genshinrolesystem.sql.mysql.MysqlDataControl;

public class PlayerListener implements Listener {


    //玩家进服事件
    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent event){
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                if (Main.sqlType.equalsIgnoreCase("mysql")) {
                    MysqlDataControl.loadPlayerData(player.getUniqueId());
                }
                //重新给予属性 如果没有data则不执行
                PlayerData data = PlayerDataController.getAllPlayerData().get(player.getUniqueId());
                if(data != null){
                    String combinationId = data.getCombinationId();
                    if(combinationId == null){
                        combinationId = RoleCombinationConfig.getDefaultCombinationId();
                    }
                    Switch.addAttribute(combinationId, player);
                    //判断是否需要取消武器显示
                    if(LockingArms.cancelArmsFollow(player)){
                        ArmorStandController.deSpawnArmorStand(player);
                    }else {
                        FollowControl.addItemFollow(player);
                    }
                }
            }
        },40L);
        //////////////////////////////////////////////////////

    }





    //玩家切换物品栏事件 用来切换组合
    @EventHandler
    public void onPlayerItemHeldEvent (PlayerItemHeldEvent event){
        //切换组合  里面带异步
        Switch.SwitchCombination(event);
    }

    //造成伤害事件
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        //攻击者是玩家才运行
        if (event.getDamager() instanceof Player player) {
            FightingState.addFightingState(player);
        }
    }
    //玩家重生事件
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event){
        //重新给予属性
        Player player = event.getPlayer();
        String combinationId = PlayerDataController.getPlayerData(player).getCombinationId();
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                Switch.CombinationAttribute(combinationId, player);
                player.setHealth(20);
            }
        },5L);
        //////////////////////////////////////////////////////
    }

    //世界切换事件
    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event){
        //重新执行属性添加
        Player player = event.getPlayer();
        String combinationId = PlayerDataController.getPlayerData(player).getCombinationId();
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                Switch.CombinationAttribute(combinationId, player);
            }
        },5L);
    }

    //玩家退出游戏事件
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        //销毁实体包
        ArmorStandController.deSpawnArmorStand(player);
        if (Main.sqlType.equalsIgnoreCase("mysql")) {
            MysqlDataControl.addPlayerData(player.getUniqueId());
            //mysql 玩家退出后需要清除玩家在服务器内的数据
            PlayerDataController.removePlayerData(player.getUniqueId());
        }
    }

    //丢弃物品事件
    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        ItemStack dropItem = event.getItemDrop().getItemStack();
        if(LockingArms.isArmsContrast(player, dropItem)){
            player.sendMessage(MessageConfig.getString("bindingItems"));
            event.setCancelled(true);
        }
    }


    //玩家与实体交互事件
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        EntityType entityType = event.getRightClicked().getType();
        Player player = event.getPlayer();
        //如果是和展示框交互 并且是用的不可交互的物品
        if(entityType == EntityType.ITEM_FRAME || entityType == EntityType.GLOW_ITEM_FRAME){
            if(LockingArms.isArmsContrast(player,player.getInventory().getItemInMainHand())){
                player.sendMessage(MessageConfig.getString("bindingItems"));
                event.setCancelled(true);
            }
        }
    }


    //玩家交互事件
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        //如果未启用虚幻核心按键兼容
        if(!DefaultConfig.getBoolean("UnrealKey.enable")){
            //连击 全程异步
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                @Override
                public void run() {
                    Combo.main(event);
                }
            });
        }
    }
    //玩家将物品切换到副手事件
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getOffHandItem();
        if(LockingArms.isArmsContrast(player, item)){
            player.sendMessage(MessageConfig.getString("bindingItems"));
            event.setCancelled(true);
        }
    }

}
