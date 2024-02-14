package org.qiuhua.genshinattributesbackpack.api;

import org.bukkit.entity.Player;
import org.qiuhua.genshinattributesbackpack.fightingstate.FightingState;

public class Api {
    public static Boolean isFightingState(Player player){
        return FightingState.isFightingState(player);
    }
}
