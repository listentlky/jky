package com.sribs.common.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.cbj.sdk.utils.UriUtil
import java.io.*

object FileUtil {

    fun getCacheDir(c: Context): String? {
        Log.i(
            "123",
            "Environment.MEDIA_MOUNTED=" + Environment.MEDIA_MOUNTED + "    netState=" + Environment.getExternalStorageState()
        )
        return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            c.cacheDir.absolutePath
        }else{
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                Environment.getExternalStorageDirectory().absolutePath + File.separator + "aisai"
                //            return c.getExternalCacheDir().getAbsolutePath();
            } else {
                c.cacheDir.absolutePath
            }
        }



//        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "aisai"
//            //            return c.getExternalCacheDir().getAbsolutePath();
//        } else {
//            c.cacheDir.absolutePath
//        }
    }
    fun getDownloadFile(c: Context, fileName: String): File {
        val tmp = c.externalCacheDir!!.absolutePath + File.separator + fileName
        Log.i("123", "tmp =$tmp")
        return File(tmp)
    }

    @Throws(IOException::class)
    fun downloadFile(c: Context, inputStream: InputStream, dstFileName: String, cb: IProgress?) {
        val f = getDownloadFile(c, dstFileName)
        if (f.exists()) f.delete()
        f.parentFile.mkdirs()
        f.createNewFile()
        val fos = FileOutputStream(f)
        val buffer = ByteArray(1024 * 64)
        var byteCount = 0
        var sum = 0
        while(inputStream.read(buffer).also { byteCount = it }!=-1){
            fos.write(buffer, 0, byteCount)//将读取的输入流写入到输出流
            sum += byteCount
            cb?.onRead(sum)
        }
        fos.flush()//刷新缓冲区
        fos.close()

    }

    fun installApk(c: Context, file: File) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val authority = "com.sribs.aisai.fileprovider"//com.sribs.ryfddandroid.fileprovider
            Log.i("123", "f=$file  authority=$authority")
            val contentUri = FileProvider.getUriForFile(c, authority, file)//fileProvider
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")

        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        c.startActivity(intent)
        Log.e("123", "start intent ok")
    }

    fun save2Album(c: Context, bitmap: Bitmap){
        var fileName = "${System.currentTimeMillis()}.JPG"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            try{
                val insertImage = MediaStore.Images.Media.insertImage(c.contentResolver,
                    bitmap,fileName,null)
                val realPath = UriUtil.getRealPathFromURI(Uri.parse(insertImage),c)
                if (realPath!=""){
                    val file1 = File(realPath)
                    c.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(file1)))
                }
            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(c,"保存图片失败",Toast.LENGTH_SHORT).show()
            }
        }else{

            var v = ContentValues()
            var uri = c.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                v.apply {
                    put(MediaStore.Images.Media.DESCRIPTION, "qr image")
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.TITLE, "Image.jpg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
                })?:return
            var outputStream: OutputStream? = null
            try{
                outputStream = c.contentResolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                outputStream!!.close()
            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(c,"保存图片失败",Toast.LENGTH_SHORT).show()
            }

        }

        Toast.makeText(c,"保存图片成功",Toast.LENGTH_SHORT).show()
    }



    fun getTempPhotoFile(c: Context): File? {
        val f: File =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){

//            File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES),"${System.currentTimeMillis()}.jpg")
                File(getCacheDir(c) + File.separator + "camera" + File.separator + System.currentTimeMillis() + ".png")
            }else{
                File(c.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath+ File.separator + System.currentTimeMillis() + ".jpg")
            }


        Log.i("123", "get tempPhotoFile=$f")
        if (!f.parentFile.exists()) {
            val isok = f.parentFile.mkdirs()
            Log.i("123", "mkdirs ok=$isok")
        }
        return f
    }



    fun saveTempPhoto(c:Context,f:File?,bitmap:Bitmap):String{
        if (f==null)return ""
        return try{
            var bos = BufferedOutputStream(FileOutputStream(f))
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bos)
            bos.flush()
            bos.close()
            f.absolutePath
        }catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(c,"保存图片失败",Toast.LENGTH_SHORT).show()
            ""
        }
    }

    fun delFile(dir:File?){
        var u:Uri
        if (dir==null)return
        if(!dir.exists())return
        if(!dir.isDirectory)return
        dir.listFiles().forEach {
            if (it.exists() && it.isFile) it.delete()
        }
    }

    fun delFile(c:Context,uri:Uri?){
        if (uri==null)return
        c.contentResolver.delete(uri,null,null)
    }

    fun saveDrawable(c:Context,@DrawableRes id:Int,name:String):File{
        var bitmap = BitmapFactory.decodeResource(c.resources,id)

        var p = c.filesDir.absolutePath+"/$name"
        var f = File(p)
        if (f.exists())return f
        try{
            f.createNewFile()
            var fout = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fout)
            fout.flush()
            fout.close()

        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            bitmap.recycle()
        }
        return f
    }


    interface IProgress {
        fun onRead(count: Int)
    }

    fun uriToFileName(uri:Uri,context: Context):String{
        return when(uri.scheme){
            ContentResolver.SCHEME_FILE -> uri.toFile().name
            ContentResolver.SCHEME_CONTENT->{
                val cursor = context.contentResolver.query(uri, null, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                    displayName
                }?:"${System.currentTimeMillis()}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))}}"

            }
            else -> "${System.currentTimeMillis()}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))}}"
        }
    }

    fun isFolderExists(c:Context,name:String): Boolean {

        val path = c?.getExternalFilesDir("")
        val folder = File(path, name)
        return folder.exists()
    }

    fun makeRecurDirs(c:Context,dirName:String): Boolean {

        var ret: Boolean = false
        var path = c?.getExternalFilesDir("")

        var list = dirName.split("/")
        var temp: String = ""
        list?.forEachIndexed  { idx, bean ->
            val folder = File(path, list[idx])
            folder.mkdir()
            ret = folder.exists()
            temp  = temp + "/" + list[idx]
            println("leon tepm=$temp")
            path = c?.getExternalFilesDir(temp)
        }

        return ret
    }

    fun getDrawingCacheRootDir(c:Context): String {

        //return "/storage/emulated/0/Android/data/com.sribs.bdd/files"
        var path = c?.getExternalFilesDir("") as File
        return path.absolutePath
    }

    fun getFileExtension(name:String): String{
        if(name.isNullOrEmpty())
            return ""
        else{
            var dotPos = name.indexOf('.')
            if(dotPos > 0){
                return name.substring(dotPos+1, name.length)
            }
            return ""
        }
    }

    fun copyFileTo(ctx:Context, soucreUri: Uri, destPath: String){

        var uriLen: Int = soucreUri?.path?.length ?: 0
        if (uriLen > 0) {
            println("leon source file=" + soucreUri.toString())

            try {
                val inStream: InputStream? = soucreUri?.let {
                    ctx.getContentResolver().openInputStream(
                        it
                    )
                }
                val bytesArray = inStream?.let { ByteArray(it.available()) }
                val read: Int = inStream?.read(bytesArray)!!

                val newFile: File = File(destPath)
                println("leon dest file=" + destPath)
                if (read > 0) {
                    val fos = FileOutputStream(newFile.path)
                    fos.write(bytesArray)
                    fos.close()
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun generateNewFileName(origiName:String, addString: String): String{
        var dotPos:Int = origiName.indexOf('.')
        var newName:String

        if (dotPos > 0) {
            newName = origiName.substring(
                0,
                dotPos
            ) + "-" + addString + origiName.substring(
                dotPos,
                origiName.length
            )
        } else {
            newName = origiName + "-" + addString
        }

        return newName
    }


    fun getFileName(pathandname: String): String? {
        val start = pathandname.lastIndexOf("/")
     //   val end = pathandname.lastIndexOf(".")
        val end = pathandname.length
        return if (start != -1 && end != -1) {
            pathandname.substring(start + 1, end)
        } else {
            null
        }
    }

}
