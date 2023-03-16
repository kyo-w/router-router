package com.kyodream.end.analyse;

import com.sun.jdi.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapAnalyse {

    public static Map<ObjectReference, ObjectReference> getHashMapObject(ThreadReference thread, ObjectReference object)
{
    HashMap<ObjectReference, ObjectReference> resultHashMap = new HashMap<>();
    try {
//        object.entrySet().iterator()
        Method entrySetMethod = object.referenceType().methodsByName("entrySet").get(0);
        ObjectReference entrySetObject = (ObjectReference) object.invokeMethod(thread, entrySetMethod, Collections.EMPTY_LIST, 0);
        Method iteratorMethod = entrySetObject.referenceType().methodsByName("iterator").get(0);
        ObjectReference iteratorObject = (ObjectReference) entrySetObject.invokeMethod(thread, iteratorMethod, Collections.EMPTY_LIST, 0);

        Method nextMethod = iteratorObject.referenceType().methodsByName("next").get(0);
        Method hasNextMethod = iteratorObject.referenceType().methodsByName("hasNext").get(0);

        while (((BooleanValue) iteratorObject.invokeMethod(thread, hasNextMethod, Collections.EMPTY_LIST, 0)).value()) {
            ObjectReference kv_Object = (ObjectReference) iteratorObject.invokeMethod(thread, nextMethod, Collections.EMPTY_LIST, 0);
            Method getKeyMethod = kv_Object.referenceType().methodsByName("getKey").get(0);
            Method getValueMethod = kv_Object.referenceType().methodsByName("getValue").get(0);
            ObjectReference key = (ObjectReference) kv_Object.invokeMethod(thread, getKeyMethod, Collections.EMPTY_LIST, 0);
            ObjectReference value = (ObjectReference) kv_Object.invokeMethod(thread, getValueMethod, Collections.EMPTY_LIST, 0);
            resultHashMap.put(key, value);
        }
    }catch (Exception e){}
        return resultHashMap;
    }
}
