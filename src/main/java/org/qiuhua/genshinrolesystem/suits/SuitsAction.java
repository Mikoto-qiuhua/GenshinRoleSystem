package org.qiuhua.genshinrolesystem.suits;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.qiuhua.genshinrolesystem.Main;
import org.qiuhua.genshinrolesystem.attribute.AttributeControl;
import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.SuitsFile;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;
import org.qiuhua.genshinrolesystem.mythic.MythicMobsSkills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuitsAction {



    //使用当前的套装列表执行动作
    public static void main(Player player, String action){
        //获取套装列表
        Map<String, Integer> suitsList = PlayerDataController.getPlayerData(player).getequipmentTagAndNumberMap();
        if(suitsList.isEmpty()){
            return;
        }
        if(DefaultConfig.getBoolean("Debug")){
            Bukkit.getLogger().info("======[GenshinRoleSystem]======");
            Bukkit.getLogger().info("本次动作 => 套装加成");
        }
        //获取配置的套装列表
        HashMap<String, ConfigurationSection> suitsConfigList = SuitsFile.getSuitsAll();
        //创建一个属性列表
        List<String> attributeList = new ArrayList<>();
        for(String suitsId : suitsList.keySet()){
            //如果配置里面有这个套装
            if(suitsConfigList.containsKey(suitsId)){
                if(DefaultConfig.getBoolean("Debug")){
                    Bukkit.getLogger().info("触发套装 => " + suitsId);
                }
                //拿到这个套装的配置
                ConfigurationSection section = suitsConfigList.get(suitsId);
                //拿到穿了几件套
                Integer number = suitsList.get(suitsId);
                //拿到这个套装配置的全部key
                for(String tagValue : section.getKeys(false)){
                    //如果玩家身上的数量大于这次获取的key值
                    if(number >= Integer.parseInt(tagValue)){
                        if(DefaultConfig.getBoolean("Debug")){
                            Bukkit.getLogger().info("触发数量 => " + number);
                        }
                        //执行mm技能
                        mySkills(player, section, tagValue, action);
                        //执行命令
                        cmd(player, section, tagValue, action);
                        //获取属性
                        attributeList = attribute(action, section, tagValue, attributeList);
                    }
                }
            }
        }
        if(!attributeList.isEmpty()){
            AttributeControl.addAttributeSuits(attributeList, player);
            if(DefaultConfig.getBoolean("Debug")){
                Bukkit.getLogger().info("增加属性 => " + attributeList);
            }
        }
        if(DefaultConfig.getBoolean("Debug")){
            Bukkit.getLogger().info("========================================");
        }


    }


    //执行mm技能
    public static void mySkills(Player player, ConfigurationSection section, String value, String action){
        //获取技能列表
        List<String> skillsList = section.getStringList(value + ".mySkills." + action);
        for(String skill : skillsList){
            MythicMobsSkills.castSkill(player, skill);
            if(DefaultConfig.getBoolean("Debug")){
                Bukkit.getLogger().info("触发技能 => " + skill);
            }
        }
    }


    //执行命令
    public static void cmd(Player player, ConfigurationSection section, String value, String action){
        List<String> cmdList = section.getStringList(value + ".cmd." + action);
        for(String cmd : cmdList){
            cmd = cmd.replace("<p>", player.getName());
            String finalCmd = cmd;
            Bukkit.getScheduler().runTask(Main.getMainPlugin(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                }
            });
            if(DefaultConfig.getBoolean("Debug")){
                Bukkit.getLogger().info("触发命令 => " + cmd);
            }
        }

    }

    //获取套件加成的属性
    public static List<String> attribute(String key, ConfigurationSection section, String value, List<String> arrayList){
        List<String> attribute = section.getStringList(value + ".attribute." + key);
        arrayList.addAll(attribute);
        return arrayList;
    }





}
