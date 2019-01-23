package com.example.gc_hank.evolutionpro.ui.bottom;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gc_hank.evolutionpro.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 底部导航栏专用RecyclerView.Adapter示例
 */
public class BottomAdapter extends RecyclerView.Adapter {

    private List<TabTitle> mTitleObjectList;//
    private int mSelectedPosition = 0;
    private OnItemClickListener onItemClickListener;

    /**
     * 对外接口
     */
    public interface OnItemClickListener {

        /**
         * @param position  位置
         * @param routePath 路由寻址标识
         */
        void onItemClick(int position, String routePath);
    }

    private Activity mActivity;
    private int itemWidth;

    public BottomAdapter(Activity activity, List<TabTitle> titleObjectList) {
        mActivity = activity;
        mTitleObjectList = titleObjectList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.adapter_list_item_bottom_title, null);
        initItemWidth();
        return new BottomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        BottomHolder bHolder = (BottomHolder) viewHolder;

        int titleNameId = mTitleObjectList.get(position).getTitleName();
        Drawable titleIconDrawable = mTitleObjectList.get(position).getDrawable();

        bHolder.tvTitle.setTextColor(mActivity.getResources().getColorStateList(mTitleObjectList.get(position).getTextColorStateList()));
        bHolder.tvTitle.setText(mActivity.getResources().getString(titleNameId));

        bHolder.ivIcon.setImageDrawable(titleIconDrawable);

        bHolder.llMain.getLayoutParams().width = itemWidth;
        bHolder.llMain.getLayoutParams().height = (int) mActivity.getResources().getDimension(R.dimen.height_b);

        bHolder.llMain.requestLayout();//由于要强制等分，所以，要重新布局一下，调整宽高

        if (mSelectedPosition == position) {// 如果当前tab被选中了
            bHolder.ivIcon.setSelected(true);
            bHolder.tvTitle.setSelected(true);
        } else {
            bHolder.ivIcon.setSelected(false);
            bHolder.tvTitle.setSelected(false);
        }

        bHolder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(position);//先改变当前的item的选中情况
                if (null != onItemClickListener)
                    onItemClickListener.onItemClick(position, mTitleObjectList.get(position).getRoutePath());//再执行外界传入的点击事件
            }
        });
    }

    /**
     * 让每一个item等分
     */
    private void initItemWidth() {
        WindowManager windowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);// 得到当前屏幕的宽度，然后分成getItemCount等分,让每个item平均分配
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int w = metrics.widthPixels;//这就是宽度像素值
        itemWidth = w / getItemCount();
    }

    @Override
    public int getItemCount() {
        return mTitleObjectList == null ? 0 : mTitleObjectList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class BottomHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.ll_main)
        LinearLayout llMain;

        public BottomHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //```````````以下全都是对外接口····················//
    public void setSelection(int position) {
        this.mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}


