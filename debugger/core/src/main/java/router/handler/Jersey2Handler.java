package router.handler;

import router.analysts.IAnalysts;
import router.analysts.MapAnalysts;
import router.analysts.SetAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.HandlerType;

public class Jersey2Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping jersey2, Context context) throws Exception {
        SetAnalysts cachedClassesRef = new SetAnalysts(objRef.getObjFields("webComponent", "appHandler", "application", "cachedClasses"));
        context.getPublish().Event(EventType.Jersey2FrameworkCount, new EventPackage(cachedClassesRef.getId(), cachedClassesRef.size()));
        for (IAnalysts classObjectRef : cachedClassesRef) {
            String className = classObjectRef.getStrFields("name");
            MapAnalysts mapInstancesRef = new MapAnalysts(classObjectRef.getObjFields("annotationData", "annotations"));
            for (MapAnalysts.Entry mapInstanceRef : mapInstancesRef.getKV()) {
                String name = mapInstanceRef.getKey().getStrFields("name");
                if (name.equals("javax.ws.rs.Path")) {
                    MapAnalysts.Entry[] memberValuesRef = new MapAnalysts(mapInstanceRef.getValue().getObjFields("h", "memberValues")).getKV();
                    for (MapAnalysts.Entry memberValueRef : memberValuesRef) {
                        if (memberValueRef.getKey().convertToString().equals("value")) {
                            String path = memberValueRef.getValue().convertToString();
                            jersey2.recordServletMap(className, urlParse.getPathByMiddleware(path));
                        }
                    }
                }
            }
            context.getPublish().Event(EventType.Jersey2FrameworkAnalystsComplete, new EventPackage(cachedClassesRef.getId(), className));
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Jersey2;
    }
}
