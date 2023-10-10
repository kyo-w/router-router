package com.kyodream.debugger.core.category;

import com.sun.jdi.ObjectReference;

import java.util.HashMap;
import java.util.Set;

/**
 * HandlerFrameworkAncestor主要父类各个标志位/数据/分析标志位的管理
 * BaseFlagControllerInterface：目标中间件或框架[存在状态/分析状态]
 */
public class HandlerFrameworkAncestor implements DataWrapper, BaseFlagControllerInterface{

    protected String handleOrFrameworkName;

    private boolean findFlag = false;
    private boolean completeFlag = false;

    protected String version = null;

    private boolean analystsChangeFlag = false;


    private HashMap<String, String> urlClassNameMap = new HashMap<>();
    @Override
    public boolean ifFind() {
        return findFlag;
    }

    @Override
    public void handlerFindAnalystsObject() {
        this.findFlag = true;
    }

    @Override
    public boolean ifCompleteAnalysts() {
        return completeFlag;
    }

    @Override
    public void completeAnalysts() {
        completeFlag = true;
    }

    @Override
    public void clearCompleteFlag() {
        completeFlag = false;
    }

    @Override
    public void clearFindFlag() {
        findFlag = false;
    }

    public HashMap<String, String> getDataWrapper() {
        return urlClassNameMap;
    }

    public void clearData(){
        urlClassNameMap.clear();
    }

    public void setHandleOrFrameworkName(){
    }

    public void setVersion(String version){
        this.version = version;
    }

    public String getVersion(){
        return this.version;
    }

    public boolean ifAnalystsChangeFlag(){
        return this.analystsChangeFlag;
    }

    public void analystsHasChangeFlag(){
        this.analystsChangeFlag = true;
    }

    public void clearAnalystsFlag(){
        this.analystsChangeFlag = false;
    }

    public String getName(){
        return this.handleOrFrameworkName;
    }

    public void clearAllFlag(){
        this.clearCompleteFlag();
        this.clearFindFlag();
        this.clearAnalystsFlag();
    }
}
