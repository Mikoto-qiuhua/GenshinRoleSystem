package org.qiuhua.genshinrolesystem.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qiuhua.genshinrolesystem.configfile.ToolConfig;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;
import org.qiuhua.genshinrolesystem.mousecombo.Combo;

public class PapiRegister extends PlaceholderExpansion {
    /**
     * 除非你的扩展有其他服务器上的依赖项 否则应该一直返回true
     *
     * @return true为没有依赖.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * 扩展的作者名称.
     *
     * @return 作者名字.
     */
    @NotNull
    @Override
    public String getAuthor(){
        return "qiuhua";
    }


    /**
     * 占位符的名称.
     * 不能重复,不能保函%和_.
     * 他是一个占位符的开始.
     * %<名称>_<value>%
     *
     * @return 返回名称.
     */
    @NotNull
    @Override
    public String getIdentifier(){
        return "GenShinPapi";
    }

    /**
     * 你的扩展版本.
     *
     * @return 返回版本字符串.
     */
    @NotNull
    @Override
    public String getVersion(){
        return "1.0.0";
    }

    /**
     * 这是我们的标识符占位符时调用的方法.
     * 找到并需要一个值.
     * 我们在此方法中指定值标识符.
     * 从 2.9.1 版本开始，您可以在请求中使用 OfflinePlayers.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  params
     *         包含标识符/值的字符串.
     *
     * @return 请求标识符的可能为空的字符串.

     */
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        //获取角色id
        if(params.equalsIgnoreCase("rolePapi")) {
            return ToolConfig.getRolePapi((Player) player);
        }
        //获取角色id
        if(params.equalsIgnoreCase("roleId")) {
            return PlayerDataController.getPlayerData((Player) player).getRoleId();
        }
        //获取连击
        if(params.equalsIgnoreCase("combo")) {
            return Combo.getPapi((Player) player);
        }
        return null; //未知占位符
    }





}
