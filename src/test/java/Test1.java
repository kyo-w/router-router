import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {
    private static final String
    info = "{*.jspx=ServletMapping[url-pattern=*.jspx, name=resin-jspx], *.php=ServletMapping[url-pattern=*.php, name=resin-php], *.jsp=ServletMapping[url-pattern=*.jsp, name=resin-jsp], *.jspf=ServletMapping[url-pattern=*.jspf, name=resin-jsp], /hello/index=ServletMapping[url-pattern=/hello/index, name=helloindex], /faces/*=ServletMapping[url-pattern=/faces/*, url-pattern=*.jsf, url-pattern=*.faces, name=FacesServlet], /*=ServletMapping[url-pattern=/*, name=hello], /index=ServletMapping[url-pattern=/index, name=index], *.faces=ServletMapping[url-pattern=/faces/*, url-pattern=*.jsf, url-pattern=*.faces, name=FacesServlet], /=ServletMapping[url-pattern=/, name=resin-file], *.jsf=ServletMapping[url-pattern=/faces/*, url-pattern=*.jsf, url-pattern=*.faces, name=FacesServlet]}";

    public static void main(String[] args) {
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        Pattern compile = Pattern.compile("\\[url-pattern=(.*?), +name=(.*?)\\]");
        Matcher matcher = compile.matcher(info);
        while (matcher.find()){
            String className = matcher.group(2);

            String replace = matcher.group(1).replace("url-pattern=", "");
            String[] split = replace.split(",");
            for(String elem: split){
                objectObjectHashMap.put(elem, className);
            }
        }
        System.out.println(11);
    }
}
