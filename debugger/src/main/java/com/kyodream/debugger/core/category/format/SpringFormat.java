package com.kyodream.debugger.core.category.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringFormat extends Format{
    private static Pattern requestUrlPattern = Pattern.compile("\\[(.*)\\]");
    private static Pattern classNamePattern = Pattern.compile("class=(.*)\\,");

    public static String getRequestMappingUrl(String url) {
        Matcher matcher = requestUrlPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
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
