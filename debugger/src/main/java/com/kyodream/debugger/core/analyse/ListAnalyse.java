package com.kyodream.debugger.core.analyse;

import com.sun.jdi.*;

import java.util.List;
import java.util.stream.Collectors;

public class ListAnalyse {
    public static List<ObjectReference> getArrayList(ObjectReference arrayListObject) {
        Field elementData = arrayListObject.referenceType().fieldByName("elementData");
        ArrayReference elementDataObject = (ArrayReference) arrayListObject.getValue(elementData);
        return elementDataObject.getValues().stream().map(elem -> (ObjectReference) elem).collect(Collectors.toList());
    }
}
