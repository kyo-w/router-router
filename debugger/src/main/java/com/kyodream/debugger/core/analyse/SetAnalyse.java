package com.kyodream.debugger.core.analyse;

import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.HashSet;

public class SetAnalyse {
    private static HashSet<ObjectReference> internalHashMap(ObjectReference mapObject) {
        HashSet<ObjectReference> result = new HashSet<>();
        ArrayReference nodeList = (ArrayReference) Utils.getFieldObject(mapObject, "table");
        if(nodeList == null){
            return result;
        }
        for (Value elemKv : nodeList.getValues()) {
            if (elemKv == null) {
                continue;
            }
            ObjectReference elemKvObject = (ObjectReference) elemKv;
            ObjectReference keyObject = Utils.getFieldObject(elemKvObject, "key");
            result.add(keyObject);
        }
        return result;
    }

    /**
     * 支持HashSet/LinkHashSet
     * @param setObject
     * @return
     */
    public static ArrayList<ObjectReference> getHashSetObject(ObjectReference setObject) {
        ObjectReference mapObject = Utils.getFieldObject(setObject, "map");
        HashSet<ObjectReference> objectReferences = internalHashMap(mapObject);
        return new ArrayList<>(objectReferences);
    }

    public static ArrayList<ObjectReference> getUnmodifiableSet(ObjectReference unmodifiableSet){
        ObjectReference c = Utils.getFieldObject(unmodifiableSet, "c");
        return getHashSetObject(c);
    }

    public static ArrayList<ObjectReference> getTreeSet(ObjectReference treeSetObject){
        ArrayList<ObjectReference> result = new ArrayList<>();
        ObjectReference m_Object = Utils.getFieldObject(treeSetObject, "m");
        ObjectReference firstPoint = Utils.getFieldObject(m_Object, "root");
        PreOrder(firstPoint, result);
        return result;
    }

    //前序遍历
    private static void PreOrder(ObjectReference root, ArrayList<ObjectReference> result){
        ObjectReference value = Utils.getFieldObject(root, "key");
        result.add(value);
        ObjectReference leftObject = Utils.getFieldObject(root, "left");
        if(leftObject != null){
            PreOrder(leftObject, result);
        }
        ObjectReference rightObject = Utils.getFieldObject(root, "right");
        if(rightObject != null){
            PreOrder(rightObject, result);
        }
    }
}
