package study.hank.com.activity_chart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import study.hank.com.annotation.ZRoute;
import study.hank.com.common.RouterPathConst;

@ZRoute(RouterPathConst.PATH_ACTIVITY_OTHER)
public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
    }
}
