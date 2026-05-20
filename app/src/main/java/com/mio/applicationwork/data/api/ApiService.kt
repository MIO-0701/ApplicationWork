package com.mio.applicationwork.data.api

import com.mio.applicationwork.data.model.*
import retrofit2.Response
import retrofit2.http.*

// Retrofit API 接口定义 —— 声明所有后端 HTTP 端点
//
// 接口分为三大块:
// 1. 盲人端（/mangRen/**）—— 登录、注册、信息管理、预约、评价
// 2. 志愿者端（/zhiYuan/**）—— 登录、注册、信息管理、接单
// 3. 通用接口（/user/**）—— 跑步数据查询
//
// 每个方法均为 suspend 函数，返回 Response<ApiResponse<T>> 以便在 Repository 层统一处理
interface ApiService {

    // ============================================================
    // 盲人端 API —— 路径前缀 /mangRen
    // ============================================================

    /** 盲人登录 —— POST /mangRen/login */
    @POST("mangRen/login")
    suspend fun mangRenLogin(@Body request: LoginRequest): Response<ApiResponse<LoginData>>

    /** 盲人注册 —— POST /mangRen/zhuCe */
    @POST("mangRen/zhuCe")
    suspend fun mangRenRegister(@Body request: MangRenRegisterRequest): Response<ApiResponse<Boolean>>

    /** 盲人获取个人信息 —— GET /mangRen/getuser?id=xxx */
    @GET("mangRen/getuser")
    suspend fun mangRenGetUser(@Query("id") userId: Int): Response<ApiResponse<MangRenUserInfo>>

    /** 盲人修改个人信息 —— POST /mangRen/updataUser */
    @POST("mangRen/updataUser")
    suspend fun mangRenUpdateUser(@Body request: UpdateUserRequest): Response<ApiResponse<Boolean>>

    /** 盲人修改密码 —— POST /mangRen/updataPassword，请求体传 {"id":1,"password":"xxx"} */
    @POST("mangRen/updataPassword")
    suspend fun mangRenUpdatePassword(@Body body: PasswordUpdateRequest): Response<ApiResponse<Boolean>>

    /** 盲人创建预约 —— POST /mangRen/createYuYue */
    @POST("mangRen/createYuYue")
    suspend fun mangRenCreateYuYue(@Body request: CreateYuYueRequest): Response<ApiResponse<Int>>

    /** 盲人获取预约列表 —— GET /mangRen/getYuYue?id=xxx */
    @GET("mangRen/getYuYue")
    suspend fun mangRenGetYuYue(@Query("id") userId: Int): Response<ApiResponse<List<YuYueItem>>>

    /** 盲人取消预约 —— GET /mangRen/delYuYue?yuYueId=xxx */
    @GET("mangRen/delYuYue")
    suspend fun mangRenDelYuYue(@Query("yuYueId") yuYueId: Int): Response<ApiResponse<Boolean>>

    /** 盲人评价志愿者 —— POST /mangRen/pingJia */
    @POST("mangRen/pingJia")
    suspend fun mangRenPingJia(@Body request: PingJiaRequest): Response<ApiResponse<Boolean>>

    // ============================================================
    // 志愿者端 API —— 路径前缀 /zhiYuan
    // ============================================================

    /** 志愿者登录 —— POST /zhiYuan/login */
    @POST("zhiYuan/login")
    suspend fun zhiYuanLogin(@Body request: LoginRequest): Response<ApiResponse<LoginData>>

    /** 志愿者注册 —— POST /zhiYuan/zhuCe */
    @POST("zhiYuan/zhuCe")
    suspend fun zhiYuanRegister(@Body request: ZhiYuanRegisterRequest): Response<ApiResponse<Boolean>>

    /** 志愿者获取个人信息 —— GET /zhiYuan/getuser?id=xxx */
    @GET("zhiYuan/getuser")
    suspend fun zhiYuanGetUser(@Query("id") userId: Int): Response<ApiResponse<ZhiYuanUserInfo>>

    /** 志愿者获取预约列表 —— GET /zhiYuan/getYuYue?id=xxx */
    @GET("zhiYuan/getYuYue")
    suspend fun zhiYuanGetYuYue(@Query("id") userId: Int): Response<ApiResponse<List<YuYueItem>>>

    /** 志愿者获取所有盲人预约请求 —— GET /zhiYuan/getAllYuYue */
    @GET("zhiYuan/getAllYuYue")
    suspend fun zhiYuanGetAllYuYue(): Response<ApiResponse<List<YuYueItem>>>

    /** 志愿者接单（确认预约）—— POST /zhiYuan/yuYue */
    @POST("zhiYuan/yuYue")
    suspend fun zhiYuanYuYue(@Body request: YuYueSelectRequest): Response<ApiResponse<Boolean>>

    /** 志愿者取消已接预约 —— GET /zhiYuan/delYuYue?yuYueId=xxx */
    @GET("zhiYuan/delYuYue")
    suspend fun zhiYuanDelYuYue(@Query("yuYueId") yuYueId: Int): Response<ApiResponse<Boolean>>

    /** 志愿者修改个人信息 —— POST /zhiYuan/updataUser */
    @POST("zhiYuan/updataZhiYuan")
    suspend fun zhiYuanUpdateUser(@Body request: UpdateUserRequest): Response<ApiResponse<Boolean>>

    /** 志愿者修改密码 —— POST /zhiYuan/updataPassword，请求体传 {"id":1,"password":"xxx"} */
    @POST("zhiYuan/updataPassword")
    suspend fun zhiYuanUpdatePassword(@Body body: PasswordUpdateRequest): Response<ApiResponse<Boolean>>

    // ============================================================
    // 通用接口 —— 路径前缀 /user
    // ============================================================

    /** 获取跑步数据（盲人/志愿者通用）—— POST /user/getRun */
    @POST("user/getRun")
    suspend fun getRunData(@Body request: RunDataRequest): Response<ApiResponse<List<RunData>>>
}
