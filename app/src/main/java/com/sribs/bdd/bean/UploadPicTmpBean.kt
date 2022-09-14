package com.sribs.bdd.bean

import com.cbj.sdk.libnet.http.bean.ResultBean
import com.sribs.common.bean.db.BaseDbBean
import com.sribs.common.bean.net.FileUploadRes

/**
 * @date 2021/8/16
 * @author elijah
 * @Description
 */
data class UploadPicTmpBean(
    var dbBean: BaseDbBean?,
    var resBean: ResultBean<FileUploadRes>?
)

