import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.AppiumUtil;
import utils.LogUtil;
import utils.DocumentUtil;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

public class DocumentUtilTest {

    private static LogUtil logger = LogUtil.getLogger(DocumentUtilTest.class);
    @Test
    public void testGetNodeXPath() {
        App app = new App();
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                "C:\\Users\\文向东\\Documents\\移动应用自动化测试题资料包\\apk\\IThouse.apk",
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }

        String pageSource = driver.getPageSource();

        logger.info(pageSource);
        Document document = DocumentUtil.buildDocument(pageSource);
        Element documentElement = document.getDocumentElement();

        travel(documentElement);
    }

    @Test
    public void testGetPageStructure() {
        App app = new App();
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                "C:\\Users\\文向东\\Documents\\移动应用自动化测试题资料包\\apk\\IThouse.apk",
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }

        String pageSource = driver.getPageSource();
        try {
            String structure = DocumentUtil.getPageStructure(pageSource);
            logger.info(structure);
        } catch (IOException | SAXException | XPathExpressionException e) {
            e.printStackTrace();
        }
    }


    private void travel(Node node) {
        String path = DocumentUtil.getNodeXPath(node);
        logger.info(node.toString(), path);
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            NodeList nodeList = (NodeList) xPath.evaluate("child::*", node, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                travel(item);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testGetByAttrs() throws XPathExpressionException {
        AndroidDriver<AndroidElement> driver = AppiumUtil.initAppiumDrier(
                "C:\\Users\\文向东\\Documents\\移动应用自动化测试题资料包\\apk\\IThouse.apk",
                "emulator-5554",
                4723,
                6000
        );
        assertNotNull(driver);

        Document document = DocumentUtil.buildDocument(driver.getPageSource());
        HashMap<String, String> attrs = new HashMap<>();
        attrs.put("clickable", "true");
        attrs.put("enabled", "true");
        NodeList nodeList = DocumentUtil.getNodesByAttributes(document, attrs);
        for (int i = 0; i < nodeList.getLength(); i++) {
            String nodeXPath = DocumentUtil.getNodeXPath(nodeList.item(i));
            AndroidElement element = driver.findElementByXPath(nodeXPath);
            logger.info(element.toJson().toString());
        }
    }
}