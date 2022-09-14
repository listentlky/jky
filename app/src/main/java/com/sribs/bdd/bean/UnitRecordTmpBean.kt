package com.sribs.bdd.bean

import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.bean.db.ReportBean
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.bean.db.RoomStatusBean

/**
 * @date 2021/8/16
 * @author elijah
 * @Description
 */
class UnitRecordTmpBean {
    var houseStatus: HouseStatusBean?=null
    var roomStatusList:ArrayList<RoomStatusBean> = ArrayList()
    var roomDetailList:ArrayList<RoomDetailBean> = ArrayList()
    var houseReport: ReportBean?=null

    private var roomMap : HashMap<String,RoomRecordTmpBean>?=null

    fun getRoom():HashMap<String,RoomRecordTmpBean>{
        if (roomMap!=null)return roomMap!!
        roomMap = HashMap()
        roomStatusList.forEach { b->
            if(roomMap!![b.name!!]==null) roomMap!![b.name!!] = RoomRecordTmpBean()
            roomMap!![b.name!!]!!.status = b
        }
        roomDetailList.forEach { b->
            if(roomMap!![b.name!!]==null) roomMap!![b.name!!] = RoomRecordTmpBean()
            if (roomMap!![b.name!!]!!.detail==null)roomMap!![b.name!!]!!.detail = ArrayList()
            roomMap!![b.name!!]!!.detail!!.add(b)
        }
        return roomMap!!
    }

     class RoomRecordTmpBean {
         var status:RoomStatusBean?=null
         var detail:ArrayList<RoomDetailBean>?=null
     }

}