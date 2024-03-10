package org.qiuhua.genshinrolesystem.armsfollow;

import ink.ptms.adyeshach.core.Adyeshach;
import ink.ptms.adyeshach.core.AdyeshachAPI;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.manager.ManagerType;

public class AdyController {


    //获取api管理器
    public static AdyeshachAPI adyApi(){
        return Adyeshach.INSTANCE.api();
    }

    //获取私有管理器
    public static Manager getEntityManager(){
        return adyApi().getPublicEntityManager(ManagerType.TEMPORARY);
    }


}
