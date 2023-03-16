package com.kyodream.end.core.category.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JerseyFormat extends Format{
    private static Pattern jerseyPattern = Pattern.compile("\\(value=(.*)\\)");
    public static String getJerseyUrl(String rawUrl){
        Matcher matcher = jerseyPattern.matcher(rawUrl);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }
}
