package study.hank.com.business_3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MineFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine);

        MineFragment mineFragment = MineFragment.newInstance(null);
        showFragment(mineFragment);
    }

    //fragment相关
    private FragmentManager mFragmentManager;//fragment管理器
    private FragmentTransaction mFragmentTransaction;//fragment切换器
    private Fragment mCurrentFragment;//标记当前已经打开的fragment

    private void showFragment(Fragment fragment) {

        //如果参数fragment就是当前这个fragment，那就不用行动了
        if (mCurrentFragment != null && fragment.getClass().getSimpleName().equals(mCurrentFragment.getClass().getSimpleName())) {
            return;
        }

        if (mFragmentManager == null)
            mFragmentManager = this.getSupportFragmentManager();//fragment管理器是从当前activity中获取的
        mFragmentTransaction = mFragmentManager.beginTransaction();//从管理器中获得事务

        String fragmentClassName = fragment.getClass().getSimpleName();
        if (null == mFragmentManager.findFragmentByTag(fragmentClassName)) {//查找当前的fragment管理器，防止重复创建浪费内存
            mFragmentTransaction.add(R.id.content_frame, fragment, fragmentClassName);//如果管理器中没有找到，则添加； 此处3个参数，第一个是容器的id，第二个是要添加的的fragment，第三个是 fragment类名(做标记用的,防止重复创建)
        }

        if (null != mCurrentFragment) {//检查是否已有显示出来的fragment
            mFragmentTransaction.hide(mCurrentFragment);//有，则先隐藏
        }

        mFragmentTransaction.show(fragment);//再显示我这次指定的fragment
        mFragmentTransaction.commitAllowingStateLoss();
        // 提交事务(这里，commit和commitAllowingStateLoss的区别是，后者可以在用户离开这个Activity时，
        // 这个Fragment的状态将会被保存。如果在保存之前，执行了commit，那可以正常commit。
        // 但是如果在保存之后，调用commit，就会跑出一个异常，这是因为当activity再次恢复时，这个fragment的状态将会丢失。
        // 而，如果业务上不在乎丢失的状态，调用commitAllowingStateLoss即可)

        mCurrentFragment = fragment;//最后赋值给fragment的标记
    }
}
