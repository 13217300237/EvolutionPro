package study.hank.com.api;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import study.hank.com.api.core.LogisticsCenter;

public class ZRouter {

    //路由，在一个app中只能存在一个实例，所以，作为单例来写
    private ZRouter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private volatile static ZRouter mInstance;

    private static Application mContext;

    public static ZRouter getInstance() {
        if (mInstance == null) {//外层if判空，是为了防止没有意义的判定锁，略微增加程序效率。因为如果没有这一层判定，每一次getInstance都会判定锁是否被占用,造成没必要的效率降低
            synchronized (ZRouter.class) {//对Router加锁，是为了让同一时间只有一个线程能够访问这个类
                if (mInstance == null) {//能同一时间进到这里的只有一个线程，这个县城判定静态变量是不是空，是空，则创建新对象
                    mInstance = new ZRouter();
                }
            }
        }
        return mInstance;
    }

    /**
     * 路由 初始化
     * <p>
     * @param context
     */
    public static void initRegister(Application context) {
        //注册所有该注册的Activity
        mContext = context;
        LogisticsCenter.init(context);//进行Activity，Fragment的统一注册
    }

    /**
     * @param mainActivity         Fragment切换依赖Activity，所以，activity的引用要传进来
     * @param contentFrame         Fragment的容器
     * @param onFragmentChangedLis 监听器，如果Fragment在子模块中发生了切换，有可能会对主界面下方的导航栏造成影响，所以这里可能要传入一个监听器
     */
    public static void initFragmentParameters(AppCompatActivity mainActivity, int contentFrame, OnFragmentChangedLis onFragmentChangedLis) {
        mActivity = mainActivity;
        mContentFrame = contentFrame;
        mOnFragmentChangedLis = onFragmentChangedLis;
    }

    /**
     * Fragment发生切换时的监听器
     */
    public interface OnFragmentChangedLis {
        /**
         * 回传 routerPath ，主界面看情况处理
         *
         * @param routePath
         */
        void setTab(String routePath);
    }

    private static OnFragmentChangedLis mOnFragmentChangedLis;


    //fragment相关
    private static AppCompatActivity mActivity;//用于Fragment切换的主界面
    private static FragmentManager mFragmentManager;//fragment管理器
    private static FragmentTransaction mFragmentTransaction;//fragment切换器
    private static Fragment mCurrentFragment;//标记当前已经打开的fragment
    private static int mContentFrame;

    //释放Fragment相关资源
    public static void release() {
        mActivity = null;
        mFragmentManager = null;
        mFragmentTransaction = null;
        mCurrentFragment = null;
        mContentFrame = -1;
        mOnFragmentChangedLis = null;
    }

    //build出一个Postcard
    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("error:传入的path怎么可以为空呢？小老弟!");
        } else {
            return new Postcard(path);
        }
    }

    /**
     * 跳转的唯一方法
     *
     * @param postcard
     * @return
     */
    Object navigation(Postcard postcard) {
        LogisticsCenter.complete(postcard);//这里拿到的postcard可能是数据不完整的，所以，调用LogisticsCenter的complete方法对属性进行完善，以便于下面的跳转或者其他操作
        switch (postcard.getRouteType()) {
            case ACTIVITY://如果是Activity，那就跳吧
                return startActivity(postcard);
            case FRAGMENT://如果是Fragment，那就切换吧
                return switchFragment(postcard);
            case PROVIDER://如果是Provider，那就执行业务逻辑
                return postcard.getProvider();//那就直接返回provider对象
            default:
                break;
        }
        return null;
    }


    /**
     * 重载一个navigation方法，用来进行组件之间的通信
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T navigation(Class<? extends T> service) {
        return navigation(service.getSimpleName());
    }

    /**
     * 那就再重载一个navigation方法，用来进行组件之间的通信.....戏好多啊你navigation
     *
     * @param serviceName
     * @param <T>
     * @return
     */
    public <T> T navigation(String serviceName) {
        Postcard postcard = LogisticsCenter.buildProvider(serviceName);
        if (null == postcard)
            return null;
        LogisticsCenter.complete(postcard);//补全postcard字段值
        return (T) postcard.getProvider();
    }


    private static Handler mHandler;

    private void runInMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    private Object startActivity(Postcard postcard) {
        Class<?> cls = postcard.getDestination();
        if (cls == null) {
            if (cls == null)
                throw new RuntimeException("没找到对应的activity，请检查路由寻址标识是否写错");
        }
        final Intent intent = new Intent(mContext, cls);
        if (Postcard.FLAG_DEFAULT != postcard.getFlag()) {//如果不是初始值，也就是说，flag值被更改过，那就用更改后的值
            intent.setFlags(postcard.getFlag());
        } else {//如果沒有设定启动模式，即 flag值没有被更改，就用常规模式启动
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//常规模式启动Activity
        }
        //跳转只能在主线程中进行
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mContext.startActivity(intent);
            }
        });
        return null;
    }

    private Object switchFragment(Postcard postcard) {
        if (mActivity == null) {
            throw new RuntimeException("请检查 initFragmentParameters()方法是否没有被调用");
        }

        if (mFragmentManager == null)
            mFragmentManager = mActivity.getSupportFragmentManager();//fragment管理器是从当前activity中获取的

        Class clz = postcard.getDestination();
        if (clz == null)
            throw new RuntimeException("没找到对应的fragment，请检查路由寻址标识是否写错");

        Fragment fragment = getFragment(clz);
        if (fragment == null) {
            throw new RuntimeException("反射创建fragment失败");
        }

        fragment.setArguments(postcard.getBundle());

        //如果参数fragment就是当前这个fragment，那就不用行动了
        if (mCurrentFragment != null && fragment.getClass().getSimpleName().equals(mCurrentFragment.getClass().getSimpleName())) {
            Log.d("showFragmentTag", "当前已经显示的是" + fragment.getClass().getSimpleName() + ";所以不用切换了");
            return null;
        }
        if (mOnFragmentChangedLis != null)
            mOnFragmentChangedLis.setTab(postcard.getPath());//如果fragment发生切换，那么可能底部tab也有可能切换， 所以这里也要执行
        mFragmentTransaction = mFragmentManager.beginTransaction();//从管理器中获得事务
        String fragmentClassName = fragment.getClass().getSimpleName();
        if (null == mFragmentManager.findFragmentByTag(fragmentClassName)) {//查找当前的fragment管理器，防止重复创建浪费内存
            mFragmentTransaction.add(mContentFrame, fragment, fragmentClassName);//如果管理器中没有找到，则添加； 此处3个参数，第一个是容器的id，第二个是要添加的的fragment，第三个是 fragment类名(做标记用的,防止重复创建)
        }
        if (null != mCurrentFragment) {//检查是否已有显示出来的fragment
            mFragmentTransaction.hide(mCurrentFragment);//有，则先隐藏
        }
        mFragmentTransaction.show(fragment);//再显示我这次指定的fragment
        mFragmentTransaction.commitAllowingStateLoss();
        mCurrentFragment = fragment;//最后赋值给fragment的标记

        return null;
    }

    /**
     * 通过class反射取得fragment对象
     *
     * @param cls
     * @return
     */
    private Fragment getFragment(Class cls) {
        if (mFragmentManager != null) {
            Fragment fragment = mFragmentManager.findFragmentByTag(cls.getSimpleName());//先从fragmentManager里面找，如果找到了，就直接用
            if (null == fragment) {//如果没找到，就自己创建
                Log.d("getFragmentTag", "FragmentManager中没找到 " + cls.getSimpleName() + "，现在反射创建");
                try {
                    Class<?> clsX = Class.forName(cls.getName());
                    fragment = (Fragment) clsX.newInstance();//反射创建，只需要传入class
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("getFragmentTag", "已经在FragmentManager中找到 " + cls.getSimpleName() + ",无须反射创建");
            }
            return fragment;
        }
        return null;
    }
}


