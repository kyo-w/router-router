package com.kyodream.debugger.core.category;

import java.util.HashMap;


/**
 * 只负责最后数据的管理
 */
public interface DataWrapper {

    HashMap<String, String> getDataWrapper();

    void clearData();

}
