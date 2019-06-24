import org.junit.Test;
import utils.AppiumUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppiumUtilTest {
    @Test
    public void testGetApkInfo() {
        final String APK_PATH = AppiumUtil.class.getClassLoader().getResource("GuDong.apk").getPath().substring(1);
        HashMap<String, String> apkInfo = AppiumUtil.getApkInfo(APK_PATH);
        System.out.println("result");
        for (Map.Entry info : apkInfo.entrySet()) {
            System.out.println(info.getKey() + ":" + info.getValue());
        }
    }

    @Test
    public void testBuildString() {
        List<AppiumUtil.UiSelectorParam> params = Arrays.asList(
                new AppiumUtil.UiSelectorParam(
                        AppiumUtil.UiSelectorParamType.CLICKABLE,
                        "true"
                ),
                new AppiumUtil.UiSelectorParam(
                        AppiumUtil.UiSelectorParamType.CLASS_NAME_MATCHES,
                        ".*"
                )
        );
        String s = AppiumUtil.buildUiSelectorString(params);
        System.out.println(s);
    }
}