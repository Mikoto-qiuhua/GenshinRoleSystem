package org.qiuhua.genshinattributesbackpack.fightingstate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.qiuhua.genshinattributesbackpack.Main;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;
import org.qiuhua.genshinattributesbackpack.configfile.MessageConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FightingState {
    private static final Map<UUID, Integer> fightingStateList = new ConcurrentHashMap<>();

    private static BukkitTask task;


    //获取玩家战斗状态
    public static boolean isFightingState(Player player) {
        UUID uuid = player.getUniqueId();
        //检测map内有没有
        return fightingStateList.containsKey(uuid);
    }

    //添加战斗状态
    public static void addFightingState(Player player){
        Integer time = DefaultConfig.getInteger("FightingState");
        UUID uuid = player.getUniqueId();
        if(!fightingStateList.containsKey(uuid)){
            player.sendMessage(MessageConfig.getString("fightingState"));
        }
        fightingStateList.put(uuid, time);
        startTask();
    }

    //减少一次时间 如果时间不能减少了 就移除
    public static void delTimeFightingState(UUID uuid){
        if(fightingStateList.containsKey(uuid)){
            Integer time = fightingStateList.get(uuid);
            if(time <= 0){
                fightingStateList.remove(uuid);
                return;
            }
            time = time - 1;
            fightingStateList.put(uuid, time);
        }
    }

    //开始线程
    public static void startTask() {
        //如果是task是空的
        if(task == null){
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), FightingState::runTaskTimerAsyncFightingState, 0L, 20L);
        }else if(task.isCancelled()){
            //如果没有在运行就启动
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), FightingState::runTaskTimerAsyncFightingState, 0L, 20L);
        }
    }

    //如果线程存在 就停止运行
    public static void stopTask() {
        if (!task.isCancelled()) {
            task.cancel();
        }
    }


    //检索战斗状态的线程
    public static void runTaskTimerAsyncFightingState(){
        //先判断map是不是空的
        if(fightingStateList.isEmpty()){
            //是空的就暂停线程
            stopTask();
            return;
        }
        for(UUID key : fightingStateList.keySet()){
            delTimeFightingState(key);
        }
    }

}
