package com.sribs.bdd.module.house

import android.content.Context
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.HouseConfigItemBean
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.bean.db.ReportBean
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.bean.db.RoomStatusBean

/**
 * @date 2021/7/29
 * @author elijah
 * @Description
 */
interface IHouseContrast {

    interface IHouseView:IBaseView{
        fun getContext():Context?
        fun onLocalConfig(l:ArrayList<RoomItemBean>)
        fun onHouseConfig(l:ArrayList<HouseConfigItemBean>)

    }

    interface IHouseListPresenter:IBasePresenter{
        fun getHouseList(projectId:Long,unitId:Long?,unitNo:String,remoteUnitId:String?)

        fun getLocalConfig(configId:Long,houseType:Int)//0:过道  1：平台   2：室
    }

    interface IHouseStatusView:IBaseView{
        fun onHouseStatus(l:ArrayList<HouseStatusBean>)
    }

    interface IHouseStatusPresenter:IBasePresenter{
        fun getAllHouseStatus(unitId: Long)
        fun getHouseStatus(configId:Long)
        fun updateHouseStatus(projectId:Long,unitId:Long,configId: Long,
                              name:String,houseType: Int,
                              status: String,furnishTime:String?)
        fun updateHouseFinish(projectId: Long,unitId: Long,configId: Long,name: String,houseType: Int,isFinish:Boolean)
        fun updateHouseInspector(projectId: Long,unitId: Long,configId: Long,name: String,houseType: Int,isFinish:Boolean?=null)
    }

    interface IRoomView:IBaseView{
        fun onRoomStatus(l:ArrayList<RoomStatusBean>)
    }

    interface IRoomPresenter:IBasePresenter{
        fun getRoomStatus(configId: Long)
        fun getRoomStatus(configId: Long,name:String)
        fun updateRoomStatus(projectId: Long,unitId: Long,configId: Long,
                             name:String,status: String,furnishTime: String?,note:String?,cb:()->Unit?)
        fun finishRoomStatus(projectId: Long,unitId: Long,configId: Long,
                             name:String,isFinish:Int?)
        fun allFinish(projectId: Long,unitId: Long,configId:Long)
        fun clearRoom(configId: Long,name:String)
    }

    interface IRoomDetailView:IBaseView{
        fun onRoomDetail(l:ArrayList<RoomDetailBean>)
    }

    interface IRoomDetailPresenter:IBasePresenter{
        fun getRoomDetail(configId:Long,name:String)
        fun getRoomDetail(configId: Long,name:String,damagePath:String)
        fun getRoomDetail(configId: Long,name:String,damagePath:String,damageIdx:Int)
        fun getRoomDetail(configId: Long,name:String,damagePath:String,description:String)
        fun updateRoomDetail(b:RoomDetailBean,cb:(Long)->Unit)
        fun deleteRoomDetail(cb:(Boolean)->Unit)
        fun deleteRoomDetail(id:Long,cb:(Boolean)->Unit)
    }

    interface IReportView:IBaseView{
        fun onRoomDetail(l:ArrayList<RoomDetailBean>)
        fun onReport(l:ArrayList<ReportBean>)
    }

    interface IReportPresenter:IBasePresenter{
        fun getRoomDetail(configId: Long)
        fun getReport(configId: Long)
        fun updateReport(projectId:Long,unitId: Long,configId: Long,
        isSave:Int?,report:String?, signPath:String?)
    }
}