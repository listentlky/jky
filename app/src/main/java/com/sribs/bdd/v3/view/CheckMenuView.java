package com.sribs.bdd.v3.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.module.CheckMenuModule;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/5
 * author: bruce
 * description:
 */
public class CheckMenuView extends LinearLayout {

    private Context mContext;

    private List<CheckMenuModule> menuModuleList = new ArrayList<>();

    private final int MENU_TYPE = 0;

    private final int ITEM_TYPE = 1;

    private final int MARK_TYPE = 2;

    public CheckMenuView(Context context) {
        super(context);
        this.mContext = context;
    }

    public CheckMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CheckMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public CheckMenuView setMenuModuleList(List<CheckMenuModule> menuModuleList) {
        this.menuModuleList = menuModuleList;
        return this;
    }

    public void build() {
        removeAllViews();
        setOrientation(VERTICAL);
        for (CheckMenuModule checkMenuModule : menuModuleList) {
            addView(addItemView(MENU_TYPE, checkMenuModule.getName()));
            addView(addLine());
            for (CheckMenuModule.Item item:checkMenuModule.getMenu()) {
                addView(addItemView(ITEM_TYPE, item.getName()));
                addView(addLine());
                for (CheckMenuModule.Item.Mark mark:item.getItem()) {
                    addView(addItemView(MARK_TYPE, mark.getName()));
                    addView(addLine());
                }
            }
        }
    }

    private View addItemView(int type, String name) {
        Log.d("111","addItemView: "+type+" ; "+name);
        switch (type) {
            case MENU_TYPE:
            TextView tv = new TextView(getContext());
            tv.setText(name);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen._10ssp));
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getContext().getResources().getDimensionPixelOffset(R.dimen._26sdp));
            tvParams.leftMargin = getContext().getResources().getDimensionPixelOffset(R.dimen._6sdp);
            tv.setLayoutParams(tvParams);
            return tv;
            case ITEM_TYPE:
                RelativeLayout itemLayout = new RelativeLayout(getContext());
                int padding = getContext().getResources().getDimensionPixelOffset(R.dimen._12sdp);
                itemLayout.setPadding(padding,0,padding/2,0);
                LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        getContext().getResources().getDimensionPixelOffset(R.dimen._18sdp));

                TextView tv2 = new TextView(getContext());
                tv2.setText(name);
                tv2.setGravity(Gravity.CENTER_VERTICAL);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen._8ssp));
                tv2.setTextColor(Color.parseColor("#FFFFFF"));
                RelativeLayout.LayoutParams tv2Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv2Params.addRule(RelativeLayout.CENTER_VERTICAL);
                itemLayout.addView(tv2,tv2Params);

                TextView tv3 = new TextView(getContext());
                tv3.setText("+");
                tv3.setGravity(Gravity.CENTER_VERTICAL);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen._8ssp));
                tv3.setTextColor(Color.parseColor("#FFFFFF"));
                RelativeLayout.LayoutParams tv3Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv3Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tv3Params.addRule(RelativeLayout.CENTER_VERTICAL);
                itemLayout.addView(tv3,tv3Params);

                itemLayout.setLayoutParams(itemLayoutParams);
                itemLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCheckMenuCallback != null){
                            mCheckMenuCallback.onClick(v);
                        }
                    }
                });

                return itemLayout;
            case MARK_TYPE:
                TextView tv4 = new TextView(getContext());
                tv4.setText(name);
                tv4.setGravity(Gravity.CENTER_VERTICAL);
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen._8ssp));
                tv4.setTextColor(Color.parseColor("#FFFFFF"));
                LinearLayout.LayoutParams tv4Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        getContext().getResources().getDimensionPixelOffset(R.dimen._18sdp));
                tv4Params.leftMargin = getContext().getResources().getDimensionPixelOffset(R.dimen._18sdp);
                tv4.setLayoutParams(tv4Params);
                return tv4;
        }
        return null;
    }

    private View addLine(){
        View line = new View(getContext());
        line.setBackgroundColor(Color.parseColor("#FFFFFF"));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                2);
        line.setLayoutParams(lineParams);
        return line;
    }

    private CheckMenuCallback mCheckMenuCallback;

    public CheckMenuView setCheckMenuCallback(CheckMenuCallback mCheckMenuCallback) {
        this.mCheckMenuCallback = mCheckMenuCallback;
        return this;
    }

    public interface CheckMenuCallback{
        void onClick(View v);
    }
}
