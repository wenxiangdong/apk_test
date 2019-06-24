package utils;

import com.google.common.collect.ImmutableSet;

public class Constants {
    /**
     * 导航栏 后退按钮的名称
     */
    public static final String NAVIGATOR_NAME = "Navigate up";

    // [checkable, checked, {class,className}, clickable, {content-desc,contentDescription}, enabled, focusable, focused, {long-clickable,longClickable}, package, password, {resource-id,resourceId}, scrollable, selection-start, selection-end, selected, {text,name}, bounds, displayed, contentSize]
    public static final ImmutableSet<String> ATTRIBUTES = ImmutableSet.of(
            "class",
            "text",
            "name",
            "resource-id",
            "clickable",
            "enabled",
            "bounds"
    );
}
