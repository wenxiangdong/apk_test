import java.util.ArrayList;
import java.util.List;

public class MyNode {
    String xpath;
    MyNode child;
    MyNode right;

    public MyNode(String xpath) {
        this.xpath = xpath;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public MyNode getChild() {
        return child;
    }

    public void setChild(MyNode child) {
        this.child = child;
    }

    public MyNode getRight() {
        return right;
    }

    public void setRight(MyNode right) {
        this.right = right;
    }
}
