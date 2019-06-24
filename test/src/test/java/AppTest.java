import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.AppiumUtil;
import utils.Constants;
import utils.DocumentUtil;
import utils.LogUtil;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class AppTest {

    private LogUtil logger = LogUtil.getLogger(AppTest.class);

    private ElementInActivity clickedElements = new ElementInActivity();

    private final String EDIT_TEXT = "EditText";



    @Test
    public void test() {
        App app = new App();
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                "C:\\Users\\文向东\\Documents\\移动应用自动化测试题资料包\\apk\\Bilibili.apk",
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        app.test(driver);

    }

    @Test
    public void testPageSource() throws XPathExpressionException, SAXException, IOException, InterruptedException {

        LogUtil logger = LogUtil.getLogger("test page structure");
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                "C:\\Users\\文向东\\Documents\\移动应用自动化测试题资料包\\apk\\IThouse.apk",
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        String pageSource = driver.getPageSource();

        List<AndroidElement> clickableElements = AppiumUtil.getClickableElements(driver);
        Queue<AndroidElement> queue = new LinkedList<>(clickableElements);
        String pre = DocumentUtil.getPageStructure(pageSource);
        Set<String> pageSet = new HashSet<>();
        pageSet.add(pre);
        while (!queue.isEmpty()) {
            AndroidElement element = queue.poll();

            if (element.getTagName().equals(Constants.NAVIGATOR_NAME)) continue;

            element.click();
            logger.info("点击", element.toJson().toString());
            Thread.sleep(1000);
            String current = DocumentUtil.getPageStructure(driver.getPageSource());


            if (pageSet.add(current)) { // 是否和以前都不同
                logger.info("变了");
                queue.clear();
                queue.addAll(AppiumUtil.getClickableElements(driver));
            }
        }
    }


    @Test
    public void testAcceptAlert() throws InterruptedException {
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                "C:\\Users\\文向东\\Documents\\移动应用自动化测试题资料包\\apk\\GuDong.apk",
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);

        Thread.sleep(3000);

        driver.executeScript("mobile:acceptAlert");
    }


    public void testCycle(String path) throws XPathExpressionException, SAXException, IOException, InterruptedException {
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                path,
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);

        String homePackage = driver.getCurrentPackage();
        String homeActivity = driver.currentActivity();

        /**
         * 点击过的元素的xpath set
         */
        Set<String> clickedElementsXpathSet = new HashSet<>();

        XpathInActivity clickedElementsXpathInActivity = new XpathInActivity();

        /**
         * 经历过的页面结构 set
         */
        Set<String> pageStructureSet = new HashSet<>();


        int loop = 0;
        while (true) {
            logger.info(++loop + "轮");
//            String pageStructure = DocumentUtil.getPageStructure(driver.getPageSource());
            pageStructureSet.add(driver.getPageSource());


            Document document = DocumentUtil.buildDocument(driver.getPageSource());
            HashMap<String, String> attrs = new HashMap<>();
            attrs.put("clickable", "true");
//            attrs.put("enabled", "true");

            NodeList nodeList = DocumentUtil.getNodesByAttributes(document, attrs);
            int index = 0;
            while (index < nodeList.getLength()) {

                // 把当前活动加入 set
                String currentActivity = driver.currentActivity();

                Node node = nodeList.item(index);
                String nodeXPath = DocumentUtil.getNodeXPath(node);

                if (!clickedElementsXpathInActivity.add(currentActivity, nodeXPath)) {
                    logger.info("点过了");
                    index++;
                    continue;
                }

                AndroidElement element;
                try {
                    element = driver.findElementByXPath(nodeXPath);
                } catch (Exception e) {
                    logger.info("没找到");
                    index++;
                    continue;
                }
                if (element == null) {
                    logger.info("element 为 null");
                    index++;
                    continue;
                }
                if (element.getTagName() != null && element.getTagName().equals(Constants.NAVIGATOR_NAME)) {
                    logger.info(Constants.NAVIGATOR_NAME);
                    index++;
                    continue;
                }
//                if (!clickedElementsXpathSet.add(nodeXPath)) {  // 是不是点击过了
//                    logger.info("点过了");
//                    index++;
//                    continue;
//                }
                if (element.getAttribute("class").contains(EDIT_TEXT)) {
                    try {
                        element.sendKeys("test");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        element.click();
                        System.out.println(element.getAttribute("clickable"));
                        System.out.println(element.getTagName());
                        System.out.println(element.getAttribute("class"));
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String currentPackage = driver.getCurrentPackage();
                String pageSource = driver.getPageSource();

                /**
                 * 是不是跳转到别的程序了
                 */
                if (!currentPackage.equals(homePackage)) {
                    logger.info("跳转到别的了");
                    driver.navigate().back();
                    document = DocumentUtil.buildDocument(pageSource);
                    nodeList = DocumentUtil.getNodesByAttributes(document, attrs);
                    index = 0;
                }

//                String structure = DocumentUtil.getPageStructure(pageSource);
                if (pageStructureSet.add(pageSource)) {  // 如果改变了页面结构，则要重新获取组件
                    logger.info("改变了");
                    document = DocumentUtil.buildDocument(pageSource);
                    nodeList = DocumentUtil.getNodesByAttributes(document, attrs);
                    index = 0;
                } else {
                    index++;
                }
            }

            while (!driver.currentActivity().equals(homeActivity)) {
                try {
                    driver.navigate().back();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            int deep = activitySet.size();
//            activitySet.clear();
//            while (deep > 1) {
//                try {
//                    driver.navigate().back();
//                } catch (Exception e) {
//
//                }
//                deep--;
//            }
        }


    }


    public void recursive(String page, AndroidDriver<AndroidElement> driver) {
        String activity = driver.currentActivity();
        String packageName = driver.getCurrentPackage();
        List<AndroidElement> clickableElements = AppiumUtil.getClickableElements(driver);
        int index = 0;
        while (index < clickableElements.size()) {
            AndroidElement element = clickableElements.get(index);

            // 排除返回键
            if (element.getTagName() != null && element.getTagName().equals(Constants.NAVIGATOR_NAME)) {
                logger.info(Constants.NAVIGATOR_NAME);
                index++;
                continue;
            }

            String elementID = AppiumUtil.getElementID(element);
            if (!clickedElements.contains(activity, elementID)) {
                try {
                    element.click();
                    logger.info("点击", elementID);
                    Thread.sleep(1000);
                    String structure = DocumentUtil.getPageStructure(driver.getPageSource());
                    if (!structure.equals(page)) {
                        logger.info("变了");
                        // 不在同一个activity
                        if (!driver.getCurrentPackage().equals(packageName)) {
                            logger.info("跳转到别的包了");
                            driver.navigate().back();
                        } else if (!activity.equals(driver.currentActivity())) {
                            recursive(structure, driver);
                            driver.navigate().back();
                        } else {
                            recursive(structure, driver);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("加入已点击", elementID);
                clickedElements.add(activity, elementID);
            }
            index++;
        }
    }


}