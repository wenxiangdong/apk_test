import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UtilTest {
    @Test
    public void testGetApkInfo() {
        final String APK_PATH = Util.class.getClassLoader().getResource("GuDong.apk").getPath().substring(1);
        HashMap<String, String> apkInfo = Util.getApkInfo(APK_PATH);
        System.out.println("result");
        for (Map.Entry info : apkInfo.entrySet()) {
            System.out.println(info.getKey() + ":" + info.getValue());
        }
    }
}