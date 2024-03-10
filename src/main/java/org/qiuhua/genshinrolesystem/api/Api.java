package org.qiuhua.genshinrolesystem.api;

import org.bukkit.entity.Player;
import org.qiuhua.genshinrolesystem.fightingstate.FightingState;

public class Api {
    public static Boolean isFightingState(Player player){
        return FightingState.isFightingState(player);
    }
}
