package org.qiuhua.genshinrolesystem;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.qiuhua.genshinrolesystem.command.GenshinRoleSystemCommand;

import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.ToolConfig;
import org.qiuhua.genshinrolesystem.event.InventoryListener;
import org.qiuhua.genshinrolesystem.event.PlayerListener;
import org.qiuhua.genshinrolesystem.event.UnrealListener;
import org.qiuhua.genshinrolesystem.key.Login;
import org.qiuhua.genshinrolesystem.papi.PapiRegister;
import org.qiuhua.genshinrolesystem.sql.mysql.MysqlControl;
import org.qiuhua.genshinrolesystem.sql.mysql.MysqlDataControl;
import org.qiuhua.genshinrolesystem.sql.sqlite.SqliteControl;
import org.qiuhua.genshinrolesystem.sql.sqlite.SqliteDataControl;


public class Main extends JavaPlugin {

    private static Main mainPlugin;

    public static Main getMainPlugin(){
        return  mainPlugin;
    }

    public static String sqlType;

    //启动时运行
    @Override
    public void onEnable(){
        //设置主插件
        mainPlugin = this;
        //注册指令
        new GenshinRoleSystemCommand().register();
        //生成配置
        ToolConfig.saveAllConfig();
        //加载配置
        ToolConfig.loadAllConfig();
        Login.main();
        //注册箱子事件监听
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        //如果启用虚幻核心按键兼容
        if(DefaultConfig.getBoolean("UnrealKey.enable")){
            this.getLogger().info("已启用虚幻核心兼容功能");
            Bukkit.getPluginManager().registerEvents(new UnrealListener(), this);
        }
        //注册papi占位符
        new PapiRegister().register();
        //加载数据库
        sqlType = DefaultConfig.getString("Sql.type");
        if (sqlType.equalsIgnoreCase("mysql")) {
            MysqlControl.enableMySQL();
            MysqlDataControl.autoSave();
        } else {
            SqliteControl.loadSQLiteJDBC();
            SqliteDataControl.StarServerLoadData();
            SqliteDataControl.autoSave();
        }
    }


    //关闭时运行
    @Override
    public void onDisable(){
        if (sqlType.equalsIgnoreCase("mysql")) {
            MysqlDataControl.addAllPlayerData();
            MysqlControl.shutdown();
        } else {
            SqliteDataControl.addAllEquipmentData();
        }
    }


    //执行重载命令时运行
    @Override
    public void reloadConfig(){
        ToolConfig.loadAllConfig();
    }
}