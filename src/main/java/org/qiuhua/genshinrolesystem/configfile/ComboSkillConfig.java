package org.qiuhua.genshinrolesystem.configfile;

import org.bukkit.configuration.file.FileConfiguration;
import org.qiuhua.genshinrolesystem.Main;

import java.io.File;

public class ComboSkillConfig {

    private static FileConfiguration file;

    //重新加载
    public static void reload ()
    {
        file = ToolConfig.load(new File(Main.getMainPlugin().getDataFolder (),"\\ComboSkill.yml"));
    }

    public static String getString(String value){
        return file.getString(value);
    }



}
