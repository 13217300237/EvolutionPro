package study.hank.com.annotation.facade.model;

import study.hank.com.annotation.facade.enums.RouteType;

/**
 * 容器：封装路由目的地信息基类
 */
public class RouteMeta {

    protected RouteType routeType;//区分路由类型
    protected Class destination;//之前map里面存的都是.class， 现在用一个字段来保存目的地class
    protected String path;//路由标识符，唯一

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public void setDestination(Class destination) {
        this.destination = destination;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public String getPath() {
        return path;
    }

    //先做一个Builder模式
    public RouteMeta() {
    }

    public RouteMeta routeType(RouteType routeType) {
        this.routeType = routeType;
        return this;
    }

    public RouteMeta destination(Class destination) {
        this.destination = destination;
        return this;
    }

    public RouteMeta path(String path) {
        this.path = path;
        return this;
    }

    public static RouteMeta getInstance() {
        return new RouteMeta();
    }

}
