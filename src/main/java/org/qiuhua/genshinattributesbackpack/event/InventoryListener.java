package org.qiuhua.genshinattributesbackpack.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.qiuhua.genshinattributesbackpack.armsfollow.LockingArms;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.MessageConfig;
import org.qiuhua.genshinattributesbackpack.gui.GuiControl;
import org.qiuhua.genshinattributesbackpack.gui.InventoryTool;

public class InventoryListener  implements Listener {
    //当玩家点击物品栏中的格子时触发事件事件
    @EventHandler
    public void onInventoryClickEvent (InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        //是否点击了不能点击的物品
        if(LockingArms.isArmsContrast(player, event)){
            player.sendMessage(MessageConfig.getString("bindingItems"));
            event.setCancelled(true);
        }
        Inventory inv = event.getInventory();
        //界面判断
        if(InventoryTool.isCustomizeInventoryHolder(inv)){
            GuiControl.click(event);
            return;
        }

    }

    //当玩家关闭物品栏时
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event){
        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();
        if(InventoryTool.isCustomizeInventoryHolder(inv)){
            //关闭gui后的事情
            GuiControl.closeGui(inv, player);
            return;
        }
    }
    //拖拽物品事件
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event){
        Inventory inv = event.getInventory();
        if(InventoryTool.isCustomizeInventoryHolder(inv)){
            InventoryTool.banDragMenu(event);
            return;
        }
    }

}
