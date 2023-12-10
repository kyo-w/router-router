import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.*;

public class ProcessUtils {
    private static String PID = "-1";

    static {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            int index = jvmName.indexOf('@');

            if (index > 0) {
                PID = Long.toString(Long.parseLong(jvmName.substring(0, index)));
            }
        } catch (Throwable e) {
        }

    }


    private static File findJps() {
        String javaHome = System.getProperty("java.home");
        String[] paths = {"bin/jps", "bin/jps.exe", "../bin/jps", "../bin/jps.exe"};

        List<File> jpsList = new ArrayList<File>();
        for (String path : paths) {
            File jpsFile = new File(javaHome, path);
            if (jpsFile.exists()) {
                jpsList.add(jpsFile);
            }
        }

        if (jpsList.isEmpty()) {
            String javaHomeEnv = System.getenv("JAVA_HOME");
            for (String path : paths) {
                File jpsFile = new File(javaHomeEnv, path);
                if (jpsFile.exists()) {
                    jpsList.add(jpsFile);
                }
            }
        }

        if (jpsList.isEmpty()) {
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        if (jpsList.size() > 1) {
            Collections.sort(jpsList, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    try {
                        return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
                    } catch (IOException e) {
                        // ignore
                    }
                    return -1;
                }
            });
        }
        return jpsList.get(0);
    }

    private static Map<Long, String> listProcessByJps(boolean v) {
        Map<Long, String> result = new LinkedHashMap<Long, String>();

        String jps = "jps";
        File jpsFile = findJps();
        if (jpsFile != null) {
            jps = jpsFile.getAbsolutePath();
        }

        String[] command = null;
        if (v) {
            command = new String[]{jps, "-v", "-l"};
        } else {
            command = new String[]{jps, "-l"};
        }

        List<String> lines = runNative(command);


        long currentPid = Long.parseLong(PID);
        for (String line : lines) {
            String[] strings = line.trim().split("\\s+");
            if (strings.length < 1) {
                continue;
            }
            try {
                long pid = Long.parseLong(strings[0]);
                if (pid == currentPid) {
                    continue;
                }
                if (strings.length >= 2 && isJpsProcess(strings[1])) { // skip jps
                    continue;
                }

                result.put(pid, line);
            } catch (Throwable e) {
            }
        }

        return result;
    }

    public static long select(Scanner scanner, Config config) throws InputMismatchException {
        System.out.println("开始选择: ");
        Map<Long, String> processMap = listProcessByJps(config.getVerbose());
        if (processMap.isEmpty()) {
            System.out.println("没找到");
            return -1;
        }

        // print list
        int count = 1;
        for (String process : processMap.values()) {
            if (count == 1) {
                System.out.println("* [" + count + "]: " + process);
            } else {
                System.out.println("  [" + count + "]: " + process);
            }
            count++;
        }

        String line = scanner.nextLine();
        if (line.trim().isEmpty()) {
            return processMap.keySet().iterator().next();
        }

        int choice = Integer.valueOf(line);

        if (choice <= 0 || choice > processMap.size()) {
            return -1;
        }

        Iterator<Long> idIter = processMap.keySet().iterator();
        for (int i = 1; i <= choice; ++i) {
            if (i == choice) {
                return idIter.next();
            }
            idIter.next();
        }

        return -1;
    }

    private static List<String> runNative(String[] cmdToRunWithArgs) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs);
        } catch (SecurityException e) {
            return new ArrayList<String>(0);
        } catch (IOException e) {
            return new ArrayList<String>(0);
        }

        ArrayList<String> sa = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sa.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            return new ArrayList<String>(0);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            IOUtils.close(reader);
        }
        return sa;
    }

    private static boolean isJpsProcess(String mainClassName) {
        return "sun.tools.jps.Jps".equals(mainClassName) || "jdk.jcmd/sun.tools.jps.Jps".equals(mainClassName);
    }
}
