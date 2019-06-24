import com.google.common.collect.ImmutableSet;
import io.appium.java_client.android.AndroidElement;
import utils.LogUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ElementInActivity {
    private LogUtil logger = LogUtil.getLogger(ElementInActivity.class);
    private HashMap<String, Set<String>> map;
    private Set<AndroidElement> elementSet;
    // [checkable, checked, {class,className}, clickable, {content-desc,contentDescription}, enabled, focusable, focused, {long-clickable,longClickable}, package, password, {resource-id,resourceId}, scrollable, selection-start, selection-end, selected, {text,name}, bounds, displayed, contentSize]
    private final ImmutableSet<String> ATTRIBUTES = ImmutableSet.of(
            "class",
            "text",
            "name",
            "resource-id",
            "checkable",
            "clickable",
            "content-desc",
            "enabled",
            "bounds"
    );
    public ElementInActivity() {
        map = new HashMap<>();
        elementSet = new HashSet<>();
        logger.close();
    }

    /**
     * 增加一个活动下的元素记录
     * @param activity 活动名
     * @return 是否已经有了
     */
    public boolean add(String activity, AndroidElement element) {
        return this.add(activity, getElementID(element));
    }


    public boolean add(String activity, String element) {
        Set<String> set = this.map.getOrDefault(activity, null);
        if (set == null) {
            set = new HashSet<>();
            this.map.put(activity, set);
        }
        return set.add(element);
    }


    public boolean contains(String activity, AndroidElement element) {
        return contains(activity, getElementID(element));
    }

    public boolean contains(String activity, String element) {
        Set<String> set = this.map.getOrDefault(activity, null);
        return set != null && set.contains(element);
    }

    public String getElementID(AndroidElement element) {
        StringBuilder builder = new StringBuilder();
        for (String attr : ATTRIBUTES) {
            String value = element.getAttribute(attr);
            builder.append(attr)
                    .append(":")
                    .append(value)
                    .append(",");
        }
        logger.info(builder.toString());
        return builder.toString();
    }
}
