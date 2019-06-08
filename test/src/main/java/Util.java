import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final String PACKAGE = "package";
    public static final String ACTIVITY = "launchable-activity";
    private static final String AAPT_PATH = "aapt.exe";
    private static final String PATTERN = "name='(.*?)'";
    public static HashMap<String, String> getApkInfo(String apkPath) {
        URL resource = Util.class.getClassLoader().getResource(AAPT_PATH);
        System.out.println(resource.getPath());
        HashMap<String, String> apkInfo = new HashMap<>();
        // 准备 命令 和 参数
        String command[] = new String[]{
                resource.getPath().substring(1),
                "dump",
                "badging",
                apkPath
        };
        for (String s : command) {
            System.out.println(s);
        }
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()
                    )
            );
            // 开始读结果
            String line = null;
            String infos[] = new String[]{PACKAGE, ACTIVITY};

            Pattern pattern = Pattern.compile(PATTERN);
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    // 是否是 package 或者 activity 信息？
                    for (String info : infos) {
                        // 如果是某一种信息
                        if (line.startsWith(info)) {
                            // 正则匹配，提取name=''的部分
                            System.out.println(line + ": " + info);
                            Matcher matcher = pattern.matcher(line);
                            System.out.println(matcher);
                            if (matcher.find()) {
                                apkInfo.put(info, matcher.group(1));
                            }
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apkInfo;
    }
}
