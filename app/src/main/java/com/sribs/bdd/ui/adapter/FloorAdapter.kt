package com.sribs.bdd.ui.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingFloor
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.bdd.databinding.ItemRoomListBinding
import com.sribs.bdd.databinding.ItemRoomListTagBinding
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.utils.TimeUtil
import kotlin.collections.ArrayList

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
class FloorAdapter(private val mCb:ICallback) :BaseListAdapter<BuildingFloor, ViewBinding>(){

    private val icons = listOf(
        R.drawable.ic_main_local,
        R.drawable.ic_main_local_upload,
        R.drawable.ic_main_cloud,
        R.drawable.ic_main_download,
        R.drawable.ic_main_download_config
    )

    override fun getItemViewType(position: Int): Int = mList!![position].type

    private fun initContent(bind: ItemRoomListBinding, beanMain: BuildingFloor, pos: Int) {

        bind.itemRoomTimeTv.text = beanMain.time
        bind.itemRoomCard.isSelected = beanMain.isCardSel

       /* if (beanMain.isFinish){
            bind.itemRoomStatusTv.text = beanMain.houseFinishStatus
            bind.itemRoomStatusTv.isSelected = true
        }else{
            bind.itemRoomStatusTv.text = beanMain.houseFinishStatus?:"未完成"
            bind.itemRoomStatusTv.isSelected = false
        }*/

        bind.itemRoomFloorneighborTv.text = beanMain.unitNo
        bind.itemRoomMemberTv.text = "检验员："+beanMain.inspector


        bind.itemRoomCard.setOnClickListener {
            selCard(bind,pos)
            mCb.onCardSelect(mList!![pos],pos)
        }
        bind.itemRoomMoreCb.isChecked = beanMain.isMenuChecked
        bind.itemRoomMoreCb.setOnClickListener {
            mList!![pos].isMenuChecked =  !mList!![pos].isMenuChecked
            selCard(bind,pos)
            mCb.onMoreChecked(beanMain,pos,mList!![pos].isMenuChecked)
        }
        bind.itemRoomStatusIv.setImageResource(icons[beanMain.status])
    }

    private fun selCard(bind:ItemRoomListBinding,pos:Int){
        if (mList?.size?:0 <= pos) return
        if (!mList!![pos].isCardSel){
            mList!![pos].isCardSel  = true
            updateOthers(bind,pos,true)
            bind.root.post { notifyItemChanged(pos) }
            LOG.I("123","selCard pos=$pos")
        }
    }

    private fun updateOthers(bind:ItemRoomListBinding,pos:Int,b:Boolean){
        if (b){
            mList?.forEachIndexed { index, projectBean ->
                if (pos!=index && projectBean.isCardSel){
                    if (projectBean.isCardSel || projectBean.isMenuChecked){
                        projectBean.isCardSel = false
                        projectBean.isMenuChecked = false
                        bind.root.post { notifyItemChanged(index) }
                    }
                }
            }
        }
    }


    fun menuUnSel(pos: Int){
        mList!![pos].isMenuChecked =  false
        notifyItemChanged(pos)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ViewBinding>  =
        when(viewType) {
            RoomItemBean.TYPE_GROUP-> newBindingViewHolder<ItemRoomListTagBinding>(parent) as BindingViewHolder<ViewBinding>
            RoomItemBean.TYPE_CONTENT-> newBindingViewHolder<ItemRoomListBinding>(parent) as  BindingViewHolder<ViewBinding>
            else-> newBindingViewHolder(parent)
        }


    interface ICallback{
        fun onMoreChecked(beanMain:BuildingFloor, pos:Int, checked:Boolean)
        fun onCardSelect(beanMain:BuildingFloor, pos:Int)
    }

    fun setData(l:ArrayList<BuildingFloor>){
        var list = ArrayList(l.sortedByDescending { it.houseType })
        var p =  list.indexOfFirst { it.houseType!=2 }
        if (p>=0){
            list.add(p, BuildingFloor(""))
        }
        var r =  list.indexOfFirst { it.houseType==2 }
        if (r>=0){
            list.add(r, BuildingFloor(""))
        }
        mList = list
        notifyDataSetChanged()
    }

    private fun initTag(bind:ItemRoomListTagBinding,bean:BuildingFloor,pos:Int){
        bind.itemRoomTag.text = bean.tag
    }

    override fun init(bind: ViewBinding, bean: BuildingFloor, pos: Int) {
        when(bean.type){
            RoomItemBean.TYPE_GROUP->initTag(bind as ItemRoomListTagBinding,bean,pos)
            RoomItemBean.TYPE_CONTENT->initContent(bind as ItemRoomListBinding,bean,pos)
        }
    }

    fun getSpanSize(pos:Int):Int = mList!![pos].spanCount


    fun getSameGroupContentIndexes(index:Int):ArrayList<Int>{
        if (mList==null)return ArrayList()
        var isRoom = mList!![index].houseType==2
        return ArrayList(mList!!.mapIndexed { i, roomItemBean -> Pair(i,roomItemBean)  }
            .filter { it.second.type == RoomItemBean.TYPE_CONTENT &&
                    if(isRoom){
                        it.second.houseType == 2
                    }else{
                        it.second.houseType !=2
                    }
            }.map { it.first })
    }

    var mKeyword:String?=null

    fun setSearch(keyword:String?){
        mKeyword = keyword
        if (mList.isNullOrEmpty())return

        mList = ArrayList<BuildingFloor>(mList!!.filter { it.unitNo!!.contains(mKeyword!!) ||
                mKeyword.isNullOrEmpty()})
        notifyDataSetChanged()
    }

}