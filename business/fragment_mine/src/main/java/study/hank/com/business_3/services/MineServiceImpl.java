package study.hank.com.business_3.services;

import android.content.Context;
import android.widget.Toast;

import study.hank.com.annotation.ZRoute;
import study.hank.com.common.RouterPathConst;

@ZRoute(RouterPathConst.PATH_PROVIDER_MINE) // 数据交互的Provider，必须用接口SimpleName来注册
public class MineServiceImpl implements MineOpenServiceApi {

    private Context mContext;

    @Override
    public String accountNo() {
        return "accountNo,hank zhou!";
    }

    @Override
    public void showAccountNo() {
        Toast.makeText(mContext, accountNo(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }
}
