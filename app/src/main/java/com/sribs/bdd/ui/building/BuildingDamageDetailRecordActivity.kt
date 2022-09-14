package com.sribs.bdd.ui.building

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.radaee.pdf.*
import com.radaee.reader.PDFLayoutView
import com.radaee.reader.PDFViewController
import com.radaee.util.PDFAssetStream
import com.radaee.util.PDFHttpStream
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityDamageDetailRecordBinding
import com.sribs.bdd.module.building.BuildingDamagePresenter
import com.sribs.common.bean.db.DrawingBean
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Route(path= com.sribs.common.ARouterPath.BLD_DAMAGE_DETAIL_RECORD_ATY)
class BuildingDamageDetailRecordActivity:BaseActivity() {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_ANNOT_REF)
    var mAnnotRef = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_DAMAGE_TYPE)
    var mDmgType = ""

    var mTabTitleList:Array<String>?=null

    private var mLocalBldId: Long = -1
    private var mPdfFilePath:String = ""
    private val mBinding : ActivityDamageDetailRecordBinding by inflate()
    private var mAssetStream: PDFAssetStream? = null
    private var mHttpStream: PDFHttpStream? = null
    private var mDoc: Document? = null
    private var mPath: String? = ""
    private var mLayout: RelativeLayout? = null
    private var mView: PDFLayoutView? = null
    private var mController: PDFViewController? = null
    private val mPresenter by lazy { BuildingDamagePresenter() }
    private var mDrawingList: ArrayList<DrawingBean> = ArrayList<DrawingBean>()

    private var mSelFloor: String = ""//当前楼层
    private var mSelFloorDrawingBeanList: ArrayList<DrawingBean>? = null //当前楼层图纸地址list,上下切换图纸用到
    private var mSelDrawingName: String = ""//当前图纸
    private var mSelDrawingPath: String = ""//当前图纸路径
    private var mCurDrawingPos: Int? = -1//当前图纸在本层楼图纸索引
    private var mDamageDescList: ArrayList<DamageDesc>? = null
//    private var mDamageDescMap:HashMap<String, HashMap<Long, String?>>? = null

    private var mDamageDescMap:HashMap<String, HashMap<String, HashMap<Long, String?>>>? = null//drawing->type->damage desc

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.BLD_DRW_DMG_DIAL_REC_FGT)
                .setTag("main")
//                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .navigation() as BaseFragment,

        )
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        println("leon FloorDamageListActivity onCreate in")
    }

    override fun deinitView() {

    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        println("leon BuildingDamageDetailRecordActivity initView")

//        Global.Init(this)

//        initData()
        initToolbar()
        initViewPager()

    }

    private fun initToolbar() {
        mBinding.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.tbTitle.text = mTitle
        mBinding.tb.showOverflowMenu()
        setSupportActionBar(mBinding.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.tb.setNavigationOnClickListener {
            //check if pdf modified and save

        }
    }

    fun initViewPager() {
        mBinding.mainFragment.setSmooth(false)
        mBinding.mainFragment.setScroll(false)
        mBinding.mainFragment.adapter = BasePagerAdapter(supportFragmentManager,mFragments)
        mBinding.mainFragment.offscreenPageLimit = 1
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_unit_vp, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("leon requestCode=${requestCode}")
        if (permissions != null) {
            for (i in 0..permissions.size - 1) {
                println("leon permissions[${i}]=${permissions.get(i)}")
            }
        }
        if (grantResults != null) {
            for (i in 0..grantResults.size - 1) {
                println("leon grantResults[${i}]=${grantResults.get(i)}")
            }
        }
    }

    fun initData() {

    }

    fun onFail(msg: String) //treat open failed.
    {
        mDoc!!.Close()
        mDoc = null
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun dispose(): Unit? {
        mFragments.forEach {
            it.dispose()
            it.deinitView()
        }
        return super.dispose()
    }

}