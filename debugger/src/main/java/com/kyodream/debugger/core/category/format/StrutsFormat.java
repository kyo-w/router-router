package com.kyodream.debugger.core.category.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrutsFormat extends Format{
    private static Pattern moduleReg = Pattern.compile("\\[\"(.*)\"\\]");
    public static String getModuleName(String rawName) {
        Matcher matcher = moduleReg.matcher(rawName);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }
}
