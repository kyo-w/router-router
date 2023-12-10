package kyodream;

import java.io.*;

public class VmUtils {
    static {
        String systemName = System.getProperty("os.name").toLowerCase();
        String shareFileName = null;
        if (systemName.contains("window")) {
            shareFileName = "/Test.dll";
        }
        if (systemName.contains("linux")) {
            shareFileName = "/test.so";
        }
        System.out.println(shareFileName);
        InputStream resourceAsStream = VmUtils.class.getResourceAsStream(shareFileName);
        try {
            BufferedInputStream reader = new BufferedInputStream(resourceAsStream);
            byte[] buffer = new byte[1024];
            File tmp = File.createTempFile("kyodream", ".dy");
            tmp.delete();
            tmp.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(tmp);
            while (reader.read(buffer) > 0) {
                fileOutputStream.write(buffer);
                buffer = new byte[1024];
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            resourceAsStream.close();
            String absolutePath = tmp.getAbsolutePath();
            System.load(absolutePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

    }

    public native static Object[] getInstanceByClassName(Class className);
}

