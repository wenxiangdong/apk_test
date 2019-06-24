import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import utils.Constants;
import utils.DocumentUtil;
import utils.LogUtil;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        System.out.println("hello world");
    }


    private LogUtil logger = LogUtil.getLogger(App.class);
    private final boolean DEBUG = true;

    private ElementInActivity clickedElement = new ElementInActivity();
    private Set<String> visitedActivities = new HashSet<>();
    private String page = "";



    /*
    点击后的等待时间
     */
    private final int CLICK_WAIT_TIME = 1500;

    private final String EDIT_TEXT = "EditText";


    public void test(AndroidDriver<AndroidElement> driver) {
        testWithTree(driver);
    }


    public void testWithTree(AndroidDriver<AndroidElement> driver) {
        config(driver);
        String homeActivity = driver.currentActivity();
        String homePackage = driver.getCurrentPackage();
        visitedActivities.add(homeActivity);

        while (true) {
            String pageSource = driver.getPageSource();
            pageChanged(pageSource);
            Document document = DocumentUtil.buildDocument(pageSource);
            Element node = document.getDocumentElement();
            boolean completed = travelDFS(node.getFirstChild(), driver);
            logger.info("complete dfs", String.valueOf(completed));
            if (completed) {
                if (!homeActivity.equals(driver.currentActivity())) {
                    driver.navigate().back();
                }
            }
            if (!driver.getCurrentPackage().equals(homePackage)) {
                logger.info("跳出app了");
                driver.startActivity(new Activity(homePackage, homeActivity));
            }
        }
    }


    public boolean travelDFS(Node node, AndroidDriver<AndroidElement> driver) {
        while (node != null) {

            boolean completed = travelDFS(node.getFirstChild(), driver);
            if (!completed) return false;


            String nodeXPath = DocumentUtil.getNodeXPath(node);
            logger.info("xpath", nodeXPath);


            // 是可输入的？
            if (canInput(node)) {
                try {
                    driver.findElementByXPath(nodeXPath).sendKeys("test");
                } catch (Exception e) {}
            }

            // 是可点的？
            if (!canClick(node)) {
                node = node.getNextSibling();
                continue;
            }


            // 点过了？
            if (clickedElement.contains(driver.currentActivity(), nodeXPath)) {
                logger.info("点过了");
                node = node.getNextSibling();
                continue;
            }
            try {
                logger.info("加入已点击");
                clickedElement.add(driver.currentActivity(), nodeXPath);

                AndroidElement element = driver.findElementByXPath(nodeXPath);
                String currentActivity = driver.currentActivity();
                element.click();
                // 等待程序响应变化
                Thread.sleep(CLICK_WAIT_TIME);

                if (!currentActivity.equals(driver.currentActivity())) {
                    if (!this.visitedActivities.add(driver.currentActivity())) {
                        logger.info("该活动已经跳转来过");
                        driver.navigate().back();
                        return false;
                    }
                }

                boolean changed = this.pageChanged(driver.getPageSource());
                if (changed) {
                    logger.info("page changed");
                    return false;
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
            node = node.getNextSibling();
        }
        return true;
    }


    public boolean pageChanged(String xml) {
        try {
            String structure = DocumentUtil.getPageStructure(xml);
            String old = page;
            page = structure;
            logger.info(structure);
            return !old.equals(page);
        } catch (IOException | SAXException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public boolean canClick(Node node) {
        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();

            Node clickable = attributes.getNamedItem("clickable");
            if (clickable != null && !Boolean.valueOf(clickable.getNodeValue())) {
                return false;
            }
            Node content = attributes.getNamedItem("content-desc");
            if (content != null && content.getNodeValue().equals(Constants.NAVIGATOR_NAME)) {
                return false;
            }

            // 根据位置和类判断是不是导航按钮
            Node classAttr = attributes.getNamedItem("class");
            Node boundsAttr = attributes.getNamedItem("bounds");
            if (classAttr != null
                && classAttr.getNodeValue().contains("ImageButton")
                && boundsAttr != null
                && boundsAttr.getNodeValue().contains("[0,48]")) {
                logger.info("导航按钮");
                return false;
            }
        }
        return true;
    }

    public boolean canInput(Node node) {
        return node.getNodeName().contains(EDIT_TEXT);
    }

    /**
     * 一些其他配置，像定位时长等
     * @param driver
     */
    private void config(AndroidDriver driver) {
        assert driver != null;
        try {
            Thread.sleep(6000);        //等待6s，待应用完全启动
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS); //设置尝试定位控件的最长时间为8s,也就是最多尝试8s
    }
}
