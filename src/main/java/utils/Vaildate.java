package utils;

import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vaildate {
    public static boolean isIPV4(String ip){
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ip);
        return m.matches();
    }

    public static boolean isPort(String port){
        int i = -1;
        try {
            i = Integer.parseInt(port);
        }catch (Exception e) {
            return false;
        }
        if(i < 0 || i > 65535){
            return false;
        }
        return true;
    }
}
