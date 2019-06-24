package utils;

import com.google.common.collect.ImmutableSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DocumentUtil {
    private static LogUtil logger = LogUtil.getLogger(DocumentUtil.class);

    private static DocumentBuilder builder = null;

    private static Set<String> EXCLUDE_ATTRIBUTES = ImmutableSet.of(
            "selected",
            "focused",
            "checked"
    );


    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 节点的 xpath
     *
     * @param node
     * @return xpath
     */
    public static String getNodeXPath(Node node) {
        Objects.requireNonNull(node);
        if (!node.hasAttributes()) {
            return "//" + node.getNodeName();
        }
        // 属性列表的长度
        int attrLength = node.getAttributes() != null
                ? node.getAttributes().getLength()
                : 0;

        // 开始构建，开头是 //[
        StringBuilder nodeXpath = new StringBuilder("//" + node.getNodeName() + "[");

        // 遍历所有属性，并加到xpath上
        while (--attrLength >= 0) {
            // 记录一个属性的 键值
            Node attrNode = node.getAttributes().item(attrLength);
            String attrName = attrNode.getNodeName();
            String attrValue = attrNode.getNodeValue();


            // 不记录排除在外的属性
            if (EXCLUDE_ATTRIBUTES.contains(attrName)) {
                continue;
            }

            // 不记录值为空的属性
            if (attrValue.length() == 0) {
                continue;
            }

            // @name="value"
            nodeXpath.append("@");
            nodeXpath.append(attrName);
            nodeXpath.append("=\"");
            nodeXpath.append(attrValue);
            nodeXpath.append("\"");

            // 如果不是最后一个属性，要加上 and
            if (attrLength > 0) {
                nodeXpath.append(" and ");
            }
        }

        // 结束构建
        nodeXpath.append("]");

        // 进一步保证最后没有 and
        return nodeXpath.toString().replace(" and ]", "]");
    }

    /**
     * 根据xml构建 一个 文档
     * @param xml
     * @return
     */
    public static Document buildDocument(String xml) {
        Document document = null;
        try {
            document = builder.parse(
                    new ByteArrayInputStream(
                            xml.getBytes(StandardCharsets.UTF_8)
                    )
            );
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }


    public static NodeList getNodesByAttributes(Node node, Map<String, ? extends Object> attrs) throws XPathExpressionException {
        StringBuilder xpathBuilder = new StringBuilder("//*[");
        for (Map.Entry<String, ? extends Object> entry : attrs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // @name="value"
            xpathBuilder.append("@")
                    .append(key)
                    .append("=\"")
                    .append(value)
                    .append("\"")
                    .append(" and ");
        }
        xpathBuilder.append("]");
        String expression = xpathBuilder.toString().replace(" and ]", "]");

        logger.info("expression", expression);

        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList)xPath.evaluate(expression, node, XPathConstants.NODESET);
    }


    public static String getPageStructure(String pageSource) throws IOException, SAXException, XPathExpressionException {
        Document document = builder.parse(
                new ByteArrayInputStream(
                        pageSource.getBytes(StandardCharsets.UTF_8)
                )
        );
        return getPageStructure(document);
    }


    public static String getPageStructure(Document document) throws XPathExpressionException {
        StringBuilder structureBuilder = new StringBuilder();
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.evaluate("//*[@clickable='true']", document, XPathConstants.NODESET);

        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            structureBuilder.append(item.getNodeName());
            structureBuilder.append("\n");
        }
        return structureBuilder.toString();
    }


}
