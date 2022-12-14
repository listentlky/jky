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
                    status = b.status,
                    isChanged = b.isChanged
                )
                })
                LogUtils.d("????????????????????????????????? "+list.toString())
                LogUtils.d("mView "+mView)
                mView?.onModuleInfo(list)
                dispose()
            },{
                dispose()
                mView?.onMsg(it.toString())
                LogUtils.d("????????????????????????????????? ${it}")
            }))
    }

    override fun saveDamageToDb(drawingV3Bean: List<DrawingV3Bean>, id: Long) {
        addDisposable(mDb.updatev3BuildingModuleDrawing(id,drawingV3Bean)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("??????????????????????????????")
            },{
                LogUtils.d("??????????????????????????????: "+it)
            }))
    }

    /**
     * ????????????
     */
    fun uploadFile(drawingV3Bean:ArrayList<DrawingV3Bean?>?) {
        if (drawingV3Bean.isNullOrEmpty() || drawingV3Bean.size <= 0) {
            return
        }

        var ob1 = drawingV3Bean.map { b ->
            b as BaseDbBean
        }


        var parts = ArrayList<MultipartBody.Part>()

        drawingV3Bean.forEach {

           var path = it!!.localAbsPath

           var fileName = path!!.substring(path.lastIndexOf("/")+1)
            var fileSuffix = path.substring(path.lastIndexOf(".")+1)
       //     var textBody = RequestBody.create(MediaType.parse("text/plain"),fileSuffix)
            var fileBody = RequestBody.create(MediaType.parse("image/*"), File(path))
            var filePart = MultipartBody.Part.createFormData("files",path,fileBody)

            LogUtils.d("????????????: "+filePart)


            parts.add(filePart)

            /*  var body = RequestBody.create(MediaType.parse("multipart/form-data"), File(it!!.localAbsPath))


              var multipartBody = MultipartBody.Builder()
                  .addFormDataPart("file", it.fileName, body)
                  .setType(MultipartBody.FORM)
                  .build()

              HttpManager.instance.getHttpService<HttpApi>()
                  .uploadFile(multipartBody.parts())
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe({
                      LogUtils.d("??????????????????: ${it}")
                  },{
                      LogUtils.d("??????????????????: ${it}")
                  })*/
        }

        HttpManager.instance.getHttpService<HttpApi>()
            .uploadFile(parts)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("??????????????????: ${it}")
            },{
                LogUtils.d("??????????????????: ${it}")
            })
    }

    override fun bindView(v: IBaseView) {
        mView = v as ICheckOBDContrast.ICheckOBDView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}