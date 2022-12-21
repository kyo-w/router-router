package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class TimeUtils {
    public static String getNow(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[yyyy/MM/dd/E HH:mm:ss]");
        return simpleDateFormat.format(date);
    }
}
