package com.sribs.bdd.v3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.module.RelativeHDiffTableModule;
import com.sribs.bdd.v3.popup.OneChoosePopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * create time: 2022/9/8
 * author: bruce
 * description:
 */
public class CheckHTableView extends LinearLayout {

    private Context mContext;

    private List<RelativeHDiffTableModule> datas= new ArrayList();

    private List<View> mTableViews = new ArrayList<>();

    private LinearLayout.LayoutParams layoutParams;

    public CheckHTableView(Context context) {
        super(context);
        init(context);
    }

    public CheckHTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckHTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CheckHTableView setDatas(List<RelativeHDiffTableModule> datas) {
        this.datas = datas;
        return this;
    }

    private void init(Context context){
        this.mContext = context;
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen._15sdp));
        setOrientation(VERTICAL);
        initTitleTable();
    }

    public void build(){
        removeAllViews();
        mTableViews.clear();
        initTitleTable();
    }

    public void initTitleTable(){
        View inflate = View.inflate(mContext, R.layout.check_table_item,null);
        inflate.findViewById(R.id.table_dh_text).setEnabled(false);
        inflate.findViewById(R.id.check_but_zd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        inflate.findViewById(R.id.check_but_delete).setEnabled(false);
        addView(inflate,layoutParams);
    }

    public void addItem(){
       /* RelativeHDiffTableModule relativeHDiffTableModule = new RelativeHDiffTableModule();
        datas.add(relativeHDiffTableModule);*/
        View inflate = View.inflate(mContext, R.layout.check_table_item,null);
        mTableViews.add(inflate);
        inflate.findViewById(R.id.check_but_zd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        inflate.findViewById(R.id.check_but_delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(inflate);
                mTableViews.remove(inflate);
                for (int i = 0; i < mTableViews.size(); i++) {
                    ((TextView)mTableViews.get(i).findViewById(R.id.table_dh_text)).setText("TP"+(i+1));
                }

            }
        });
        TextView dhText = (TextView) inflate.findViewById(R.id.table_dh_text);
        dhText.setText("TP"+mTableViews.size());
        dhText.setSelected(true);
        dhText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new OneChoosePopupWindow(mContext, dhText.getWidth()-10, Arrays.asList("闭合点"),
                        -1, Gravity.CENTER,0,new OneChoosePopupWindow.PopupCallback() {
                    @Override
                    public void onSelect(String text) {
                        dhText.setText(text);
                    }
                }).showAsDropDown(dhText,5,2);
            }
        });
        addView(inflate,layoutParams);
    }
}
