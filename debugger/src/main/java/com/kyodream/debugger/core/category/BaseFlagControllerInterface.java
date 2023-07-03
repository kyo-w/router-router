package com.kyodream.debugger.core.category;

/**
 * 单独负责处理某个Handler或者框架的标志位
 */
public interface BaseFlagControllerInterface {

    /**
     * 用于获取一个Handler或者框架是否已经找到了分析对象
     * @return
     */
    boolean ifFind();

    /**
     * 用于确定一个Handler或者框架已经找到了分析对象
     */
    void handlerFindAnalystsObject();

    /**
     * 用于清空标志位
     */
    void clearFindFlag();


    /**
     * 用于获取一个Handler或者框架是否已经完成了分析工作
     * @return
     */
    boolean ifCompleteAnalysts();

    /**
     * 用于确定一个Handler或者框架已经完成了分析工作
     * @return
     */
    void completeAnalysts();

    /**
     * 用于清空标志位
     */
    void clearCompleteFlag();
}
