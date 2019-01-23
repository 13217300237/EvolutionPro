package study.hank.com.api.facade.template;

import android.content.Context;

/**
 * IProvider，为模块之间提供数据交互的接口
 */
public interface IProvider {
    void init(Context context);
}
