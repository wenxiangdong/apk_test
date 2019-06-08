import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("hello world");
    }

    private HashMap<String, String> getAppInfo() {
        HashMap<String, String> appInfo = new HashMap<>();

        return appInfo;
    }

    private AppiumDriver initAppiumDrier(String appPath, String udid, int port, int timeout) {
        AppiumDriver driver = null;

        // app读取
        File app = new File(appPath);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        // udid
        capabilities.setCapability("udid", udid);

        //设置apk
        capabilities.setCapability("app", app.getAbsolutePath());
        // TODO 如何自动获取到，apk的package和activity
        capabilities.setCapability("appPackage", "name.gudong.translate");
        capabilities.setCapability("appActivity", "name.gudong.translate.ui.activitys.MainActivity");


        /**
         * 其他配置
          */
        capabilities.setCapability("noSign", "true");
        //设置使用unicode键盘，支持输入中文和特殊字符
        capabilities.setCapability("unicodeKeyboard", "true");
        //设置用例执行完成后重置键盘
        capabilities.setCapability("resetKeyboard", "true");
        // 权限问题
        capabilities.setCapability("autoGrantPermissions", "true");

        //初始化
        try {
            driver = new AppiumDriver(new URL("http://127.0.0.1:" + port + "/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return driver;

    }
}
