package study.hank.com.api.core;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import study.hank.com.annotation.facade.model.RouteMeta;
import study.hank.com.api.facade.template.IProvider;

/**
 * 货舱，存放 所有map
 */
public class Warehouse {

    public static Map<String, RouteMeta> routeMap = new HashMap<>();//用routes来存，不去具体区分Activity，还是Fragment,或者是Provider

    public static Map<Class, IProvider> providerMap = new HashMap<>();//专门用来存放Provider对象，防止没必要地重复创建

    //*************************************辅助方法************************************
    public static void traversalCommMap() {
        for (String key : routeMap.keySet()) {
            Log.d("traversalMap", key + "   --------   " + routeMap.get(key).getDestination() + "-" + routeMap.get(key).getRouteType());
        }
    }

    public static void clear() {
        routeMap.clear();
        providerMap.clear();
    }
}
