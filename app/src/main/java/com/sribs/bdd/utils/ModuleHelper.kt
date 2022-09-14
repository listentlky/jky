package com.sribs.bdd.utils

/**
 * @date 2021/7/16
 * @author elijah
 * @Description
 */
object ModuleHelper {

    val moduleMap = mapOf<String,String?>(
        "BLD_TYPE_INHAB" to "inhab",//居住类建筑损伤
        "BLD_TYPE_NONINHAB" to "non_inhab"//非居住类建筑损伤
    )

    const val FILE_PICKER_REQUEST_CODE_EAST_FACADE:Int = 50000
    const val FILE_PICKER_REQUEST_CODE_SOUTH_FACADE:Int = 50001
    const val FILE_PICKER_REQUEST_CODE_WEST_FACADE:Int = 50002
    const val FILE_PICKER_REQUEST_CODE_NORTH_FACADE:Int = 50003
    const val FILE_PICKER_REQUEST_CODE_OVERALL_FACADE:Int = 50004
    const val FILE_PICKER_REQUEST_CODE_FLAT_FACADE:Int = 50005

    const val CUR_PRO_NAME:String = "current_project_name"
    const val CUR_BLD_NO:String = "current_building_no"
    const val CUR_BLD_INS:String = "current_building_ins"
    const val DRAWING_CACHE_FOLDER:String = "图纸"
    const val CUR_PRO_LEADER:String = "current_pro_leader"

    const val DRAWING_DMG_LINE_BUTTON_CLICKED:String = "annot_line"
    const val DRAWING_DMG_RECT_BUTTON_CLICKED:String = "annot_rect"
    const val DRAWING_DMG_CIRCLE_BUTTON_CLICKED:String = "annot_circle"
    const val DRAWING_DMG_TEXT_BUTTON_CLICKED:String = "annot_text"
    const val DRAWING_DMG_MAN_BUTTON_CLICKED:String = "annot_man"
    //    const val BLD_TYPE_NONINHAB:String = "nonInhab"//非居住类建筑
}