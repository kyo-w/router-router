package router.handler;

import router.analysts.IAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.except.FrameworkExcept;
import router.parse.UrlParse;
import router.type.HandlerType;

public interface FrameworkHandler {
       default void handler(UrlParse urlParse, IAnalysts objRef, Context context) throws Exception {
         FrameworkMapping frameworkMapping = new FrameworkMapping();
         context.pushFramework(frameworkMapping);
         String packageVersion = objRef.getPackageVersion(objRef.getClassName());
         frameworkMapping.setVersion(packageVersion);
         frameworkMapping.setType(getHandlerName());
         frameworkMapping.setContextPath(urlParse.getVirtualPath());
         try {
             analystsTargetObject(urlParse, objRef, frameworkMapping, context);
         }catch (Exception e){
             throw new FrameworkExcept(getHandlerName() + "--> " + e.getMessage());
         }
    }

    void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping frameworkRecord, Context context) throws Exception;

     HandlerType getHandlerName();
}

