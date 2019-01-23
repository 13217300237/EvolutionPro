package study.hank.com.api.core;

import android.app.Application;

import java.util.Set;

import study.hank.com.annotation.RouterConst;
import study.hank.com.annotation.facade.model.RouteMeta;
import study.hank.com.api.ClassUtils;
import study.hank.com.annotation.IRouterZ;
import study.hank.com.api.Postcard;
import study.hank.com.api.facade.template.IProvider;

/**
 * 路由的一些特殊逻辑，集中到这里
 */
public class LogisticsCenter {

    private static Application mContext;

    public static void init(Application context) {
        mContext = context;
        registerComm();
    }

    /**
     * 反射执行APT注册文件的注册方法
     */
    private static void registerComm() {
        try {
            Set<String> classNames = ClassUtils.getFileNameByPackageName(mContext, RouterConst.GENERATION_PACKAGE_NAME);//找到包名下的所有class
            for (String className : classNames) {
                Class<?> clz = Class.forName(className);
                if (IRouterZ.class.isAssignableFrom(clz)) {
                    IRouterZ iRouterComm = (IRouterZ) clz.getConstructor().newInstance();
                    iRouterComm.onLoad(Warehouse.routeMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Warehouse.traversalCommMap();
        }
    }

    /**
     * Postcard字段补全
     *
     * @param postcard
     */
    public static void complete(Postcard postcard) {
        if (null == postcard) {
            throw new RuntimeException("err:postcard 是空的，怎么搞的？");
        }

        RouteMeta routeMeta = Warehouse.routeMap.get(postcard.getPath());//
        if (null == routeMeta) {//如果路由meta是空，说明可能这个路由没注册，也有可能路由表没有去加载到内存中
            throw new RuntimeException("err:路由寻址失败，请检查是否path写错了");
        } else {
            postcard.setDestination(routeMeta.getDestination());
            postcard.setRouteType(routeMeta.getRouteType());

            switch (routeMeta.getRouteType()) {
                case PROVIDER://如果是数据接口Provider的话
                    Class<? extends IProvider> clz = (Class<? extends IProvider>) routeMeta.getDestination();
                    //从map中找找看
                    IProvider provider = Warehouse.providerMap.get(clz);
                    //如果没找到
                    if (null == provider) {
                        //执行反射方法创建，并且存入到map
                        try {
                            provider = clz.getConstructor().newInstance();
                            provider.init(mContext);
                            Warehouse.providerMap.put(clz, provider);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    postcard.setProvider(provider);
                    break;
                default:
                    break;
            }
        }
    }

    public static Postcard buildProvider(String name) {
        RouteMeta routeMeta = Warehouse.routeMap.get(name);
        if (null == routeMeta) {
            return null;
        } else {
            return new Postcard(routeMeta.getPath());
        }
    }
}
