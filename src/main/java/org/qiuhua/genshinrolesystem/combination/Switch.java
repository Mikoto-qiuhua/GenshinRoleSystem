package org.qiuhua.genshinrolesystem.combination;

import com.skillw.attsystem.api.AttrAPI;
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.genshinrolesystem.Main;
import org.qiuhua.genshinrolesystem.armsfollow.ArmorStandController;
import org.qiuhua.genshinrolesystem.armsfollow.FollowControl;
import org.qiuhua.genshinrolesystem.armsfollow.LockingArms;
import org.qiuhua.genshinrolesystem.attribute.AttributeControl;
import org.qiuhua.genshinrolesystem.configfile.DefaultConfig;
import org.qiuhua.genshinrolesystem.configfile.RoleCombinationConfig;
import org.qiuhua.genshinrolesystem.configfile.ToolConfig;
import org.qiuhua.genshinrolesystem.data.PlayerData;
import org.qiuhua.genshinrolesystem.data.PlayerDataController;
import org.qiuhua.genshinrolesystem.fightingstate.FightingState;
import org.qiuhua.genshinrolesystem.suits.ArtifactsTag;
import org.qiuhua.genshinrolesystem.suits.SuitsAction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Switch {

    //切换组合
    public static void SwitchCombination(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        //下面的过程异步运行以免卡顿
        //切换后
        int newSlot = event.getNewSlot();
        //切换前
        int previousSlot = event.getPreviousSlot();
        if(!player.isSneaking()){
            if(LockingArms.isArmsSlot(newSlot)){
                ArmorStandController.deSpawnArmorStand(player);
                return;
            }
            if(LockingArms.isArmsSlot(previousSlot)){
                FollowControl.addItemFollow(player);
                return;
            }
            return;
        }
        if(FightingState.isFightingState(player)){
            event.setCancelled(true);
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                PlayerData data = PlayerDataController.getPlayerData(player);
                //这里是判断时间
                long startTime = System.currentTimeMillis();  //获取时间
                if(data.getTimeStatistics() == null){
                    data.setTimeStatistics(startTime);
                }else{
                    Integer switchTimeCd = DefaultConfig.getInteger("SwitchTimeCd");
                    long interval = startTime - data.getTimeStatistics();
                    if(interval < switchTimeCd){
                        return;
                    }
                    data.setTimeStatistics(startTime);
                }
                //获取组合id
                String combinationId = data.getCombinationId();
                if(previousSlot == 8 && newSlot == 0){
                    combinationId = SwitchNextCombinationId(player);
                }else if(previousSlot == 0 && newSlot == 8){
                    combinationId = SwitchBackCombinationId(player);
                }else if(newSlot > previousSlot) {
                    combinationId = SwitchNextCombinationId(player);
                }else if(newSlot < previousSlot) {
                    combinationId = SwitchBackCombinationId(player);
                }
                //查看下个组合玩家有没有放置角色
                //拿到角色卡的槽位
                Integer roleSlot = RoleCombinationConfig.getRoleSlot(combinationId);
                //如果有物品 才执行
                if(data.getEquipmentMap().containsKey(roleSlot)){
                    CombinationAttribute(combinationId, player);
//                    //判断是否需要取消武器显示
//                    if(LockingArms.cancelArmsFollow(event)){
//                        ArmorStandController.deSpawnArmorStand(player);
//                    }else {
//                        FollowControl.addItemFollow(player);
//                    }
                    if(DefaultConfig.getBoolean("Debug")){
                        Bukkit.getLogger().info("======[GenshinRoleSystem]======");
                        Bukkit.getLogger().info("本次动作 => 切换组合");
                        Bukkit.getLogger().info("切换角色 => " + data.getRoleId());
                        Bukkit.getLogger().info(newSlot + " => " + previousSlot);
                        Bukkit.getLogger().info("切换组合 => " + combinationId);
                        Bukkit.getLogger().info("========================================");
                    }
                    if(LockingArms.isArmsSlot(newSlot)){
                        ArmorStandController.deSpawnArmorStand(player);
                    }else {
                        FollowControl.addItemFollow(player);
                    }
                    return;
                }
                if(LockingArms.isArmsSlot(newSlot)){
                    ArmorStandController.deSpawnArmorStand(player);
                    return;
                }
                if(LockingArms.isArmsSlot(previousSlot)){
                    FollowControl.addItemFollow(player);
                    return;
                }
            }
        });

    }



    //拿到下一个组合
    public static String SwitchNextCombinationId(Player player){
        //拿到当前组合
        String combinationId = PlayerDataController.getPlayerData(player).getCombinationId();
        //拿到全部组合id
        Set<String> allCombinationKey = RoleCombinationConfig.getCombinationList();
        //转成列表
        String[] list = allCombinationKey.toArray(new String[0]);
        if(combinationId == null){
            return list[0];
        }
        for(int i = 0 ; i < list.length ; i++){
            //如果这次遍历对得上
            if(list[i].equals(combinationId)){
                //看看是否是遍历到最后一个 如果是最后一个组合 那再往下切换应该切换到第一个组合
                if(i == list.length - 1){
                    //切换到第一个
                    return list[0];
                }
                //拿到下一个组合id
                return list[i + 1];
            }
        }
        return list[0];
    }

    //拿到上一个组合
    public static String SwitchBackCombinationId(Player player){
        //拿到当前组合
        String combinationId = PlayerDataController.getPlayerData(player).getCombinationId();
        //拿到全部组合id
        Set<String> allCombinationKey = RoleCombinationConfig.getCombinationList();
        //转成列表
        String[] list = allCombinationKey.toArray(new String[0]);
        if(combinationId == null){
            return list[0];
        }
        for(int i = 0 ; i < list.length ; i++){
            //如果这次遍历对得上
            if(list[i].equals(combinationId)){
                //看看是否是遍历到第一个 如果是第一组合 那再往上切换应该切换到最后个组合
                if(i == 0){
                    //切换到最后一个
                    return list[list.length - 1];
                }
                //拿到上一个组合id
                return list[i - 1];
            }
        }
        return list[0];
    }



    //切换属性的主要代码
    public static void CombinationAttribute(String combinationId, Player player){
        //清空属性
        if(DefaultConfig.getBoolean("Debug")){
            Bukkit.getLogger().info("========================================");
            Bukkit.getLogger().info("本次动作 => 清空属性");
            Bukkit.getLogger().info("========================================");
        }
        AttrAPI.removeCompiledData(player, "GenshinAttributes_Suits");
        AttrAPI.removeCompiledData(player, "GenshinAttributes");
        //记录角色血量
        setRoleHealth(player);
        //执行执行套装提供的动作
        SuitsAction.main(player, "end");
        //切换前做的事情↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
        //切换后的事情↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        //修改组合标签
        PlayerDataController.getPlayerData(player).setCombinationId(combinationId);
        //修改角色 角色信息是自动通过标签拿到对应的装备才修改的  并不能直接传递一个角色信息进去
        PlayerDataController.getPlayerData(player).setRoleId(getItemRole(player));
        //修改武器类型   自动通过标签拿到对应的装备才修改的  并不能直接传递一个武器信息进去
        PlayerDataController.getPlayerData(player).setArmsType(getArmsType(player));
        //添加属性
        AttributeControl.addAttribute(combinationId, player);
        //执行切换后统计套装tag操作
        ArtifactsTag.addTag(combinationId, player);
        //执行套装提供的动作
        SuitsAction.main(player, "star");
        //更新属性
        AttrAPI.updateSync(player);
        try{
            //恢复生命值
            getRoleHealth(player);
        }catch (IllegalArgumentException e){
            player.setHealth(20);
            Bukkit.getLogger().warning(player.getDisplayName() +  " 切换角色出现异常 已恢复为20血");
        }
        //给予武器
        LockingArms.addArmsSlot(player);
    }

    //添加属性的代码 和切换的区别就是 不会触发切换前动作
    public static void addAttribute(String combinationId, Player player){
        //清空属性
        AttrAPI.removeCompiledData(player, "GenshinAttributes_Suits");
        AttrAPI.removeCompiledData(player, "GenshinAttributes");
        //修改组合标签
        PlayerDataController.getPlayerData(player).setCombinationId(combinationId);
        //修改角色 角色信息是自动通过标签拿到对应的装备才修改的  并不能直接传递一个角色信息进去
        PlayerDataController.getPlayerData(player).setRoleId(getItemRole(player));
        //修改武器类型   自动通过标签拿到对应的装备才修改的  并不能直接传递一个武器信息进去
        PlayerDataController.getPlayerData(player).setArmsType(getArmsType(player));
        //添加属性
        AttributeControl.addAttribute(combinationId, player);
        //执行切换后统计套装tag操作
        ArtifactsTag.addTag(combinationId, player);
        //执行套装提供的动作
        SuitsAction.main(player, "star");
        //更新属性
        AttrAPI.updateSync(player);
        try{
            //恢复生命值
            getRoleHealth(player);
        }catch (IllegalArgumentException e){
            player.setHealth(20);
            Bukkit.getLogger().warning(player.getDisplayName() +  " 切换角色出现异常 已恢复为20血");
        }
        //给予武器
        LockingArms.addArmsSlot(player);
    }



    //获取物品上的角色信息
    public static String getItemRole(Player player){
        String role = "无角色";
        //获取玩家数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        //获取组合id
        String combinationId = data.getCombinationId();
        //获取role槽位
        Integer roleSlot = RoleCombinationConfig.getRoleSlot(combinationId);
        Map<Integer, ItemStack> map = data.getEquipmentMap();
        if(!map.containsKey(roleSlot)){
            return role;
        }
        ItemStack item = map.get(roleSlot);
        if(item == null){
            return role;
        }
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Role.condition") + "(.*)");
        if (loreList != null) {
            for(String lore : loreList){
                lore = ToolConfig.removeColorCode(lore);
                Matcher matcher = pattern.matcher(lore);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
        }
        return role;
    }


    //获取当前武器类型
    public static String getArmsType(Player player){
        PlayerData data = PlayerDataController.getPlayerData(player);
        ItemStack item = data.getEquipmentItem(RoleCombinationConfig.getArmsSlot(data.getCombinationId()));
        if(item == null){
            return "无武器";
        }
        List<String> loreList = Objects.requireNonNull(item.getItemMeta()).getLore();
        Pattern pattern = Pattern.compile(DefaultConfig.getString("Arms.condition") + "(.*)");
        if (loreList != null) {
            for(String lore : loreList){
                lore = ToolConfig.removeColorCode(lore);
                Matcher matcher = pattern.matcher(lore);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
        }
        return null;
    }







    //记录当前健康值
    public static void setRoleHealth(Player player){
        PlayerData data = PlayerDataController.getPlayerData(player);
        String role = data.getRoleId();
        Double health = player.getHealth();
        data.putRoleHealth(role, health);
        if(DefaultConfig.getBoolean("Debug")){
            Bukkit.getLogger().info("======[GenshinRoleSystem]======");
            Bukkit.getLogger().info("本次动作 => 记录健康值");
            Bukkit.getLogger().info("记录角色 => " + role);
            Bukkit.getLogger().info("记录生命值 => " + health);
            Bukkit.getLogger().info("========================================");
        }

    }

    //恢复健康值
    public static void getRoleHealth(Player player){
        PlayerData data = PlayerDataController.getPlayerData(player);
        String role = data.getRoleId();

        AttributeDataCompound attrData = AttrAPI.getAttrData(player.getUniqueId());
        //默认情况下 恢复的血量等于生命值上限
        Double health = null;
        if (attrData != null) {
            health = attrData.getAttrValue("生命上限", "total");
        }
        if(health == null){
            health = 0.0;
        }
        health = health + 20;
        try {
            if(data.getRoleHealthList().containsKey(role)){
                //如果记录的血量小于或者等于最大生命值
                if(data.getRoleHealth(role) <= health){
                    //使用记录的血量
                    health = data.getRoleHealth(role);
                }
                //如果血量小于0 那就回复20血量
                if(health <= 0){
                    health = 20.0;
                }
                player.setHealth(health);
            }else{
                player.setHealth(health);
            }
        }finally {
            if(DefaultConfig.getBoolean("Debug")){
                Bukkit.getLogger().info("======[GenshinRoleSystem]======");
                Bukkit.getLogger().info("本次动作 => 恢复健康值");
                Bukkit.getLogger().info("恢复角色 => " + role);
                Bukkit.getLogger().info("恢复生命值 => " + health);
                Bukkit.getLogger().info(data.getRoleHealthList().toString());
                Bukkit.getLogger().info("========================================");
            }
        }

    }
}
