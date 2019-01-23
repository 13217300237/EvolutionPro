package study.hank.com.api;

import android.os.Bundle;

import study.hank.com.annotation.facade.model.RouteMeta;
import study.hank.com.api.facade.template.IProvider;

/**
 * 用于封装 目的地的各种操作，比如跳转Activity，切换Fragment，或者执行 业务模块暴露出来的service
 */
public class Postcard extends RouteMeta {


    private IProvider provider;//为了模块间数据交互而预备的属性，先放着

    private Bundle bundle;// activity跳转的时候有可能会携带参数，这里先预留一个属性
    private int flag = FLAG_DEFAULT;// Activity的启动模式，在java里面是用int值表示的，所以这里也留个字段

    public static final int FLAG_DEFAULT = -1;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int mFlag) {
        this.flag = mFlag;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public IProvider getProvider() {
        return provider;
    }

    public void setProvider(IProvider provider) {
        this.provider = provider;
    }

    public Postcard(String path) {
        this.path = path;
    }

    /**
     * 开始执行任务，是跳转Activity呢？还是切换Fragment呢？还是 执行业务模块暴露的服务呢。。。
     */
    public Object navigation() {
        return ZRouter.getInstance().navigation(this);
    }
}
