package study.hank.com.annotation.facade.enums;

/**
 * 路由框架，支持ACTIVITY,FRAGMENT,PROVIDER 三种信息互通格式
 * <p>
 * 现在既然有了三种格式的定义，那么，我在路由表注册的时候，就不用区分ACTIVITY,FRAGMENT,PROVIDER了，对这3种东西进行再次封装
 */
public enum RouteType {
    ACTIVITY("android.app.Activity"),
    FRAGMENT("android.app.Fragment"),
    FRAGMENT_V4("android.support.v4.app.Fragment"),
    PROVIDER("study.hank.com.api.facade.template.IProvider"),
    UNKNOWN("Unknown route type");

    private String className;//提供一个全限定类名

    public String getClassName() {
        return className;
    }

    RouteType(String className) {
        this.className = className;
    }
}
