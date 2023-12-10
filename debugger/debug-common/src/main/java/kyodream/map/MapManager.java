package kyodream.map;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import kyodream.breakpoint.BreakPointHandler;
import kyodream.breakpoint.MethodPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MapManager {
    public static HashMap<AnalystsType, List<MethodPoint>> methodPointsMap = new HashMap<>();
    public static HashSet<MethodPoint> methodPointsCache = new HashSet<>();

    public static MethodPoint createMethodPoint(AnalystsType debugType, String classname, String methodName, BreakPointHandler breakPointHandler) {
        MethodPoint methodPoint = new MethodPoint(classname, methodName, breakPointHandler);
        if (methodPointsCache.contains(methodPoint)) {
            return null;
        }
        methodPointsCache.add(methodPoint);


        List<MethodPoint> methodPoints = methodPointsMap.get(debugType);
        if (methodPoints == null) {
            ArrayList<MethodPoint> methodPointArrayList = new ArrayList<>();
            methodPointsMap.put(debugType, methodPointArrayList);
            methodPoints = methodPointArrayList;
        }
        methodPoints.add(methodPoint);
        return methodPoint;
    }
}
