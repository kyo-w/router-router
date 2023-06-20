package com.kyodream.debugger.core.analyse;

import com.sun.jdi.*;

public class Utils {
    /**
     * get field object of Target Object
     *
     * @param objectReference target Object
     * @param fieldString     field name
     * @return field object
     */
    public static ObjectReference getFieldObject(ObjectReference objectReference, String fieldString) {
        Field field = objectReference.referenceType().fieldByName(fieldString);
        return (ObjectReference) objectReference.getValue(field);
    }
}
