package org.qiuhua.genshinrolesystem.event;


import com.daxton.unrealcore.common.event.PlayerKeyBoardEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.qiuhua.genshinrolesystem.Main;
import org.qiuhua.genshinrolesystem.unrealcore.KeyBoard;

public class UnrealListener implements Listener {

    @EventHandler
    public void onPlayerKeyBoard(PlayerKeyBoardEvent event) {
        //获取是否是即时输入模式 也就是在输入框中输入 是的话就结束
        if(event.isInputNow()){
            return;
        }
        //按键放技能 全程异步
        Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                KeyBoard.unleashSkills(event);;
            }
        });
    }


}
