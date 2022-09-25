package com.sribs.bdd.v3.ui.check.bs.fm

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureGridBinding
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import kotlinx.android.synthetic.main.double_edit_item.view.*

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_GRID_FRAGMENT)
class CheckBSGridFragment : BaseFragment(R.layout.fragment_check_build_structure_grid){

    private val mBinding : FragmentCheckBuildStructureGridBinding by bindView()

    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    /**
     * 当前添加的mark
     */
    var mAddAnnotReF:Long = -1L

    override fun deinitView() {

    }

    override fun initView() {
        mBinding.checkBsFloorZwsjz.checkEditName.text="轴网设计值(mm)"
        mBinding.checkBsFloorZwsjz.checkEdit.hint="请输入轴网设计值"

        mBinding.checkBsFloorZwscz.checkEditName.text="轴网实测值(mm)"
        mBinding.checkBsFloorZwscz.checkEdit.hint="请输入轴网实测值"

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
            (context as CheckBuildStructureActivity).scaleDamageInfo(2)
        }

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

            if (mBinding.checkBsFloorZwsjz.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入轴网设计值")
                return@setOnClickListener
            }
            if (mBinding.checkBsFloorZwscz.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入轴网实测值")
                return@setOnClickListener
            }
            if (mBinding.checkBsFloorHintText.text.isNullOrEmpty()) {
                showToast("请输入备注")
                return@setOnClickListener
            }

            mAddAnnotReF =  (activity as CheckBuildStructureActivity).mCurrentAddAnnotReF

            var damage = DamageV3Bean(
                -1,
                (activity as CheckBuildStructureActivity).mCurrentDrawing!!.drawingID,
                "轴网",
                0,
                mAddAnnotReF,
                mBinding.checkBsFloorHintText.text.toString(),
                if(mDamageCreateTime<0) System.currentTimeMillis() else mDamageCreateTime,
                mAxisNote,
                mAxisNoteList,
                (context as CheckBuildStructureActivity).mCurrentDrawing!!.floorName,
                mBinding.checkBsFloorZwsjz.checkEdit.text.toString(),
                mBinding.checkBsFloorZwscz.checkEdit.text.toString()
            )
            (context as CheckBuildStructureActivity).saveDamage(damage)
        }

    }

    fun resetView(damageV3Bean: DamageV3Bean?){
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
            mBinding.checkBsFloorZwsjz.checkEdit.setText("")
            mBinding.checkBsFloorZwscz.checkEdit.setText("")
            mBinding.checkBsFloorHintText.setText("")
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

            mBinding.checkBsFloorZwsjz.checkEdit.setText(damageV3Bean.gridDesign)
            mBinding.checkBsFloorZwscz.checkEdit.setText(damageV3Bean.gridReal)
            mBinding.checkBsFloorHintText.setText(damageV3Bean.note)
            mDamageCreateTime = damageV3Bean.createTime
        }
    }
}