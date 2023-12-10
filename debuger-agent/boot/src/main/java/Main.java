import com.beust.jcommander.JCommander;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) {
        File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        String workDir = file.getParentFile().getAbsolutePath();
        Config config = new Config();
        JCommander build = JCommander.newBuilder().addObject(config).build();
        build.parse(args);
        if (config.getHelp()) {
            build.usage();
            return;
        }
        Scanner scan = new Scanner(System.in);
        if (config.getIp() == null) {
            System.out.println("默认监听本地0.0.0.0");
            config.setIp("0.0.0.0");
        }
        if (config.getPort() == null) {
            config.setPort(String.valueOf(getPort(config.getIp())));
            System.out.println("随机监听端口: " + config.getPort());
        }

        String javaHome = findJavaHome();
        File javaPath = findJava(javaHome);
        if (javaPath == null) {
            throw new IllegalArgumentException(
                    "Can not find java/java.exe executable file under java home: " + javaHome);
        }
        File toolsJar = findToolsJar(javaHome, workDir);
        List<String> command = new ArrayList<>();
        System.out.println(javaPath.getAbsolutePath());
        command.add(javaPath.getAbsolutePath());

        /**
         * 高版本已经不支持此参数，并且tools.jar已经变成模块了
         */
        if(!SystemUtils.javaHasToolsJar()) {
            command.add("-Xbootclasspath/a:" + toolsJar.getAbsolutePath());
        }
        command.add("-jar");
        command.add(Paths.get(workDir, "core.jar").toString());
        command.add("--port");
        command.add(config.getPort());
        command.add("--ip");
        command.add(config.getIp());
        command.add("--work-path");
        command.add(workDir);


        System.out.println("开始运行");
//        防止agent参数冲突
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("JAVA_TOOL_OPTIONS", "");
        try {
            final Process proc = pb.start();
            Thread redirectStdout = new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = proc.getInputStream();
                    try {
                        IOUtils.copy(inputStream, System.out);
                    } catch (IOException e) {
                        IOUtils.close(inputStream);
                    }

                }
            });

            Thread redirectStderr = new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = proc.getErrorStream();
                    try {
                        IOUtils.copy(inputStream, System.err);
                    } catch (IOException e) {
                        IOUtils.close(inputStream);
                    }

                }
            });
            redirectStdout.start();
            redirectStderr.start();
            redirectStdout.join();
            redirectStderr.join();

            int exitValue = proc.exitValue();
            if (exitValue != 0) {
                System.exit(1);
            }
        } catch (Throwable e) {
        }
    }

    private static String findJavaHome() {
        String javaHome = System.getProperty("java.home");

        if (!SystemUtils.javaHasToolsJar()) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                return javaHome;
            }

            if (!toolsJar.exists()) {
                String javaHomeEnv = System.getenv("JAVA_HOME");
                if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
                    toolsJar = new File(javaHomeEnv, "lib/tools.jar");
                    if (!toolsJar.exists()) {
                        toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
                    }
                }

                if (toolsJar.exists()) {
                    return javaHomeEnv;
                }

                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
            }
        }
        return javaHome;
    }

    private static File findJava(String javaHome) {
        String[] paths = {"bin/java", "bin/java.exe", "../bin/java", "../bin/java.exe"};

        List<File> javaList = new ArrayList<File>();
        for (String path : paths) {
            File javaFile = new File(javaHome, path);
            if (javaFile.exists()) {
                javaList.add(javaFile);
            }
        }

        if (javaList.isEmpty()) {
            return null;
        }

        if (javaList.size() > 1) {
            Collections.sort(javaList, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    try {
                        return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
                    } catch (IOException e) {
                    }
                    return -1;
                }
            });
        }
        return javaList.get(0);
    }

    private static File findToolsJar(String javaHome, String workDir) {
//        if (SystemUtils.javaHasToolsJar()) {
//            return null;
//        }
//
//        File toolsJar = new File(javaHome, "lib/tools.jar");
//        if (!toolsJar.exists()) {
//            toolsJar = new File(javaHome, "../lib/tools.jar");
//        }
//        if (!toolsJar.exists()) {
//            toolsJar = new File(javaHome, "../../lib/tools.jar");
//        }
//
//        if (!toolsJar.exists()) {
//            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
//        }
        return new File(workDir + File.separator + "native" + File.separator + "tools.jar");
    }

    private static int getPort(String ip) {
        int testPort = DEFAULT_PORT;
        while (isPortUsing(ip, testPort)) {
            testPort++;
        }
        return testPort;
    }


    private static String getJVMProcessPid(Scanner scanner, Config config) {
        return String.valueOf(ProcessUtils.select(scanner, config));
    }

    public static boolean isPortUsing(String host, int port) {
        boolean flag = false;
        InetAddress theAddress = null;
        try {
            theAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {
        }
        return flag;
    }

}
