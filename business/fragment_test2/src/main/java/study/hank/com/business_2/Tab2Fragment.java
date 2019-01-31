package study.hank.com.business_2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import study.hank.com.annotation.ZRoute;
import study.hank.com.common.RouterPathConst;

@ZRoute(RouterPathConst.PATH_FRAGMENT_TAB2)
public class Tab2Fragment extends Fragment {
    public Tab2Fragment() {
    }

    public static Tab2Fragment newInstance(Bundle bundle) {
        Tab2Fragment tab2Fragment = new Tab2Fragment();
        tab2Fragment.setArguments(bundle);
        return tab2Fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab2, null);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
