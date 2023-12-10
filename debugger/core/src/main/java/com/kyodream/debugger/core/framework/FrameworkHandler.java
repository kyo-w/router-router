package com.kyodream.debugger.core.framework;

import kyodream.analysts.ObjectAnalysts;
import kyodream.map.AnalystsType;
import kyodream.record.ContextUrlRecord;
import kyodream.record.FrameworkRecord;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface FrameworkHandler {
    HashMap<AnalystsType, FrameworkHandler> handlerMap = new HashMap<>() {{
        put(AnalystsType.SPRING, new SpringMvcHandler());
        put(AnalystsType.JERSEY_1, new Jersey1Handler());
        put(AnalystsType.JERSEY_2, new Jersey2Handler());
        put(AnalystsType.STRUTS_1, new Struts1Handler());
        put(AnalystsType.STRUTS_2, new Struts2Handler());
    }};
    HashMap<Class, AnalystsType> aliasMap = new HashMap<>() {{
        put(SpringMvcHandler.class, AnalystsType.SPRING);
        put(Jersey1Handler.class, AnalystsType.JERSEY_1);
        put(Jersey2Handler.class, AnalystsType.JERSEY_2);
        put(Struts1Handler.class, AnalystsType.STRUTS_1);
        put(Struts2Handler.class, AnalystsType.STRUTS_2);
    }};
    HashMap<ObjectAnalysts, CacheFrameworkRecord> analystsCache = new HashMap<>();

    default void handler(ContextUrlRecord contextUrlRecord, ObjectAnalysts objRef) {
        AnalystsType analystsType = aliasMap.get(this.getClass());
        CacheFrameworkRecord cacheFrameworkRecord = analystsCache.get(objRef);
        if (cacheFrameworkRecord == null) {
            cacheFrameworkRecord = new CacheFrameworkRecord();
            analystsTargetObject(objRef, cacheFrameworkRecord);
            analystsCache.put(objRef, cacheFrameworkRecord);
            objRef.debugSuccess(objRef.className() + "完成分析");

        } else {
            objRef.debugSuccess(objRef.className() + "重复出现，直接组装");
        }
        FrameworkRecord frameworkRecord = createFrameworkRecord(analystsType, contextUrlRecord, cacheFrameworkRecord);

        String packageVersion = objRef.getPackageVersion(objRef.className());
        frameworkRecord.setVersion(packageVersion);
        objRef.frameworkMsg(frameworkRecord);
    }

    private FrameworkRecord createFrameworkRecord(AnalystsType analystsType, ContextUrlRecord contextUrlRecord, CacheFrameworkRecord cacheFrameworkRecord) {
        FrameworkRecord frameworkRecord = new FrameworkRecord();
        frameworkRecord.setType(analystsType);
        frameworkRecord.setContextPath(contextUrlRecord.getVirtualPath());
        HashMap<String, String> urlMap = cacheFrameworkRecord.getUrlMap();
        HashMap<String, String> filterMap = cacheFrameworkRecord.getFilterMap();
        if (urlMap != null) {
            HashMap<String, String> urlMapResult = new HashMap<>();
            urlMap.forEach((subPath, className) -> {
                String path = contextUrlRecord.concatContextPathAndSubPath(subPath);
                urlMapResult.put(path, className);
            });
            frameworkRecord.setUrlMap(urlMapResult);
        }
        if (filterMap != null) {
            LinkedHashMap<String, String> filterMapResult = new LinkedHashMap<>();
            filterMap.forEach((subPath, className) -> {
                String path = contextUrlRecord.concatContextPathAndSubPath(subPath);
                filterMapResult.put(path, className);
            });
            frameworkRecord.setFilterMap(filterMapResult);
        }
        return frameworkRecord;
    }

    void analystsTargetObject(ObjectAnalysts objRef, CacheFrameworkRecord frameworkRecord);


    public class CacheFrameworkRecord {
        private HashMap<String, String> urlMap;
        private LinkedHashMap<String, String> filterMap;

        public HashMap<String, String> getUrlMap() {
            return urlMap;
        }

        public void setUrlMap(HashMap<String, String> urlMap) {
            this.urlMap = urlMap;
        }

        public HashMap<String, String> getFilterMap() {
            return filterMap;
        }

        public void setFilterMap(LinkedHashMap<String, String> filterMap) {
            this.filterMap = filterMap;
        }
    }
}
