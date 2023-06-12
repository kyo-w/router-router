package com.kyodream.debugger.core.category.format;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JettyFormat extends Format{
    private static Pattern splitUrlAndClassNamePattern = Pattern.compile("\\[(.*)\\]=>(.*)\\-");
    private static Pattern classNamePattern = Pattern.compile(".*==(.*?),");
    private static Pattern mapAndClassNamePattern = Pattern.compile("\\[(.*)\\]=>(.*)");
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

    public static HashMap<String, String> splitMapAndClassName(String rawResult, HashMap<String, String> servletObject, String prefix){
        HashMap<String, String> result = new HashMap<>();
        Matcher matcher = mapAndClassNamePattern.matcher(rawResult);
        if(matcher.find()){
            String map = matcher.group(1).trim();
            String aliasClassName = matcher.group(2).trim();
            String className = servletObject.get(aliasClassName);
            String url = prefix +  map;
            result.put(doubleSlash(url), className);
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
}
