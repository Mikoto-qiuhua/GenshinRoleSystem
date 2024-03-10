package org.qiuhua.genshinrolesystem.configfile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.qiuhua.genshinrolesystem.Main;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class SuitsFile {
    private static final HashMap<String, ConfigurationSection> suitsAll = new HashMap<>();  //全部方案的容器
    private static final File folder = new File(Main.getMainPlugin().getDataFolder(), "Suits");

    //加载方案文件夹
    public static void load(){
        // 如果指定文件夹不存在，或者不是一个文件夹，则退出
        if (!folder.exists() || !folder.isDirectory()) {
            Main.getMainPlugin().getLogger().warning("未读取到Scheme文件夹");
            return;
        }
        //清空map
        suitsAll.clear();
        // 遍历指定文件夹内的所有文件
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            // 如果不是一个YAML文件，则跳过
            if (!file.getName().endsWith(".yml")) {
                continue;
            }
            readYml(file.getName());
        }
    }

    //读取yml
    public static void readYml(String fileName){
        //加载配置
        FileConfiguration section = ToolConfig.load(new File(Main.getMainPlugin().getDataFolder (),"Suits/" + fileName));
        //获取全部key
        Set<String> scheme = section.getKeys(false);
        //如果不是空的
        if(!scheme.isEmpty()){

            Main.getMainPlugin().getLogger().info("加载配置文件 => " + fileName);
            //转成独立节点
            for(String node : scheme){
                ConfigurationSection a = (ConfigurationSection) section.get(node);
                if(suitsAll.containsKey(node)){
                    Main.getMainPlugin().getLogger().warning("出现重复方案 => " + node);
                    Main.getMainPlugin().getLogger().warning("此方案不会加载,请注意检查");
                }else{
                    suitsAll.put(node, a);
                    Main.getMainPlugin().getLogger().info("读取方案 => " + node);
                }
            }
        }

    }

    //获取方案容器
    public static HashMap<String, ConfigurationSection> getSuitsAll(){
        return suitsAll;
    }

    //获取单个方案
    public static ConfigurationSection getSuits(String name){
        return suitsAll.get(name);
    }




}
