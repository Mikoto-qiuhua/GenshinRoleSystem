package org.qiuhua.genshinattributesbackpack.data;

import ink.ptms.adyeshach.core.entity.EntityInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerData {

    //武器信息
    private String ArmsType = "无武器";

    public String getArmsType() {
        return this.ArmsType;
    }
    public void setArmsType(String armsType) {
        this.ArmsType = armsType;
    }



    //角色信息
    private String RoleId = "无角色";
    public String getRoleId() {
        return this.RoleId;
    }
    public void setRoleId(String roleId) {
        this.RoleId = roleId;
    }

    //装备列表
    private final Map<Integer, ItemStack> equipmentList = new HashMap<>();
    public void putEquipmentList (Integer slot, ItemStack item)
    {
        this.equipmentList.put(slot, item);
    }
    public ItemStack getEquipmentItem (Integer slot)
    {
        return this.equipmentList.get(slot);
    }
    public Map<Integer, ItemStack> getEquipmentMap()
    {
        return this.equipmentList;
    }
    public void removeEquipmentMap()
    {
        this.equipmentList.clear();
    }

    //玩家身上的套装标签和数量
    private final Map<String, Integer> equipmentTagAndNumberList = new HashMap<>();
    public void putEquipmentTagAndNumber(String tag, Integer number){
        if(this.equipmentTagAndNumberList.containsKey(tag)){
            Integer i = this.equipmentTagAndNumberList.get(tag);
            this.equipmentTagAndNumberList.put(tag, i + number);
        }else {
            this.equipmentTagAndNumberList.put(tag, number);
        }
    }
    public Integer getNumberByEquipmentTag(String tag){
        return this.equipmentTagAndNumberList.get(tag);
    }
    public void removeEquipmentTagAndNumber(){
        this.equipmentTagAndNumberList.clear();
    }
    public Map<String, Integer> getequipmentTagAndNumberMap(){
        return this.equipmentTagAndNumberList;
    }

    //当前使用的组合名称
    private String combinationId = null;
    public String getCombinationId() {
        return combinationId;
    }
    public void setCombinationId(String combinationId) {
        this.combinationId = combinationId;
    }

    //玩家的各个角色血量
    private final Map<String, Double> roleHealthList = new HashMap<>();
    public void putRoleHealth(String role, Double health){
        this.roleHealthList.put(role, health);
    }
    public Double getRoleHealth(String role){
        return this.roleHealthList.get(role);
    }

    public Map<String, Double> getRoleHealthList(){
        return this.roleHealthList;
    }


    //连击的按键顺序
    private final ArrayList<String> comboList = new ArrayList<>(); //记录按键的列表
    //写入按键
    public void addComboList(String gesture){
        this.comboList.add(gesture);
    }
    //获取全部按键
    public ArrayList<String> getAllComboList(){
        return this.comboList;
    }
    //清空List
    public void clearComboList(){
        this.comboList.clear();
    }
    //是否为空
    public Boolean isEmptyComboList(){ return this.comboList.isEmpty();}


    //连击的间隔
    private Long comboInterval = null;//上一次的记录时间
    //获取间隔
    public Long getComboInterval(){
        return this.comboInterval;
    }
    //设置间隔
    public void setComboInterval(Long comboInterval){
        this.comboInterval = comboInterval;
    }




    //切换组合的间隔
    private Long timeStatistics = null;//上一次的记录时间
    //获取间隔
    public Long getTimeStatistics(){
        return this.timeStatistics;
    }
    //设置间隔
    public void setTimeStatistics(Long timeStatistics){
        this.timeStatistics = timeStatistics;
    }


    //玩家的发包盔甲架
    EntityInstance entityArmorStand = null;
    public void setEntityArmorStand(EntityInstance entity){
        this.entityArmorStand = entity;
    }
    public EntityInstance getEntityArmorStand(){
        return this.entityArmorStand;
    }


}
