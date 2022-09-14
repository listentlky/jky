package com.sribs.common.net

import androidx.annotation.NonNull
import com.cbj.sdk.libnet.http.bean.ListBean
import com.cbj.sdk.libnet.http.bean.ResultBean
import com.sribs.common.bean.net.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*



/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
interface HttpApi {
    @Streaming
    @GET
    @Headers("Connection:keep-alive")
    fun download(@Url url: String): Observable<ResponseBody>

    /**
     * @Description 项目
     */
    //1001 项目列表
    @POST("api/v1/app/project/list")
    fun getProjectList(@Body req:ProjectListReq):Observable<ResultBean<ListBean<ProjectListRes>>>

    //1002 单元列表
    @POST("api/v1/app/project/unit/list")
    fun getUnitList(@Body req:UnitListReq):Observable<ResultBean<ListBean<UnitListRes>>>

    //1003 房屋列表
    @POST("api/v1/app/project/unit/part/list")
    fun getPartList(@Body req:PartListReq):Observable<ResultBean<ListBean<PartListRes>>>

    //1006 记录详情
    @POST("api/v1/app/project/record/summary")
    fun getProjectSummary(@Body req:ProjectSummaryReq):Observable<ResultBean<ProjectSummaryRes>>

    /**
     * @Description 项目配置
     */
    //2001 下载项目/单元配置历史版本列表
    @POST("api/v1/app/config/history/list")
    fun getConfigHistoryList(@Body req:HistoryListReq):Observable<ResultBean<ListBean<HistoryListConfigRes>>>

    //2011 下载项目配置
    @POST("api/v1/app/config/project/download")
    fun projectConfigDownload(@Body downloadReq:ProjectDownloadReq):Observable<ResultBean<ProjectConfigDownloadRes>>

    //2021 下载单元配置
    @POST("api/v1/app/config/unit/download")
    fun unitConfigDownload(@Body downloadReq:UnitDownloadReq):Observable<ResultBean<ConfigUnit>>

    //2023 上传单元配置
    @POST("api/v1/app/config/unit/upload")
    fun unitUpload(@Body configReq:UploadUnitConfigReq):Observable<ResultBean<UploadUnitConfigRes>>

    /**
     * @Description 项目记录
     */
    //3001 下载项目/单元记录历史版本列表
    @POST("api/v1/app/record/history/list")
    fun getRecordHistoryList(@Body req:HistoryListReq):Observable<ResultBean<ListBean<HistoryListRecordRes>>>

    //3011 下载项目记录
    @POST("api/v1/app/record/project/download")
    fun projectRecordDownload(@Body req: ProjectDownloadReq):Observable<ResultBean<ProjectRecordDownloadRes>>

    //3021 下载单元记录
    @POST("api/v1/app/record/unit/download")
    fun unitRecordDownload(@Body req: UnitDownloadReq):Observable<ResultBean<RecordUnit>>


    //3023 上传单元记录
    @POST("api/v1/app/record/unit/upload")
    fun unitRecordUpload(@Body req:UploadUnitRecordReq):Observable<ResultBean<Any>>

    /**
     * @Description 用户模块
     */
    //4001 用户登录
    @POST("api/v1/app/user/login")
    fun login(@Body req: LoginReq): Observable<ResultBean<LoginRes>>

    //4002 登录用户信息
    @POST("api/v1/app/user/info")
    fun userInfo():Observable<ResultBean<UserInfoRes>>

    /**
     * @Description 系统模块
     */
    //5001 版本检测
    @POST("api/v1/app/system/version")
    fun systemVersion():Observable<ResultBean<VersionRes>>

    //5011 系统用户信息下载
    @POST("api/v1/app/system/user/info/download")
    fun userInfos():Observable<ResultBean<ListBean<UserInfoRes>>>

    //5021 文件上传
    @Multipart
    @POST("api/v1/app/system/file/upload")
    fun fileUpload(
        @Part @NonNull file: MultipartBody.Part,
        @Part("typeCode") @NonNull body: RequestBody
    ): Observable<ResultBean<FileUploadRes>>


    //7002 添加/编辑项目
    @POST("api/v1/project/submit")
    fun updateProject(@Body req:ProjectUpdateReq):Observable<ResultBean<ProjectUpdateRes>>

    /**
     * @Description  用户管理
     */
    //9106 获取指定角色类型的用户
    @POST("api/v1/role/user/list")
    fun getRoleUserList(@Body req:RoleUserListReq):Observable<ResultBean<ListBean<RoleUserListRes>>>

    /**
     * V3
     */
  //  @GET(“http://103.21.143.227:40001/mock/191/api/v3/app/building/project/list”)

}