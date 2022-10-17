package com.sribs.common.net

import androidx.annotation.NonNull
import com.cbj.sdk.libnet.http.bean.ListBean
import com.cbj.sdk.libnet.http.bean.ResultBean
import com.sribs.common.bean.net.*
import com.sribs.common.bean.net.v3.*
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


    @POST("api/v3/app/building/list")
    fun getV3UnitList(@QueryName projectId:String):Observable<ResultBean<ListBean<UnitListRes>>>


    /**
     * V3 ↓
     * 查询所有项目
     */
    //
    @GET("api/v3/app/building/project/list")
    fun getV3ProjectList():Observable<ResultBean<List<ProjectListRes>>>

    /**
     * 查询指定项目版本列表
     */
    @GET("api/v3/app/building/project/version/list")
    fun getV3ProjectVersionList(@Query("projectId") remoteProjectId: String):Observable<ResultBean<List<V3VersionListRes>>>

    /**
     * 下载项目的指定版本
     */
    @POST("api/v3/app/building/project/download/versionList")
    fun downloadV3ProjectVersionList(@Body req:V3VersionDownloadReq):Observable<ResultBean<Any>>


    @POST("api/v3/app/project/save")
    fun saveV3Project(@Body req:V3UploadProjectReq):Observable<ResultBean<Any>>

    @POST("api/v3/app/project/delete")
    fun deleteProject(@Body req:V3VersionDeleteReq):Observable<ResultBean<Any>>

    /**
     * 查询项目下所有楼
     */
    @POST("api/v3/app/building/list")
    fun getV3BuildingList(@Query("projectId") projectId:String,@Query("version") version:Int):Observable<ResultBean<ListBean<Any>>>

    /**
     * 查询指定楼的楼版本列表
     */
    @POST("api/v3/app/building/version/list")
    fun getV3BuildingVersionList(@Body req:V3VersionReq):Observable<ResultBean<ListBean<V3VersionListRes>>>

    /**
     * 下载指定版本楼
     */
    @POST("api/v3/app/building/versionList")
    fun downloadV3BuildingVersionList(@Body req:V3VersionDownloadReq):Observable<ResultBean<Any>>

    /**
     * 新建保存楼
     */
    @POST("api/v3/app/building/save")
    fun saveV3Building(@Body req: V3BuildingSaveReq):Observable<ResultBean<Any>>

    /**
     * 删除楼
     */
    @POST("api/v3/app/building/delete")
    fun deleteBuilding(@Body req:V3VersionDeleteReq):Observable<ResultBean<Any>>

    /**
     * 查询楼下的模块
     */
    @POST("/api/v3/app/building/module/list")
    fun getV3BuildingModuleList(@Query("buildingId") buildingRemoteId: String,@Query("version") version:Int):Observable<ResultBean<ListBean<Any>>>

    /**
     * 查询指定楼的楼模块列表
     */
    @POST("api/v3/app/building/module/version/list")
    fun getV3ModuleVersionList(@Body req:V3VersionReq):Observable<ResultBean<ListBean<V3VersionListRes>>>

    /**
     * 下载指定模块
     */
    @POST("api/v3/app/building/versionList")
    fun downloadV3ModuleVersionList(@Body req:V3VersionDownloadReq):Observable<ResultBean<Any>>

    /**
     * 新建保存模块
     */
    @POST("/api/v3/app/module/save")
    fun saveBuildingModule(@Body req: V3BuildingModuleListRes):Observable<ResultBean<Any>>

    /**
     * 删除模块
     */
    @POST("/api/v3/app/module/delete")
    fun deleteBuildingModule(@Body req:V3VersionDeleteReq):Observable<ResultBean<Any>>

    /**
     * 文件上传
     */

    @Multipart
    @POST("api/v3/app/file/upload")
    fun uploadFile(
        @Part @NonNull file: List<MultipartBody.Part>
    ): Observable<ResultBean<List<V3UploadDrawingRes>>>

}