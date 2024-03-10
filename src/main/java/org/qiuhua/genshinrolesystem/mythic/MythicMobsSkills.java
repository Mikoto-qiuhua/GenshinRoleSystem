package org.qiuhua.genshinrolesystem.mythic;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MythicMobsSkills {

    public static void castSkill(Player player, String skills){
        Collection<Entity> entityList = new ArrayList<>(Collections.singletonList(player));
        MythicBukkit.inst().getAPIHelper().castSkill(player, skills, player, player.getLocation(), entityList, null, 1);
    }


    public static Entity getTargetedEntity(Player player) {
        World world = player.getWorld();
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();
        location.add(direction);
        RayTraceResult rayTraceEntities = world.rayTraceEntities(location, location.getDirection(), 64.0D);
        if(rayTraceEntities == null) return player;
        Entity hitEntity = rayTraceEntities.getHitEntity();
        if(hitEntity == null) return player;
        return hitEntity;
    }
}
