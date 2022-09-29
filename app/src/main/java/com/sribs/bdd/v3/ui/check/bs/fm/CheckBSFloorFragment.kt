package com.sribs.bdd.v3.ui.check.bs.fm

import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureFloorBinding
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.util.LogUtils.d
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import kotlinx.android.synthetic.main.double_edit_item.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_FLOOR_FRAGMENT)
class CheckBSFloorFragment : BaseFragment(R.layout.fragment_check_build_structure_floor) {

    private val mBinding: FragmentCheckBuildStructureFloorBinding by bindView()

    private var mJgZgList: ArrayList<String>? = ArrayList()

    private var mSelectPosition = 0

    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    /**
     * 当前添加的mark
     */
    var mAddAnnotReF:Long = -1L

    var mAddAnnotX:Int = 0

    var mAddAnnotY:Int = 0

    override fun deinitView() {

    }

    override fun initView() {
        mJgZgList!!.addAll(Arrays.asList("净高", "总高"))

        /**
         * 默认选中净高  装饰面板厚度不可输入
         */
        mBinding.checkBsFloorMchd.checkEdit.setText("")
        mBinding.checkBsFloorMchd.checkEdit.isEnabled = false

        mBinding.checkBsFloorSpinner.setSpinnerData(mJgZgList)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback { position: Int ->
                mSelectPosition = position
                d("当前选择：$position")
                if (position == 0) {
                    mBinding.checkBsFloorMchd.checkEdit.setText("")
                    mBinding.checkBsFloorMchd.checkEdit.isEnabled = false
                } else {
                    mBinding.checkBsFloorMchd.checkEdit.isEnabled = true
                }
            }.build()
        mBinding.checkBsChooseBut.setOnClickListener {
            when (mBinding.checkBsFloorZxEdit.visibility) {
                View.GONE -> {
                    mBinding.checkBsFloorEdit1.root.visibility = View.INVISIBLE
                    mBinding.checkBsFloorFj.visibility = View.INVISIBLE
                    mBinding.checkBsFloorEdit2.root.visibility = View.INVISIBLE
                    mBinding.checkBsFloorZxEdit.visibility = View.VISIBLE
                }
                View.VISIBLE -> {
                    mBinding.checkBsFloorEdit1.root.visibility = View.VISIBLE
                    mBinding.checkBsFloorFj.visibility = View.VISIBLE
                    mBinding.checkBsFloorEdit2.root.visibility = View.VISIBLE
                    mBinding.checkBsFloorZxEdit.visibility = View.GONE
                }
            }
        }

        mBinding.checkBsFloorCgsj.checkEditName.text = "层高设计值(mm)"
        mBinding.checkBsFloorCgsj.checkEdit.hint = "请输入层高设计值"

        mBinding.checkBsFloorCgsc.checkEditName.text = "层高实测值(mm)"
        mBinding.checkBsFloorCgsc.checkEdit.hint = "请输入层高实测值"

        mBinding.checkBsFloorSjbh.checkEditName.text = "设计板厚(mm)"
        mBinding.checkBsFloorSjbh.checkEdit.hint = "请输入设计板厚"

        mBinding.checkBsFloorMchd.checkEditName.text = "装饰面层厚度(mm)"
        mBinding.checkBsFloorMchd.checkEdit.hint = "请输入装饰面层厚度"
        mBinding.checkBsFloorMchd.checkEdit.isEnabled = false

        /**
         * menu关闭
         */
        mBinding.checkMenuLayout.checkObdMenuClose.setOnClickListener {
            (context as CheckBuildStructureActivity).setVpCurrentItem(0)
        }

        /**
         * menu 缩小
         */
        mBinding.checkMenuLayout.checkObdMenuScale.setOnClickListener {
            (context as CheckBuildStructureActivity).scaleDamageInfo(1)
        }
        mBinding.checkBsFloorConfirm.setOnClickListener {
            var mAxisNote: String = "" // 轴线
            var mAxisNoteList:ArrayList<String>?=ArrayList() // 多轴线

            if (mBinding.checkBsFloorZxEdit.visibility == View.GONE) {
                if (mBinding.checkBsFloorEdit1.root.check_edit.text.isNullOrEmpty() ||
                    mBinding.checkBsFloorEdit1.root.check_edit_flag.text.isNullOrEmpty() ||
                    mBinding.checkBsFloorEdit2.root.check_edit.text.isNullOrEmpty() ||
                    mBinding.checkBsFloorEdit1.root.check_edit_flag.text.isNullOrEmpty()
                ) {
                    showToast("请完善轴线")
                    return@setOnClickListener
                }
                mAxisNoteList!!.add(mBinding.checkBsFloorEdit1.root.check_edit.text.toString())
                mAxisNoteList!!.add(mBinding.checkBsFloorEdit1.root.check_edit_flag.text.toString())
                mAxisNoteList!!.add(mBinding.checkBsFloorEdit2.root.check_edit.text.toString())
                mAxisNoteList!!.add(mBinding.checkBsFloorEdit2.root.check_edit_flag.text.toString())
            } else {
                if (mBinding.checkBsFloorZxEdit.text.isNullOrEmpty()) {
                    showToast("请输入轴线")
                    return@setOnClickListener
                }
                mAxisNote = mBinding.checkBsFloorZxEdit.text.toString()
            }
            if (mSelectPosition == -1) {
                showToast("请选择净高/总高")
                return@setOnClickListener
            }
            if (mBinding.checkBsFloorCgsj.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入层高设计值")
                return@setOnClickListener
            }
            if (mBinding.checkBsFloorCgsc.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入层高实测值")
                return@setOnClickListener
            }
            if (mBinding.checkBsFloorSjbh.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入设计板厚")
                return@setOnClickListener
            }
            if (mSelectPosition == 1 && mBinding.checkBsFloorMchd.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入装饰面层厚度")
                return@setOnClickListener
            }
            if (mBinding.checkBsFloorHintText.text.isNullOrEmpty()) {
                showToast("请输入备注")
                return@setOnClickListener
            }

            mAddAnnotReF =  (activity as CheckBuildStructureActivity).mCurrentAddAnnotReF
            mAddAnnotX =  (activity as CheckBuildStructureActivity).mCurrentAddAnnotX
            mAddAnnotY =  (activity as CheckBuildStructureActivity).mCurrentAddAnnotY

            var damage = DamageV3Bean(
                -1,
                (activity as CheckBuildStructureActivity).mCurrentDrawing!!.drawingID,
                "层高",
                0,
                mAddAnnotReF,
                mBinding.checkBsFloorHintText.text.toString(),
                if(mDamageCreateTime<0) System.currentTimeMillis() else mDamageCreateTime,
                mAddAnnotX,
                mAddAnnotY,
                mAxisNote,
                mAxisNoteList,
                mJgZgList!![mSelectPosition],
                (context as CheckBuildStructureActivity).mCurrentDrawing!!.floorName,
                mBinding.checkBsFloorCgsj.checkEdit.text.toString(),
                mBinding.checkBsFloorCgsc.checkEdit.text.toString(),
                mBinding.checkBsFloorSjbh.checkEdit.text.toString(),
                mBinding.checkBsFloorMchd.checkEdit.text.toString()
            )
            (context as CheckBuildStructureActivity).saveDamage(damage)
        }

    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?) {

        LogUtils.d("重新resetView："+damageV3Bean)

        mBinding.checkBsFloorCurrent.setText("当前层数: "+(context as CheckBuildStructureActivity).mCurrentDrawing!!.floorName)
        if(damageV3Bean == null){
            mBinding.checkBsFloorEdit1.root.visibility = View.VISIBLE
            mBinding.checkBsFloorEdit1.root.check_edit.setText("")
            mBinding.checkBsFloorEdit1.root.check_edit_flag.setText("")
            mBinding.checkBsFloorFj.visibility = View.VISIBLE
            mBinding.checkBsFloorEdit2.root.visibility = View.VISIBLE
            mBinding.checkBsFloorEdit2.root.check_edit.setText("")
            mBinding.checkBsFloorEdit2.root.check_edit_flag.setText("")
            mBinding.checkBsFloorZxEdit.visibility = View.GONE
            mBinding.checkBsFloorZxEdit.setText("")
            mSelectPosition = 0
            mBinding.checkBsFloorSpinner.setText(mJgZgList!!.get(0))
            mBinding.checkBsFloorCgsj.checkEdit.setText("")
            mBinding.checkBsFloorCgsc.checkEdit.setText("")
            mBinding.checkBsFloorSjbh.checkEdit.setText("")
            mBinding.checkBsFloorMchd.checkEdit.setText("")
            mBinding.checkBsFloorHintText.setText("")
            mBinding.checkBsFloorMchd.checkEdit.isEnabled = false
            mDamageCreateTime = -1L
        }else{
            if(!damageV3Bean.axisNoteList.isNullOrEmpty() && damageV3Bean.axisNoteList!!.size>0){
                mBinding.checkBsFloorEdit1.root.visibility = View.VISIBLE
                mBinding.checkBsFloorEdit1.root.check_edit.setText(damageV3Bean.axisNoteList!!.get(0))
                mBinding.checkBsFloorEdit1.root.check_edit_flag.setText(damageV3Bean.axisNoteList!!.get(1))
                mBinding.checkBsFloorFj.visibility = View.VISIBLE
                mBinding.checkBsFloorEdit2.root.visibility = View.VISIBLE
                mBinding.checkBsFloorEdit2.root.check_edit.setText(damageV3Bean.axisNoteList!!.get(2))
                mBinding.checkBsFloorEdit2.root.check_edit_flag.setText(damageV3Bean.axisNoteList!!.get(3))
                mBinding.checkBsFloorZxEdit.visibility = View.GONE
            }else{
                mBinding.checkBsFloorEdit1.root.visibility = View.INVISIBLE
                mBinding.checkBsFloorEdit1.root.check_edit.setText("")
                mBinding.checkBsFloorEdit1.root.check_edit_flag.setText("")
                mBinding.checkBsFloorFj.visibility = View.INVISIBLE
                mBinding.checkBsFloorEdit2.root.visibility = View.INVISIBLE
                mBinding.checkBsFloorEdit2.root.check_edit.setText("")
                mBinding.checkBsFloorEdit2.root.check_edit_flag.setText("")
                mBinding.checkBsFloorZxEdit.visibility = View.VISIBLE
                mBinding.checkBsFloorZxEdit.setText(damageV3Bean.axisNote)
            }
            if(damageV3Bean.decorateDesign.isNullOrEmpty()){
                mBinding.checkBsFloorMchd.checkEdit.setText("")
                mSelectPosition = 0
                mBinding.checkBsFloorSpinner.setText(mJgZgList!!.get(mSelectPosition))
                mBinding.checkBsFloorSpinner.setSelect(mSelectPosition)
                mBinding.checkBsFloorMchd.checkEdit.isEnabled = false
            }else{
                mBinding.checkBsFloorMchd.checkEdit.setText(damageV3Bean.decorateDesign)
                mSelectPosition = 1
                mBinding.checkBsFloorSpinner.setText(mJgZgList!!.get(mSelectPosition))
                mBinding.checkBsFloorSpinner.setSelect(mSelectPosition)
                mBinding.checkBsFloorMchd.checkEdit.isEnabled = true
            }
            mBinding.checkBsFloorCgsj.checkEdit.setText(damageV3Bean.floorDesign)
            mBinding.checkBsFloorCgsc.checkEdit.setText(damageV3Bean.floorReal)
            mBinding.checkBsFloorSjbh.checkEdit.setText(damageV3Bean.plateDesign)
            mBinding.checkBsFloorHintText.setText(damageV3Bean.note)
            mDamageCreateTime = damageV3Bean.createTime
        }
    }
}