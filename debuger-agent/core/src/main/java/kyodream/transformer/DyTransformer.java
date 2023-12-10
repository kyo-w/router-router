package kyodream.transformer;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class DyTransformer extends CacheTransformer {
    protected LinkedHashSet<String> blackList = new LinkedHashSet<>();

    protected Set<String> classNames = new HashSet<>();

    protected static ClassLoader routerClassLoader;

    public static void setRouterClassLoader(ClassLoader contextClassLoader) {
        routerClassLoader = contextClassLoader;
    }


    protected boolean match(String classNameRaw) {
        String className = classNameRaw.replace("/", ".");
        if (this.classNames.contains(className)) {
            return true;
        }
        return false;
    }

    protected boolean addDyClass(String className) {
        if (!blackList.contains(className)) {
            this.classNames.add(className);
            return true;
        }
        return false;
    }
}
