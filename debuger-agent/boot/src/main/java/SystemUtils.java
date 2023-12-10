public class SystemUtils {
    private static final float JAVA_VERSION = Float.parseFloat(System.getProperty("java.specification.version"));

    public static boolean javaHasToolsJar() {
        return JAVA_VERSION >= 9.0f;
    }

    public static String JavaHome() {
        return System.getProperty("java.home");
    }
}
