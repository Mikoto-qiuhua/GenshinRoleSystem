package org.qiuhua.genshinattributesbackpack.configfile;

import org.bukkit.configuration.file.FileConfiguration;
import org.qiuhua.genshinattributesbackpack.Main;


import java.io.File;
import java.util.List;

public class DefaultConfig {
    private static FileConfiguration file;

    //重新加载
    public static void reload ()
    {
        file = ToolConfig.load(new File(Main.getMainPlugin().getDataFolder (),"\\Config.yml"));
    }

    public static Boolean getBoolean( String value){
        return file.getBoolean(value);
    }

    public static String getString(String value){
        return file.getString(value);
    }




    public static Integer getInteger(String value){
        return file.getInt(value);
    }

    public static List<String> getList(String value){
        return file.getStringList(value);
    }

    public static Double getDouble(String value){
        return file.getDouble(value);
    }


}
