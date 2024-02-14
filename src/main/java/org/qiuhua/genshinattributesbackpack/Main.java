package org.qiuhua.genshinattributesbackpack;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.qiuhua.genshinattributesbackpack.command.GenshinAttributesBackpackCommand;

import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.ToolConfig;
import org.qiuhua.genshinattributesbackpack.event.InventoryListener;
import org.qiuhua.genshinattributesbackpack.event.PlayerListener;
import org.qiuhua.genshinattributesbackpack.key.Login;
import org.qiuhua.genshinattributesbackpack.papi.PapiRegister;
import org.qiuhua.genshinattributesbackpack.sql.mysql.MysqlControl;
import org.qiuhua.genshinattributesbackpack.sql.mysql.MysqlDataControl;
import org.qiuhua.genshinattributesbackpack.sql.sqlite.SqliteControl;
import org.qiuhua.genshinattributesbackpack.sql.sqlite.SqliteDataControl;


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
        new GenshinAttributesBackpackCommand().register();
        //生成配置
        ToolConfig.saveAllConfig();
        //加载配置
        ToolConfig.loadAllConfig();
        Login.main();
        //注册箱子事件监听
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
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