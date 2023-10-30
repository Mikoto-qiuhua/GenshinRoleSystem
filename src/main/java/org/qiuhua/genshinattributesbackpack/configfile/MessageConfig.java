package org.qiuhua.genshinattributesbackpack.configfile;

import org.bukkit.configuration.file.FileConfiguration;
import org.qiuhua.genshinattributesbackpack.Main;


import java.io.File;

public class MessageConfig {


    private static FileConfiguration file;

    //重新加载
    public static void reload ()
    {
        file = ToolConfig.load(new File(Main.getMainPlugin().getDataFolder (),"\\Message.yml"));
    }


    public static String getString(String value){
        return file.getString(value);
    }


}
