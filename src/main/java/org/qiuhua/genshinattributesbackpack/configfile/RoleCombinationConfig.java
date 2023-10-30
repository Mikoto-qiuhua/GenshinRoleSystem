package org.qiuhua.genshinattributesbackpack.configfile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RoleCombinationConfig {
    private static FileConfiguration file;

    //重新加载
    public static void reload ()
    {
        file = ToolConfig.load(new File(Main.getMainPlugin().getDataFolder (),"\\RoleCombination.yml"));
    }

    public static Boolean getBoolean( String value){
        return file.getBoolean(value);
    }

    public static String getString(String value){
        return file.getString(value);
    }

    public static int getInt(String value){
        return file.getInt(value);
    }

    public static List<String> getList(String value){
        return file.getStringList(value);
    }

    public static Double getDouble(String value){
        return file.getDouble(value);
    }

    //这里会返回一级节点列表
    public static Set<String> getCombinationList(){ return file.getKeys(false); }

    //这里会返回Artifacts的列表
    //传递的key是一级节点名称
    public static ConfigurationSection getArtifactsSection(String key){
        //获取配置文件
        return (ConfigurationSection) file.get(key + ".Artifacts");
    }


    //获取组合slot列表   提供组合id字符串
    public static ArrayList<Integer> getCombinationSlotList(String combination){
        if(combination == null){
            combination = getDefaultCombinationId();
        }
        //创建一个列表
        ArrayList<Integer> slotList = new ArrayList<>();
        //获取Artifacts的slot
        //在方案里面获取Artifacts列表
        ConfigurationSection artifactsSection = getArtifactsSection(combination);
        //这里开始遍历Artifacts
        for(String artifactsKey : artifactsSection.getKeys(false)){
            //获取对应的格子
            int slot = artifactsSection.getInt(artifactsKey + ".slot");
            //添加进列表
            slotList.add(slot);
        }
        //添加角色卡格子
        slotList.add(getInt(combination + ".Role.slot"));
        //添加武器格子
        slotList.add(getInt(combination + ".Arms.slot"));
        //返还这个列表
        return slotList;
    }

    //获取第一套组合id
    public static String getDefaultCombinationId(){
        String[] list = getCombinationList().toArray(new String[0]);
        return list[0];
    }

    //获取角色卡槽位
    public static Integer getRoleSlot(String combination){
        return getInt(combination + ".Role.slot");
    }

    //获取武器槽位
    public static Integer getArmsSlot(String combination){
        return getInt(combination + ".Arms.slot");
    }


    //获取全部角色卡槽位
    public static List<Integer> gerAllRoleSlotList(){
        List<Integer> list = new ArrayList<>();
        String[] combinationIdList = getCombinationList().toArray(new String[0]);
        for(String combinationId : combinationIdList){
            list.add(getRoleSlot(combinationId));
        }
        return list;
    }
}
