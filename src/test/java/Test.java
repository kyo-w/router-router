import monitor.MyTableModel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private static String info = "{j_security_check=ServletConfigImpl[name=j_security_check,class=class com.caucho.server.security.FormLoginServlet], resin-xtp=ServletConfigImpl[name=resin-xtp,class=class com.caucho.jsp.XtpServlet], resin-jsp=ServletConfigImpl[name=resin-jsp,class=class com.caucho.jsp.JspServlet], resin-jspx=ServletConfigImpl[name=resin-jspx,class=class com.caucho.jsp.JspServlet], index=ServletConfigImpl[name=index,class=class com.kyo.IndexController], helloindex=ServletConfigImpl[name=helloindex,class=class com.kyo.TestController], FacesServlet=ServletConfigImpl[name=FacesServlet,class=class javax.faces.webapp.FacesServlet], hello=ServletConfigImpl[name=hello,class=class com.kyo.HelloController], resin-file=ServletConfigImpl[name=resin-file,class=class com.caucho.servlets.FileServlet], resin-php=ServletConfigImpl[name=resin-php,class=class com.caucho.quercus.servlet.QuercusServlet]}";
    public static void main(String[] args) throws InterruptedException {
        Pattern compile = Pattern.compile("\\[name=(.*?),class=class (.*?)\\]");
        Matcher matcher = compile.matcher(info);
        while (matcher.find()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }
}
