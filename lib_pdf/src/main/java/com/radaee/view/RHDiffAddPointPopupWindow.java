package com.radaee.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.radaee.viewlib.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * create time: 2022/9/27
 * author: bruce
 * description:
 */
public class RHDiffAddPointPopupWindow extends PopupWindow {

    private String group;
    private String pointName;
    private Long mAnnotRef= -1L;

    private EditText mGroupEdit;

    private EditText mPointEdit;

    private TextView mCancel,mConfirm,mDelete;

    public RHDiffAddPointPopupWindow(Context context,int width,Long annotRef,RHDiffAddPointCallback rhDiffAddPointCallback) {
        super(context);
        this.mAnnotRef = annotRef;
        View view = LayoutInflater.from(context).inflate(R.layout.rhd_add_point_layout,null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
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
                dismiss();
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
                    rhDiffAddPointCallback.onAddPoint(mGroupEdit.getText().toString(),mPointEdit.getText().toString(),mAnnotRef);
                }
                dismiss();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rhDiffAddPointCallback != null){
                    rhDiffAddPointCallback.onDeletePoint(mGroupEdit.getText().toString(),mPointEdit.getText().toString(),mAnnotRef);
                }
                dismiss();
            }
        });
    }

    //显示虚拟键盘
    public static void ShowKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );

        imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);

    }

    public void setInfo(String group,String pointName,Long annotRef){
        Log.d("bruce","setInfo: "+group+" "+pointName+" "+annotRef);
        this.group = group;
        this.pointName = pointName;
        this.mAnnotRef = annotRef;
        if(mGroupEdit != null){
            mGroupEdit.setText(group);
        }
        if(mPointEdit!= null){
            mPointEdit.setText(pointName);
        }
    }

    public interface RHDiffAddPointCallback{

        void onAddPoint(String group,String point,Long annotRef);

        void onDeletePoint(String group,String point,Long annotRef);
    }
}
