package com.radaee.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.radaee.viewlib.R;

/**
 * create time: 2022/9/27
 * author: bruce
 * description:
 */
public class RHDiffAddPointPopupWindow extends PopupWindow {

    private String group;
    private String pointName;

    private String mAnnotName;

    private String mColorBg;

    private EditText mGroupEdit;

    private EditText mPointEdit;

    private TextView mCancel,mConfirm,mDelete;

    private boolean isClickDismiss;

    public RHDiffAddPointPopupWindow(Context context,int width,RHDiffAddPointCallback rhDiffAddPointCallback) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.rhd_add_point_layout,null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
    //    setOutsideTouchable(false);
        setFocusable(true);
        setWidth(width);
        mGroupEdit = view.findViewById(R.id.check_zm_edit);
        mPointEdit = view.findViewById(R.id.check_cdm_edit);
        mCancel = view.findViewById(R.id.rhd_add_point_cancel);
        mConfirm = view.findViewById(R.id.rhd_add_point_confirm);
        mDelete = view.findViewById(R.id.rhd_add_point_delete);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickDismiss = true;
                dismiss();
                if(rhDiffAddPointCallback != null){
                    rhDiffAddPointCallback.onCancel();
                }
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mGroupEdit.getText())){
                    Toast.makeText(context,"请输入组名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(mPointEdit.getText())){
                    Toast.makeText(context,"请输入测点名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(rhDiffAddPointCallback != null){
                    rhDiffAddPointCallback.onAddPoint(mGroupEdit.getText().toString(),mPointEdit.getText().toString(),mAnnotName,mColorBg);
                }
                isClickDismiss = true;
                dismiss();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rhDiffAddPointCallback != null){
                    rhDiffAddPointCallback.onDeletePoint(mGroupEdit.getText().toString(),mPointEdit.getText().toString(),mAnnotName);
                }
                isClickDismiss = true;
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        if(isClickDismiss) {
            super.dismiss();
            isClickDismiss = false;
        }
    }

    public void setInfo(String group, String pointName,String annotName,String colorBg,boolean isDelete){
        Log.d("bruce","setInfo: "+group+" "+pointName+" ");
        this.group = group;
        this.pointName = pointName;
        this.mAnnotName = annotName;
        this.mColorBg = colorBg;
        if(mGroupEdit != null){
            mGroupEdit.setText(group);
        }
        if(mPointEdit!= null){
            mPointEdit.setText(pointName);
        }
        if(isDelete){
            if(mDelete != null){
                mDelete.setVisibility(View.VISIBLE);
            }
        }else {
            if(mDelete != null) {
                mDelete.setVisibility(View.GONE);
            }
        }
    }

    public interface RHDiffAddPointCallback{

        void onCancel();

        void onAddPoint(String group,String point,String annotName,String colorBg);

        void onDeletePoint(String group,String point,String annotName);
    }

}
