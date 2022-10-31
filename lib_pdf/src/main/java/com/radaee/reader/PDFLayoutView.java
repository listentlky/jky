package com.radaee.reader;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.radaee.annotui.UIAnnotDlgSign;
import com.radaee.annotui.UIAnnotDlgSignProp;
import com.radaee.annotui.UIAnnotMenu;
import com.radaee.annotui.UIAnnotPopEdit;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Ink;
import com.radaee.pdf.Matrix;
import com.radaee.pdf.Page;
import com.radaee.pdf.Page.Annotation;
import com.radaee.pdf.Path;
//import com.radaee.sribs.DamageTypeAdapter;
//import com.radaee.sribs.DamageTypeItem;
import com.radaee.util.ComboList;
import com.radaee.util.CommonUtil;
import com.radaee.util.PopupEditAct;
import com.radaee.view.DamageTypeSelectDialog;
import com.radaee.view.ILayoutView;
import com.radaee.view.PDFLayout;
import com.radaee.view.PDFLayout.LayoutListener;
import com.radaee.view.PDFLayout.PDFPos;
import com.radaee.view.PDFLayoutDual;
import com.radaee.view.PDFLayoutHorz;
import com.radaee.view.PDFLayoutVert;
import com.radaee.view.RHDiffAddPointPopupWindow;
import com.radaee.view.V3DamagePopupWindow;
import com.radaee.view.VPage;
import com.radaee.view.VSel;
import com.radaee.viewlib.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDFLayoutView extends View implements ILayoutView, LayoutListener {

    static final protected int STA_NONE = 0;
    static final protected int STA_ZOOM = 1;
    static final protected int STA_SELECT = 2;
    static final protected int STA_INK = 3;
    static final protected int STA_RECT = 4;
    static final protected int STA_ELLIPSE = 5;
    static final protected int STA_NOTE = 6;
    static final protected int STA_LINE = 7;
    static final protected int STA_STAMP = 8;
    static final protected int STA_EDITBOX = 9;
    static final protected int STA_POLYGON = 10;
    static final protected int STA_POLYLINE = 11;
    static final protected int STA_ANNOT = 100;
    protected Bitmap.Config m_bmp_format = Bitmap.Config.ALPHA_8;
    public PDFLayout m_layout;
    private Document m_doc;
    protected int m_status = STA_NONE;
    private boolean m_zooming = false;
    private int m_pageno = 0;
    protected PDFPos m_goto_pos = null;

    protected GestureDetector m_gesture;
    private Annotation m_annot = null;
    private PDFPos m_annot_pos = null;
    private Page m_annot_pg = null;
    private VPage m_annot_page = null;
    private float[] m_annot_rect;
    private float[] m_annot_rect0;
    private float m_annot_x0;
    private float m_annot_y0;

    private float m_annot_note_x0;
    private float m_annot_note_y0;

    private boolean mReadOnly = false;
    private Ink m_ink = null;
    private Path m_polygon;
    private Bitmap m_icon = null;
    private Document.DocImage m_dicon = null;
    private float[] m_rects;
    private VPage[] m_note_pages;
    private int[] m_note_indecs;
    private ILayoutView.PDFLayoutListener m_listener;
    private VSel m_sel = null;
    private int m_edit_type = 0;
    private int m_combo_item = -1;
    private UIAnnotPopEdit m_pEdit = null;
    private PopupWindow m_pCombo = null;
    private UIAnnotMenu m_aMenu = null;
    private Bitmap m_sel_icon1 = null;
    private Bitmap m_sel_icon2 = null;
    private PDFLayoutOPStack m_opstack = new PDFLayoutOPStack();
    //leon add start
    private int mNewAnnotColor = 0;
    private MotionEvent mCacheMotionEvent = null;

    //v3
    private boolean mIsV3 = false;
    //是否展示损伤
    private boolean isShowDamage = true;

    public List<String> mModuleType = Arrays.asList("建筑结构复核", "倾斜测量", "相对高差测量", "构件检测");

    private String mCurrentModuleType = "建筑结构复核"; //默认模块

    private List<String> mV3DamageType = Arrays.asList("层高", "轴网");

    public float mMarkX, mMarkY;

    public PDFPos mMarkPDFPos;
    private boolean mIsIntercept;
    private int layoutWidth,layoutHieght;

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public int getLayoutHieght() {
        return layoutHieght;
    }

    public float[] getM_rects() {
        return m_rects;
    }

    public PDFPos getMarkPDFPos() {
        return mMarkPDFPos;
    }

    public float getMarkX() {
        return mMarkX;
    }

    public float getMarkY() {
        return mMarkY;
    }

    public void setShowDamage(boolean showDamage) {
        isShowDamage = showDamage;
    }

    public void setV3Version(boolean isV3) {
        mIsV3 = isV3;
    }

    public void setV3DamageType(List<String> damageType) {
        mV3DamageType = damageType;
    }

    public void setCurrentModuleType(String mCurrentModuleType) {
        this.mCurrentModuleType = mCurrentModuleType;
    }

    //leon add start
//    private List<DamageTypeItem> mDamageTypeList = new ArrayList<DamageTypeItem>();
//    private AlertDialog mDrawingDamageDialog;
    private String mSelectedDamageType = "";
    //leon add end

    class PDFGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (m_layout == null) return false;
            if (m_status == STA_NONE && m_hold) {
                float dx = e2.getX() - e1.getX();
                float dy = e2.getY() - e1.getY();
                Log.d("bdd","onFling:"+dx+" ; "+dy+" ; "+velocityX+" ; "+velocityY);
                return m_layout.vFling(m_hold_docx, m_hold_docy, dx, dy, velocityX, velocityY);
            } else return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i("bdd", "onDoubleTapEvent22222");
            if (m_layout == null) return false;
            if (m_status == STA_NONE && e.getActionMasked() == MotionEvent.ACTION_UP) {
                float x = e.getX();
                float y = e.getY();
                int pageno = m_layout.vGetPage((int) x, (int) y);
                if (m_listener == null || !m_listener.OnPDFDoubleTapped(pageno, x, y))
                    return false;
                return true;
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("leon", "PdfLayoutView onLongPress in, event.getActionMasked()=" + e.getActionMasked());
            if (m_layout == null) return;
            if (m_status == STA_NONE && m_listener != null) {
                float x = e.getX();
                float y = e.getY();
                m_annot_note_x0 = x;
                m_annot_note_y0 = y;
                int pageno = m_layout.vGetPage((int) x, (int) y);
//                Log.i("leon","PdfLayoutView onLongPress x="+x+", y="+y);
                m_listener.OnPDFLongPressed(pageno, x, y);
            }

            mCacheMotionEvent = e;
            mCacheMotionEvent.setAction(1);
            mMarkX = e.getX();
            mMarkY = e.getY();
            if (!isShowDamage) {
                return;
            }
            if (mIsV3) {
                mCacheMotionEvent.setAction(0);
                if (mCurrentModuleType.equals(mModuleType.get(2))) {
                    PDFSetStamp(0);
                    m_rects = null;
                    onTouchStamp(mCacheMotionEvent);
                   showV3RHDiffAddPointPopupWindow(null,null,""+System.currentTimeMillis(),null,false);
                } else {
                    showV3DamagePopupWindow();
                }
            } else {
                promptSelectDamageTypeDialog();
            }
            Log.i("leon", "PDFLayout onLongPress will exit");
//            m_status = STA_NOTE;

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("bdd","onScroll:"+distanceX+" ; "+distanceY);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        /**
         * we make it public, because some developers may want to override this method.
         */
        public void onEditAnnot() {
            try {
                int[] location = new int[2];
                getLocationOnScreen(location);
                Intent intent = new Intent(getContext(), PopupEditAct.class);
                intent.putExtra("txt", m_annot.GetEditText());
                intent.putExtra("x", m_annot_rect[0] + location[0]);
                intent.putExtra("y", m_annot_rect[1] + location[1]);
                intent.putExtra("w", m_annot_rect[2] - m_annot_rect[0]);
                intent.putExtra("h", m_annot_rect[3] - m_annot_rect[1]);
                intent.putExtra("type", m_annot.GetEditType());
                intent.putExtra("max", m_annot.GetEditMaxlen());
                intent.putExtra("size", m_annot.GetEditTextSize() * m_layout.vGetScale());
                m_edit_type = 1;
                PopupEditAct.ms_listener = new PopupEditAct.ActRetListener() {
                    @Override
                    public void OnEditValue(String val) {
                        Log.d("bdd", "设置文字注释回调: " + val);
                        if (m_annot != null) {
                            m_annot.SetEditText(val);
                            m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                            if (m_annot != null && Global.g_exec_js)
                                executeAnnotJS();
                            m_layout.vRenderSync(m_annot_page);
                            if (m_listener != null)
                                m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                            PDFEndAnnot();
                            m_edit_type = 0;
                        }
                    }
                };
                getContext().startActivity(intent);
            } catch (Exception ignored) {
            }
        }

        boolean[] mCheckedItems;

        private void onListAnnot() {
            try {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                String[] items = new String[m_annot.GetListItemCount()];
                int cur = 0;
                while (cur < items.length) {
                    items[cur] = m_annot.GetListItem(cur);
                    cur++;
                }
                final int[] selectedItems = m_annot.GetListSels();
                mCheckedItems = new boolean[items.length];
                for (int item : selectedItems)
                    mCheckedItems[item] = true;

                if (m_annot.IsListMultiSel()) {
                    alertBuilder.setMultiChoiceItems(items, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                            mCheckedItems[which] = isChecked;
                        }
                    });
                } else {
                    alertBuilder.setSingleChoiceItems(items, selectedItems[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mCheckedItems[i] = true;
                            mCheckedItems[selectedItems[0]] = false;
                        }
                    });
                }
                AlertDialog alert = alertBuilder.create();
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        List<Integer> listSels = new ArrayList<>();
                        for (int i = 0; i < mCheckedItems.length; i++)
                            if (mCheckedItems[i]) listSels.add(i);
                        int[] sels = new int[listSels.size()];
                        for (int i = 0; i < listSels.size(); i++)
                            sels[i] = listSels.get(i);
                        m_annot.SetListSels(sels);
                        m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                        if (m_annot != null && Global.g_exec_js)
                            executeAnnotJS();
                        m_layout.vRenderSync(m_annot_page);
                        if (m_listener != null)
                            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                        PDFEndAnnot();
                    }
                });
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("bdd", "onSingleTapConfirmed "+e.getAction());
            if (m_layout == null) return false;
            if (m_status != STA_NONE && m_status != STA_ANNOT) return false;
            if (m_annot_pg != null) {
                m_annot_pg.Close();
                m_annot_pg = null;
            }
            m_annot_pos = m_layout.vGetPos((int) e.getX(), (int) e.getY());
            m_annot_page = m_layout.vGetPage(m_annot_pos.pageno);
            m_annot_pg = m_doc.GetPage(m_annot_page.GetPageNo());
            m_annot = m_annot_pg.GetAnnotFromPoint(m_annot_pos.x, m_annot_pos.y);
            if (m_annot == null) {
                if (m_listener != null) {
                    if (m_status == STA_ANNOT)
                        m_listener.OnPDFAnnotTapped(m_annot_pos.pageno, null);
                    else
                        m_listener.OnPDFBlankTapped(m_annot_pos.pageno);
                }
                m_annot_page = null;
                m_annot_pos = null;
                m_annot_rect = null;
                m_annot_pg.Close();
                m_annot_pg = null;
                PDFEndAnnot();
                m_status = STA_NONE;
            } else {
                m_annot_pg.ObjsStart();
                m_annot_rect = m_annot.GetRect();
                float tmp = m_annot_rect[1];
                m_annot_rect[0] = m_annot_page.GetVX(m_annot_rect[0]) - m_layout.vGetX();
                m_annot_rect[1] = m_annot_page.GetVY(m_annot_rect[3]) - m_layout.vGetY();
                m_annot_rect[2] = m_annot_page.GetVX(m_annot_rect[2]) - m_layout.vGetX();
                m_annot_rect[3] = m_annot_page.GetVY(tmp) - m_layout.vGetY();
                m_status = STA_ANNOT;
                int check = m_annot.GetCheckStatus();
                if (Global.g_annot_readonly && m_annot.IsReadOnly()) {
                    Toast.makeText(getContext(), "Readonly annotation", Toast.LENGTH_SHORT).show();
                    if (m_listener != null)
                        m_listener.OnPDFAnnotTapped(m_annot_pos.pageno, m_annot);
                } else if (PDFCanSave() && check >= 0) {
                    Toast.makeText(getContext(), "1111111111", Toast.LENGTH_SHORT).show();
                    switch (check) {
                        case 0:
                            m_annot.SetCheckValue(true);
                            m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                            break;
                        case 1:
                            m_annot.SetCheckValue(false);
                            m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                            break;
                        case 2:
                        case 3:
                            m_annot.SetRadio();
                            m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                            break;
                    }
                    if (m_annot != null && Global.g_exec_js)
                        executeAnnotJS();
                    m_layout.vRenderSync(m_annot_page);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                    PDFEndAnnot();
                } /*else if (PDFCanSave() && m_annot.GetEditType() > 0) {//if form edit-box.
                    Log.d("bruce","if form edit-box.: "+m_annot.GetEditType());
                    onEditAnnot();
                } */else if (PDFCanSave() && m_annot.GetComboItemCount() >= 0)//if form choice
                {
                    try {
                        int[] location = new int[2];
                        getLocationOnScreen(location);
                        String[] opts = new String[m_annot.GetComboItemCount()];
                        int cur = 0;
                        while (cur < opts.length) {
                            opts[cur] = m_annot.GetComboItem(cur);
                            cur++;
                        }
                        m_pCombo = new PopupWindow(LayoutInflater.from(getContext()).inflate(R.layout.pop_combo, null));
                        Drawable dw = new ColorDrawable(0);
                        m_pCombo.setFocusable(true);
                        m_pCombo.setTouchable(true);
                        m_pCombo.setBackgroundDrawable(dw);
                        m_pCombo.setWidth((int) (m_annot_rect[2] - m_annot_rect[0]));
                        if ((m_annot_rect[3] - m_annot_rect[1] - 4) * opts.length > 250)
                            m_pCombo.setHeight(250);
                        else
                            m_pCombo.setHeight((int) (m_annot_rect[3] - m_annot_rect[1] - 4) * opts.length);
                        ComboList combo = (ComboList) m_pCombo.getContentView().findViewById(R.id.annot_combo);
                        combo.set_opts(opts);
                        combo.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                m_combo_item = i;
                                m_pCombo.dismiss();
                            }
                        });
                        m_edit_type = 2;
                        m_combo_item = -1;
                        m_pCombo.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                if (m_edit_type == 2)//combo
                                {
                                    if (m_combo_item >= 0) {
                                        m_annot.SetComboItem(m_combo_item);
                                        m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                                        if (m_annot != null && Global.g_exec_js)
                                            executeAnnotJS();
                                        m_layout.vRenderSync(m_annot_page);
                                        if (m_listener != null)
                                            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                                    }
                                    m_combo_item = -1;
                                    PDFEndAnnot();
                                }
                                m_edit_type = 0;

                            }
                        });
                        m_pCombo.showAtLocation(PDFLayoutView.this, Gravity.NO_GRAVITY, (int) m_annot_rect[0] + location[0], (int) (m_annot_rect[3] + location[1]));
                    } catch (Exception ignored) {
                    }
                } else if (PDFCanSave() && m_annot.GetListItemCount() >= 0) {  //if list choice
                    onListAnnot();
                } else if (PDFCanSave() && m_annot.GetFieldType() == 4 &&
                        m_annot.GetSignStatus() == 0 && Global.g_hand_signature) {  //signature field
                    handleSignatureField();
                } else if (m_annot.GetURI() != null && Global.g_auto_launch_link && m_listener != null) { // launch link automatically
                    m_listener.OnPDFOpenURI(m_annot.GetURI());
                    PDFEndAnnot();
                } else if (m_listener != null) {
                    Log.i("leon", "onSingleTapConfirmed 222 PDFCanSave()=" + PDFCanSave());
                    Log.i("leon", "onSingleTapConfirmed 222 m_aMenu=" + m_aMenu);
                    Log.d("bruce", "m_annot GetType: " + m_annot.GetType());
                    m_listener.OnPDFAnnotTapped(m_annot_pos.pageno, m_annot);
                    if (PDFCanSave() && m_aMenu != null) {
                        Log.i("leon", "onSingleTapConfirmed m_aMenu.show");
                        if(mIsV3){
                            m_rects = m_annot_rect;
                        }
                        if(mCurrentModuleType == mModuleType.get(2) && m_annot.GetType() == 13){
                            if(mV3AddGroupPointCallback != null){
                                mMarkX = m_annot_rect[0];
                                mMarkY = m_annot_rect[1];
                                mV3AddGroupPointCallback.onShowPoint(m_annot,true);
                            }
                            return true;
                        }
                        m_aMenu.show(m_annot, m_annot_rect, new UIAnnotMenu.IMemnuCallback() {
                            //the update need new operator in OPStack
                            @Override
                            public void onUpdate() {
                                Log.d("bdd", "PDFLayoutView m_aMenu onUpdate");
                                m_layout.vRenderSync(m_annot_page);
                                if (m_listener != null)
                                    m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                                PDFEndAnnot();
                            }

                            @Override
                            public void onRemove() {
                                Log.d("bdd", "PDFLayoutView m_aMenu onRemove");
                                PDFRemoveAnnot();
                            }

                            @Override
                            public void onPerform() {
                                Log.d("bdd", "PDFLayoutView m_aMenu onPerform");
                                PDFPerformAnnot();
                            }

                            @Override
                            public void onCancel() {
                                Log.d("bdd", "PDFLayoutView m_aMenu onCancel");
                                PDFCancelAnnot();
                            }

                            @Override
                            public void onMenuClicked(int whichBtn, Page.Annotation annot) {
                                Log.i("leon", "PDFLayoutView onMenuClicked whichBtn=" + whichBtn + ", annot=" + annot);
                                if (m_listener != null) {

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("action", whichBtn);//BUTTON_POPMENU_EDIT/BUTTON_POPMENU_DEL
                                        jsonObject.put("annotRef", annot.GetRef());
                                        jsonObject.put("type", mSelectedDamageType);
                                        jsonObject.put("axis", "");
                                        jsonObject.put("content", "");
                                        jsonObject.put("annotX", (int) e.getX());
                                        jsonObject.put("annotY", (int) e.getY());
                                        jsonObject.put("annotName",annot.GetName());
                                    } catch (JSONException e) {
                                        Log.i("leon", "PDFLayoutView onMenuClicked edit annot failed!");
                                    }

                                    String jsonString = jsonObject.toString();
                                    m_listener.onPDFNoteEdited(jsonString);

                                }
                            }
                        });
                    }
                }
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        private void handleSignatureField() {
            int ssta = m_annot.GetSignStatus();
            if (ssta < 0)
                Toast.makeText(getContext(), "only premium licese support signature", Toast.LENGTH_LONG).show();
            else if (m_annot.GetSignStatus() == 1) {
                UIAnnotDlgSignProp dlg = new UIAnnotDlgSignProp(getContext());
                dlg.show(m_annot, m_doc, new UIAnnotMenu.IMemnuCallback() {
                    @Override
                    public void onUpdate() {
                    }

                    @Override
                    public void onRemove() {
                    }

                    @Override
                    public void onPerform() {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onMenuClicked(int whichBtn, Page.Annotation annot) {

                    }
                });
            } else {
                UIAnnotDlgSign dlg = new UIAnnotDlgSign(getContext());
                dlg.show(m_annot, m_doc, new UIAnnotMenu.IMemnuCallback() {
                    @Override
                    public void onUpdate() {
                        m_layout.vRenderSync(m_annot_page);
                        if (m_listener != null)
                            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                        PDFEndAnnot();
                    }

                    @Override
                    public void onRemove() {
                    }

                    @Override
                    public void onPerform() {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onMenuClicked(int whichBtn, Page.Annotation annot) {

                    }
                });
            }
            /*
            if (CommonUtil.isFieldGraphicallySigned(m_annot)) {
                new AlertDialog.Builder(getContext()).setTitle(R.string.warning).setMessage(R.string.delete_signature_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateSignature(null, true);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            } else {
                CaptureSignature.CaptureSignatureListener.setListener(new CaptureSignature.CaptureSignatureListener.OnSignatureCapturedListener() {
                    @Override
                    public void OnSignatureCaptured(Bitmap signature) {
                        updateSignature(signature, false);
                    }
                });
                Intent intent = new Intent(getContext(), CaptureSignature.class);
                intent.putExtra(CaptureSignature.SIGNATURE_PAD_DESCR, Global.sSignPadDescr);
                intent.putExtra(CaptureSignature.FIT_SIGNATURE_BITMAP, Global.sFitSignatureToField);
                getContext().startActivity(intent);
            }
            */
        }

        private void updateSignature(Bitmap signature, boolean remove) {
            if (m_annot != null) {
                float[] annotRect = m_annot.GetRect();
                float annotWidth = annotRect[2] - annotRect[0];
                float annotHeight = annotRect[3] - annotRect[1];

                if (remove)
                    signature = Bitmap.createBitmap((int) annotWidth, (int) annotHeight, Bitmap.Config.ARGB_8888);

                if (signature != null) {
                    Document.DocForm form = CommonUtil.createImageForm(m_doc, signature, annotWidth, annotHeight);
                    if (form != null && m_annot.SetIcon("Signature", form)) {
                        m_layout.vRenderSync(m_annot_page);
                        if (m_listener != null)
                            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                        PDFEndAnnot();
                    }
                    signature.recycle();
                }
            }
        }
    }

    static class PDFVPageSet {
        PDFVPageSet(int max_len) {
            pages = new VPage[max_len];
            pages_cnt = 0;
        }

        void Insert(VPage vpage) {
            int cur;
            for (cur = 0; cur < pages_cnt; cur++) {
                if (pages[cur] == vpage) return;
            }
            pages[cur] = vpage;
            pages_cnt++;
        }

        VPage[] pages;
        int pages_cnt;
    }

    private ActivityManager m_amgr;
    private final ActivityManager.MemoryInfo m_info = new ActivityManager.MemoryInfo();
    private final Paint m_info_paint = new Paint();

    public PDFLayoutView(Context context) {
        super(context);
        Log.i("leon", "PDFLayoutView create 1 in");
        m_doc = null;
        m_gesture = new GestureDetector(context, new PDFGestureListener());
        setBackgroundColor(Global.g_readerview_bg_color);
        if (Global.debug_mode) {
            m_amgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            m_info_paint.setColor(0xFFFF0000);
            m_info_paint.setTextSize(30);
        }

//        initDamageTypeList();
//        createDialog();
    }

    public PDFLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.i("leon", "PDFLayoutView create 2 in");

        m_doc = null;
        m_gesture = new GestureDetector(context, new PDFGestureListener());
        setBackgroundColor(Global.g_readerview_bg_color);
        if (Global.debug_mode) {
            m_amgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            m_info_paint.setColor(0xFFFF0000);
            m_info_paint.setTextSize(30);
        }

//        Thread thread=new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                Log.i("leon", "create new thread to run initilization");
//                initDamageTypeList();
//                createDialog();
//            }
//        });
//        thread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (m_layout != null && m_status != STA_ANNOT && w > 0 && h > 0) {
            m_layout.vResize(w, h);

            if (m_goto_pos != null) {
                m_pageno = m_goto_pos.pageno;
                m_layout.vSetPos(0, 0, m_goto_pos);
                m_goto_pos = null;
                invalidate();
            }

            m_layout.vZoomSet(m_layout.vGetWidth() / 2, m_layout.vGetHeight() / 2, m_layout.vGetPos(0, 0), 1);
            PDFGotoPage(m_pageno);
        }
    }

    private void onDrawSelect(Canvas canvas) {
        if (m_status == STA_SELECT && m_sel != null && m_annot_page != null) {
            int orgx = m_annot_page.GetVX(0) - m_layout.vGetX();
            int orgy = m_annot_page.GetVY(m_doc.GetPageHeight(m_annot_page.GetPageNo())) - m_layout.vGetY();
            float scale = m_layout.vGetScale();
            float pheight = m_doc.GetPageHeight(m_annot_page.GetPageNo());
            m_sel.DrawSel(canvas, scale, pheight, orgx, orgy);
            int[] rect1 = m_sel.GetRect1(scale, pheight, orgx, orgy);
            int[] rect2 = m_sel.GetRect2(scale, pheight, orgx, orgy);
            if (rect1 != null && rect2 != null && Global.g_use_sel_icons) {
                canvas.drawBitmap(m_sel_icon1, rect1[0] - m_sel_icon1.getWidth(), rect1[1] - m_sel_icon1.getHeight(), null);
                canvas.drawBitmap(m_sel_icon2, rect2[2], rect2[3], null);
            }
        }
    }

    private void onDrawAnnot(Canvas canvas) {
        if (m_status == STA_ANNOT && Global.g_highlight_annotation) {
            Paint paint = new Paint();
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(0x80000000);
            canvas.drawRect(m_annot_rect[0],
                    m_annot_rect[1],
                    m_annot_rect[2],
                    m_annot_rect[3], paint);
        }
    }

    private void onDrawRect(Canvas canvas) {
        if (m_status == STA_RECT && m_rects != null) {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint();
            Paint paint2 = new Paint();
            paint1.setStyle(Style.STROKE);
            paint1.setStrokeWidth(Global.g_rect_annot_width);
            paint1.setColor(Global.g_rect_annot_color);
            paint2.setStyle(Style.FILL);
            paint2.setColor(Global.g_rect_annot_fill_color);
            float rad = Global.g_rect_annot_width * 0.5f;
            for (cur = 0; cur < len; cur += 4) {
                float[] rect = new float[4];
                if (m_rects[cur] > m_rects[cur + 2]) {
                    rect[0] = m_rects[cur + 2];
                    rect[2] = m_rects[cur];
                } else {
                    rect[0] = m_rects[cur];
                    rect[2] = m_rects[cur + 2];
                }
                if (m_rects[cur + 1] > m_rects[cur + 3]) {
                    rect[1] = m_rects[cur + 3];
                    rect[3] = m_rects[cur + 1];
                } else {
                    rect[1] = m_rects[cur + 1];
                    rect[3] = m_rects[cur + 3];
                }
                canvas.drawRect(rect[0], rect[1], rect[2], rect[3], paint1);
                canvas.drawRect(rect[0] + rad, rect[1] + rad, rect[2] - rad, rect[3] - rad, paint2);
            }
        }
    }

    private void onDrawLine(Canvas canvas) {
        if (m_status == STA_LINE && m_rects != null) {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint();
            paint1.setStyle(Style.STROKE);
            paint1.setStrokeWidth(Global.g_line_annot_width);
            paint1.setColor(Global.g_line_annot_color);
            for (cur = 0; cur < len; cur += 4) {
                canvas.drawLine(m_rects[cur], m_rects[cur + 1], m_rects[cur + 2], m_rects[cur + 3], paint1);
            }
        }
    }

    private void onDrawStamp(Canvas canvas) {

        if (m_status == STA_STAMP && m_rects != null) {
            int len = m_rects.length;
            int cur;
            for (cur = 0; cur < len; cur += 4) {
                float[] rect = new float[4];
                if (m_rects[cur] > m_rects[cur + 2]) {
                    rect[0] = m_rects[cur + 2];
                    rect[2] = m_rects[cur];
                } else {
                    rect[0] = m_rects[cur];
                    rect[2] = m_rects[cur + 2];
                }
                if (m_rects[cur + 1] > m_rects[cur + 3]) {
                    rect[1] = m_rects[cur + 3];
                    rect[3] = m_rects[cur + 1];
                } else {
                    rect[1] = m_rects[cur + 1];
                    rect[3] = m_rects[cur + 3];
                }
                if (m_icon != null) {
                    Rect rc = new Rect();
                    rc.left = (int) rect[0];
                    rc.top = (int) rect[1];
                    rc.right = (int) rect[2];
                    rc.bottom = (int) rect[3];
                    canvas.drawBitmap(m_icon, null, rc, null);
                }
            }
        }
    }

    private void onDrawEditbox(Canvas canvas) {
        if (m_status == STA_EDITBOX && m_rects != null) {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint();
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setStrokeWidth(3);
            paint1.setColor(0x80FF0000);
            for (cur = 0; cur < len; cur += 4) {
                float[] rect = new float[4];
                if (m_rects[cur] > m_rects[cur + 2]) {
                    rect[0] = m_rects[cur + 2];
                    rect[2] = m_rects[cur];
                } else {
                    rect[0] = m_rects[cur];
                    rect[2] = m_rects[cur + 2];
                }
                if (m_rects[cur + 1] > m_rects[cur + 3]) {
                    rect[1] = m_rects[cur + 3];
                    rect[3] = m_rects[cur + 1];
                } else {
                    rect[1] = m_rects[cur + 1];
                    rect[3] = m_rects[cur + 3];
                }
                canvas.drawRect(rect[0], rect[1], rect[2], rect[3], paint1);
            }
        }
    }

    private void onDrawEllipse(Canvas canvas) {
        if (m_status == STA_ELLIPSE && m_rects != null) {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint();
            Paint paint2 = new Paint();
            paint1.setStyle(Style.STROKE);
            paint1.setStrokeWidth(Global.g_oval_annot_width);
            paint1.setColor(Global.g_oval_annot_color);
            paint2.setStyle(Style.FILL);
            paint2.setColor(Global.g_oval_annot_fill_color);
            for (cur = 0; cur < len; cur += 4) {
                float[] rect = new float[4];
                if (m_rects[cur] > m_rects[cur + 2]) {
                    rect[0] = m_rects[cur + 2];
                    rect[2] = m_rects[cur];
                } else {
                    rect[0] = m_rects[cur];
                    rect[2] = m_rects[cur + 2];
                }
                if (m_rects[cur + 1] > m_rects[cur + 3]) {
                    rect[1] = m_rects[cur + 3];
                    rect[3] = m_rects[cur + 1];
                } else {
                    rect[1] = m_rects[cur + 1];
                    rect[3] = m_rects[cur + 3];
                }
                RectF rc = new RectF();
                rc.left = rect[0];
                rc.top = rect[1];
                rc.right = rect[2];
                rc.bottom = rect[3];
                canvas.drawOval(rc, paint1);
                rc.left += 1.5f;
                rc.top += 1.5f;
                rc.right -= 1.5f;
                rc.bottom -= 1.5f;
                canvas.drawOval(rc, paint2);
            }
        }
    }

    private static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density);
    }

    private void onDrawPolygon(Canvas canvas) {
        if (m_status != STA_POLYGON || m_polygon == null) return;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Global.g_line_annot_color);
        paint.setStrokeWidth(Global.g_line_annot_width);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        m_polygon.OnDraw(canvas, 0, 0, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Global.g_line_annot_fill_color);
        if (m_polygon.GetNodeCount() > 2)
            m_polygon.OnDraw(canvas, 0, 0, paint);
        m_polygon.onDrawPoint(canvas, 0, 0, dp2px(getContext(), 4), paint);
    }

    private void onDrawPolyline(Canvas canvas) {
        if (m_status != STA_POLYLINE || m_polygon == null) return;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Global.g_line_annot_color);
        paint.setStrokeWidth(Global.g_line_annot_width);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        m_polygon.OnDraw(canvas, 0, 0, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Global.g_line_annot_fill_color);
        m_polygon.onDrawPoint(canvas, 0, 0, dp2px(getContext(), 4), paint);
    }

    /**
     * the draw function invoke onDraw and then call dispatchDraw. so we override only to draw on Canvas to reduce drawing time.
     *
     * @param canvas Canvas object
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onDraw(Canvas canvas) {
        if (m_layout != null) {
            m_layout.vDraw(canvas, m_zooming || m_status == STA_ZOOM);
            onDrawSelect(canvas);
            onDrawRect(canvas);
            onDrawEllipse(canvas);
            onDrawAnnot(canvas);
            onDrawLine(canvas);
            onDrawStamp(canvas);
            onDrawEditbox(canvas);
            onDrawPolygon(canvas);
            onDrawPolyline(canvas);
            if (m_status == STA_INK && m_ink != null) {
                m_ink.OnDraw(canvas, 0, 0);
            }
        }
        if (Global.debug_mode && m_amgr != null) {
            try {
                m_amgr.getMemoryInfo(m_info);
                canvas.drawText("AvialMem:" + m_info.availMem / (1024 * 1024) + " M", 20, 150, m_info_paint);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean m_hold = false;
    private float m_hold_x;
    private float m_hold_y;
    private int m_hold_docx;
    private int m_hold_docy;
    private PDFPos m_zoom_pos;
    private float m_zoom_dis0;
    private float m_zoom_scale;

    private boolean onTouchEditbox(MotionEvent event) {
        if (m_status != STA_EDITBOX) return false;
        int len = 0;
        if (m_rects != null) len = m_rects.length;
        PDFPos pos;
        int cur;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float[] rects = new float[len + 4];
                for (cur = 0; cur < len; cur++)
                    rects[cur] = m_rects[cur];
                rects[cur] = event.getX();
                rects[cur + 1] = event.getY();
                rects[cur + 2] = event.getX();
                rects[cur + 3] = event.getY();
                m_rects = rects;
                break;
            case MotionEvent.ACTION_MOVE:
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                break;
            case MotionEvent.ACTION_UP://when touch up, it shall popup editbox on page
            case MotionEvent.ACTION_CANCEL:
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                pos = m_layout.vGetPos((int) m_rects[0], (int) m_rects[1]);
                m_annot_page = m_layout.vGetPage(pos.pageno);
                m_annot_pg = m_doc.GetPage(m_annot_page.GetPageNo());
                PDFSetEditbox(1);//end editbox.
            {//popup editbox from UI.
                m_annot_pg.ObjsStart();
                m_annot = m_annot_pg.GetAnnot(m_annot_pg.GetAnnotCount() - 1);
                if (m_annot == null) {
                    Log.e("bdd", "m_annot is null : " + m_annot);
                    return false;
                }
                m_annot_rect = m_annot.GetRect();
                float tmp = m_annot_rect[1];
                m_annot_rect[0] = m_annot_page.GetVX(m_annot_rect[0]) - m_layout.vGetX();
                m_annot_rect[1] = m_annot_page.GetVY(m_annot_rect[3]) - m_layout.vGetY();
                m_annot_rect[2] = m_annot_page.GetVX(m_annot_rect[2]) - m_layout.vGetX();
                m_annot_rect[3] = m_annot_page.GetVY(tmp) - m_layout.vGetY();
                if (m_listener != null)
                    m_listener.OnPDFAnnotTapped(m_annot_page.GetPageNo(), m_annot);
                m_status = STA_ANNOT;
                if (m_pEdit == null) m_pEdit = new UIAnnotPopEdit(this);
                m_pEdit.update(m_annot, m_annot_rect, m_annot_page.vGetScale());
                m_edit_type = 1;
                m_pEdit.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (m_annot != null) {
                            String txt = m_pEdit.getEditText();
                            Log.i("bruce", "get EditText=" + txt);
                            if (!m_annot.SetEditText(m_pEdit.getEditText())) {
                                Log.e("bruce", "set EditText failed.");
                            }
                            m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                            //if (m_annot != null && Global.sExecuteAnnotJS) executeAnnotJS();//there is JS on free text annotation.
                            m_layout.vRenderSync(m_annot_page);
                            if (m_listener != null)
                                m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                            PDFEndAnnot();
                            m_edit_type = 0;
                        }
                    }
                });
                post(new Runnable() {
                    @Override
                    public void run() {
                        int[] location = new int[2];
                        getLocationOnScreen(location);
                        m_pEdit.show(PDFLayoutView.this, (int) m_annot_rect[0] + location[0], (int) (m_annot_rect[1] + location[1]));
                    }
                });
            }
            break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchPolygon(MotionEvent event) {
        if (m_status != STA_POLYGON) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (m_annot_page == null) {
                    PDFPos pos = m_layout.vGetPos((int) event.getX(), (int) event.getY());
                    m_annot_page = m_layout.vGetPage(pos.pageno);
                }
                if (m_polygon.GetNodeCount() < 1)
                    m_polygon.MoveTo(event.getX(), event.getY());
                else
                    m_polygon.LineTo(event.getX(), event.getY());
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchPolyline(MotionEvent event) {
        if (m_status != STA_POLYLINE) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (m_annot_page == null) {
                    PDFPos pos = m_layout.vGetPos((int) event.getX(), (int) event.getY());
                    m_annot_page = m_layout.vGetPage(pos.pageno);
                }
                if (m_polygon.GetNodeCount() < 1)
                    m_polygon.MoveTo(event.getX(), event.getY());
                else
                    m_polygon.LineTo(event.getX(), event.getY());
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchNone(MotionEvent event) {
        if (m_status != STA_NONE) return false;
        boolean ret = m_gesture.onTouchEvent(event);
//        Log.i("leon", "onTouchNone ret of m_gesture.onTouchEvent=" + ret);

        if (ret) return true;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                m_hold_x = event.getX();
                m_hold_y = event.getY();
                m_hold_docx = m_layout.vGetX();
                m_hold_docy = m_layout.vGetY();
                m_layout.vScrollAbort();
                invalidate();
                m_hold = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (m_hold) {
                    m_layout.vSetX((int) (m_hold_docx + m_hold_x - event.getX()));
                    m_layout.vSetY((int) (m_hold_docy + m_hold_y - event.getY()));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (m_hold) {
                    m_layout.vSetX((int) (m_hold_docx + m_hold_x - event.getX()));
                    m_layout.vSetY((int) (m_hold_docy + m_hold_y - event.getY()));
                    m_layout.vMoveEnd();
                    invalidate();
                    m_hold = false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    m_status = STA_ZOOM;
                    m_hold_x = (event.getX(0) + event.getX(1)) / 2;
                    m_hold_y = (event.getY(0) + event.getY(1)) / 2;
                    m_zoom_pos = m_layout.vGetPos((int) m_hold_x, (int) m_hold_y);
                    float dx = event.getX(0) - event.getX(1);
                    float dy = event.getY(0) - event.getY(1);
                    m_zoom_dis0 = Global.sqrtf(dx * dx + dy * dy);
                    m_zoom_scale = m_layout.vGetZoom();
                    m_status = STA_ZOOM;
                    m_layout.vZoomStart();
                    if (m_listener != null)
                        m_listener.OnPDFZoomStart();
                }
                break;
        }
        return true;
    }

    private boolean onTouchZoom(MotionEvent event) {
        if (m_status != STA_ZOOM) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (m_status == STA_ZOOM && event.getPointerCount() >= 2) {
                    float dx = event.getX(0) - event.getX(1);
                    float dy = event.getY(0) - event.getY(1);
                    float dis1 = Global.sqrtf(dx * dx + dy * dy);
                    m_layout.vZoomSet((int) m_hold_x, (int) m_hold_y, m_zoom_pos, m_zoom_scale * dis1 / m_zoom_dis0);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (m_status == STA_ZOOM && event.getPointerCount() == 2) {
                    float dx = event.getX(0) - event.getX(1);
                    float dy = event.getY(0) - event.getY(1);
                    float dis1 = Global.sqrtf(dx * dx + dy * dy);
                    m_layout.vZoomSet((int) m_hold_x, (int) m_hold_y, m_zoom_pos, m_zoom_scale * dis1 / m_zoom_dis0);
                    m_hold_x = -10000;
                    m_hold_y = -10000;
                    m_status = STA_NONE;
                    m_zooming = true;
                    m_layout.vZoomConfirmed();
                    if (!Global.g_zoomed_stop_on_boundaries)
                        m_layout.vMoveEnd();
                    invalidate();
                    m_hold = false;
                    if (m_listener != null)
                        m_listener.OnPDFZoomEnd();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (m_status == STA_ZOOM)//event captured by system gesture.
                {
                    float dx = event.getX(0) - event.getX(1);
                    float dy = event.getY(0) - event.getY(1);
                    float dis1 = Global.sqrtf(dx * dx + dy * dy);
                    m_layout.vZoomSet((int) m_hold_x, (int) m_hold_y, m_zoom_pos, m_zoom_scale * dis1 / m_zoom_dis0);
                    m_hold_x = -10000;
                    m_hold_y = -10000;
                    m_status = STA_NONE;
                    m_zooming = true;
                    m_layout.vZoomConfirmed();
                    invalidate();
                    m_hold = false;
                    if (m_listener != null)
                        m_listener.OnPDFZoomEnd();
                }
                break;
        }
        return true;
    }

    private boolean onTouchSelect(MotionEvent event) {
        if (m_status != STA_SELECT) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                m_hold_x = event.getX();
                m_hold_y = event.getY();
                if (m_sel != null) {
                    m_sel.Clear();
                    m_sel = null;
                }
                m_annot_pos = m_layout.vGetPos((int) m_hold_x, (int) m_hold_y);
                m_annot_page = m_layout.vGetPage(m_annot_pos.pageno);
                m_sel = new VSel(m_doc.GetPage(m_annot_pos.pageno));
                break;
            case MotionEvent.ACTION_MOVE:
                if (m_sel != null) {
                    m_sel.SetSel(m_annot_pos.x, m_annot_pos.y,
                            m_annot_page.ToPDFX(event.getX(), m_layout.vGetX()),
                            m_annot_page.ToPDFY(event.getY(), m_layout.vGetY()));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (m_sel != null) {
                    m_sel.SetSel(m_annot_pos.x, m_annot_pos.y,
                            m_annot_page.ToPDFX(event.getX(), m_layout.vGetX()),
                            m_annot_page.ToPDFY(event.getY(), m_layout.vGetY()));
                    invalidate();
                    if (m_listener != null) m_listener.OnPDFSelectEnd(m_sel.GetSelString());
                }
                break;
        }
        return true;
    }

    private boolean onTouchInk(MotionEvent event) {
        if (m_status != STA_INK) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (m_annot_page == null) {
                    PDFPos pos = m_layout.vGetPos((int) event.getX(), (int) event.getY());
                    m_annot_page = m_layout.vGetPage(pos.pageno);
                }
                m_ink.OnDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                m_ink.OnMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                m_ink.OnUp(event.getX(), event.getY());
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchRect(MotionEvent event) {
        if (m_status != STA_RECT) return false;
        int len = 0;
        if (m_rects != null) len = m_rects.length;
        int cur;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float[] rects = new float[len + 4];
                for (cur = 0; cur < len; cur++)
                    rects[cur] = m_rects[cur];
                rects[cur] = event.getX();
                rects[cur + 1] = event.getY();
                rects[cur + 2] = event.getX();
                rects[cur + 3] = event.getY();
                m_rects = rects;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchEllipse(MotionEvent event) {
        if (m_status != STA_ELLIPSE) return false;
        int len = 0;
        if (m_rects != null) len = m_rects.length;
        int cur;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float[] rects = new float[len + 4];
                for (cur = 0; cur < len; cur++)
                    rects[cur] = m_rects[cur];
                rects[cur] = event.getX();
                rects[cur + 1] = event.getY();
                rects[cur + 2] = event.getX();
                rects[cur + 3] = event.getY();
                m_rects = rects;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchAnnot(MotionEvent event) {
        Log.i("leon", "onTouchAnnot x=" + event.getX() + ", y=" + event.getY());
        if (m_status != STA_ANNOT) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                m_annot_x0 = event.getX();
                m_annot_y0 = event.getY();

                if (m_annot_x0 > m_annot_rect[0] && m_annot_y0 > m_annot_rect[1] &&
                        m_annot_x0 < m_annot_rect[2] && m_annot_y0 < m_annot_rect[3]) {
                    m_annot_rect0 = new float[4];
                    m_annot_rect0[0] = m_annot_rect[0];
                    m_annot_rect0[1] = m_annot_rect[1];
                    m_annot_rect0[2] = m_annot_rect[2];
                    m_annot_rect0[3] = m_annot_rect[3];
                } else
                    m_annot_rect0 = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (m_annot_rect0 != null && !m_annot.IsLocked() && !(Global.g_annot_readonly && m_annot.IsReadOnly()) && PDFCanSave()) {
                    float x = event.getX();
                    float y = event.getY();
                    m_annot_rect[0] = m_annot_rect0[0] + x - m_annot_x0;
                    m_annot_rect[1] = m_annot_rect0[1] + y - m_annot_y0;
                    m_annot_rect[2] = m_annot_rect0[2] + x - m_annot_x0;
                    m_annot_rect[3] = m_annot_rect0[3] + y - m_annot_y0;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (m_annot_rect0 != null && !m_annot.IsLocked() && !(Global.g_annot_readonly && m_annot.IsReadOnly()) && PDFCanSave()) {
                    float x = event.getX();
                    float y = event.getY();
                    PDFPos pos = m_layout.vGetPos((int) x, (int) y);
                    m_annot_rect[0] = m_annot_rect0[0] + x - m_annot_x0;
                    m_annot_rect[1] = m_annot_rect0[1] + y - m_annot_y0;
                    m_annot_rect[2] = m_annot_rect0[2] + x - m_annot_x0;
                    m_annot_rect[3] = m_annot_rect0[3] + y - m_annot_y0;
                    if (m_annot_page.GetPageNo() == pos.pageno) {
                        m_annot_rect0[0] = m_annot_page.ToPDFX(m_annot_rect[0], m_layout.vGetX());
                        m_annot_rect0[1] = m_annot_page.ToPDFY(m_annot_rect[3], m_layout.vGetY());
                        m_annot_rect0[2] = m_annot_page.ToPDFX(m_annot_rect[2], m_layout.vGetX());
                        m_annot_rect0[3] = m_annot_page.ToPDFY(m_annot_rect[1], m_layout.vGetY());
                        //add to redo/undo stack.
                        float[] rect = m_annot.GetRect();
                        m_opstack.push(new OPMove(pos.pageno, rect, pos.pageno, m_annot.GetIndexInPage(), m_annot_rect0));
                        m_annot.SetRect(m_annot_rect0[0], m_annot_rect0[1], m_annot_rect0[2], m_annot_rect0[3]);
                        m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                        m_layout.vRenderSync(m_annot_page);
                        if (m_listener != null)
                            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                    } else {
                        VPage vpage = m_layout.vGetPage(pos.pageno);
                        Page page = m_doc.GetPage(vpage.GetPageNo());
                        if (page != null) {
                            page.ObjsStart();
                            m_annot_rect0[0] = vpage.ToPDFX(m_annot_rect[0], m_layout.vGetX());
                            m_annot_rect0[1] = vpage.ToPDFY(m_annot_rect[3], m_layout.vGetY());
                            m_annot_rect0[2] = vpage.ToPDFX(m_annot_rect[2], m_layout.vGetX());
                            m_annot_rect0[3] = vpage.ToPDFY(m_annot_rect[1], m_layout.vGetY());
                            //add to redo/undo stack.
                            float[] rect = m_annot.GetRect();
                            m_opstack.push(new OPMove(m_annot_page.GetPageNo(), rect, pos.pageno, page.GetAnnotCount(), m_annot_rect0));
                            m_annot.MoveToPage(page, m_annot_rect0);
                            m_annot.SetModifyDate(CommonUtil.getCurrentDate());
                            //page.CopyAnnot(m_annot, m_annot_rect0);
                            page.Close();
                        }
                        m_layout.vRenderSync(m_annot_page);
                        m_layout.vRenderSync(vpage);
                        if (m_listener != null) {
                            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                            m_listener.OnPDFPageModified(vpage.GetPageNo());
                        }
                    }
                }
                PDFEndAnnot();
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchLine(MotionEvent event) {
        Log.i("leon", "PDFLayout onTouchLine m_status=" + m_status);
        if (m_status != STA_LINE) return false;
        int len = 0;
        if (m_rects != null) len = m_rects.length;
        int cur;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float[] rects = new float[len + 4];
                for (cur = 0; cur < len; cur++)
                    rects[cur] = m_rects[cur];
                rects[cur] = event.getX();
                rects[cur + 1] = event.getY();
                rects[cur + 2] = event.getX();
                rects[cur + 3] = event.getY();
                Log.i("bdd", " bruce:  rects[0]" + rects[0] + "   " + rects[1] + "   " + rects[2] + "   " + rects[3]);
                m_rects = rects;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                Log.i("bdd", " bruce:  m_rects[2]" + m_rects[2] + "   " + m_rects[3]);
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchStamp2(MotionEvent event) {
        int len = 0;
        if (m_rects != null) len = m_rects.length;
        int cur;
        Log.d("bruce","onTouchStamp2 event.getActionMasked(): "+event.getActionMasked());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float[] rects = new float[len + 4];
                for (cur = 0; cur < len; cur++)
                    rects[cur] = m_rects[cur];
                rects[cur] = event.getX();
                rects[cur + 1] = event.getY();
                rects[cur + 2] = event.getX();
                rects[cur + 3] = event.getY();
                m_rects = rects;
                for (float f:m_rects){
                    Log.d("bruce","onTouchStamp2 ACTION_DOWN: "+f);
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                for (float f:m_rects){
                    Log.d("bruce","onTouchStamp2 ACTION_CANCEL: "+f);
                }
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                break;
        }
        invalidate();
        return true;
    }

    private boolean onTouchStamp(MotionEvent event) {
        if (m_status != STA_STAMP) return false;
        int len = 0;
        if (m_rects != null) len = m_rects.length;
        int cur;
        Log.d("bruce","onTouchStamp event.getActionMasked(): "+event.getActionMasked());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float[] rects = new float[len + 4];
                for (cur = 0; cur < len; cur++)
                    rects[cur] = m_rects[cur];
                rects[cur] = event.getX();
                rects[cur + 1] = event.getY();
                rects[cur + 2] = event.getX();
                rects[cur + 3] = event.getY();
                m_rects = rects;
                layoutWidth = m_layout.vGetWidth();
                layoutHieght = m_layout.vGetHeight();
                for (float f:m_rects){
                    Log.d("bruce","onTouchStamp ACTION_DOWN: "+f);
            }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                for (float f:m_rects){
                    Log.d("bruce","onTouchStamp ACTION_CANCEL: "+f);
                }
                m_rects[len - 2] = event.getX();
                m_rects[len - 1] = event.getY();
                break;
        }
        invalidate();
        return true;
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_custom_stamp);
            m_dicon = m_doc.NewImage(bitmap, 0);
        }
    }

    Page mPage = null;

    private boolean onTouchNote(MotionEvent event) {
        if (m_status != STA_NOTE) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                PDFPos pos = m_layout.vGetPos((int) event.getX(), (int) event.getY());
                VPage vpage = m_layout.vGetPage(pos.pageno);
                Page page = m_doc.GetPage(vpage.GetPageNo());
                mPage = page;
                if (page != null) {
                    page.ObjsStart();
                    if (m_note_pages == null) {
                        m_note_pages = new VPage[1];
                        m_note_indecs = new int[1];
                        m_note_pages[0] = vpage;
                        m_note_indecs[0] = page.GetAnnotCount();
                    } else {
                        int cur = 0;
                        int cnt = m_note_pages.length;
                        while (cur < cnt) {
                            if (m_note_pages[cur] == vpage) break;
                            cur++;
                        }
                        if (cur >= cnt)//append 1 page
                        {
                            VPage[] pages = new VPage[cnt + 1];
                            int[] indecs = new int[cnt + 1];
                            for (cur = 0; cur < cnt; cur++) {
                                pages[cur] = m_note_pages[cur];
                                indecs[cur] = m_note_indecs[cur];
                            }
                            pages[cnt] = vpage;
                            indecs[cnt] = page.GetAnnotCount();
                            m_note_pages = pages;
                            m_note_indecs = indecs;
                        }
                    }
                    float[] pt = new float[2];
                    pt[0] = pos.x;
                    pt[1] = pos.y;
                    Log.d("bruce", "x " + pos.x + " ; y " + pos.y);
                    mMarkPDFPos = pos;
                    page.AddAnnotText(pt);
                    m_annot = page.GetAnnot(page.GetAnnotCount() - 1);
                    onAnnotCreated(m_annot);
                    //add to redo/undo stack.
                    m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                    m_layout.vRenderSync(vpage);
                    invalidate();
                    //page.Close();
                    m_annot_pg = page;

                    if (m_annot == null)
                        Log.d("bruce", "onTouchNote m_annot is NULL");
                    else
                        Log.d("bruce", "onTouchNote m_annot is NOT NULL ");

                    m_annot = m_annot_pg.GetAnnot(m_annot_pg.GetAnnotCount() - 1);
                    m_annot_rect = m_annot.GetRect();
                    float tmp = m_annot_rect[1];
                    m_annot_page = m_layout.vGetPage(pos.pageno);
                    m_annot_rect[0] = m_annot_page.GetVX(m_annot_rect[0]) - m_layout.vGetX();
                    m_annot_rect[1] = m_annot_page.GetVY(m_annot_rect[3]) - m_layout.vGetY();
                    m_annot_rect[2] = m_annot_page.GetVX(m_annot_rect[2]) - m_layout.vGetX();
                    m_annot_rect[3] = m_annot_page.GetVY(tmp) - m_layout.vGetY();
                    mMarkX = m_annot_rect[2] - (m_annot_rect[2] - m_annot_rect[0]) / 2;
                    mMarkY = m_annot_rect[3] - (m_annot_rect[3] - m_annot_rect[1]) / 2;

                    Log.d("bruce", "m_annot_rect " + m_annot_rect[0] + " ; " + m_annot_rect[1] + " ; " + m_annot_rect[2] + " ; " + m_annot_rect[3]);
                    if (m_listener != null)
                        m_listener.OnPDFAnnotTapped(m_annot_page.GetPageNo(), m_annot);
                    m_status = STA_ANNOT;
                    PDFEditAnnot();
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
                break;
        }
        return true;
    }

    public float[] getCurrentRect() {
        return new float[]{m_annot_rect[0], m_annot_rect[1], m_annot_rect[2], m_annot_rect[3]};
    }

    public static Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawingCache); // 创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
            if (bitmap == null) {
                return null;
            }
            view.setDrawingCacheEnabled(false);
            bitmap.setHasAlpha(false);
            bitmap.prepareToDraw();
            return bitmap;
        }
    }


    public static Bitmap getViewBitmap(View view,float roationAngle) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();

        android.graphics.Matrix matrix=new android.graphics.Matrix();
        matrix.postRotate(roationAngle);
        if (drawingCache == null) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            Canvas canvas = new Canvas(bitmap1);
            view.draw(canvas);
            return bitmap1;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawingCache, 0, 0, drawingCache.getWidth(), drawingCache.getHeight(), matrix, false); // 创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
            if (bitmap == null) {
                return null;
            }
            view.setDrawingCacheEnabled(false);
            bitmap.setHasAlpha(false);
            bitmap.prepareToDraw();
            return bitmap;
        }
    }


    public void layoutView(View v, int width, int height) {
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private void onAnnotCreated(Annotation annot) {
        Log.i("leon", "PDFLayoutView onAnnotCreated in, m_status=" + m_status);
        if (annot != null) {

/*            if (m_status == STA_NOTE) {
                int width = 80;
                int height = 80;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_custom_stamp);
                switch (mSelectedDamageType){
                    case "层高":
                    case "轴网":
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.damage_mark_index_layout,null);
                        TextView damageIndex = view.findViewById(R.id.damage_index);
                        damageIndex.setText("1");
                        layoutView(view, width, height);
                        bitmap = getViewBitmap(view);
                        break;
                }
                Log.d("leon", "bitmap: " + bitmap);
                Document.DocForm form2 = CommonUtil.createImageForm(m_doc,bitmap, 500, 500);

                Log.d("leon", "form: " + form2);

                boolean is = annot.SetIcon(mSelectedDamageType,form2);

                Log.d("leon", "is: " + is);
                int fillColor = (255 << 24) | mNewAnnotColor;
                Log.d("leon", "mNewAnnotColor: " + mNewAnnotColor);
              //  annot.SetFillColor(fillColor);
             //   annot.SetStrokeWidth(20);
             //   annot.SetName("1111111");
             //   annot.SetStrokeColor(0xFFFF0000);

            }*/
            //leon add end

          /*  if((m_status == STA_LINE) || (m_status == STA_RECT) || (m_status == STA_ELLIPSE) || (m_status == STA_INK) || (m_status == STA_EDITBOX)){
                annot.SetReadOnly(true);
            }*/

            annot.SetModifyDate(CommonUtil.getCurrentDate());
            if (!TextUtils.isEmpty(Global.g_annot_def_author))
                annot.SetPopupLabel(Global.g_annot_def_author);

            //Leon add start
            if (m_status == STA_NOTE && m_listener != null) {
                Long ref = annot.GetRef();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", 0);//BUTTON_POPMENU_EDIT/BUTTON_POPMENU_DEL
                    jsonObject.put("annotRef", ref);
                    jsonObject.put("type", mSelectedDamageType);
                    jsonObject.put("axis", "");
                    jsonObject.put("content", "");
                    jsonObject.put("annotX", (int) mMarkX);
                    jsonObject.put("annotY", (int) mMarkY);
                } catch (JSONException e) {
//                        Log.i("leon","PDFLayoutView onMenuClicked edit annot failed!");
                }

                String jsonString = jsonObject.toString();
                Log.i("leon", "PDFLayoutView onAnnotCreated jsonString =" + jsonString);
                m_listener.onPDFNoteAdded(jsonString);
            }
            //Leon add end
        }
        Log.i("leon", "PDFLayoutView onAnnotCreated out");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsIntercept) {
            Log.e("bruce","事件被拦截");
            return true;
        }
        if (m_layout == null) return false;
        if (onTouchNone(event)) return true;
        if (onTouchZoom(event)) return true;
        if (onTouchSelect(event)) return true;
        if (onTouchInk(event)) return true;
        if (onTouchRect(event)) return true;
        if (onTouchEllipse(event)) return true;
        if (onTouchNote(event)) return true;
        if (onTouchLine(event)) return true;
        if (onTouchStamp(event)) return true;
        if (onTouchEditbox(event)) return true;
        if (onTouchPolygon(event)) return true;
        if (onTouchPolyline(event)) return true;
        if (onTouchAnnot(event)) return true;
        return true;
    }

    @Override
    public void computeScroll() {
        if (m_layout != null && m_layout.vScrollCompute())
            invalidate();
    }

    public int PDFGetView() {
        return m_view_mode;
    }

    public void PDFSetView(int style) {
        PDFPos pos = null;
        if (m_layout != null)
            pos = m_layout.vGetPos(0, 0);
        PDFClose();
        m_view_mode = style;
        switch (style) {
            case 1:
                m_layout = new PDFLayoutHorz(getContext(), Global.g_layout_rtol);
                break;
            case 3: {
                PDFLayoutDual layout = new PDFLayoutDual(getContext());
                boolean[] paras = new boolean[m_doc.GetPageCount()];
                int cur = 0;
                while (cur < paras.length) {
                    paras[cur] = false;
                    cur++;
                }
                layout.vSetLayoutPara(null, paras, Global.g_layout_rtol, false);
                m_layout = layout;
            }
            break;
            case 4: {
                PDFLayoutDual layout = new PDFLayoutDual(getContext());
                boolean[] paras = new boolean[m_doc.GetPageCount()];
                int cur = 0;
                while (cur < paras.length) {
                    paras[cur] = true;
                    cur++;
                }
                layout.vSetLayoutPara(null, paras, Global.g_layout_rtol, false);
                m_layout = layout;
            }
            break;
            case 6: {
                PDFLayoutDual layout = new PDFLayoutDual(getContext());
                layout.vSetLayoutPara(null, null, Global.g_layout_rtol, false);
                m_layout = layout;
            }
            break;
            default: {
                PDFLayoutVert layout = new PDFLayoutVert(getContext());
                m_layout = layout;
            }
            break;
        }
        m_layout.vOpen(m_doc, this);
        if (m_bmp_format != Bitmap.Config.ALPHA_8) {
            m_layout.vSetBmpFormat(m_bmp_format);
            m_bmp_format = Bitmap.Config.ALPHA_8;
        }
        if (getWidth() > 0 && getHeight() > 0) {
            m_layout.vResize(getWidth(), getHeight());
            if (m_goto_pos != null) {
                m_layout.vSetPos(0, 0, m_goto_pos);
                m_goto_pos = null;
                invalidate();
            } else if (pos != null) {
                if (style == 3 || style == 4 || style == 6)
                    m_layout.vGotoPage(pos.pageno);
                else
                    m_layout.vSetPos(0, 0, pos);
                m_layout.vMoveEnd();
            }
        }
        layoutWidth = m_layout.vGetWidth();
        layoutHieght = m_layout.vGetHeight();
        invalidate();
    }

    private int m_view_mode;
    private PDFLayout.PDFPos m_save_pos;

    public void PDFSaveView() {
        int w = getWidth();
        int h = getHeight();
        if (m_layout != null) m_save_pos = m_layout.vGetPos(w >> 1, h >> 1);
        else m_save_pos = null;
        PDFSetEditbox(2);//cancel
        if (m_layout != null) {
            PDFLayout layout = m_layout;
            m_layout = null;
            layout.vClose();
        }
    }

    public void PDFRestoreView() {
        //reset stack.
        m_opstack = new PDFLayoutOPStack();
        switch (m_view_mode) {
            case 1:
                m_layout = new PDFLayoutHorz(getContext(), Global.g_layout_rtol);
                break;
            case 3: {
                PDFLayoutDual layout = new PDFLayoutDual(getContext());
                boolean[] paras = new boolean[m_doc.GetPageCount()];
                int cur = 0;
                while (cur < paras.length) {
                    paras[cur] = false;
                    cur++;
                }
                layout.vSetLayoutPara(null, paras, Global.g_layout_rtol, false);
                m_layout = layout;
            }
            break;
            case 4: {
                PDFLayoutDual layout = new PDFLayoutDual(getContext());
                boolean[] paras = new boolean[m_doc.GetPageCount()];
                int cur = 0;
                while (cur < paras.length) {
                    paras[cur] = true;
                    cur++;
                }
                layout.vSetLayoutPara(null, paras, Global.g_layout_rtol, false);
                m_layout = layout;
            }
            break;
            case 6: {
                PDFLayoutDual layout = new PDFLayoutDual(getContext());
                layout.vSetLayoutPara(null, null, Global.g_layout_rtol, false);
                m_layout = layout;
            }
            break;
            default: {
                PDFLayoutVert layout = new PDFLayoutVert(getContext());
                m_layout = layout;
            }
            break;
        }
        m_layout.vOpen(m_doc, this);
        if (m_bmp_format != Bitmap.Config.ALPHA_8) {
            m_layout.vSetBmpFormat(m_bmp_format);
            m_bmp_format = Bitmap.Config.ALPHA_8;
        }
        if (getWidth() > 0 && getHeight() > 0) {
            m_layout.vResize(getWidth(), getHeight());
            if (m_goto_pos != null) {
                m_layout.vSetPos(0, 0, m_goto_pos);
                m_goto_pos = null;
                invalidate();
            } else if (m_save_pos != null) {
                if (m_view_mode == 3 || m_view_mode == 4 || m_view_mode == 6)
                    m_layout.vGotoPage(m_save_pos.pageno);
                else
                    m_layout.vSetPos(0, 0, m_save_pos);
                m_layout.vMoveEnd();
            }
        }
        invalidate();
    }

    public void PDFOpen(Document doc, ILayoutView.PDFLayoutListener listener) {
        m_doc = doc;
        m_listener = listener;
        PDFSetView(Global.g_view_mode);
    }

    public void setAnnotMenu(UIAnnotMenu amenu) {
//        Log.i("leon", "PDFLayoutView setAnnotMenu amenu=" + amenu);
        m_aMenu = amenu;

    }

    public void PDFSetBmpFormat(Bitmap.Config format) {
        if (format == Bitmap.Config.ALPHA_8) return;
        if (m_layout != null) {
            m_layout.vSetBmpFormat(format);
            m_bmp_format = Bitmap.Config.ALPHA_8;
            invalidate();
        } else if (m_bmp_format != format)
            m_bmp_format = format;
    }

    public void PDFGotoPage(int pageno) {
        if (m_layout == null) return;
        if (m_layout.vGetHeight() <= 0 || m_layout.vGetWidth() <= 0) {
            m_goto_pos = new PDFPos();
            m_goto_pos.pageno = pageno;
            m_goto_pos.x = 0;
            m_goto_pos.y = m_doc.GetPageHeight(pageno) + 1;
        } else {
            m_layout.vGotoPage(pageno);
            invalidate();
        }
    }

    public void PDFScrolltoPage(int pageno) {
        if (m_layout == null) return;
        if (m_layout.vGetHeight() <= 0 || m_layout.vGetWidth() <= 0) {
            m_goto_pos = new PDFPos();
            m_goto_pos.pageno = pageno;
            m_goto_pos.x = 0;
            m_goto_pos.y = m_doc.GetPageHeight(pageno) + 1;
        } else {
            m_layout.vScrolltoPage(pageno);
            invalidate();
        }
    }

    public void PDFCloseOnUI() {
        if (m_layout != null) {
            PDFCancelAnnot();
            PDFEndAnnot();
        }
    }

    public void PDFClose() {
        if (m_layout != null) {
            m_layout.vClose();
            m_layout = null;
            m_status = STA_NONE;
            m_zooming = false;
            m_pageno = -1;
        }
    }

    public boolean PDFIsOpen() {
        return m_layout != null && m_doc != null && m_doc.IsOpened();
    }

    public void OnPageChanged(int pageno) {
        m_pageno = pageno;
        if (m_listener != null)
            m_listener.OnPDFPageChanged(pageno);
    }

    public void OnPageRendered(int pageno) {
        invalidate();
        if (m_listener != null && m_layout != null)
            m_listener.OnPDFPageRendered(m_layout.vGetPage(pageno));
    }

    @Override
    public void OnCacheRendered(int pageno) {
        invalidate();
    }

    public void OnFound(boolean found) {
        if (found) invalidate();
        else Toast.makeText(getContext(), R.string.no_more_found, Toast.LENGTH_SHORT).show();
        if (m_listener != null)
            m_listener.OnPDFSearchFinished(found);
    }

    public void OnPageDisplayed(Canvas canvas, VPage vpage) {
        Log.d("bruce","OnPageDisplayed: "+vpage);
        if (m_listener != null) m_listener.OnPDFPageDisplayed(canvas, vpage);
    }

    public void OnTimer() {
        if (m_layout != null) {
            if (m_zooming && m_layout.vZoomEnd()) {
                m_zooming = false;
                invalidate();
            }
        }
    }


    public void setIntercept(boolean isIntercept) {
        mIsIntercept = isIntercept;
    }

    public boolean PDFSetAttachment(String attachmentPath) {
        boolean result = false;
        Page page = m_doc.GetPage(0);
        if (page != null) {
            result = page.AddAnnotAttachment(attachmentPath, 0, new float[]{0, 0, 0, 0});
            if (result && m_listener != null) m_listener.OnPDFPageModified(0);
            page.Close();
        }
        return result;
    }

    public void PDFSetInk(int code) {
        if (code == 0)//start
        {
            m_status = STA_INK;
            m_ink = new Ink(Global.g_ink_width, Global.g_ink_color);
        } else if (code == 1)//end
        {
            m_status = STA_NONE;
            if (m_annot_page != null) {
                Page page = m_doc.GetPage(m_annot_page.GetPageNo());
                if (page != null) {
                    page.ObjsStart();
                    Matrix mat = m_annot_page.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                    mat.TransformInk(m_ink);
                    page.AddAnnotInk(m_ink);
                    mat.Destroy();
                    onAnnotCreated(page.GetAnnot(page.GetAnnotCount() - 1));
                    //add to redo/undo stack.
                    m_opstack.push(new OPAdd(m_annot_page.GetPageNo(), page, page.GetAnnotCount() - 1));
                    m_layout.vRenderSync(m_annot_page);
                    page.Close();
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                }
            }
            if (m_ink != null) m_ink.Destroy();
            m_ink = null;
            m_annot_page = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_ink.Destroy();
            m_ink = null;
            m_annot_page = null;
            invalidate();
        }
    }

    public void PDFSetPolygon(int code) {
        if (code == 0)//start
        {
            m_status = STA_POLYGON;
            m_polygon = new Path();
        } else if (code == 1)//end
        {
            m_status = STA_NONE;
            if (m_annot_page != null) {
                Page page = m_doc.GetPage(m_annot_page.GetPageNo());
                if (page != null && m_polygon.GetNodeCount() > 2) {
                    page.ObjsStart();
                    Matrix mat = m_annot_page.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                    mat.TransformPath(m_polygon);
                    page.AddAnnotPolygon(m_polygon, Global.g_line_annot_color, Global.g_line_annot_fill_color, m_annot_page.ToPDFSize(Global.g_line_annot_width));
                    mat.Destroy();
                    int aidx = page.GetAnnotCount() - 1;
                    onAnnotCreated(page.GetAnnot(aidx));
                    //add to redo/undo stack.
                    m_opstack.push(new OPAdd(m_annot_page.GetPageNo(), page, aidx));
                    m_layout.vRenderSync(m_annot_page);
                    page.Close();
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                }
            }
            if (m_polygon != null) m_polygon.Destroy();
            m_polygon = null;
            m_annot_page = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_polygon.Destroy();
            m_polygon = null;
            m_annot_page = null;
            invalidate();
        }
    }

    public void PDFSetPolyline(int code) {
        if (code == 0)//start
        {
            m_status = STA_POLYLINE;
            m_polygon = new Path();
        } else if (code == 1)//end
        {
            m_status = STA_NONE;
            if (m_annot_page != null) {
                Page page = m_doc.GetPage(m_annot_page.GetPageNo());
                if (page != null && m_polygon.GetNodeCount() > 1) {
                    page.ObjsStart();
                    Matrix mat = m_annot_page.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                    mat.TransformPath(m_polygon);
                    page.AddAnnotPolyline(m_polygon, 0, 0, Global.g_line_annot_color, Global.g_line_annot_fill_color, m_annot_page.ToPDFSize(Global.g_line_annot_width));
                    mat.Destroy();
                    int aidx = page.GetAnnotCount() - 1;
                    onAnnotCreated(page.GetAnnot(aidx));
                    //add to redo/undo stack.
                    m_opstack.push(new OPAdd(m_annot_page.GetPageNo(), page, aidx));
                    m_layout.vRenderSync(m_annot_page);
                    page.Close();
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
                }
            }
            if (m_polygon != null) m_polygon.Destroy();
            m_polygon = null;
            m_annot_page = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_polygon.Destroy();
            m_polygon = null;
            m_annot_page = null;
            invalidate();
        }
    }

    public void PDFSetRect(int code) {
        if (code == 0)//start
        {
            m_status = STA_RECT;
        } else if (code == 1)//end
        {
            Log.d("bdd", "m_rects: " + m_rects);
            if (m_rects != null) {
                int len = m_rects.length;
                int cur;
                PDFVPageSet pset = new PDFVPageSet(len);
                for (cur = 0; cur < len; cur += 4) {
                    PDFPos pos = m_layout.vGetPos((int) m_rects[cur], (int) m_rects[cur + 1]);
                    VPage vpage = m_layout.vGetPage(pos.pageno);
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    Log.d("bdd", "page: " + page);
                    if (page != null) {
                        page.ObjsStart();
                        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                        float[] rect = new float[4];
                        if (m_rects[cur] > m_rects[cur + 2]) {
                            rect[0] = m_rects[cur + 2];
                            rect[2] = m_rects[cur];
                        } else {
                            rect[0] = m_rects[cur];
                            rect[2] = m_rects[cur + 2];
                        }
                        if (m_rects[cur + 1] > m_rects[cur + 3]) {
                            rect[1] = m_rects[cur + 3];
                            rect[3] = m_rects[cur + 1];
                        } else {
                            rect[1] = m_rects[cur + 1];
                            rect[3] = m_rects[cur + 3];
                        }
                        mat.TransformRect(rect);
                        page.AddAnnotRect(rect, vpage.ToPDFSize(Global.g_rect_annot_width), Global.g_rect_annot_color, Global.g_rect_annot_fill_color);
                        mat.Destroy();
                        onAnnotCreated(page.GetAnnot(page.GetAnnotCount() - 1));
                        //add to redo/undo stack.
                        m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                        pset.Insert(vpage);
                        page.Close();
                    }
                }
                for (cur = 0; cur < pset.pages_cnt; cur++) {
                    VPage vpage = pset.pages[cur];
                    m_layout.vRenderSync(vpage);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
            }
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        }
    }


    public void PDFSetEllipse(int code) {
        if (code == 0)//start
        {
            m_status = STA_ELLIPSE;
        } else if (code == 1)//end
        {
            if (m_rects != null) {
                int len = m_rects.length;
                int cur;
                PDFVPageSet pset = new PDFVPageSet(len);
                for (cur = 0; cur < len; cur += 4) {
                    PDFPos pos = m_layout.vGetPos((int) m_rects[cur], (int) m_rects[cur + 1]);
                    VPage vpage = m_layout.vGetPage(pos.pageno);
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    if (page != null) {
                        page.ObjsStart();
                        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                        float[] rect = new float[4];
                        if (m_rects[cur] > m_rects[cur + 2]) {
                            rect[0] = m_rects[cur + 2];
                            rect[2] = m_rects[cur];
                        } else {
                            rect[0] = m_rects[cur];
                            rect[2] = m_rects[cur + 2];
                        }
                        if (m_rects[cur + 1] > m_rects[cur + 3]) {
                            rect[1] = m_rects[cur + 3];
                            rect[3] = m_rects[cur + 1];
                        } else {
                            rect[1] = m_rects[cur + 1];
                            rect[3] = m_rects[cur + 3];
                        }
                        mat.TransformRect(rect);
                        page.AddAnnotEllipse(rect, vpage.ToPDFSize(Global.g_oval_annot_width), Global.g_oval_annot_color, Global.g_oval_annot_fill_color);
                        mat.Destroy();
                        onAnnotCreated(page.GetAnnot(page.GetAnnotCount() - 1));
                        //add to redo/undo stack.
                        m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                        page.Close();
                        pset.Insert(vpage);
                    }
                }
                for (cur = 0; cur < pset.pages_cnt; cur++) {
                    VPage vpage = pset.pages[cur];
                    m_layout.vRenderSync(vpage);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
            }
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        }
    }

    public void PDFSetSelect() {
        if (m_status == STA_SELECT) {
            if (Global.g_use_sel_icons) {
                m_sel_icon1.recycle();
                m_sel_icon2.recycle();
                m_sel_icon1 = null;
                m_sel_icon2 = null;
            }
            m_annot_page = null;
            m_status = STA_NONE;
            invalidate();
        } else {
            if (Global.g_use_sel_icons) {
                m_sel_icon1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.pt_start);
                m_sel_icon2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.pt_end);
            }
            m_annot_page = null;
            m_status = STA_SELECT;
        }
    }

    public void PDFSetNote(int code) {
        if (code == 0) {
            m_note_pages = null;
            m_note_indecs = null;
            m_status = STA_NOTE;
        } else if (code == 1)//end
        {
            if (m_listener != null && m_note_pages != null) {
                int cur = 0;
                int cnt = m_note_pages.length;
                while (cur < cnt) {
                    m_listener.OnPDFPageModified(m_note_pages[cur].GetPageNo());
                    cur++;
                }
            }
            m_note_pages = null;
            m_note_indecs = null;
            m_status = STA_NONE;
        } else//cancel
        {
            if (m_note_pages != null)//remove added note.
            {
                int cur = 0;
                int cnt = m_note_pages.length;
                while (cur < cnt) {
                    VPage vpage = m_note_pages[cur];
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    page.ObjsStart();
                    int index = m_note_indecs[cur];
                    Annotation annot;
                    while ((annot = page.GetAnnot(index)) != null) {
                        annot.RemoveFromPage();
                        m_opstack.undo();
                    }
                    page.Close();
                    m_layout.vRenderSync(vpage);
                    cur++;
                }
                m_note_pages = null;
                m_note_indecs = null;
                invalidate();
            }
            m_status = STA_NONE;
        }
    }


    public void PDFSetLine(int code) {
        Log.i("leon", "PDFLayoutView PDFSetLine code=" + code + ", status=" + m_status);
        if (code == 0)//start
        {
            m_status = STA_LINE;
        } else if (code == 1)//end
        {
            if (m_rects != null) {
                Log.i("leon", "PDFLayoutView PDFSetLine 000");
                int len = m_rects.length;
                int cur;
                float[] pt1 = new float[2];
                float[] pt2 = new float[2];
                PDFVPageSet pset = new PDFVPageSet(len);

                for (cur = 0; cur < len; cur += 4) {
                    PDFPos pos = m_layout.vGetPos((int) m_rects[cur], (int) m_rects[cur + 1]);
                    VPage vpage = m_layout.vGetPage(pos.pageno);
                    pt1[0] = m_rects[cur];
                    pt1[1] = m_rects[cur + 1];
                    pt2[0] = m_rects[cur + 2];
                    pt2[1] = m_rects[cur + 3];
                    Log.i("bruce", "第一次  " + pt1[0] + " pt1[1] " + pt1[1] +
                            "pt2[0] " + pt2[0] + " pt2[1] " + pt2[1]);
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    if (page != null) {
                        page.ObjsStart();
                        Log.i("bruce", "m_layout.vGetX()  " + m_layout.vGetX() + "-----------" +
                                m_layout.vGetY());

                        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());

                        mat.TransformPoint(pt1);
                        mat.TransformPoint(pt2);
                        Log.i("bruce", "第二次 pt1[0] " + pt1[0] + " pt1[1] " + pt1[1] +
                                "pt2[0] " + pt2[0] + " pt2[1] " + pt2[1]);
                        page.AddAnnotLine(pt1, pt2, 0, 0, vpage.ToPDFSize(Global.g_line_annot_width), Global.g_line_annot_color, Global.g_line_annot_fill_color);
                        mat.Destroy();
                        onAnnotCreated(page.GetAnnot(page.GetAnnotCount() - 1));
                        //add to redo/undo stack.
                        m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                        page.Close();
                        pset.Insert(vpage);
                        Log.i("leon", "PDFLayoutView PDFSetLine 222");
                    }
                }
                for (cur = 0; cur < pset.pages_cnt; cur++) {
                    Log.i("leon", "PDFLayoutView PDFSetLine 333");
                    VPage vpage = pset.pages[cur];
                    m_layout.vRenderSync(vpage);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
            }
            Log.i("leon", "PDFLayoutView PDFSetLine 111");
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        }
    }

    public void PDFCancelStamp(){
        m_status = STA_NONE;
        invalidate();
        if (m_icon != null)
            m_icon.recycle();
        m_icon = null;
    }

    public void PDFSetStamp(int code, Bitmap bitmap, float width, float height,String annotationName) {
        m_icon = bitmap;
        if (code == 0)//start
        {
            m_status = STA_STAMP;
            if(m_icon == null) {
                m_icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_custom_stamp).copy(Bitmap.Config.ARGB_8888, true);
            }
        } else if (code == 1)//end
        {
            m_status = STA_STAMP;
            if(m_icon == null) {
                m_icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_custom_stamp).copy(Bitmap.Config.ARGB_8888, true);
            }
            m_dicon = m_doc.NewImage(m_icon, 0);
            if (m_rects != null) {
                int len = m_rects.length;
                int cur;
                PDFVPageSet pset = new PDFVPageSet(len);
                for (cur = 0; cur < len; cur += 4) {
                    PDFPos pos = m_layout.vGetPos((int) m_rects[cur], (int) m_rects[cur + 1]);
                    VPage vpage = m_layout.vGetPage(pos.pageno);
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    if (page != null) {
                        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                        float[] rect = new float[4];
                        if (m_rects[cur] > m_rects[cur + 2]) {
                            rect[0] = m_rects[cur + 2];
                            rect[2] = m_rects[cur];
                        } else {
                            rect[0] = m_rects[cur];
                            rect[2] = m_rects[cur + 2];
                        }
                        if (m_rects[cur + 1] > m_rects[cur + 3]) {
                            rect[1] = m_rects[cur + 3];
                            rect[3] = m_rects[cur + 1];
                        } else {
                            rect[1] = m_rects[cur + 1];
                            rect[3] = m_rects[cur + 3];
                        }

                        for (float f:m_rects) {
                            Log.d("bruce","-111--"+f);
                        }

                        if(rect[0] == rect[2]){
                            float w = (width/2);
                            rect[0] = rect[0]-w;
                            rect[2] = rect[2]+w;
                        }

                        if(rect[1] == rect[3]){
                            float h = (height/2);
                            rect[1] = rect[1]-h;
                            rect[3] = rect[3]+h;
                        }

                        for (float f:rect) {
                            Log.d("bruce","-222--"+f);
                        }

                        mat.TransformRect(rect);
                        page.ObjsStart();
                        page.AddAnnotBitmap(m_dicon, rect);
                        mat.Destroy();
                        Annotation annotation = page.GetAnnot(page.GetAnnotCount() - 1);
                        if(!TextUtils.isEmpty(annotationName)){
                            annotation.SetName(annotationName);
                        }
                        onAnnotCreated(annotation);
                        //add to redo/undo stack.
                        m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                        page.Close();
                        pset.Insert(vpage);
                    }
                }
                for (cur = 0; cur < pset.pages_cnt; cur++) {
                    VPage vpage = pset.pages[cur];
                    m_layout.vRenderSync(vpage);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
            }
            invalidate();
            m_status = STA_NONE;
            m_rects = null;
            if (m_icon != null)
                m_icon.recycle();
            m_icon = null;
        } else//cancel
        {
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
            if (m_icon != null)
                m_icon.recycle();
            m_icon = null;
        }
    }

    public int testX,testY;

    public void PDFSetStamp(int code) {
        if (code == 0)//start
        {
            m_status = STA_STAMP;
            if (m_dicon == null) {
                m_icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_custom_stamp);
                if (m_icon != null) {
                    m_dicon = m_doc.NewImage(m_icon, 0);
                }
            }
        } else if (code == 1)//end
        {
            if (m_rects != null) {
                int len = m_rects.length;
                int cur;
                PDFVPageSet pset = new PDFVPageSet(len);
                for (cur = 0; cur < len; cur += 4) {
                    PDFPos pos = m_layout.vGetPos((int) m_rects[cur], (int) m_rects[cur + 1]);
                    VPage vpage = m_layout.vGetPage(pos.pageno);
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    if (page != null) {
                        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                        float[] rect = new float[4];
                        if (m_rects[cur] > m_rects[cur + 2]) {
                            rect[0] = m_rects[cur + 2];
                            rect[2] = m_rects[cur];
                        } else {
                            rect[0] = m_rects[cur];
                            rect[2] = m_rects[cur + 2];
                        }
                        if (m_rects[cur + 1] > m_rects[cur + 3]) {
                            rect[1] = m_rects[cur + 3];
                            rect[3] = m_rects[cur + 1];
                        } else {
                            rect[1] = m_rects[cur + 1];
                            rect[3] = m_rects[cur + 3];
                        }
                        mat.TransformRect(rect);
                        page.ObjsStart();
                        page.AddAnnotBitmap(m_dicon, rect);
                        mat.Destroy();
                        onAnnotCreated(page.GetAnnot(page.GetAnnotCount() - 1));
                        //add to redo/undo stack.
                        m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                        Log.e("bruce", "PDFSetStamp: "+ rect[0]+"//"+rect[1]+"//"+rect[2]+"//"+rect[3]);
                        Log.e("bruce", "PDFSetStamp22: "+ m_rects[0]+"//"+ m_rects[1]+"//"+m_rects[2]+"//"+m_rects[3]);
                        testX = (int) m_rects[0];
                        testY = (int) m_rects[1];
                        page.Close();
                        pset.Insert(vpage);
                    }
                }
                for (cur = 0; cur < pset.pages_cnt; cur++) {
                    VPage vpage = pset.pages[cur];
                    m_layout.vRenderSync(vpage);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
            }
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
            if (m_icon != null)
                m_icon.recycle();
            m_icon = null;
        } else//cancel
        {
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
            if (m_icon != null)
                m_icon.recycle();
            m_icon = null;
        }
    }

    public void PDFSetEditbox(int code) {
        if (code == 0)//start
        {
            m_status = STA_EDITBOX;
        } else if (code == 1)//end
        {
            if (m_rects != null) {
                int len = m_rects.length;
                int cur;
                PDFVPageSet pset = new PDFVPageSet(len);
                for (cur = 0; cur < len; cur += 4) {
                    PDFPos pos = m_layout.vGetPos((int) m_rects[cur], (int) m_rects[cur + 1]);
                    VPage vpage = m_layout.vGetPage(pos.pageno);
                    Page page = m_doc.GetPage(vpage.GetPageNo());
                    if (page != null) {
                        page.ObjsStart();
                        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
                        float[] rect = new float[4];
                        if (m_rects[cur] > m_rects[cur + 2]) {
                            rect[0] = m_rects[cur + 2];
                            rect[2] = m_rects[cur];
                        } else {
                            rect[0] = m_rects[cur];
                            rect[2] = m_rects[cur + 2];
                        }
                        if (m_rects[cur + 1] > m_rects[cur + 3]) {
                            rect[1] = m_rects[cur + 3];
                            rect[3] = m_rects[cur + 1];
                        } else {
                            rect[1] = m_rects[cur + 1];
                            rect[3] = m_rects[cur + 3];
                        }
                        mat.TransformRect(rect);
                        if (rect[2] - rect[0] < 80) rect[2] = rect[0] + 80;
                        if (rect[3] - rect[1] < 16) rect[1] = rect[3] - 16;
                        page.AddAnnotEditbox(rect, 0xFFFF0000, 0, 0, 26, 0xFFFF0000);
                        mat.Destroy();
                        //add to redo/undo stack.
                        m_opstack.push(new OPAdd(pos.pageno, page, page.GetAnnotCount() - 1));
                        pset.Insert(vpage);
                        page.Close();
                    }
                }
                for (cur = 0; cur < pset.pages_cnt; cur++) {
                    VPage vpage = pset.pages[cur];
                    m_layout.vRenderSync(vpage);
                    if (m_listener != null)
                        m_listener.OnPDFPageModified(vpage.GetPageNo());
                }
            }
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        } else//cancel
        {
            m_status = STA_NONE;
            m_rects = null;
            invalidate();
        }
    }

    public void PDFCancelAnnot() {
        if (m_status == STA_NOTE) PDFSetNote(2);
        if (m_status == STA_RECT) PDFSetRect(2);
        if (m_status == STA_INK) PDFSetInk(2);
        if (m_status == STA_LINE) PDFSetLine(2);
        if (m_status == STA_STAMP) PDFSetStamp(2);
        if (m_status == STA_ELLIPSE) PDFSetEllipse(2);
        if (m_status == STA_EDITBOX) PDFSetEditbox(2);
        if (m_status == STA_ANNOT) PDFEndAnnot();
        invalidate();
    }

    public void removeCurrentNone() {
        //   Log.d("bruce","removeCurrentNone: "+x+" ; "+y);
       /* m_annot_pos = m_layout.vGetPos(x, y);
        m_annot_page = m_layout.vGetPage(m_annot_pos.pageno);
        m_annot_pg = m_doc.GetPage(m_annot_page.GetPageNo());
        m_annot = m_annot_pg.GetAnnotFromPoint(m_annot_pos.x, m_annot_pos.y);

        m_annot_rect = m_annot.GetRect();
        float tmp = m_annot_rect[1];
        m_annot_rect[0] = m_annot_page.GetVX(m_annot_rect[0]) - m_layout.vGetX();
        m_annot_rect[1] = m_annot_page.GetVY(m_annot_rect[3]) - m_layout.vGetY();
        m_annot_rect[2] = m_annot_page.GetVX(m_annot_rect[2]) - m_layout.vGetX();
        m_annot_rect[3] = m_annot_page.GetVY(tmp) - m_layout.vGetY();*/
        m_status = STA_ANNOT;
        PDFRemoveAnnot();
    }


    public void PDFRemoveAnnot() {
        Log.d("bruce","PDFRemoveAnnot m_status "+m_status);
        if (m_status != STA_ANNOT && m_status != STA_STAMP) return;
        if (!PDFCanSave() || (Global.g_annot_readonly && m_annot.IsReadOnly())
                || (Global.g_annot_lock && m_annot.IsLocked())) {
            Toast.makeText(getContext(), R.string.cannot_write_or_encrypted, Toast.LENGTH_SHORT).show();
            PDFEndAnnot();
            return;
        }
        Log.d("bruce", "PDFRemoveAnnot:3333333 ");
        //add to redo/undo stack.
        Page page = m_doc.GetPage(m_annot_page.GetPageNo());
        page.ObjsStart();
        m_opstack.push(new OPDel(m_annot_page.GetPageNo(), page, m_annot.GetIndexInPage()));
        page.Close();

        m_annot.RemoveFromPage();
        m_annot = null;
        m_layout.vRenderSync(m_annot_page);
        if (m_listener != null)
            m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
        PDFEndAnnot();

    }

    public void PDFEndAnnot() {
        if (m_status != STA_ANNOT && m_status != STA_STAMP) return;
        if (m_aMenu != null) m_aMenu.hide();
        if (m_annot_pg != null) {
            m_annot_pg.Close();
            m_annot_pg = null;
        }
        m_annot_page = null;
        m_annot_pos = null;
        m_annot = null;
        invalidate();
        m_status = STA_NONE;
        try {
            if (m_pEdit != null && m_pEdit.isShowing()) m_pEdit.dismiss();
            if (m_pCombo != null && m_pCombo.isShowing()) m_pCombo.dismiss();
        } catch (Exception ignored) {
        }
        if (m_listener != null)
            m_listener.OnPDFAnnotTapped(-1, null);
    }

    public void PDFEditAnnot() {
        Log.i("leon", "PDFLayoutView PDFEditAnnot in");
        if (m_status != STA_ANNOT) return;
        if (!PDFCanSave()) {
            Toast.makeText(getContext(), R.string.cannot_write_or_encrypted, Toast.LENGTH_SHORT).show();
            PDFEndAnnot();
            return;
        }
//        Log.i("leon", "PDFLayoutView PDFEditAnnot ref=" + ref);
//        String[] items = getContext().getResources().getStringArray(R.Array.drawing_damage_type);
    }

    /**
     * 3期损伤弹窗
     */
    private V3DamagePopupWindow.PopupCallback popupCallback = new V3DamagePopupWindow.PopupCallback() {
        @Override
        public void onSelect(int position, int color) {
            Log.d("bruce", "当前选择损伤: " + mV3DamageType.get(position) + "color: " + color);
            mSelectedDamageType = mV3DamageType.get(position);
         //   PDFSetNote(0);
         //   mNewAnnotColor = color;
         //   onTouchNote(mCacheMotionEvent);
            PDFSetStamp(0);
            m_rects = null;
            onTouchStamp(mCacheMotionEvent);
            if (mV3SelectDamageTypeCallback != null) {
                mV3SelectDamageTypeCallback.onSelect(mV3DamageType.get(position));
            }
        }
    };

    private V3SelectDamageTypeCallback mV3SelectDamageTypeCallback;

    public void setV3SelectDamageCallback(V3SelectDamageTypeCallback mV3SelectDamageTypeCallback) {
        this.mV3SelectDamageTypeCallback = mV3SelectDamageTypeCallback;
    }

    public interface V3SelectDamageTypeCallback {
        void onSelect(String type);
    }

    private V3DamagePopupWindow v3DamagePopupWindow;
    private int mV3DefaultColor = 65408;

    public void setMarkV3DefaultColor(int color) {
        mV3DefaultColor = color;
    }

    public interface V3AddGroupPointCallback {

        void onAddPoint(String group, String point,String annotName,String colorBg);

        void onShowPoint(Annotation annot,boolean isShowDelete);

        void onDeletePoint(String annotName);
    }

    private V3AddGroupPointCallback mV3AddGroupPointCallback;

    public void setV3AddGroupPointCallback(V3AddGroupPointCallback V3AddGroupPointCallback) {
        this.mV3AddGroupPointCallback = V3AddGroupPointCallback;
    }

    /**
     * 选择损伤类型popup
     */
    private void showV3DamagePopupWindow() {
        if (v3DamagePopupWindow == null) {
            v3DamagePopupWindow = new V3DamagePopupWindow(getContext(), dp2px(getContext(), 90), mV3DamageType
                    , popupCallback);
            v3DamagePopupWindow.initFirstColor(mV3DefaultColor);
        }
        v3DamagePopupWindow.showAtLocation(this, Gravity.NO_GRAVITY, (int) calculateDamageTypeX(dp2px(getContext(), 90)), (int) mMarkY);
        //   v3DamagePopupWindow.showAsDropDown(this,(int)(Math.round(m_annot_note_x0)),(Math.round(m_annot_note_x0)));
    }

    /**
     * 相对高差添加组名popup
     */
    private RHDiffAddPointPopupWindow rHDiffAddPointPopupWindow;

    private RHDiffAddPointPopupWindow.RHDiffAddPointCallback rhDiffAddPointCallback = new RHDiffAddPointPopupWindow.RHDiffAddPointCallback() {
        @Override
        public void onCancel() {
            PDFCancelStamp();
        }

        @Override
        public void onAddPoint(String group, String point,String annotName,String colorBg) {
            if(mV3AddGroupPointCallback != null){
                mV3AddGroupPointCallback.onAddPoint(group,point,annotName,colorBg);
            }
        }

        @Override
        public void onDeletePoint(String group, String point,String annotName) {
            PDFRemoveAnnot();
            if(mV3AddGroupPointCallback != null){
                mV3AddGroupPointCallback.onDeletePoint(annotName);
            }
        }
    };

    public void showV3RHDiffAddPointPopupWindow(String group,String point,String annotName,String colorBg,boolean isShowDelete){
        if(rHDiffAddPointPopupWindow == null) {
            rHDiffAddPointPopupWindow = new RHDiffAddPointPopupWindow(getContext(), dp2px(getContext(), 300), rhDiffAddPointCallback);
        }
        rHDiffAddPointPopupWindow.setInfo(group,point,annotName,colorBg,isShowDelete);
        rHDiffAddPointPopupWindow.showAtLocation(this, Gravity.NO_GRAVITY, (int) calculateX(dp2px(getContext(), 300)), (int) mMarkY-dp2px(getContext(), 20));
    }

    /**
     * dp 2 px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    private float calculateDamageTypeX(float menuWidth) {
        float screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        float x = mMarkX + menuWidth * 2;
        if (x + menuWidth >= screenWidth)
            x -= (x + menuWidth) - screenWidth;
        return x;
    }


    private float calculateX(float menuWidth) {
        float screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        float x = mMarkX-menuWidth/2;
        if (x + menuWidth >= screenWidth)
            x -= (x + menuWidth) - screenWidth;
        return x;
    }

    private void promptSelectDamageTypeDialog() {
//        PhoneUtil.getPhoneWidth();
        DamageTypeSelectDialog dialog = new DamageTypeSelectDialog(getContext(), dialogClick);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = (int) (Math.round(m_annot_note_x0)) - 340;
        params.y = (int) (Math.round(m_annot_note_y0)) - 270;
//        Log.i("leon", "PDFEditAnnot x=" + params.x + ", y=" + params.y );
        params.width = 0;
        params.height = 0;
        window.setAttributes(params);
        dialog.show();
//        dialog.getWindow().setLayout(570,750);
//        dialog.getWindow().setLayout(770,750);
        dialog.setCanceledOnTouchOutside(false);
    }

    private DamageTypeSelectDialog.IClickCallback dialogClick = new DamageTypeSelectDialog.IClickCallback() {
        @Override
        public void onViewClicked(int selectIndex, boolean checked, int color) {
            String[] items = {"结构构件损伤", "耐久性损伤", "渗漏水", "填充墙斜裂缝", "高坠隐患", "附属构件损坏", "其它/不稳定", "裂缝监测点"};
//            long ref = m_annot.GetRef();
            Log.i("leon", "dialogClick item index=" + selectIndex + ", isCheck=" + checked);
            if (selectIndex >= 0 && checked) {
                mSelectedDamageType = items[selectIndex];
                mNewAnnotColor = color;
                PDFSetNote(0);
                onTouchNote(mCacheMotionEvent);
            } else {
//                PDFUndo();
//                PDFEndAnnot();
            }
        }
    };

    public void PDFPerformAnnot() {
        if (m_status != STA_ANNOT) return;
        Page page = m_doc.GetPage(m_annot_page.GetPageNo());
        if (page == null || m_annot == null) return;
        page.ObjsStart();
        int dest = m_annot.GetDest();
        if (dest >= 0) {
            m_layout.vGotoPage(dest);
            invalidate();
        }
        String js = m_annot.GetJS();
        if (Global.g_exec_js)
            executeAnnotJS();
        if (m_listener != null && js != null)
            m_listener.OnPDFOpenJS(js);
        String uri = m_annot.GetURI();
        if (m_listener != null && uri != null)
            m_listener.OnPDFOpenURI(uri);
        int index;
        String mov = m_annot.GetMovie();
        if (mov != null) {
            index = mov.lastIndexOf('\\');
            if (index < 0) index = mov.lastIndexOf('/');
            if (index < 0) index = mov.lastIndexOf(':');
            String save_file = Global.tmp_path + "/" + mov.substring(index + 1);
            m_annot.GetMovieData(save_file);
            if (m_listener != null)
                m_listener.OnPDFOpenMovie(save_file);
        }
        String snd = m_annot.GetSound();
        if (snd != null) {
            int[] paras = new int[4];
            index = snd.lastIndexOf('\\');
            if (index < 0) index = snd.lastIndexOf('/');
            if (index < 0) index = snd.lastIndexOf(':');
            String save_file = Global.tmp_path + "/" + snd.substring(index + 1);
            m_annot.GetSoundData(paras, save_file);
            if (m_listener != null)
                m_listener.OnPDFOpenSound(paras, save_file);
        }
        String att = m_annot.GetAttachment();
        if (att != null) {
            index = att.lastIndexOf('\\');
            if (index < 0) index = att.lastIndexOf('/');
            if (index < 0) index = att.lastIndexOf(':');
            String save_file = Global.tmp_path + "/" + att.substring(index + 1);
            m_annot.GetAttachmentData(save_file);
            if (m_listener != null)
                m_listener.OnPDFOpenAttachment(save_file);
        }
        /*
        String rend = m_annot.GetRendition();
        if (rend != null) {
            index = rend.lastIndexOf('\\');
            if (index < 0) index = rend.lastIndexOf('/');
            if (index < 0) index = rend.lastIndexOf(':');
            String save_file = Global.tmp_path + "/" + rend.substring(index + 1);
            m_annot.GetRenditionData(save_file);
            if (m_listener != null)
                m_listener.OnPDFOpenRendition(save_file);
        }
         */
        String f3d = m_annot.Get3D();
        if (f3d != null) {
            index = f3d.lastIndexOf('\\');
            if (index < 0) index = f3d.lastIndexOf('/');
            if (index < 0) index = f3d.lastIndexOf(':');
            String save_file = Global.tmp_path + "/" + f3d.substring(index + 1);
            m_annot.Get3DData(save_file);
            if (m_listener != null)
                m_listener.OnPDFOpen3D(save_file);
        }

        boolean reset = m_annot.GetReset();
        if (reset && PDFCanSave()) {
            m_annot.SetReset();
            m_layout.vRenderSync(m_annot_page);
            if (m_listener != null)
                m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
        }
        String tar = m_annot.GetSubmitTarget();
        if (tar != null) {
            if (m_listener != null)
                m_listener.OnPDFOpenURI(tar + "?" + m_annot.GetSubmitTarget());
        }
        page.Close();
        PDFEndAnnot();
    }

    public final void PDFFindStart(String key, boolean match_case, boolean whole_word) {
        m_layout.vFindStart(key, match_case, whole_word);
    }

    public final void PDFFindStart(String key, boolean match_case, boolean whole_word, boolean skipBlank) {
        m_layout.vFindStart(key, match_case, whole_word, skipBlank);
    }

    public final void PDFFind(int dir) {
        m_layout.vFind(dir);
    }

    public final void PDFFindEnd() {
        m_layout.vFindEnd();
        invalidate();
    }

    public boolean PDFSetSelMarkup(int type) {
        if (m_status == STA_SELECT && m_sel != null && m_sel.SetSelMarkup(type)) {
            //add to redo/undo stack.
            Page page = m_sel.GetPage();
            onAnnotCreated(page.GetAnnot(page.GetAnnotCount() - 1));
            m_opstack.push(new OPAdd(m_annot_page.GetPageNo(), page, page.GetAnnotCount() - 1));
            m_layout.vRenderSync(m_annot_page);
            invalidate();
            if (m_listener != null)
                m_listener.OnPDFPageModified(m_annot_page.GetPageNo());
            return true;
        } else {
            return false;
        }
    }

    public final PDFPos PDFGetPos(int x, int y) {
        Log.d("bruce","w: "+m_layout.vGetWidth()+" ; h: "+m_layout.vGetHeight());
        if (m_layout != null)
            return m_layout.vGetPos(x, y);
        else return null;
    }

    public final void PDFSetPos(PDFPos pos, int x, int y) {
        if (m_layout != null) {
            m_layout.vSetPos(x, y, pos);
            invalidate();
        }
    }

    public void BundleSavePos(Bundle bundle) {
        if (m_layout != null) {
            PDFPos pos = m_layout.vGetPos(0, 0);
            bundle.putInt("view_page", pos.pageno);
            bundle.putFloat("view_x", pos.x);
            bundle.putFloat("view_y", pos.y);
        }
    }

    public void BundleRestorePos(Bundle bundle) {
        if (m_layout != null) {
            PDFPos pos = new PDFPos();
            pos.pageno = bundle.getInt("view_page");
            pos.x = bundle.getFloat("view_x");
            pos.y = bundle.getFloat("view_y");
            if (m_layout.vGetHeight() <= 0 || m_layout.vGetWidth() <= 0) {
                m_goto_pos = pos;
            } else {
                m_layout.vSetPos(0, 0, pos);
                invalidate();
            }
        }
    }

    public final Document PDFGetDoc() {
        return m_doc;
    }

    public final boolean PDFCanSave() {
        return !mReadOnly && m_doc.CanSave();
    }

    @Override
    public boolean PDFSave() {
        return m_doc.Save();
    }

    public void PDFUndo() {
        //if(m_opstack.can_undo()) return;
        OPItem item = m_opstack.undo();
        if (item != null) {
            item.op_undo(m_doc);
            PDFGotoPage(item.m_pageno);
            m_layout.vRenderSync(m_layout.vGetPage(item.m_pageno));
            invalidate();
        } else
            Toast.makeText(getContext(), R.string.no_more_undo, Toast.LENGTH_SHORT).show();
    }

    public void PDFRedo() {
        //if(m_opstack.can_redo()) return;
        OPItem item = m_opstack.redo();
        if (item != null) {
            item.op_redo(m_doc);
            PDFGotoPage(item.m_pageno);
            m_layout.vRenderSync(m_layout.vGetPage(item.m_pageno));
            invalidate();
        } else
            Toast.makeText(getContext(), R.string.no_more_redo, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void finalize() throws Throwable {
        PDFClose();
        super.finalize();
    }

    public float PDFGetScale() {
        if (m_layout != null)
            return m_layout.vGetScale();
        return 1;
    }

    public float PDFGetMinScale() {
        if (m_layout != null)
            return m_layout.vGetMinScale();
        return 1;
    }

    public float PDFGetX() {
        return m_layout != null ? m_layout.vGetX() : 0;
    }

    public float PDFGetY() {
        return m_layout != null ? m_layout.vGetY() : 0;
    }

    public void PDFUpdatePage(int pageno) {
        if (m_layout != null)
            m_layout.vRenderSync(m_layout.vGetPage(pageno));
    }

    public void setReadOnly(boolean readonly) {
        mReadOnly = readonly;
    }

    public void PDFSetZoom(int vx, int vy, PDFPos pos, float zoom) {
        if (m_layout != null) m_layout.vZoomSet(vx, vy, pos, zoom);
    }

    public float PDFGetZoom() {
        return m_layout != null ? m_layout.vGetZoom() : 0;
    }

    public float[] toPDFRect(float[] viewRect) {
        if (m_layout != null) {
            VPage vpage = m_layout.vGetPage(m_pageno);
            Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
            mat.TransformRect(viewRect);
        }
        return viewRect;
    }

    private static int tmp_idx = 0;

    private void executeAnnotJS() {
        if (!TextUtils.isEmpty(m_annot.GetJS()))
            runJS(m_annot.GetJS());
        if (!TextUtils.isEmpty(m_annot.GetAdditionalJS(1)))
            runJS(m_annot.GetAdditionalJS(1));
    }

    private void runJS(String js) {
        try {
            m_doc.RunJS(js, new Document.PDFJSDelegate() {
                @Override
                public void OnConsole(int cmd, String para) {
                    //cmd-> 0:clear, 1:hide, 2:println, 3:show
                }

                @Override
                public int OnAlert(int btn, String msg, String title) {
//                    Log.d(PDFLayoutView.class.getSimpleName(), "Alert {title:\"" + title + "\",message:\"" + msg + "\",button:" + btn + ",return:1}\r\n");
                    return 1;
                }

                @Override
                public boolean OnDocClose() {
                    return false;
                }

                @Override
                public String OnTmpFile() {
                    tmp_idx++;
                    return Global.tmp_path + "/" + tmp_idx + ".tmp";
                }

                @Override
                public void OnUncaughtException(int code, String msg) {
//                    Log.d(PDFLayoutView.class.getSimpleName(), "code = " + code + ", msg = " + msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    @Override
    public void PDFAddAnnotRect(float x, float y, float width, float height, int p)
    {
        // init the page
        VPage vpage = m_layout.vGetPage(p);
        Page page = m_doc.GetPage(p);

        // init objects
        page.ObjsStart();

        // create the annotation rect
        float[] rect = new float[4];
        rect[0] = x;            //left
        rect[1] = y;            //top
        rect[2] = x + width;    //right
        rect[3] = y + height;   //bottom

        // add the annotation
        boolean res = page.AddAnnotRect(rect, vpage.ToPDFSize(Global.rect_annot_width), Global.rect_annot_color, Global.rect_annot_fill_color);
        page.Close();

        // reload the page
        if (res) m_layout.vRenderSync(vpage);
    }
    */

    public void addTextBox(float x, float y) {
        float[] rect = new float[4];
        rect[0] = x;
        rect[1] = y;
        rect[2] = x + 300;
        rect[3] = y + 50;

        VPage vpage = m_layout.vGetPage(m_pageno);
        if (vpage == null) return;
        Page page = m_doc.GetPage(vpage.GetPageNo());
        if (page == null) return;

        Matrix mat = vpage.CreateInvertMatrix(m_layout.vGetX(), m_layout.vGetY());
        mat.TransformRect(rect);

        page.AddAnnotEditbox(rect, Color.TRANSPARENT, 0, Color.WHITE, 13, Color.BLACK);
        Page.Annotation newAnnot = page.GetAnnot(page.GetAnnotCount() - 1);

        newAnnot.SetEditText("Example Text");

        m_layout.vRenderSync(vpage);
        invalidate();
    }

    @Override
    public int GetScreenX(float pdfX, int pageno) {
        VPage vPage = m_layout.vGetPage(pageno);
        return vPage.GetVX(pdfX) - m_layout.vGetX();
    }

    @Override
    public int GetScreenY(float pdfY, int pageno) {
        VPage vPage = m_layout.vGetPage(pageno);
        return vPage.GetVY(pdfY) - m_layout.vGetY();
    }

    //leon add start
    @Override
    public void onButtonPrevClicked() {
        Log.i("leon", "PDFViewView prev pressed");
        if (m_listener != null)
            m_listener.onButtonPrevPressed();
    }

    @Override
    public void onButtonNextClicked() {
        Log.i("leon", "PDFViewView next pressed");
        if (m_listener != null)
            m_listener.onButtonNextPressed();
    }
    //leon add end
}