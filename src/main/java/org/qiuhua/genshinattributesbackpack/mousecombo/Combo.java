package org.qiuhua.genshinattributesbackpack.mousecombo;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.armsfollow.LockingArms;
import org.qiuhua.genshinattributesbackpack.configfile.ComboSkillConfig;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.MessageConfig;
import org.qiuhua.genshinattributesbackpack.configfile.ToolConfig;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;
import org.qiuhua.genshinattributesbackpack.mythic.MythicMobsSkills;

import java.util.List;

public class Combo {
    public static void main(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        if(action == Action.PHYSICAL) return;
        ItemStack item = event.getItem();
        if(item== null || item.getType() == Material.AIR || item.getItemMeta() ==null) return;

        //如果用来交互的武器是占位武器
        if(LockingArms.getPlaceholderArms(player).isSimilar(item)){
            player.sendMessage(MessageConfig.getString("needArms"));
            return;
        }
        //如果是指定的武器
        if(LockingArms.isArmsContrast(player ,item)){
            //获取玩家数据
            PlayerData data = PlayerDataController.getPlayerData(player);
            //获取当前时间
            long time = System.currentTimeMillis();
            //获取本次动作类型
            String mouseAction = mouseAction(action, player.isSneaking());
            //如果连接间隔小于
            if(data.getComboInterval() != null && time - data.getComboInterval() <= DefaultConfig.getInteger("ComboIntervalMin")) {
                return;
            }
            //如果连击超时
            if(isComboIntervalMax(time, data)){
                data.clearComboList();
            }
            //设置间隔
            data.setComboInterval(time);
            //写入按键
            data.addComboList(mouseAction);
            //如果长度等于3
            if(data.getAllComboList().size() == DefaultConfig.getInteger("ComboMax")){
                try{
                    //获取当前角色
                    String roleId = data.getRoleId();
                    //获取连击列表
                    String combo = String.join("", data.getAllComboList());
                    String skill = ComboSkillConfig.getString(roleId + "." + combo);
                    if(skill == null || skill.equals("")){
                        return;
                    }
                    MythicMobsSkills.castSkill(player, skill);
                    if(DefaultConfig.getBoolean("Debug")){
                        Bukkit.getLogger().info("======[GenshinAttributesBackpack]======");
                        Bukkit.getLogger().info("本次动作 => 鼠标连击");
                        Bukkit.getLogger().info("施法玩家 => " + player.getName());
                        Bukkit.getLogger().info("释法角色 => " + roleId);
                        Bukkit.getLogger().info("释法顺序 => " + combo);
                        Bukkit.getLogger().info("释法技能 => " + skill);
                        Bukkit.getLogger().info("========================================");
                    }
                }finally {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            data.clearComboList();
                        }
                    }, 3);
                }
            }


        }
    }




    //获取点击动作类型
    public static String mouseAction(Action action, Boolean shift){
        String mouseAction = "";
        if(shift) mouseAction = "S";
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) return mouseAction + "L";
        return mouseAction + "R";
    }


    //判断连击是否超时
    public static Boolean isComboIntervalMax(Long time, PlayerData data){
        if(data.getComboInterval() == null) return false;
        //用现在时间减去上一次点击的时间
        long intervalTime = time - data.getComboInterval();
        //如果小于或者等于 则没超时
        if(intervalTime <= DefaultConfig.getInteger("ComboIntervalMax")){
            return false;
        }
        return true;
    }



    public static String getPapi(Player player){
        List<String> comboList = PlayerDataController.getPlayerData(player).getAllComboList();
        StringBuilder combo = new StringBuilder();
        for(int i = 0; i < comboList.size(); i++){
            if(i == comboList.size() - 1){
                combo.append(ToolConfig.getPapiString(player, DefaultConfig.getString("ComboPapi." + comboList.get(i))));
            }else {
                combo.append(ToolConfig.getPapiString(player, DefaultConfig.getString("ComboPapi." + comboList.get(i)))).append(ToolConfig.getPapiString(player, DefaultConfig.getString("ComboPapi.Divide")));
            }
        }
        //将长度增加到不会改变图片位置的长度
        int comboMax = DefaultConfig.getInteger("ComboMax");
        //如果长度小于5
        if(combo.length() < comboMax + comboMax - 1){
            for(int i = combo.length(); i < comboMax + comboMax - 1; i++){
                combo.append(ToolConfig.getPapiString(player, DefaultConfig.getString("ComboPapi.Default")));
            }
        }
//        player.sendMessage(comboList.toString());
//        player.sendMessage(combo + " 长度 " + combo.length());
        return combo.toString();
    }

}
