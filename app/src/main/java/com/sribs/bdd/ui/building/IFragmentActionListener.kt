package com.sribs.bdd.ui.building

import android.content.Context
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.Building
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.common.bean.db.*
import com.sribs.db.project.building.BuildingBean

/**
 * @date 2021/7/13
 * @author elijah
 * @Description
 */
interface IFragmentActionListener {

    fun onFragmentAction(action: String)

}