package org.qiuhua.genshinattributesbackpack.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.configfile.MessageConfig;
import org.qiuhua.genshinattributesbackpack.gui.GuiControl;
import org.qiuhua.genshinattributesbackpack.mousecombo.Combo;


import java.util.ArrayList;
import java.util.List;

public class GenshinAttributesBackpackCommand implements CommandExecutor, TabExecutor {

    public void register() {
        Bukkit.getPluginCommand("GenshinAttributesBackpack").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(sender instanceof Player player){
            if(args.length == 1 && args[0].equalsIgnoreCase("gui")){
                if(player.hasPermission("genshinattributesbackpack.gui")){
                    GuiControl.open(player);
                }else{
                    player.sendMessage(MessageConfig.getString("permissions"));
                }
                return true;
            }
            //重载配置文件
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
                if(player.hasPermission("genshinattributesbackpack.reload")){
                    Main.getMainPlugin().reloadConfig();
                }else{
                    player.sendMessage(MessageConfig.getString("permissions"));
                }
                return true;
            }


            if(args.length == 1 && args[0].equalsIgnoreCase("test")){
                if(player.hasPermission("genshinattributesbackpack.test")){
                    Combo.getPapi(player);
                }else{
                    player.sendMessage(MessageConfig.getString("permissions"));
                }
                return true;
            }


        }else if(sender instanceof ConsoleCommandSender){
            if(args[0].equals("gui")) {
                Bukkit.getConsoleSender().sendMessage("[GenshinAttributesBackpack] 该指令无法在控制台执行");
                return true;
            }
            if(args[0].equals("reload")) {
                Main.getMainPlugin().reloadConfig();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            List<String> result = new ArrayList<>();
            //当参数长度是1时
            if(args.length == 1) {
                if (player.hasPermission("genshinattributesbackpack.gui"))
                    result.add("gui");
                if (player.hasPermission("genshinattributesbackpack.reload"))
                    result.add("reload");
                if (player.hasPermission("genshinattributesbackpack.test"))
                    result.add("test");
                return  result;
            }
        }
        return null;
    }


}
