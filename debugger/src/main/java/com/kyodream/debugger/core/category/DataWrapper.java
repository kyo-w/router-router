package com.kyodream.debugger.core.category;

import java.util.HashMap;

public interface DataWrapper {
    //    每一个中间件或者框架都必须提供一个结果集接口用于管理类获取数据
    //    Each middleware or framework must provide a result set interface for the management class to obtain data
    HashMap<String, String> getDataWrapper();

    String getVersion();


//    每一个实现需要提供用于管理类能感知某个Handle是否存在
    boolean isFind();


    void clearFindFlag();

    void clearData();

}
