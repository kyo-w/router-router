package router.utils;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import router.analysts.IAnalysts;
import router.analysts.InvokeUtils;
import router.analysts.ObjAnalysts;
import router.analysts.SetAnalysts;

public class SpringUtils {
    /**
     * @param handlerRef
     * @return
     */
    public static SetAnalysts getPatternsRef(IAnalysts handlerRef) throws Exception{
        if (handlerRef.existFieldByName("mapping", "patternsCondition", "patterns")) {
            return new SetAnalysts(handlerRef.getObjFields("mapping", "patternsCondition", "patterns"));
        } else if (handlerRef.existFieldByName("mapping", "pathPatternsCondition", "patterns")) {
            return new SpringSetAnalysts(handlerRef.getObjFields("mapping", "pathPatternsCondition", "patterns"));
        } else {
            throw new Exception("No such handler");
        }
    }

    public static class SpringSetAnalysts extends SetAnalysts {

        public SpringSetAnalysts(IAnalysts obj) {
            super(obj);
            init(obj.getRawValue());
            if (isImplementOf("java.util.Set")) {
                return;
            }
            this.thread = obj.getThreadRawRef();
            this.data = InvokeUtils.getSet(thread, (ObjectReference) getRawValue())
                    .stream()
                    .map(elem -> new SpringPathPatternObj(ObjAnalysts.parseObject(thread, elem)))
                    .toArray(ObjAnalysts[]::new);
        }
    }

    public static class SpringPathPatternObj extends ObjAnalysts {
        public SpringPathPatternObj(IAnalysts ref) {
            this.init(ref.getRawValue());
            this.thread = ref.getThreadRawRef();
        }

        @Override
        public String convertToString() {
            try {
                return getStrFields("patternString");
            } catch (Exception e) {
                return null;
            }
        }
    }
}
