package com.mio.applicationwork.data.model

/**
 * 数据模型层 —— 所有与后端接口对应的请求/响应数据结构
 *
 * 后端统一返回格式: { "code": 200/0, "message": "", "data": ... }
 * - code=200 表示成功，code=0 表示失败
 * - data 为具体业务数据，类型随接口不同而变化
 */

// ============================================================
// 统一响应包装
// ============================================================

/** 后端统一响应外壳，T 为 data 字段的具体类型 */
data class ApiResponse<T>(
    val code: Int,        // 200=成功, 0=失败
    val message: String?, // 错误时的提示信息，成功时可能为 null
    val data: T?          // 业务数据，可为空
)

// ============================================================
// 登录相关
// ============================================================

/** 登录请求体，盲人端和志愿者端共用 */
data class LoginRequest(
    val zhangHao: String, // 账号
    val password: String  // 密码
)

/** 登录成功后后端返回的数据 */
data class LoginData(
    val id: Int,     // 用户唯一ID，后续请求需要携带
    val token: String // 认证令牌，后续请求需在 Header 中携带
)

// ============================================================
// 盲人用户（mangRen）
// ============================================================

/** 盲人注册请求体 */
data class MangRenRegisterRequest(
    val name: String,     // 真实姓名
    val zhangHao: String, // 登录账号
    val password: String, // 登录密码
    val sex: Int,         // 性别: 1=男, 0=女
    val suDu: Double,     // 配速 (min/km)
    val gongLi: Double    // 可跑公里数
)

/** 盲人用户信息（getuser 返回） */
data class MangRenUserInfo(
    val id: Int,
    val name: String,
    val sex: Int,
    val suDu: Double,
    val gongLi: Double,
    val createTime: String? = null // 注册时间，可能为 null
)

/** 修改用户信息请求体（盲人/志愿者共用，志愿者可额外携带 renZhen） */
data class UpdateUserRequest(
    val id: Int,
    val name: String,
    val sex: Int,
    val suDu: Double,
    val gongLi: Double,
    val renZhen: String = "" // 仅志愿者需要，盲人留空即可
)

/** 修改密码请求体 */
data class PasswordUpdateRequest(
    val id: Int,
    val password: String
)

// ============================================================
// 志愿者用户（zhiYuan）
// ============================================================

/** 志愿者注册请求体 —— 比盲人多一个 renZhen（认证信息）字段 */
data class ZhiYuanRegisterRequest(
    val name: String,
    val zhangHao: String,
    val password: String,
    val renZhen: String,  // 志愿者特有的认证信息（如身份证明等）
    val sex: Int,
    val suDu: Double,
    val gongLi: Double
)

/** 志愿者用户信息 —— 比盲人多 pingFen（评分）和 renZhen（认证号）字段 */
data class ZhiYuanUserInfo(
    val id: Int,
    val name: String,
    val sex: Int,
    val pingFen: Double? = null,  // 综合评分，由盲人评价累积（可空防御后端缺失字段）
    val renZhen: String? = null,  // 志愿者认证号（注册时填写，可空防御后端缺失字段）
    val suDu: Double,
    val gongLi: Double,
    val createTime: String? = null
)

// ============================================================
// 预约（yuYue）
// ============================================================

/** 创建预约请求体（盲人端发起） */
data class CreateYuYueRequest(
    val id: Int,             // 盲人用户ID
    val diDian: String,      // 跑步地点
    val createTime: String   // 预约时间，格式 "yyyy-MM-dd HH:mm:ss"
)

/** 创建预约成功后返回的数据 */
data class CreateYuYueData(
    val yuYueId: Int // 预约ID，负数为创建失败
)

/**
 * 预约列表中的单项
 * - 盲人端返回: yuYueID, zhiYuanId, diDian, createTime, suDu, gongLi
 * - 志愿者端返回: yuYueID, mangRenId, diDian, createTime, suDu, gongLi
 * 两端字段略有不同，故将两个ID字段都设为可选（默认值0）
 */
data class YuYueItem(
    val yuYueID: Int,
    val zhiYuanId: Int = 0,  // 盲人端返回时有此字段
    val mangRenId: Int = 0,  // 志愿者端返回时有此字段
    val diDian: String,      // 跑步地点
    val createTime: String? = null, // 预约时间（可空，防御后端返回null）
    val suDu: Double,        // 要求的配速
    val gongLi: Double       // 要求的公里数
)

/** 志愿者接单请求体 */
data class YuYueSelectRequest(
    val id: Int,       // 志愿者用户ID
    val yuYueId: Int   // 要接的预约ID
)

// ============================================================
// 评价
// ============================================================

/** 盲人对志愿者的评价请求体 */
data class PingJiaRequest(
    val zhiYuanId: Int, // 被评价的志愿者ID
    val pingFen: Int    // 评分: 1~5
)

// ============================================================
// 跑步数据（通用接口 /user/getRun）
// ============================================================

/** 获取跑步数据的请求体 */
data class RunDataRequest(
    val userType: Int, // 用户类型: 0=盲人, 1=志愿者
    val userId: Int    // 用户ID
)

/** 单条跑步记录 */
data class RunData(
    val juLi: Double,       // 距离（公里）
    val suDu: Double,       // 配速（min/km）
    val shiChang: Double    // 时长（分钟）
)

// ============================================================
// 匹配（piPei）
// ============================================================

/** 匹配请求体 */
data class PiPeiRequest(
    val usertype: Int,   // 0=盲人, 1=志愿者
    val userId: Int,
    val diDian: String   // 地点
)

/** 匹配到的用户信息 */
data class MatchUserInfo(
    val id: Int? = null,
    val name: String? = null,
    val zhangHao: String? = null,
    val password: String? = null,
    val sex: Int? = null,
    val suDu: Double? = null,
    val gongLi: Double? = null,
    val isDel: Int? = null,
    val createTime: String? = null,
    val updataTime: String? = null,
    // 盲人专有字段
    val zaiXian: String? = null,
    val pingFen: Double? = null,
    val isRenZhen: Int? = null
)

/** 匹配响应 */
data class PiPeiResponse(
    val userId: Int,
    val usertype: Int,
    val diDian: String,
    val data: MatchUserInfo?  // null 表示未匹配成功
)

/** 添加跑步数据请求体 */
data class AddRunRequest(
    val id: Int = 0,
    val userType: Int,   // 0=盲人, 1=志愿者
    val userId: Int,
    val suDu: Int,       // 跑步平均速度
    val shiChang: Int,   // 此次跑步时长
    val juLi: Int        // 此次跑步公里数
)
