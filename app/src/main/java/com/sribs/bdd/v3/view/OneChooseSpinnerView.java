package com.sribs.bdd.v3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.popup.OneChoosePopupWindow;
import com.sribs.bdd.v3.util.LogUtils;

import java.util.List;

/**
 * create time: 2022/9/9
 * author: bruce
 * description:
 */
public class OneChooseSpinnerView extends LinearLayout {

    private Context mContext;

    private LinearLayout.LayoutParams layoutParams;
    private TextView mSpinnerText;
    private boolean mIsOpen = false;
    private OneChoosePopupWindow mOneChoosePopupWindow;
    private List<String> mSpinnerData;
    private ImageView mSpinnerArrow;
    private SpinnerCallback mSpinnerCallback;
    private int mSelectPosition;
    private View inflate;
    private int mSpinnerTextGravity = Gravity.CENTER;

    public OneChooseSpinnerView(Context context) {
        super(context);
        init(context);
    }

    public OneChooseSpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OneChooseSpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public OneChooseSpinnerView setSpinnerData(List<String> mSpinnerData) {
        this.mSpinnerData = mSpinnerData;
        return this;
    }

    public OneChooseSpinnerView setSpinnerTextGravity(int mSpinnerTextGravity) {
        this.mSpinnerTextGravity = mSpinnerTextGravity;
        return this;
    }

    public OneChooseSpinnerView setSpinnerCallback(SpinnerCallback mSpinnerCallback) {
        this.mSpinnerCallback = mSpinnerCallback;
        return this;
    }

    private void init(Context context){
        this.mContext = context;
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen._14sdp));
        setOrientation(VERTICAL);
    }

    public void build(){
        removeAllViews();
        inflate = View.inflate(mContext, R.layout.spinner_layout,null);
        inflate.findViewById(R.id.root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOneChoosePopupWindow == null){
                    mOneChoosePopupWindow = new OneChoosePopupWindow(mContext, getWidth(), mSpinnerData,
                            mSelectPosition, mSpinnerTextGravity ,mContext.getResources().getDimensionPixelSize(R.dimen._10sdp),
                            new OneChoosePopupWindow.PopupCallback(){
                        @Override
                        public void onSelect(String text) {
                            LogUtils.INSTANCE.d("onSelect: "+text);
                            mSelectPosition = mSpinnerData.indexOf(text);
                            if(mSpinnerText != null){
                                mSpinnerText.setText(text);
                            }
                            if(mSpinnerCallback != null){
                                mSpinnerCallback.onSelect(mSelectPosition);
                            }
                            chooseSpinnerStatus();
                        }
                    });
                }
                mOneChoosePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if(mSpinnerArrow != null){
                            mSpinnerArrow.setRotation(0f);
                        }
                    }
                });
                chooseSpinnerStatus();
            }
        });
        mSpinnerText = (TextView)inflate.findViewById(R.id.spinner_Text);
        mSpinnerText.setText(mSpinnerData.get(0));
        mSpinnerArrow = (ImageView)inflate.findViewById(R.id.spinner_arrow);

        addView(inflate,layoutParams);
    }

    public void setText(String text){
        if(mSpinnerText != null){
            mSpinnerText.setText(text);
        }
    }

    public void setSelect(int position){
        mSelectPosition = position;
        if(mOneChoosePopupWindow != null) {
            mOneChoosePopupWindow.setSelect(position);
        }
    }

    private void chooseSpinnerStatus(){
        if(!mIsOpen){
            mSpinnerArrow.setRotation(180f);
            mOneChoosePopupWindow.showAsDropDown(inflate);
            mIsOpen = true;
        }else {
            mSpinnerArrow.setRotation(0f);
            mOneChoosePopupWindow.dismiss();
            mIsOpen = false;
        }
    }

    public interface SpinnerCallback{
        void onSelect(int position);
    }
}
