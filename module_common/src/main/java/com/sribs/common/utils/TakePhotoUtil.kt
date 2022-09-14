package com.sribs.common.utils

import android.app.Activity
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import cc.shinichi.library.tool.ui.PhoneUtil
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libbase.utils.LOG
import com.sl.utakephoto.compress.CompressConfig
import com.sl.utakephoto.crop.CropOptions
import com.sl.utakephoto.exception.TakeException
import com.sl.utakephoto.manager.ITakePhotoResult
import com.sl.utakephoto.manager.UTakePhoto
import com.sl.utakephoto.utils.TUriUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
//import com.sribs.sdpsdk.PhoneUtil
//import xyz.mercs.sdpsdk.PhoneUtil
import java.io.File

import java.util.concurrent.TimeUnit

/**
 * @date 2021/6/15
 * @author elijah
 * @Description 兼容Android11  拍照 相册获取图片
 */
object TakePhotoUtil {

    fun useDefault(){
        UTakePhoto.init(CompressConfig.Builder().setLeastCompressSize(300).create(),
        CropOptions.Builder().setOutputX(500).setOutputY(500).setWithOwnCrop(true).create())
    }
    var outF:File?=null

    fun takePhoto(activity:Activity,type:Int,callBackCode:Int){

        val takePhotoManager = UTakePhoto.with(activity)
        if (type==1){
            takePhotoManager.openAlbum()
        }else if(type == 2){
            outF = FileUtil.getTempPhotoFile(activity)
            var u = TUriUtils.getUriFromFile(activity,outF)
//            takePhotoManager.openCamera("Pictures/TakePhoto")
            takePhotoManager.openCamera(u)
        }
        var w = PhoneUtil.getPhoneWid(activity)
        var h = PhoneUtil.getPhoneHei(activity)
        val x = 2560
        val y = 2560/4*3
        LOG.I("123","x=$x  y=$y  w=$w   h=$h")
        var crop = CropOptions.Builder()
            .setWithOwnCrop(true)
//            .setOutputX(x)
//            .setOutputY(y)
            .setAspectX(4)
            .setAspectY(3)
        takePhotoManager.setCrop(crop.create())
//        takePhotoManager.setCrop(null) //不压缩
        takePhotoManager.setCameraPhotoRotate(false)
        takePhotoManager.setCompressConfig(
            CompressConfig.Builder().setLeastCompressSize(50).setTargetUri(
                activity.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentValues()
                )
            ).create()
        )


        takePhotoManager.build(object :ITakePhotoResult{
            override fun takeSuccess(uriList: MutableList<Uri>?) {
                LOG.I("123","uriList=$uriList")
                uriList?.get(0)?.let {
                    //保存到本地
                    val pfd = activity.contentResolver.openFileDescriptor(it, "r")
                    if (pfd!=null){
                        val bitmap =
                            BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                        var path = FileUtil.saveTempPhoto(activity,
                            FileUtil.getTempPhotoFile(activity),bitmap)
                        try{
                            outF?.delete()
                            outF = null
//                        FileUtil.delFile(activity,it)
                            var field = takePhotoManager::class.java.getDeclaredField("tempUri")
                            field.isAccessible = true
                            var tempUri = field.get(takePhotoManager) as Uri
                            FileUtil.delFile(activity,tempUri)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }


                        Observable.timer(500,TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({
                                RxBus.getDefault()
                                    .postWithCode(callBackCode,path)

                            },{ e->
                                e.printStackTrace()
                            })

                    }
                }



                //删除原图与截图
                // /sdcard/Pictures/DICM    /sdcard/Pictures/TakePhoto
//                var f1:File?=null
//                var f2:File?=null
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//
//                }else{
//                    f1= File( Environment.getExternalStorageDirectory(),
//                        "DICM")
//                    f2= File( Environment.getExternalStorageDirectory(),
//                        "TakePhoto")
//                }
//
//
//
//                LOG.I("123","f1=$f1    path=${f1.absolutePath}\n" +
//                        "f2=$f2  path=${f2.absolutePath}")
//                FileUtil.delFile(f1)
//                FileUtil.delFile(f2)
            }

            override fun takeFailure(ex: TakeException?) {
                LOG.E("123","takeFailure $ex")
                if (ex!=null) {
                    Toast.makeText(activity, ex.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun takeCancel() {
                LOG.E("123","takeCancel")
            }

        })

    }
}