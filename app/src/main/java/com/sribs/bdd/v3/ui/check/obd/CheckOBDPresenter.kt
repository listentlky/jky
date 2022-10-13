package com.sribs.bdd.v3.ui.check.obd

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.bean.UploadPicTmpBean
import com.sribs.bdd.v3.bean.CheckOBDMainBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.BaseDbBean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.ReportBean
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CheckOBDPresenter :BasePresenter(),ICheckOBDContrast.ICheckOBDPresenter{

    private var mView: ICheckOBDContrast.ICheckOBDView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getModuleInfo(localProjectId:Long,localBuildingId:Long,localModuleId:Long) {
        addDisposable(mDb.getv3BuildingModuleOnce(localProjectId,localBuildingId,localModuleId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var list = ArrayList(it.map { b->CheckOBDMainBean(
                    projectId = b.projectId,
                    bldId = b.buildingId,
                    moduleId = b.id,
                    moduleName = b.moduleName,
                    drawing = b.drawings,
                    inspectorName = b.inspectors,
                    leaderNamr = b.leaderName,
                    createTime = b.createTime,
                    updateTime = b.updateTime,
                    deleteTime = b.deleteTime,
                    version = b.version,
                    status = b.status
                )
                })
                LogUtils.d("获取到该模块下所有数据 "+list.toString())
                mView?.onModuleInfo(list)
            },{
                mView?.onMsg(it.toString())
                LogUtils.d("获取到该模块下数据失败 ${it}")
            }))
    }

    override fun saveDamageToDb(drawingV3Bean: List<DrawingV3Bean>, id: Long) {
        addDisposable(mDb.updatev3BuildingModuleDrawing(id,drawingV3Bean)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("更新图纸损伤信息成功")
            },{
                LogUtils.d("更新图纸损伤信息失败: "+it)
            }))
    }

    /**
     * 上传图纸
     */
    fun uploadFile(drawingV3Bean:ArrayList<DrawingV3Bean?>?) {
        if (drawingV3Bean.isNullOrEmpty() || drawingV3Bean.size <= 0) {
            return
        }

        var ob1 = drawingV3Bean.map { b ->
            b as BaseDbBean
        }



        drawingV3Bean.forEach {

            var path = it!!.localAbsPath

           var fileName = path!!.substring(path.lastIndexOf("/")+1)
            var fileSuffix = path.substring(path.lastIndexOf(".")+1)
            var textBody = RequestBody.create(MediaType.parse("text/plain"),fileSuffix)
            var fileBody = RequestBody.create(MediaType.parse("image/*"), File(path))
            var filePart = MultipartBody.Part.createFormData("file",path,fileBody)

            HttpManager.instance.getHttpService<HttpApi>()
                .fileUpload(filePart,textBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("文件上传成功: ${it}")
                },{
                    LogUtils.d("文件上传失败: ${it}")
                })


      /*      var body = RequestBody.create(MediaType.parse("multipart/form-data"), File(it!!.localAbsPath))


            var multipartBody = MultipartBody.Builder()
                .addFormDataPart("file", it.fileName, body)
                .setType(MultipartBody.FORM)
                .build()

            HttpManager.instance.getHttpService<HttpApi>()
                .uploadFile(multipartBody.parts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("文件上传成功: ${it}")
                },{
                    LogUtils.d("文件上传失败: ${it}")
                })*/
        }
    }

    override fun bindView(v: IBaseView) {
        mView = v as ICheckOBDContrast.ICheckOBDView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}