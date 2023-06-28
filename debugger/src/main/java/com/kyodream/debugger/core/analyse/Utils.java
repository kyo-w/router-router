package com.kyodream.debugger.core.analyse;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;

public class Utils {
    /**
     * Utils.getFieldObject已经不再为handler或者plugin服务，只为analyse包下的类服务
     * @param objectReference
     * @param fieldString
     * @return
     */
    public static ObjectReference getFieldObject(ObjectReference objectReference, String fieldString) {
        Field field = null;
        try {
            field = objectReference.referenceType().fieldByName(fieldString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ObjectReference) objectReference.getValue(field);
    }
}
