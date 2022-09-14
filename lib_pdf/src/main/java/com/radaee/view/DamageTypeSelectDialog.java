package com.radaee.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.radaee.annotui.UIIconButton;
import com.radaee.pdf.Global;
import com.radaee.viewlib.R;

public class DamageTypeSelectDialog extends Dialog {

    Activity context;
    IClickCallback mIClickCallback;

    private ImageView mBtn01;
    private ImageView mBtn02;
    private ImageView mBtn03;
    private ImageView mBtn04;
    private ImageView mBtn05;
    private ImageView mBtn06;
    private ImageView mBtn07;
    private ImageView mBtn08;

    private CheckBox mCbx_01;
    private CheckBox mCbx_02;
    private CheckBox mCbx_03;
    private CheckBox mCbx_04;
    private CheckBox mCbx_05;
    private CheckBox mCbx_06;
    private CheckBox mCbx_07;
    private CheckBox mCbx_08;

    private Button mBtnOk;
    private Button mBtnCancel;

    public DamageTypeSelectDialog(Context context, IClickCallback iCallback) {
        super(context);

        this.context = (Activity) context;
        mIClickCallback = iCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.pop_damage_type_choice);

        mBtn01 = (ImageView) findViewById(R.id.btn_01);
        mBtn02 = (ImageView) findViewById(R.id.btn_02);
        mBtn03 = (ImageView) findViewById(R.id.btn_03);
        mBtn04 = (ImageView) findViewById(R.id.btn_04);
        mBtn05 = (ImageView) findViewById(R.id.btn_05);
        mBtn06 = (ImageView) findViewById(R.id.btn_06);
        mBtn07 = (ImageView) findViewById(R.id.btn_07);
        mBtn08 = (ImageView) findViewById(R.id.btn_08);

        mCbx_01 = (CheckBox) findViewById(R.id.cbx_01);
        mCbx_02 = (CheckBox) findViewById(R.id.cbx_02);
        mCbx_03 = (CheckBox) findViewById(R.id.cbx_03);
        mCbx_04 = (CheckBox) findViewById(R.id.cbx_04);
        mCbx_05 = (CheckBox) findViewById(R.id.cbx_05);
        mCbx_06 = (CheckBox) findViewById(R.id.cbx_06);
        mCbx_07 = (CheckBox) findViewById(R.id.cbx_07);
        mCbx_08 = (CheckBox) findViewById(R.id.cbx_08);

        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);

//        setIcon(mBtn01, 0);
//        setIcon(mBtn02, 13);
//        setIcon(mBtn03, 1);
//        setIcon(mBtn04, 15);
//        setIcon(mBtn05, 6);
//        setIcon(mBtn06, 5);
//        setIcon(mBtn07, 3);
//        setIcon(mBtn08, 12);

        mCbx_01.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(1, b);
                if(b){
                    mCbx_02.setChecked(false);
                    mCbx_03.setChecked(false);
                    mCbx_04.setChecked(false);
                    mCbx_05.setChecked(false);
                    mCbx_06.setChecked(false);
                    mCbx_07.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(2, b);
                if(b){
                    mCbx_01.setChecked(false);
                    mCbx_03.setChecked(false);
                    mCbx_04.setChecked(false);
                    mCbx_05.setChecked(false);
                    mCbx_06.setChecked(false);
                    mCbx_07.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_03.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(3, b);
                if(b){
                    mCbx_01.setChecked(false);
                    mCbx_02.setChecked(false);
                    mCbx_04.setChecked(false);
                    mCbx_05.setChecked(false);
                    mCbx_06.setChecked(false);
                    mCbx_07.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_04.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(4, b);
                if(b){
                    mCbx_01.setChecked(false);
                    mCbx_02.setChecked(false);
                    mCbx_03.setChecked(false);
                    mCbx_05.setChecked(false);
                    mCbx_06.setChecked(false);
                    mCbx_07.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_05.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(5, b);
                if(b){
                    mCbx_01.setChecked(false);
                    mCbx_02.setChecked(false);
                    mCbx_03.setChecked(false);
                    mCbx_04.setChecked(false);
                    mCbx_06.setChecked(false);
                    mCbx_07.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_06.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(6, b);
                if(b){
                    mCbx_01.setChecked(false);
                    mCbx_02.setChecked(false);
                    mCbx_03.setChecked(false);
                    mCbx_04.setChecked(false);
                    mCbx_05.setChecked(false);
                    mCbx_07.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_07.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(7, b);
                if(b){
                    mCbx_01.setChecked(false);
                    mCbx_02.setChecked(false);
                    mCbx_03.setChecked(false);
                    mCbx_04.setChecked(false);
                    mCbx_05.setChecked(false);
                    mCbx_06.setChecked(false);
                    mCbx_08.setChecked(false);
                }
            }
        });

        mCbx_08.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mIClickCallback.onViewClicked(8, b);
                mCbx_01.setChecked(false);
                mCbx_02.setChecked(false);
                mCbx_03.setChecked(false);
                mCbx_04.setChecked(false);
                mCbx_05.setChecked(false);
                mCbx_06.setChecked(false);
                mCbx_07.setChecked(false);
            }
        });

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mCbx_01.isChecked() && !mCbx_02.isChecked() && !mCbx_03.isChecked() && !mCbx_04.isChecked() && !mCbx_05.isChecked() && !mCbx_06.isChecked() && !mCbx_07.isChecked() && !mCbx_08.isChecked()){
                    Toast.makeText(getContext(),"未选择损伤类型", Toast.LENGTH_LONG);
                    return;
                }

                if(mCbx_01.isChecked()){
                    mIClickCallback.onViewClicked(0, true, 16711680);
                }else if(mCbx_02.isChecked()){
                    mIClickCallback.onViewClicked(1, true, 16742912);
                }else if(mCbx_03.isChecked()){
                    mIClickCallback.onViewClicked(2, true, 16776960);
                }else if(mCbx_04.isChecked()){
                    mIClickCallback.onViewClicked(3, true, 65408);
                }else if(mCbx_05.isChecked()){
                    mIClickCallback.onViewClicked(4, true, 255);
                }else if(mCbx_06.isChecked()){
                    mIClickCallback.onViewClicked(5, true, 16711935);
                }else if(mCbx_07.isChecked()){
                    mIClickCallback.onViewClicked(6, true, 16751359);
                }else if(mCbx_08.isChecked()){
                    mIClickCallback.onViewClicked(7, true, 0);
                }

                dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickCallback.onViewClicked(-1, true, -1);
                dismiss();
            }
        });
    }

    public interface IClickCallback {

        void onViewClicked(int selectIndex, boolean checked, int color);

    }

    private void setIcon(ImageView iview, int icon)
    {
        Log.i("leon","DamageTypeSelectDialog setIcon icon=" + icon);
        Bitmap bmp = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(-1);

        Global.drawAnnotIcon(1, icon, bmp);
        iview.setImageBitmap(bmp);
    }
}
