package utils;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.Constants.ATTRIBUTES;

public class AppiumUtil {


    /**
     * 初始化appium
     * @param appPath
     * @param udid
     * @param port
     * @param timeout
     * @return
     */
    public static AndroidDriver<AndroidElement> initAppiumDrier(String appPath, String udid, int port, int timeout) {
        AndroidDriver<AndroidElement> driver = null;

        // app读取
        File app = new File(appPath);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        // 使用uiautomator 2
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
        // udid
        capabilities.setCapability(MobileCapabilityType.UDID, udid);

        //设置apk
        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        // 如何自动获取到，apk的package和activity
        HashMap<String, String> apkInfo = AppiumUtil.getApkInfo(app.getAbsolutePath());
        String packageInfo = apkInfo.getOrDefault(AppiumUtil.PACKAGE, "");
        String activityInfo = apkInfo.getOrDefault(AppiumUtil.ACTIVITY, "");
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, packageInfo);
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, activityInfo);

        /**
         * 其他配置
         */
        capabilities.setCapability(AndroidMobileCapabilityType.NO_SIGN, "true");
        //设置使用unicode键盘，支持输入中文和特殊字符
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, "true");
        //设置用例执行完成后重置键盘
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, "true");
        // 权限问题
        capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, "true");

        //初始化
        try {
            driver = new AndroidDriver<>(new URL("http://127.0.0.1:" + port + "/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return driver;

    }


    /**
     * 获取 apk 信息
     */
    public static final String PACKAGE = "package";
    public static final String ACTIVITY = "launchable-activity";
    private static final String AAPT_PATH = "aapt.exe";
    // 正则匹配，提交name=后面的部分，要使用非贪婪模式
    private static final String PATTERN = "name='(.*?)'";
    public static HashMap<String, String> getApkInfo(String apkPath) {
        // 获取 aapt 文件资源
        URL aaptResource = AppiumUtil.class.getClassLoader().getResource(AAPT_PATH);
        System.out.println(aaptResource.getPath());
        HashMap<String, String> apkInfo = new HashMap<>();
        // 准备 命令 和 参数
        String command[] = new String[]{
                aaptResource.getPath().substring(1),
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


    /**
     * 获取所有可以点击的元素
     * @param driver
     * @return
     */
    public static List<AndroidElement> getClickableElements(AndroidDriver<AndroidElement> driver) {
        return driver.findElements(MobileBy.AndroidUIAutomator(
                "new UiSelector().clickable(true)"
        ));
    }

    public static class UiSelectorParam {
        UiSelectorParamType type;
        String value;

        public UiSelectorParam(UiSelectorParamType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return MessageFormat.format(
                    type.getValue(),
                    value
            );
        }
    }
    public static enum UiSelectorParamType {
        CLICKABLE(".clickable({0})"),
        CLASS_NAME_MATCHES(".classNameMatches(\"{0}\")");

        private String value;

        UiSelectorParamType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 构建 UiSelector 选择 字符串
     * @param params
     * @return
     */
    public static String buildUiSelectorString(List<UiSelectorParam> params) {
        StringBuilder stringBuilder = new StringBuilder("new UiSelector()");
        for (UiSelectorParam param : params) {
            stringBuilder
                    .append(param.toString());
        }
        return  stringBuilder.toString();
    }


    public static Document parseXml(String xml) throws ParserConfigurationException, IOException, SAXException {
        DOMParser parse = new DOMParser();
        InputSource source = new InputSource();
        source.setByteStream(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        parse.parse(source);
        return parse.getDocument();
    }

    public static String getElementID(AndroidElement element) {
        StringBuilder builder = new StringBuilder();
        for (String attr : ATTRIBUTES) {
            String value = element.getAttribute(attr);
            builder.append(attr)
                    .append(":")
                    .append(value)
                    .append(",");
        }
        return builder.toString();
    }
}
