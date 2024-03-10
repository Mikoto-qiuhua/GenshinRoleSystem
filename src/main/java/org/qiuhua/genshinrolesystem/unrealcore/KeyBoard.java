package org.qiuhua.genshinrolesystem.unrealcore;

import com.daxton.unrealcore.common.event.PlayerKeyBoardEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinrolesystem.armsfollow.LockingArms;
import org.qiuhua.genshinrolesystem.configfile.ComboSkillConfig;
import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.MessageConfig;
import org.qiuhua.genshinrolesystem.data.PlayerData;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;
import org.qiuhua.genshinrolesystem.mythic.MythicMobsSkills;

import java.util.List;

public class KeyBoard {

    //释放技能
    public static void unleashSkills(PlayerKeyBoardEvent event){
        Player player = event.getPlayer();
        //获取按键
        String keyName = event.getKeyName();
        //获取允许的按键列
        List<String> keyList = DefaultConfig.getList("UnrealKey.keyList");
        //如果按键不在允许的列表内
        if(!keyList.contains(keyName)){
            return;
        }

        // 获取玩家主手物品
        ItemStack item = player.getInventory().getItemInMainHand();
        //获取玩家数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        if(item.getType() == Material.AIR || item.getItemMeta() == null) return;
        //如果用来交互的武器是占位武器 或者不是指定武器
        if(LockingArms.getPlaceholderArms(player).isSimilar(item) || !LockingArms.isArmsContrast(player ,item)){
            player.sendMessage(MessageConfig.getString("needArms"));
            data.setLongPress(false);
            return;
        }
        //获取当前角色
        String roleId = data.getRoleId();
        //获取动作类型 1是按下 2是按着 0是弹起
        int keyAction = event.getKeyAction();
        //如果是长按
        if(keyAction == 2){
            data.setLongPress(true);
            return;
        }
        //如果不是弹起
        if(keyAction != 0){
            return;
        }
        if(data.getIsLongPress()){
            keyName = keyName+keyName;
        }
        //获取技能
        String skill = ComboSkillConfig.getString(roleId + "." + keyName);
        if(skill == null || skill.equals("")){
            return;
        }
        //释放
        MythicMobsSkills.castSkill(player, skill);
        data.setLongPress(false);
        if(DefaultConfig.getBoolean("Debug")){
            Bukkit.getLogger().info("======[GenshinRoleSystem]======");
            Bukkit.getLogger().info("本次动作 => 虚幻核心按键施法");
            Bukkit.getLogger().info("施法玩家 => " + player.getName());
            Bukkit.getLogger().info("释法角色 => " + roleId);
            Bukkit.getLogger().info("释法按键 => " + keyName);
            Bukkit.getLogger().info("释法技能 => " + skill);
            Bukkit.getLogger().info("========================================");
        }

    }


}
