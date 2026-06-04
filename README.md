# 助盲跑 Android 项目

助盲跑是一款面向视障跑者与陪跑志愿者的 Android 应用。项目通过“盲人用户”和“志愿者用户”两种身份，提供一键求助、实时匹配、预约陪跑、运动数据记录、志愿者评价等功能，帮助视障跑者更安全、便捷地参与跑步运动。

## APK下载链接
https://github.com/MIO-0701/ApplicationWork/releases/download/1.0/app-debug.apk.1.1

## 核心功能截图
app/src/main/res

## 项目特点

- 双角色系统：支持盲人用户和志愿者用户登录、注册和不同功能入口。
- 一键求助/接单：盲人发起求助，志愿者参与匹配，系统轮询后端完成实时配对。
- 预约陪跑：盲人可提前创建预约，志愿者可查看预约并接单。
- 运动数据：支持记录和查看跑步距离、配速、时长等数据。
- 评价反馈：盲人可对陪跑志愿者进行评分。
- 免登录：支持 7 天本地会话保存，提升使用便利性。

## 技术栈

- 开发语言：Kotlin
- UI 框架：Jetpack Compose
- 架构模式：MVVM
- 页面导航：Navigation Compose
- 网络请求：Retrofit + OkHttp
- 数据解析：Gson
- 本地存储：SharedPreferences
- 最低 SDK：Android 7.0 / API 24

## 项目架构

项目采用 MVVM 分层架构：

```text
View 层
  负责页面展示和用户交互，例如 LoginScreen、HomeScreen、QuickHelpScreen。

ViewModel 层
  负责页面状态管理、表单校验和业务流程控制，例如 LoginViewModel、QuickHelpViewModel。

Repository 层
  负责封装数据请求，例如 UserRepository、MatchRepository、ReservationRepository。

Network / Model 层
  ApiService 定义后端接口，RetrofitClient 统一创建网络客户端，Models.kt 定义请求和响应数据结构。
```

## 目录结构

```text
app/src/main/java/com/mio/applicationwork
├── MainActivity.kt                         # 应用入口
├── data
│   ├── api
│   │   ├── ApiService.kt                   # 后端接口定义
│   │   └── RetrofitClient.kt               # Retrofit 网络客户端
│   ├── local
│   │   └── SessionManager.kt               # 7 天免登录会话管理
│   ├── model
│   │   └── Models.kt                       # 请求/响应数据模型
│   └── repository
│       ├── UserRepository.kt               # 登录、注册、用户、运动数据
│       ├── MatchRepository.kt              # 实时匹配
│       └── ReservationRepository.kt        # 预约、接单、评价
└── ui
    ├── navigation
    │   └── NavGraph.kt                     # 页面导航图
    ├── screen
    │   ├── Screen.kt                       # 页面路由定义
    │   ├── login                           # 登录
    │   ├── register                        # 注册
    │   ├── home                            # 主页
    │   ├── quickhelp                       # 一键求助/实时匹配
    │   ├── createreservation               # 创建预约
    │   ├── reservationlist                 # 预约列表/接单
    │   ├── rundata                         # 运动数据
    │   ├── ratevolunteer                   # 志愿者评价
    │   ├── profile                         # 个人中心
    │   ├── editprofile                     # 编辑资料
    │   └── changepassword                  # 修改密码
    └── theme                               # Compose 主题
```

## 核心功能说明

### 登录与免登录

登录页面支持盲人和志愿者两种身份切换。登录成功后，客户端保存后端返回的 token，并在后续请求中自动添加到 `Authorization` 请求头。

如果用户勾选“7 天免登录”，`SessionManager` 会把 token、用户 ID、用户类型和登录时间保存到本地。应用下次启动时会检查会话是否过期，未过期则自动进入主页。

### 一键求助与实时匹配

一键求助是项目的核心功能，主要由 `QuickHelpScreen`、`QuickHelpViewModel` 和 `MatchRepository` 实现。

匹配流程如下：

```text
输入跑步地点
 -> 点击发起求助/接单
 -> ViewModel 校验地点
 -> 调用 MatchRepository 请求 /piPei/piPei
 -> 每 3 秒轮询一次后端
 -> 后端返回匹配对象
 -> 页面切换为匹配成功状态
 -> 展示对方信息并记录本次跑步数据
```

匹配页面使用四种状态控制 UI：

```kotlin
enum class MatchPhase {
    Idle,       // 初始状态
    Matching,   // 匹配中
    Matched,    // 匹配成功
    Error       // 匹配失败
}
```

用户取消匹配时，客户端会停止本地轮询，并调用 `/piPei/delPiPei` 通知后端将用户移出匹配池。

### 预约陪跑

盲人用户可以填写跑步地点，并通过日期选择器和时间选择器创建预约。预约信息提交到后端后，志愿者可以在预约列表中查看并接单。

志愿者端预约列表支持两种视图：

- 全部预约：查看所有盲人发出的预约请求。
- 我的接单：查看当前志愿者已经接下的预约。

### 运动数据

运动数据模块支持查看跑步记录，包括距离、配速和时长。页面会自动统计总距离、总时长和记录数量。

匹配成功后，用户也可以录入本次跑步数据，提交到后端保存。

### 志愿者评价

盲人用户可以输入志愿者 ID，并通过评分滑块提交 1 到 5 分的评价，用于形成志愿者信用反馈。

## 后端接口

主要接口包括：

```text
POST /mangRen/login          # 盲人登录
POST /zhiYuan/login          # 志愿者登录
POST /mangRen/zhuCe          # 盲人注册
POST /zhiYuan/zhuCe          # 志愿者注册

POST /piPei/piPei            # 发起匹配
POST /piPei/delPiPei         # 取消匹配

POST /mangRen/createYuYue    # 创建预约
GET  /mangRen/getYuYue       # 盲人查看预约
GET  /zhiYuan/getAllYuYue    # 志愿者查看全部预约
POST /zhiYuan/yuYue          # 志愿者接单

POST /user/getRun            # 获取运动数据
POST /user/addRun            # 添加运动数据
POST /mangRen/pingFen        # 评价志愿者
```

当前后端基础地址配置在：

```kotlin
app/src/main/java/com/mio/applicationwork/data/api/RetrofitClient.kt
```

```kotlin
private const val BASE_URL = "http://36.151.146.231:8080/"
```

## 运行方式

1. 使用 Android Studio 打开项目根目录。
2. 等待 Gradle 同步完成。
3. 确认 `local.properties` 中配置了本机 Android SDK 路径。
4. 连接 Android 真机或启动模拟器。
5. 选择 `app` 模块运行。

## 权限说明

当前项目主要使用网络请求能力：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

后续如果接入地图和定位功能，需要增加定位权限，例如：

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 后续优化方向

- 接入地图 API，实现当前位置获取、地图选点和路线规划。
- 增加语音播报和语音输入，提高视障用户的无障碍体验。
- 将匹配轮询升级为 WebSocket 或服务端推送，降低延迟。
- 增加紧急联系人和 SOS 求助能力。
- 补充 ViewModel 和 Repository 的单元测试。
- 正式发布前关闭完整 token 日志输出，提高安全性。

