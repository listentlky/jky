package com.sribs.bdd.v3.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.bean.CheckRHDChoosePointBean;
import com.sribs.bdd.v3.popup.ChooseRHDPointPopupWindow;
import com.sribs.bdd.v3.popup.OneChoosePopupWindow;
import com.sribs.bdd.v3.util.LogUtils;
import com.sribs.common.bean.db.DamageV3Bean;
import com.sribs.common.bean.db.RelativeHDiffInfoBean;
import com.sribs.common.bean.db.RelativeHDiffPointBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * create time: 2022/9/8
 * author: bruce
 * description:
 */
public class CheckHTableView extends LinearLayout {

    private Context mContext;

    private List<DamageV3Bean> datas = new ArrayList();

    private List<ViewHolder> mViewHolderList = new ArrayList<>();

    private List<RelativeHDiffPointBean> mChoosePointList = new ArrayList<>();

    private LayoutParams layoutParams;

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

    public CheckHTableView setDatas(List<DamageV3Bean> datas) {
        mChoosePointList = new ArrayList<>();
        LogUtils.INSTANCE.d("mChoosePointList111: "+mChoosePointList);
        //设置测点数据
        if(datas != null && datas.size()>0){
            this.datas = datas;
            DamageV3Bean damageV3Bean = datas.get(0);
            if(damageV3Bean != null){
                List<RelativeHDiffPointBean> pointList = damageV3Bean.getPointList();
                LogUtils.INSTANCE.d("pointList: "+pointList);
                if(pointList != null && pointList.size()>0){
                    mChoosePointList.addAll(pointList);
                }
            }
            LogUtils.INSTANCE.d("mChoosePointList2222: "+mChoosePointList);
        }
        return this;
    }

    public void addPointBean(RelativeHDiffPointBean choosePointBean) {

        boolean isMatch = false;

        LogUtils.INSTANCE.d("addPointBean: " + mChoosePointList);
        Iterator<RelativeHDiffPointBean> pointBeanIterator = mChoosePointList.iterator();
        while (pointBeanIterator.hasNext()) {
            RelativeHDiffPointBean pointNext = pointBeanIterator.next();

            if (choosePointBean.getName().equals(pointNext.getName())) {  //组相同
                isMatch = true;
                Iterator<RelativeHDiffPointBean.Item> pointItemIterator = pointNext.getMenu().iterator();
                while (pointItemIterator.hasNext()) {
                    RelativeHDiffPointBean.Item pointMenuItem = pointItemIterator.next();

                    Iterator<RelativeHDiffPointBean.Item> addMenuIterator = choosePointBean.getMenu().iterator();
                    while (addMenuIterator.hasNext()) {
                        RelativeHDiffPointBean.Item addMenuItem = addMenuIterator.next();
                        if (pointMenuItem.getName().equals(addMenuItem.getName())) {
                            addMenuIterator.remove();
                        }
                    }
                }
                if (choosePointBean.getMenu().size() > 0) {
                    LogUtils.INSTANCE.d("choosePointBean.getMenu().size()>0: " + choosePointBean.getMenu().toString());
                    pointNext.getMenu().addAll(choosePointBean.getMenu());
                }
            }
        }
        if (!isMatch) {
            LogUtils.INSTANCE.d("!isMatch: " + choosePointBean);
            mChoosePointList.add(choosePointBean);
            LogUtils.INSTANCE.d("!isMatch22: " + mChoosePointList);
        }
    }

    /**
     * 删除对应添加的测点
     */
    public void remove(String annotName) {
        LogUtils.INSTANCE.d("remove  annotName:" + annotName);
        Iterator<RelativeHDiffPointBean> pointBeanIterator = mChoosePointList.iterator();
        while (pointBeanIterator.hasNext()) {
            RelativeHDiffPointBean next = pointBeanIterator.next();
            Iterator<RelativeHDiffPointBean.Item> iterator = next.getMenu().iterator();
            while (iterator.hasNext()) {
                RelativeHDiffPointBean.Item next2 = iterator.next();
                if (next2.getAnnotName().equals(annotName)) {
                    iterator.remove();
                }
            }
            if (!next.getName().equals("闭合点") && next.getMenu().size() <= 0) {
                pointBeanIterator.remove();
            }
        }
    }

    public List<RelativeHDiffPointBean> getPointBean() {
        return mChoosePointList;
    }

    private void init(Context context) {
        this.mContext = context;
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen._15sdp));
        setOrientation(VERTICAL);
        if (mChoosePointList.size() <= 0) {
            RelativeHDiffPointBean pointBean = new RelativeHDiffPointBean();
            pointBean.setName("闭合点");
            mChoosePointList.add(pointBean);
        }
        initTitleTable();
    }

    public void build() {
        removeAllViews();
        mViewHolderList.clear();
        if (mChoosePointList.size() <= 0) {
            RelativeHDiffPointBean pointBean = new RelativeHDiffPointBean();
            pointBean.setName("闭合点");
            mChoosePointList.add(pointBean);
        }
        initTitleTable();
    }

    public void initTitleTable() {
        View inflate = View.inflate(mContext, R.layout.check_table_item, null);
        TextView dhText = (TextView) inflate.findViewById(R.id.table_dh);
        dhText.setEnabled(false);
        inflate.findViewById(R.id.check_but_zd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem("TP", null);
            }
        });
        inflate.findViewById(R.id.check_but_delete).setEnabled(false);
        EditText hsText = (EditText) inflate.findViewById(R.id.table_hs_edit);
        EditText qsText = (EditText) inflate.findViewById(R.id.table_qs_edit);
        EditText qhsgcText = (EditText) inflate.findViewById(R.id.table_qhsgc_edit);
        EditText gcText = (EditText) inflate.findViewById(R.id.table_gc_edit);
        EditText cdxdgcText = (EditText) inflate.findViewById(R.id.table_cdxdgc_edit);

        ViewHolder vh = new ViewHolder();
        vh.inflate = inflate;
        vh.dnTag = dhText;
        vh.EditViewList.add(hsText);
        vh.EditViewList.add(qsText);
        vh.EditViewList.add(qhsgcText);
        vh.EditViewList.add(gcText);
        vh.EditViewList.add(cdxdgcText);
        mViewHolderList.add(vh);
        resetItem();
        addView(inflate, layoutParams);

        if (datas != null
                && datas.size() > 0
                && datas.get(0) != null
                && datas.get(0).getRhdiffInfo() != null
                && datas.get(0).getRhdiffInfo().size()>0) {
            for (int i = 0; i < datas.get(0).getRhdiffInfo().size(); i++) {
                RelativeHDiffInfoBean infoBean = datas.get(0).getRhdiffInfo().get(i);
                if (i == 0) { //BM1
                    hsText.setText(infoBean.getAfter());
                    gcText.setText(infoBean.getHeight());
                } else {
                    addItem(infoBean.getTag(), infoBean);
                }
            }
        }
    }

    public void addItem(String type, RelativeHDiffInfoBean infoBean) {
        View inflate = View.inflate(mContext, R.layout.check_table_item, null);
        inflate.findViewById(R.id.check_but_zd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem("TP", null);
            }
        });
        inflate.findViewById(R.id.check_but_delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(inflate);
                removeViewHolder(inflate);
            }
        });
        TextView dhText = (TextView) inflate.findViewById(R.id.table_dh);
        dhText.setSelected(true);
        if (type.startsWith("TP")) {
            dhText.setText("TP");
            dhText.setEnabled(false);
        } else {
            dhText.setText("");
            dhText.setEnabled(true);
        }
        dhText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChooseRHDPointPopupWindow(mContext, (int)(dhText.getWidth()*1.5),
                        mChoosePointList, new ChooseRHDPointPopupWindow.PopupCallback() {
                    @Override
                    public void onSelect(String text) {
                        dhText.setText(text);
                    }
                }).showAsDropDown(dhText, 5, 2);
            }
        });
        EditText hsText = (EditText) inflate.findViewById(R.id.table_hs_edit);
        EditText qsText = (EditText) inflate.findViewById(R.id.table_qs_edit);
        EditText qhsgcText = (EditText) inflate.findViewById(R.id.table_qhsgc_edit);
        EditText gcText = (EditText) inflate.findViewById(R.id.table_gc_edit);
        EditText cdxdgcText = (EditText) inflate.findViewById(R.id.table_cdxdgc_edit);

        ViewHolder vh = new ViewHolder();
        vh.inflate = inflate;
        vh.dnTag = dhText;
        vh.EditViewList.add(hsText);
        vh.EditViewList.add(qsText);
        vh.EditViewList.add(qhsgcText);
        vh.EditViewList.add(gcText);
        vh.EditViewList.add(cdxdgcText);
        mViewHolderList.add(vh);
        resetItem();
        setTopText();
        addView(inflate, layoutParams);
        if (infoBean != null) {
            if (type.startsWith("TP")) {
                dhText.setText(infoBean.getTag());
                hsText.setText(infoBean.getAfter());
                qsText.setText(infoBean.getBefore());
                qhsgcText.setText(infoBean.getAbDiff());
                gcText.setText(infoBean.getHeight());
                cdxdgcText.setText(infoBean.getPointDiff());
            } else if (type.equals("闭合点")) {
                dhText.setText(infoBean.getTag());
                qsText.setText(infoBean.getBefore());
                qhsgcText.setText(infoBean.getAbDiff());
                gcText.setText(infoBean.getHeight());
                cdxdgcText.setText(infoBean.getPointDiff());
            } else {
                dhText.setText(infoBean.getTag());
                qsText.setText(infoBean.getBefore());
                qhsgcText.setText(infoBean.getAbDiff());
                gcText.setText(infoBean.getHeight());
                cdxdgcText.setText(infoBean.getPointDiff());
            }
        }
    }

    private void resetItem() {
        for (ViewHolder viewHolder : mViewHolderList) {
            List<EditText> EditList = viewHolder.EditViewList;
            String tag = viewHolder.dnTag.getText().toString();
            if (tag.equals("BM1")) {
                for (int i = 0; i < EditList.size(); i++) {
                    if (i == 0 || i == 3) {
                        EditList.get(i).setEnabled(true);
                    } else {
                        EditList.get(i).setEnabled(false);
                    }
                }
            } else if (tag.startsWith("TP")) {
                for (int i = 0; i < EditList.size(); i++) {
                    if (i < 2) {
                        EditList.get(i).setEnabled(true);
                    } else {
                        EditList.get(i).setEnabled(false);
                    }
                }
            } else if (tag.startsWith("闭合点")) {
                for (int i = 0; i < EditList.size(); i++) {
                    if (i == 1) {
                        EditList.get(i).setEnabled(true);
                    } else {
                        EditList.get(i).setEnabled(false);
                    }
                }
            } else { //自填测点
                for (int i = 0; i < EditList.size(); i++) {
                    if (i == 1) {
                        EditList.get(i).setEnabled(true);
                    } else {
                        EditList.get(i).setEnabled(false);
                    }
                }
            }
        }
    }

    private void removeViewHolder(View inflate) {
        Iterator<ViewHolder> iterator = mViewHolderList.iterator();
        while (iterator.hasNext()) {
            ViewHolder next = iterator.next();
            if (next.inflate == inflate) {
                iterator.remove();
            }
        }
        setTopText();
    }

    public void setTopText() {
        List<TextView> topList = new ArrayList<>();
        for (int k = 0; k < mViewHolderList.size(); k++) {
            ViewHolder viewHolder = mViewHolderList.get(k);
            if (viewHolder.dnTag.getText().toString().startsWith("TP")) {
                topList.add(viewHolder.dnTag);
            }
        }
        for (int i = 0; i < topList.size(); i++) {
            topList.get(i).setText("TP" + (i + 1));
        }
    }

    /**
     * 获取表格数据
     * @return
     */
    public ArrayList<RelativeHDiffInfoBean> getTableInfo() {
        ArrayList<RelativeHDiffInfoBean> infoBeanList = new ArrayList<>();
        for (ViewHolder viewHolder : mViewHolderList) {
            RelativeHDiffInfoBean infoBean = new RelativeHDiffInfoBean();
            infoBean.setTag(viewHolder.dnTag.getText().toString());
            List<EditText> EditList = viewHolder.EditViewList;
            infoBean.setAfter(EditList.get(0).getText().toString());
            infoBean.setBefore(EditList.get(1).getText().toString());
            infoBean.setAbDiff(EditList.get(2).getText().toString());
            infoBean.setHeight(EditList.get(3).getText().toString());
            infoBean.setPointDiff(EditList.get(4).getText().toString());
            infoBeanList.add(infoBean);
        }
        LogUtils.INSTANCE.d("生成数据: " + infoBeanList.toString());
        return infoBeanList;
    }

    public ViewHolder beforeTPVH(int index) {
        for (int k = index; k >= 0; k--) {
            ViewHolder viewHolder = mViewHolderList.get(k);
            if (viewHolder.dnTag.getText().toString().startsWith("TP")) {
                return viewHolder;
            }
        }
        return null;
    }

    /**
     * 计算
     */
    public void calculate(boolean isJSpc) {
        if(isJSpc){
            if(!mViewHolderList.get(mViewHolderList.size()-1).dnTag.getText().toString().equals("闭合点")){
                Toast.makeText(getContext(), "请在最后添加闭合点", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<TextView> topList = new ArrayList<>();
        for (ViewHolder viewHolder : mViewHolderList) {
            if (viewHolder.dnTag.getText().toString().startsWith("TP")) {
                topList.add(viewHolder.dnTag);
            }
        }

        for (int k = 0; k < mViewHolderList.size(); k++) {
            ViewHolder viewHolder = mViewHolderList.get(k);
            List<EditText> EditList = viewHolder.EditViewList;
            String tag = viewHolder.dnTag.getText().toString();

            ViewHolder viewHolder1 = beforeTPVH(k - 1); // 为null代表前面无转点，即为第一个转点
            if (viewHolder1 == null) {
                LogUtils.INSTANCE.d("上一个 TP点 is null： " + viewHolder1);
            } else {
                LogUtils.INSTANCE.d("上一个 TP点： " + viewHolder1.toString());
            }
            LogUtils.INSTANCE.d("tag： " + tag);
            if (tag.equals("BM1")) {
                ViewHolder viewHolder2 = mViewHolderList.get(0); //BM1
                String bm1hs = viewHolder2.EditViewList.get(0).getText().toString();
                String bm1gc = viewHolder2.EditViewList.get(3).getText().toString();
                if (TextUtils.isEmpty(bm1hs)) {
                    Toast.makeText(getContext(), "请填写BM1后视", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(bm1gc)) {
                    Toast.makeText(getContext(), "请填写BM1高程", Toast.LENGTH_SHORT).show();
                    return;
                }
                continue;
            } else if (tag.startsWith("TP")) {
                if (viewHolder1 == null) {
                    ViewHolder viewHolder2 = mViewHolderList.get(0); //BM1
                    String bm1hs = viewHolder2.EditViewList.get(0).getText().toString();
                    String bm1gc = viewHolder2.EditViewList.get(3).getText().toString();
                    if (TextUtils.isEmpty(bm1hs)) {
                        Toast.makeText(getContext(), "请填写BM1后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(bm1gc)) {
                        Toast.makeText(getContext(), "请填写BM1高程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(0).getText())) {
                        Toast.makeText(getContext(), "请填写TP1后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(1).getText())) {
                        Toast.makeText(getContext(), "请填写TP1前视", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int dqgc = Integer.valueOf(bm1hs) - Integer.valueOf(EditList.get(1).getText().toString());

                    EditList.get(2).setText(""+dqgc); //前后视高差  BM1后视-当前前视-闭合差
                    EditList.get(3).setText(""+(Integer.valueOf(bm1gc) + dqgc));  //高程  BM1高程+当前高差
                //    EditList.get(4).setText("4444");  //高程  当前测点高程-测点高程最大值

                } else { //上一个转点
                    String onTPhs = viewHolder1.EditViewList.get(0).getText().toString();
                    String onTPqs = viewHolder1.EditViewList.get(1).getText().toString();
                    String onTPgc = viewHolder1.EditViewList.get(3).getText().toString();

                    if (TextUtils.isEmpty(onTPhs)) {
                        Toast.makeText(getContext(), "请填写" + viewHolder1.dnTag.getText() + "转点后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(onTPqs)) {
                        Toast.makeText(getContext(), "请填写" + viewHolder1.dnTag.getText() + "转点前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(0).getText())) {
                        Toast.makeText(getContext(), "请填写" + viewHolder.dnTag.getText() + "转点后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(1).getText())) {
                        Toast.makeText(getContext(), "请填写" + viewHolder.dnTag.getText() + "转点前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int dqgc = Integer.valueOf(onTPhs) - Integer.valueOf(EditList.get(1).getText().toString());
                    EditList.get(2).setText(""+dqgc); //前后视高差  上个转点后视-当前前视-闭合差
                    EditList.get(3).setText(""+(Integer.valueOf(onTPgc)+dqgc));  //高程  上个转点高程+当前高差
                //    EditList.get(4).setText("5555");  //高程  当前测点高程-测点高程最大值
                }
            } else if (tag.equals("闭合点")) {

                if (viewHolder1 == null) {
                    ViewHolder viewHolder2 = mViewHolderList.get(0); //BM1
                    String bm1hs = viewHolder2.EditViewList.get(0).getText().toString();
                    String bm1gc = viewHolder2.EditViewList.get(3).getText().toString();
                    if (TextUtils.isEmpty(bm1hs)) {
                        Toast.makeText(getContext(), "请填写BM1后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(bm1gc)) {
                        Toast.makeText(getContext(), "请填写BM1高程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(1).getText())) {
                        Toast.makeText(getContext(), "请填写" + viewHolder.dnTag.getText() + "前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int dqgc = Integer.valueOf(bm1hs) - Integer.valueOf(EditList.get(1).getText().toString());
                    EditList.get(2).setText(""+dqgc); //前后视高差  BM1后视-当前前视-闭合差
                    EditList.get(3).setText(""+(Integer.valueOf(bm1gc)+dqgc));  //高程  BM1高程+当前高差
                 //   EditList.get(4).setText("6666");  //高程  当前测点高程-测点高程最大值

                } else { //上一个转点
                    String onTPhs = viewHolder1.EditViewList.get(0).getText().toString();
                    String onTPqs = viewHolder1.EditViewList.get(1).getText().toString();
                    String onTPgc = viewHolder1.EditViewList.get(3).getText().toString();

                    if (TextUtils.isEmpty(onTPhs)) {
                        Toast.makeText(getContext(), "请填写" + viewHolder1.dnTag.getText() + "转点后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(onTPqs)) {
                        Toast.makeText(getContext(), "请填写" + viewHolder1.dnTag.getText() + "转点前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(1).getText())) {
                        Toast.makeText(getContext(), "请填写" + viewHolder.dnTag.getText() + "前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int dqgc = Integer.valueOf(onTPhs) - Integer.valueOf(EditList.get(1).getText().toString());
                    EditList.get(2).setText(""+dqgc); //前后视高差  上个转点后视-当前前视-闭合差
                    EditList.get(3).setText(""+(Integer.valueOf(onTPgc)+dqgc));  //高程  上个转点高程+当前高差
                 //   EditList.get(4).setText("7777");  //高程  当前测点高程-测点高程最大值
                }
            } else { //自填测点
                if (viewHolder1 == null) {
                    ViewHolder viewHolder2 = mViewHolderList.get(0); //BM1
                    String bm1hs = viewHolder2.EditViewList.get(0).getText().toString();
                    String bm1gc = viewHolder2.EditViewList.get(3).getText().toString();
                    if (TextUtils.isEmpty(bm1hs)) {
                        Toast.makeText(getContext(), "请填写BM1后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(bm1gc)) {
                        Toast.makeText(getContext(), "请填写BM1高程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(1).getText())) {
                        Toast.makeText(getContext(), "请填写" + viewHolder.dnTag.getText() + "前视", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int dqgc = Integer.valueOf(bm1hs) - Integer.valueOf(EditList.get(1).getText().toString());

                    EditList.get(2).setText(""+dqgc); //前后视高差  BM1后视-当前前视-闭合差
                    EditList.get(3).setText(""+(Integer.valueOf(bm1gc)+dqgc));  //高程  BM1高程+当前高差
                 //   EditList.get(4).setText("8888");  //高程  当前测点高程-测点高程最大值

                } else { //上一个转点
                    String onTPhs = viewHolder1.EditViewList.get(0).getText().toString();
                    String onTPqs = viewHolder1.EditViewList.get(1).getText().toString();
                    String onTPgc = viewHolder1.EditViewList.get(3).getText().toString();

                    if (TextUtils.isEmpty(onTPhs)) {
                        Toast.makeText(getContext(), "请填写" + viewHolder1.dnTag.getText() + "转点后视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(onTPqs)) {
                        Toast.makeText(getContext(), "请填写" + viewHolder1.dnTag.getText() + "转点前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(EditList.get(1).getText())) {
                        Toast.makeText(getContext(), "请填写" + viewHolder.dnTag.getText() + "前视", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int dqgc = Integer.valueOf(onTPhs) - Integer.valueOf(EditList.get(1).getText().toString());
                    EditList.get(2).setText(""+dqgc); //前后视高差  上个转点后视-当前前视-闭合差
                    LogUtils.INSTANCE.d("111111： "+Integer.valueOf(onTPgc));
                    LogUtils.INSTANCE.d("222222： "+dqgc);
                    EditList.get(3).setText(""+(Integer.valueOf(onTPgc)+dqgc));  //高程  上个转点高程+当前高差
                 //   EditList.get(4).setText("9999");  //高程  当前测点高程-测点高程最大值
                }
            }
        }

        /**
         * 是否计算闭合差
         */
        if(isJSpc){
            calculatePCZ();
        }

        /**
         * 计算相对高差
         */
        for (int q = 1; q < mViewHolderList.size(); q++) {
            ViewHolder viewHolder = mViewHolderList.get(q);
            if(viewHolder.dnTag.getText().toString().contains("BM1")||
                    viewHolder.dnTag.getText().toString().contains("TP")||
                    viewHolder.dnTag.getText().toString().contains("闭合点")){
                continue;
            }
            List<EditText> EditList = viewHolder.EditViewList;
            int currentGc = Integer.valueOf(EditList.get(3).getText().toString());
            int currentGroupMaxGc = getCurrentGroupMaxGc(viewHolder.dnTag.getText().toString().split("-")[0],currentGc);
            EditList.get(4).setText(""+(currentGc-currentGroupMaxGc));
        }

        getTableInfo();
    }

    /**
     * 计算当前组的最大高程值
     * @return
     */
    private int getCurrentGroupMaxGc(String group,int gcMax){

        for (int j = 1; j < mViewHolderList.size(); j++) {
            ViewHolder viewHolder = mViewHolderList.get(j);
            if(viewHolder.dnTag.getText().toString().contains("BM1")||
                    viewHolder.dnTag.getText().toString().contains("TP")||
                    viewHolder.dnTag.getText().toString().contains("闭合点")){
                continue;
            }
            if(viewHolder.dnTag.getText().toString().split("-")[0].equals(group)) {
                List<EditText> EditList = viewHolder.EditViewList;
                int currentGc = Integer.valueOf(EditList.get(3).getText().toString());
                if (gcMax < currentGc) {
                    gcMax = currentGc;
                }
            }
        }
        LogUtils.INSTANCE.d("分组为 "+group+" ; 高程最大值: "+gcMax);
        return gcMax;
    }


    private int bhc;

    /**
     * 获取闭合差
     * @return
     */
    public String getBHC(){
        return ""+bhc;
    }
    /**
     * 计算平差值
     * return 闭合差 用于UI展示
     */
    private void calculatePCZ(){
        bhc = 0;
        int zdCount = 0;
        for (int i = 1; i < mViewHolderList.size(); i++) {
            ViewHolder viewHolder = mViewHolderList.get(i);
            String tag = viewHolder.dnTag.getText().toString();
            if(!tag.startsWith("TP") && !tag.equals("闭合点")){
                continue;
            }
            LogUtils.INSTANCE.d("calculatePCZ： "+tag);
            if(tag.startsWith("TP")){
                zdCount++;
            }
            List<EditText> EditList = viewHolder.EditViewList;
            int currentqhsgc = Integer.valueOf(EditList.get(2).getText().toString());
            bhc+=currentqhsgc;
        }

        LogUtils.INSTANCE.d("calculatePCZ222： "+zdCount);
        LogUtils.INSTANCE.d("calculatePCZ333： "+bhc);

        for (int i = 1; i < mViewHolderList.size(); i++) {
            ViewHolder viewHolder = mViewHolderList.get(i);
            String tag = viewHolder.dnTag.getText().toString();
            if(!tag.startsWith("TP")){
                continue;
            }
            List<EditText> EditList = viewHolder.EditViewList;
            int currentqhsgc = Integer.valueOf(EditList.get(2).getText().toString());
            if(zdCount <= 0){
                EditList.get(2).setText(""+(currentqhsgc-bhc));
            }else {
                int pcz = -bhc/zdCount;
                EditList.get(2).setText(""+(currentqhsgc+pcz));
            }
        }
    }

    public void addPoint() {
        addItem("", null);
    }

    class ViewHolder {
        private View inflate;

        private TextView dnTag;

        private List<EditText> EditViewList = new ArrayList<>();

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "inflate=" + inflate +
                    ", dnTag=" + dnTag +
                    ", EditViewList=" + EditViewList +
                    '}';
        }
    }
}
