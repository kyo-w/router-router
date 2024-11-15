package router.pipeline;

import router.analysts.IAnalysts;
import router.context.Context;
import router.handler.Struts2Handler;
import router.parse.UrlParse;

public class FilterPipeLine {
    public static boolean doFilter(UrlParse urlParse, IAnalysts filterRef, Context context) {
        if (filterRef.isInstanceof("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter") ||
                filterRef.isInstanceof("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter") ||
                filterRef.isInstanceof("org.apache.struts2.dispatcher.FilterDispatcher")) {
            new Struts2Handler().handler(urlParse, filterRef, context);
        }
        return true;
    }
}
