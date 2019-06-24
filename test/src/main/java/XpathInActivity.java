import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 活动与该活动下元素xpath的集合
 */
public class XpathInActivity {
    private HashMap<String, Set<String>> map;
    public XpathInActivity() {
        map = new HashMap<>();
    }

    /**
     * 增加一个活动下的元素记录
     * @param activity 活动名
     * @param xpath 元素xpath
     * @return 是否已经有了
     */
    public boolean add(String activity, String xpath) {
        Set<String> set = this.map.getOrDefault(activity, null);
        if (set == null) {
            set = new HashSet<>();
            this.map.put(activity, set);
        }
        return set.add(xpath);
    }

}
