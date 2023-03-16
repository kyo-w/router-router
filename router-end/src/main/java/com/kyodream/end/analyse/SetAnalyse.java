package com.kyodream.end.analyse;

import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.Collections;

public class SetAnalyse {

    /**
     *
     * @param thread
     * @param setObject
     * @return
     * @throws ClassNotLoadedException
     * @throws IncompatibleThreadStateException
     * @throws InvocationException
     * @throws InvalidTypeException
     */
    public static ArrayList<ObjectReference> getSetObject(ThreadReference thread, ObjectReference setObject) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        ArrayList<ObjectReference> resultList = new ArrayList<>();
        Method iterator = setObject.referenceType().methodsByName("iterator").get(0);
        ObjectReference iterObject = (ObjectReference) setObject.invokeMethod(thread, iterator, Collections.EMPTY_LIST, 0);
        Method hasNextMethod = iterObject.referenceType().methodsByName("hasNext").get(0);
        Method nextMethod = iterObject.referenceType().methodsByName("next").get(0);

        while(((BooleanValue)iterObject.invokeMethod(thread, hasNextMethod, Collections.EMPTY_LIST, 0)).value()){
            ObjectReference value = (ObjectReference) iterObject.invokeMethod(thread, nextMethod, Collections.EMPTY_LIST, 0);
            resultList.add(value);
        }
        return resultList;
    }
}
