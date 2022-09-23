package com.sribs.bdd.v3.ui.check.cd.fm

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.google.android.material.button.MaterialButton
import com.radaee.pdf.Document
import com.radaee.pdf.Global
import com.radaee.reader.PDFGLLayoutView
import com.radaee.util.PDFAssetStream
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionBeamEditBinding
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.view.OneChooseSpinnerView
import com.sribs.common.ARouterPath

@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_BEAM_FRAGMENT)
class CheckEditCDBFragment : BaseFragment(R.layout.fragment_check_componentdetection_beam_edit) {

    private val mBinding: FragmentCheckComponentdetectionBeamEditBinding by bindView()

    private var mLeftSpinner1Data = arrayListOf<String>("总高", "净高")
    private var mLeftSpinner2Data = arrayListOf<String>("矩形", "H型", "T型", "其他")
    private lateinit var mLeftRealSpinner1: OneChooseSpinnerView
    private lateinit var mLeftRealSpinner2: OneChooseSpinnerView

    private lateinit var mLeftDesignSpinner1: OneChooseSpinnerView
    private lateinit var mLeftDesignSpinner2: OneChooseSpinnerView

    //梁-标题
    private lateinit var mMenuBeamNameET: EditText

    //梁-轴线
    private lateinit var mMenuBeamAxisView1: View
    private lateinit var mMenuBeamAxisView2: View
    private lateinit var mMenuBeamAxisET: EditText
    private lateinit var mMenuBeamAxisTV: TextView
    private lateinit var mMenuBeamAxisET1: EditText
    private lateinit var mMenuBeamAxisET2: EditText
    private lateinit var mMenuBeamAxisET3: EditText


    //梁-切换
    private lateinit var mBtnMenuChangeAxis: MaterialButton
    private var mIsDefaultOneInput: Boolean = true

    //梁-完成
    private lateinit var mBtnMenuConfirm: MaterialButton


    //左侧menu父布局
    private lateinit var mViewLeftReal: View
    private lateinit var mViewLeftDesign: View

    //左侧实际menu
    private lateinit var mViewLeftRealMenu1: TextView
    private lateinit var mViewLeftRealMenu2: TextView

    //左侧设计menu
    private lateinit var mViewLeftDesignMenu1: TextView
    private lateinit var mViewLeftDesignMenu2: TextView


    //实测截面类型-矩形
    private lateinit var mViewLeftRealR: View
    private lateinit var mEditViewLeftRealR1: EditText
    private lateinit var mEditViewLeftRealR2: EditText

    //实测截面类型-H型
    private lateinit var mViewLeftRealH: View
    private lateinit var mEditViewLeftRealH1: EditText
    private lateinit var mEditViewLeftRealH2: EditText
    private lateinit var mEditViewLeftRealH3: EditText
    private lateinit var mEditViewLeftRealH4: EditText

    //实测截面类型-T型
    private lateinit var mViewLeftRealT: View
    private lateinit var mEditViewLeftRealT1: EditText
    private lateinit var mEditViewLeftRealT2: EditText
    private lateinit var mEditViewLeftRealT3: EditText
    private lateinit var mEditViewLeftRealT4: EditText

    //实测截面类型-其他
    private lateinit var mViewLeftRealA: View
    private lateinit var mEditViewLeftRealA1: EditText
    private lateinit var mLeftRealAnotherEA2: EditText



    //实测截面类型-备注
    private lateinit var mEditViewLeftRealNote: EditText
    private lateinit var mEditViewLeftDesignNote: EditText

    override fun deinitView() {
    }



    override fun initView() {


        //梁-标题
        mMenuBeamNameET = mBinding.checkCpdSubtitle1.checkEdit


        //左侧父布局
        mViewLeftReal = mBinding.checkCpdBeamLeftRealUi.content
        mViewLeftDesign = mBinding.checkCpdBeamLeftDesignUi.content

        //左侧实际menu
        mViewLeftRealMenu1 = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftMenu.checkCpdLeftMenu1
        mViewLeftRealMenu2 = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftMenu.checkCpdLeftMenu2

        //左侧设计menu
        mViewLeftDesignMenu1 = mBinding.checkCpdBeamLeftDesignUi.checkCpdLeftMenu.checkCpdLeftMenu1
        mViewLeftDesignMenu2 = mBinding.checkCpdBeamLeftDesignUi.checkCpdLeftMenu.checkCpdLeftMenu2

        //梁-切换
        mBtnMenuChangeAxis = mBinding.checkCpdSubtitleChange
        mMenuBeamAxisView1 = mBinding.checkCpdSubtitle2.content
        mMenuBeamAxisView2 = mBinding.checkCpdSubtitle2Second.content
        mMenuBeamAxisET = mBinding.checkCpdSubtitle2Second.checkEdit
        mMenuBeamAxisTV = mBinding.checkCpdSubtitle2Second.checkEditName
        mMenuBeamAxisTV.setText("轴线")

        mMenuBeamAxisET1 = mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1
        mMenuBeamAxisET2 = mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2
        mMenuBeamAxisET3 = mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis3

        //梁-完成
        mBtnMenuConfirm = mBinding.checkCpdSubtitleConfirm


        // 实测截面类型-下拉框1
        mLeftRealSpinner1 = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealSpinner1
        mLeftRealSpinner1.setBackgroundResource(R.drawable.check_gray_stroke_bg)
        mLeftRealSpinner1.setSpinnerData(mLeftSpinner1Data).build()

        mLeftDesignSpinner1 = mBinding.checkCpdBeamLeftDesignUi.checkCpdLeftRealSpinner1
        mLeftDesignSpinner1.setBackgroundResource(R.drawable.check_gray_stroke_bg)
        mLeftDesignSpinner1.setSpinnerData(mLeftSpinner1Data).build()


        // 实测截面类型-下拉框2
        mLeftRealSpinner2 = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealSpinner2
        mLeftRealSpinner2.setBackgroundResource(R.drawable.check_gray_stroke_bg)
        mLeftRealSpinner2.setSpinnerData(mLeftSpinner2Data).build()

        mLeftDesignSpinner2 = mBinding.checkCpdBeamLeftDesignUi.checkCpdLeftRealSpinner2
        mLeftDesignSpinner2.setBackgroundResource(R.drawable.check_gray_stroke_bg)
        mLeftDesignSpinner2.setSpinnerData(mLeftSpinner1Data).build()

        //实测截面类型-四种类型
        mViewLeftRealR = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealRectangleTv.content
        mViewLeftRealH = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealHTv.content
        mViewLeftRealT = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealTTv.content
        mViewLeftRealA = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealAnotherTv.content

        mEditViewLeftRealR1 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealRectangleTv.checkCpdLeftRectangle1
        mEditViewLeftRealR2 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealRectangleTv.checkCpdLeftRectangle2

        mEditViewLeftRealH1 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealHTv.checkCpdLeftRectangle1
        mEditViewLeftRealH2 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealHTv.checkCpdLeftRectangle2
        mEditViewLeftRealH3 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealHTv.checkCpdLeftRectangle3
        mEditViewLeftRealH4 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealHTv.checkCpdLeftRectangle4

        mEditViewLeftRealT1 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealTTv.checkCpdLeftRectangle1
        mEditViewLeftRealT2 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealTTv.checkCpdLeftRectangle2
        mEditViewLeftRealT3 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealTTv.checkCpdLeftRectangle3
        mEditViewLeftRealT4 =
            mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealTTv.checkCpdLeftRectangle4


        //实测截面类型-备注
        mEditViewLeftRealNote = mBinding.checkCpdBeamLeftRealUi.checkCpdLeftRealRemarkContent


        //设计截面类型-备注
        mEditViewLeftDesignNote = mBinding.checkCpdBeamLeftDesignUi.checkCpdLeftDesignRemarkContent

        initListener()
    }


    fun initListener() {


        mViewLeftRealMenu2.setOnClickListener {
            mViewLeftReal.visibility = INVISIBLE
            mViewLeftDesign.visibility = VISIBLE

        }


        mViewLeftDesignMenu1.setOnClickListener {
            mViewLeftReal.visibility = VISIBLE
            mViewLeftDesign.visibility = INVISIBLE
        }

        //梁-完成
        mBtnMenuConfirm.setOnClickListener {

        }

        //梁-切换
        mBtnMenuChangeAxis.setOnClickListener {
            if (mIsDefaultOneInput) {
                mMenuBeamAxisView1.visibility = INVISIBLE
                mMenuBeamAxisView2.visibility = VISIBLE
                mIsDefaultOneInput = false;
            } else {
                mMenuBeamAxisView1.visibility = VISIBLE
                mMenuBeamAxisView2.visibility = INVISIBLE
                mIsDefaultOneInput = true;
            }
        }

        // 实测截面类型-下拉框2监听回调
        mLeftRealSpinner2.setSpinnerCallback {
            when (it) {
                0 -> {
                    mViewLeftRealR.visibility = VISIBLE
                    mViewLeftRealH.visibility = INVISIBLE
                    mViewLeftRealT.visibility = INVISIBLE
                    mViewLeftRealA.visibility = INVISIBLE
                }
                1 -> {
                    mViewLeftRealR.visibility = INVISIBLE
                    mViewLeftRealH.visibility = VISIBLE
                    mViewLeftRealT.visibility = INVISIBLE
                    mViewLeftRealA.visibility = INVISIBLE
                }
                2 -> {
                    mViewLeftRealR.visibility = INVISIBLE
                    mViewLeftRealH.visibility = INVISIBLE
                    mViewLeftRealT.visibility = VISIBLE
                    mViewLeftRealA.visibility = INVISIBLE
                }
                3 -> {
                    mViewLeftRealR.visibility = INVISIBLE
                    mViewLeftRealH.visibility = INVISIBLE
                    mViewLeftRealT.visibility = INVISIBLE
                    mViewLeftRealA.visibility = VISIBLE
                }
            }

        }
    }


}