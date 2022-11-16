package com.sribs.common

/**
 * @date 2021/6/21
 * @author elijah
 * @Description
 */
object ARouterPath {
    const val MAIN_ATY         = "/main/aty"
    const val MAIN_MAP_FGT     = "/main/fgt/map"
    const val MAIN_LIST_FGT    = "/main/fgt/list"
    const val MAIN_DRAWER_FGT  = "/main/fgt/drawer"
    const val MAIN_SETTING_ATY = "/main/setting/aty"

    const val DAMAGE_MAIN_ATY         = "/damage/main/aty"

    //3.0新增 先创建项目
    const val PRO_CREATE_ATY_TYPE   = "/pro/aty/create_type"
    const val DRAW_WHITE   = "/pro/aty/draw_white"
    const val FRAGMENT_DRAW_WHITE   = "/pro/fgt/draw_white"
    const val PRO_CREATE_ATY_FLOOR_LIST   = "/pro/fgt/floor_list"
    const val PRO_ITEM_ATY_FLOOR  = "/pro/aty/floor_item"

    const val PRO_CREATE_ATY   = "/pro/aty/create"
    const val PRO_CREATE_FGT   = "/pro/fgt/create"
    const val PRO_CREATE_DETAIL= "/pro/fgt/detail"
    const val PRO_DETAIL_UNIT  = "/pro/fgt/detail/unit"
    const val PRO_CONFIG_ATY   = "/pro/aty/config"
    const val PRO_COPY_ATY     = "/pro/aty/copy"

    const val HOUSE_LIST_ATY    = "/house/list/aty"
    const val HOUSE_DAMAGE_LIST_ATY = "/house/damage/list/aty"
    const val HOUSE_STATUS_ATY = "/house/status/aty"
    const val HOUSE_DAMAGE_DES_ATY = "/house/damage/des/aty"
    const val HOUSE_DAMAGE_DES_FGT = "/house/damage/des/fgt"
    const val HOUSE_DAMAGE_RECORD_FGT = "/house/damage/record/fgt"
    const val HOUSE_DAMAGE_PIC_FGT = "/house/damage/pic/fgt"
    const val HOUSE_DAMAGE_EDIT_FGT = "/house/damage/edit/fgt"
    const val HOUSE_DAMAGE_OTHER_FGT = "/house/damage/other/fgt"
    const val HOUSE_UNIT_LIST_ATY = "/house/unit/list/aty"
    const val HOUSE_UNIT_LIST_FGT = "/house/unit/list/fgt"

    const val REPORT_ATY = "/report/aty"

    const val SRV_DB           = "/srv/db"

    const val VAL_COMMON_LOCAL_ID = "local_id"
    const val VAL_COMMON_REMOTE_ID = "remote_id"
    const val VAL_COMMON_STATUS = "status"
    const val VAL_COMMON_VERSION = "version"
    const val VAL_COMMON_LEADER = "leader"
    const val VAL_COMMON_INSPECTOR = "inspector"
    const val VAL_COMMON_CODE = "code"
    const val VAL_COMMON_TITLE = "title"
    const val VAL_COMMON_ID = "id"
    const val VAL_COMMON_LOCAL_CURRENT_PDF = "local_current_pdf"
    const val VAL_ABOVE_NUMBER = 0
    const val VAL_AFTER_NUMBER = 0

    const val VAL_PROJECT_ID = "project_id"
    const val VAL_PROJECT_UUID = "project_uuid"
    const val VAL_PROJECT_NAME = "project_name"
    const val VAL_BUILDING_ID = "building_id"
    const val VAL_BUILDING_UUID = "building_uuid"
    const val VAL_BUILDING_NO = "building_no"
    const val VAL_BUILDING_NAME = "building_name"
    const val VAL_MODULE_ID = "module_id"
    const val VAL_MODULE_NAME = "module_name"
    const val VAL_UNIT_CODE = "unit_code"
    const val VAL_UNIT_NO = "unit_no"
    const val VAL_UNIT_CONFIG_DES_CODE = "unit_des_code"
    const val VAL_UNIT_IDX = "unit_idx"
    const val VAL_UNIT_ID = "unit_id"
    const val VAL_UNIT_SHOW_DEL = "unit_show_del"
    const val VAL_UNIT_CONFIG_TYPE = "config_type"
    const val VAL_UNIT_CONFIG_DATA = "config_data"
    const val VAL_UNIT_CONFIG_FLOOR_NUM = "floor_num"
    const val VAL_UNIT_CONFIG_NEIGHBOR_NUM = "neighbor_num"
    const val VAL_UNIT_CONFIG_ID   = "config_id"
    const val VAL_UNIT_CONFIG_COPY_LIST = "config_copy_list"


    const val VAL_HOUSE_POS = "house_position"
    const val VAL_DAMAGE_TYPE = "damage_type"
    const val VAL_ANNOT_REF = "annot_ref"
    const val FLOOR_TOTAL_NUM= "floor_total_num"//总层数

    const val VAL_HOUSE_NAME = "house_name"
    const val VAL_HOUSE_TYPE = "house_type" //0楼梯间  1平台 2室
    const val VAL_HOUSE_ROOM_ID = "house_room_id" //house_status_id  or  room status_id 根据houseType区分
    const val VAL_PART_NO = "part_no" // 101室

    const val PRO_CREATE_STOREY = "/pro/fgt/storey"//非居住类建筑损伤项目明细
    const val FLOOR_COPY_ATY = "/floor/copy/activity"//Copy drawings to dst floor
    const val BLD_DRW_DMG_MNG_MAIN_FGT = "/bld/fgt/drawimg/damage/main"     //按每栋楼图纸损伤大类管理
    const val BLD_DRW_DMG_MNG_SUB_FGT = "/bld/fgt/drawimg/damage/sub"       //按每栋楼图纸损伤子类管理
    const val BLD_DRW_DMG_DIAL_REC_FGT = "/bld/drawimg/damage/detial/rec"   //按每栋楼图纸损伤大类管理

    const val BLD_DAMAGE_LIST_ATY= "/bld/damage/list/aty"//图纸列表
    const val BLD_DAMAGE_DETAIL_RECORD_ATY= "/bld/damage/detail/record/aty"//损伤明细记录

    const val VAL_DAMAGE_MONITOR_ID = "damage_monitor_id"

    /**
     * 3.0项目检测
     */

    const val CHECK_NON_RESIDENTS_ACTIVITY = "/check/nonResidents/activity"; //非居民检测

    const val CHECK_NON_RESIDENTS_FRAGMENT = "/check/nonResidents/fragment"; //非居民检测

    const val CHECK_NON_RESIDENTS_EDIT_FRAGMENT = "/check/nonResidents/edit/fragment"; //非居民检测


    const val CHECK_OBLIQUE_DEFORMATION_ACTIVITY = "/check/obliqueDeformation/activity"; //倾斜变形检测

    const val CHECK_OBLIQUE_DEFORMATION_FRAGMENT = "/check/obliqueDeformation/fragment"; //倾斜变形检测

    const val CHECK_OBLIQUE_DEFORMATION_Edit_FRAGMENT = "/check/obliqueDeformation/edit/fragment"; //倾斜变形检测

    const val CHECK_RELATIVE_ELEVATION_DIFFERENCE_ACTIVITY = "/check/relativeElevationDifference/activity"; //相对高差变形检测

    const val CHECK_COMPONENT_SIZE_AND_REINFORCEMENT_ACTIVITY = "/check/componentSizeAndReinforcement/activity"; //构件尺寸和配筋检测

    const val CHECK_BUILD_STRUCTURE_ACTIVITY = "/check/buildStructure/activity"; //建筑结构复核

    const val CHECK_BUILD_STRUCTURE_FRAGMENT = "/check/buildStructure/fragment"; //建筑结构复核

    const val CHECK_BUILD_STRUCTURE_GRID_FRAGMENT = "/check/buildStructure/grid/fragment"; //建筑结构复核 轴网

    const val CHECK_BUILD_STRUCTURE_FLOOR_FRAGMENT = "/check/buildStructure/floor/fragment"; //建筑结构复核 层高

    const val CHECK_RELATIVE_H_DIFF_ACTIVITY = "/check/relativeHDiff/activity"; //相对高差测量

    const val CHECK_RELATIVE_H_DIFF_FRAGMENT = "/check/relativeHDiff/fragment"; //相对高差测量

    const val CHECK_RELATIVE_H_DIFF_EDIT_FRAGMENT = "/check/relativeHDiffedit/fragment"; //相对高差测量

    const val CHECK_COMPONENT_DETECTION_ACTIVITY = "/check/componentdetection/activity"; //构件检测

    const val CHECK_COMPONENT_DETECTION_FRAGMENT = "/check/componentdetection/fragment"; //构件检测-梁

    const val CHECK_COMPONENT_DETECTION_BEAM_FRAGMENT = "/check/componentdetection/beam/fragment"; //构件检测-梁

    const val CHECK_COMPONENT_DETECTION_COLUMN_FRAGMENT = "/check/componentdetection/column/fragment"; //构件检测-柱

    const val CHECK_COMPONENT_DETECTION_WALL_FRAGMENT = "/check/componentdetection/wall/fragment"; //构件检测-墙

    const val CHECK_COMPONENT_DETECTION_PLATE_FRAGMENT = "/check/componentdetection/plate/fragment"; //构件检测-板

    /**
     * 3.0模块配置
     */
    const val CHECK_MODULE_CONFIG_TYPE_FLOOR_ACTIVITY = "/check/module_config/type_floor_activity"; //
    const val CHECK_MODULE_CONFIG_TYPE_BUILDING_ACTIVITY = "/check/module_config/type_building_activity"; //

    /**
     * 3.0楼栋单元列表
     */
    const val FLOOR_ACTIVITY = "/floor/activity"; //楼栋单元列表

    /**
     * 3.0绘制PDF
     */
    const val DRAW_PDF_ACTIVITY = "/draw/pdf/activity" //绘制pdf

}