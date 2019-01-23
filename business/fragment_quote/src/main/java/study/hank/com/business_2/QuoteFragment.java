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

@ZRoute(RouterPathConst.PATH_FRAGMENT_QUOTE)
public class QuoteFragment extends Fragment {
    public QuoteFragment() {
    }

    public static QuoteFragment newInstance(Bundle bundle) {
        QuoteFragment quoteFragment = new QuoteFragment();
        quoteFragment.setArguments(bundle);
        return quoteFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quote, null);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
