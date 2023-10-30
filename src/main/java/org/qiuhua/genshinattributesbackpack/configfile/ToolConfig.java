package org.qiuhua.genshinattributesbackpack.configfile;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.data.PlayerData;
import org.qiuhua.genshinattributesbackpack.data.PlayerDataController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolConfig {


    //创建配置文件
    public static void saveAllConfig(){
        //创建一个插件文件夹路径为基础的 并追加下一层。所以此时的文件应该是Config.yml
        //exists 代表是否存在
        if (!(new File (Main.getMainPlugin().getDataFolder() ,"\\Config.yml").exists()))
            Main.getMainPlugin().saveResource("Config.yml", false);

        if (!(new File (Main.getMainPlugin().getDataFolder() ,"\\Message.yml").exists()))
            Main.getMainPlugin().saveResource("Message.yml", false);

        if (!(new File (Main.getMainPlugin().getDataFolder() ,"\\RoleCombination.yml").exists()))
            Main.getMainPlugin().saveResource("RoleCombination.yml", false);

        if (!(new File (Main.getMainPlugin().getDataFolder() ,"Suits/默认示例.yml").exists()))
            Main.getMainPlugin().saveResource("Suits/默认示例.yml", false);

        if (!(new File (Main.getMainPlugin().getDataFolder() ,"\\ComboSkill.yml").exists()))
            Main.getMainPlugin().saveResource("ComboSkill.yml", false);
    }

    //重新加载配置文件

    public static void loadAllConfig(){
        DefaultConfig.reload();
        MessageConfig.reload();
        RoleCombinationConfig.reload();
        SuitsFile.load();
        ComboSkillConfig.reload();
        Bukkit.getLogger().info("[GenshinAttributesBackpack] 文件重载完成");
    }


    public static YamlConfiguration load (File file)
    {
        return YamlConfiguration.loadConfiguration(file);
    }

    // 使用 PAPI 替换占位符
    public static String getPapiString(Player player, String string){
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static List<String> getPapiList(Player player, List<String> list){
        List<String>  newList = new ArrayList<>();
        list.forEach((e) -> {
            newList.add(PlaceholderAPI.setPlaceholders(player, e));
        });
        return newList;
    }


    //去除颜色代码
    public static String removeColorCode(String str){
        Pattern pattern = Pattern.compile("§[0-9a-fklmnor]");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }


    //获取玩家当前角色的papi
    public static String getRolePapi(Player player){
        PlayerData data = PlayerDataController.getPlayerData(player);
        String roleId = data.getRoleId();

        String rolePapi = ToolConfig.getPapiString(player, DefaultConfig.getString("Role.Papi." + roleId));
        if(rolePapi.equals("")){
            rolePapi = "请配置改角色";
        }
        return rolePapi;
    }



    //判断玩家当前武器符不符合
        public static Boolean isRoleArmsType(Player player){
        PlayerData data = PlayerDataController.getPlayerData(player);
        //拿到当前武器类型
        String armsType = data.getArmsType();
        if(armsType.equals("无武器")) return false;
        String roleId = data.getRoleId();
        if(roleId.equals("无角色")) return false;
        List<String> list = DefaultConfig.getList("Arms." + armsType);
        if(list.isEmpty()) return false;
        for(String a : list){
            if(a.equals(roleId)){
                return true;
            }
        }
        return false;
    }

    //判断玩家当前武器符不符合
    public static Boolean isRoleArmsType(String roleId, String armsType){
        if(armsType.equals("无武器")) return false;
        if(roleId.equals("无角色")) return false;
        List<String> list = DefaultConfig.getList("Arms." + armsType);
        if(list.isEmpty()) return false;
        for(String a : list){
            if(a.equals(roleId)){
                return true;
            }
        }
        return false;
    }










    //获取当前武器类型
    public static String getArmsType(ItemStack item){
        if(item == null || item.getType() == Material.AIR){
            return "无武器";
        }
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Arms.condition") + "(.*)");
        if (loreList != null) {
            for(String lore : loreList){
                lore = ToolConfig.removeColorCode(lore);
                Matcher matcher = pattern.matcher(lore);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
        }
        return "无武器";
    }




}
