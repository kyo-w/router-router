package com.kyodream.end.analyse;

import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListAnalyse {
    /**
     * debug mode :get list from object
     *
     * @param object target object
     * @param thread current thread
     * @return
     */
    public static List<ObjectReference> getArrayList(ThreadReference thread, VirtualMachine attach, ObjectReference object, boolean CopyOnWriteArrayList) {
        ArrayList<ObjectReference> resultList = new ArrayList<>();

        Method sizeMethod = object.referenceType().methodsByName("size").get(0);

//        List size
        int size = 0;
        try {
            size = ((IntegerValue) object.invokeMethod(thread, sizeMethod, Collections.EMPTY_LIST, 0)).value();
        } catch (InvalidTypeException e) {
            e.printStackTrace();
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        } catch (InvocationException e) {
            e.printStackTrace();
        }

        List<Method> get = object.referenceType().methodsByName("get");
        Method getMethod = null;
        if(CopyOnWriteArrayList){
            getMethod = get.get(1);
        }else{
            getMethod = get.get(0);
        }
        for (int i = 0; i < size; i++) {
            ArrayList<Value> arrayList = new ArrayList<>();

            arrayList.add(attach.mirrorOf(i));
            ObjectReference objectOfList = null;
            try {
                objectOfList = (ObjectReference) object.invokeMethod(thread, getMethod, arrayList, 0);
            } catch (InvalidTypeException e) {
                e.printStackTrace();
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            } catch (InvocationException e) {
                e.printStackTrace();
            }
            resultList.add(objectOfList);
        }
        return resultList;
    }


}
