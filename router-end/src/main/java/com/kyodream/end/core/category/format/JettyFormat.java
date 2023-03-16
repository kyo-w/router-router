package com.kyodream.end.core.category.format;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JettyFormat extends Format{
    private static Pattern splitUrlAndClassNamePattern = Pattern.compile("\\[(.*)\\]=>(.*)\\-");
    private static Pattern classNamePattern = Pattern.compile(".*==(.*?),");
    public static HashMap<String, String> splitUrlAndClassName(String urlAndClassName, String prefix){
        HashMap<String, String> result = new HashMap<>();
        Matcher matcher = splitUrlAndClassNamePattern.matcher(urlAndClassName);
        if(matcher.find()){
            String url = matcher.group(1);
            String className = matcher.group(2);
            String fullUrl = doubleSlash(prefix + url);
            result.put(fullUrl, className);
        }
        return result;
    }

    public static String getClassName(String rawClassName){
        Matcher matcher = classNamePattern.matcher(rawClassName);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public static String doubleSlash(String url){
        return url.replace("\\\\", "/").replace("//", "/");
    }
}
