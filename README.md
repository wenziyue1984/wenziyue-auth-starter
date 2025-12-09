# **wenziyue-auth-starter**



wenziyue-auth-starter 是一个面向微服务体系的认证上下文 Starter。它与 **Spring Cloud Gateway** 配合使用，由网关完成统一的登录、鉴权、白名单、token 续期与用户缓存校验；本 Starter 只负责在下游服务中**从请求头恢复用户上下文**，将 LoginUser 转换为 Authentication 写入 **SecurityContext**，让业务服务可以继续使用 **@PreAuthorize** 与统一的用户获取工具类。



这意味着你不再需要在每个业务服务中重复写 JWT 过滤器，也不再需要实现单体模式的 UserDetailsServiceByIdOrToken 之类扩展接口。**入口安全只在网关做一次，下游只负责“接住身份”。**



------



## **✨ 功能特点**

- ✅ 面向“网关统一鉴权 + 下游身份恢复”的微服务模式
- ✅ 从网关透传的请求头中解析 LoginUser
- ✅ 自动构建 Authentication 并写入 **SecurityContext**
- ✅ 兼容原有 **@PreAuthorize(“hasAuthority(‘USER’)”)** 写法
- ✅ 提供 AuthHelper 简化获取当前登录用户
- ✅ Starter 自动配置，业务服务开箱即用
- ✅ 默认不做 URL 层硬拦截，交由网关统一管理白名单与入口权限



------



## **🛠️ 快速开始**



### **1. 引入依赖**

首先在 settings.xml 中添加以下认证信息：

```
<server>
    <id>wenziyue-auth</id>
    <username>你的GitHub用户名</username>
    <password>你的GitHub Token（建议只赋予 read:packages 权限）</password>
</server>
```

再在 pom.xml 中添加 GitHub 仓库地址：

```
<repositories>
    <repository>
        <id>wenziyue-auth</id>
        <url>https://maven.pkg.github.com/wenziyue1984/wenziyue-auth-starter</url>
    </repository>

    <!-- auth-core 仓库（如果你没有在父pom统一管理） -->
    <repository>
        <id>github-auth-core</id>
        <url>https://maven.pkg.github.com/wenziyue1984/wenziyue-auth-core</url>
    </repository>
</repositories>
```

然后引入依赖：

```
<dependency>
    <groupId>com.wenziyue</groupId>
    <artifactId>wenziyue-auth-starter</artifactId>
    <version>1.0.0（请使用最新版本）</version>
</dependency>
```

> 💡 注意：访问 GitHub Packages 需要在 Maven 的 settings.xml 中配置 Token 授权。

------



### **2. 自动启用**

引入后无需额外配置。Spring Boot 会自动加载该 Starter 的自动配置类，注册 HeaderAuthFilter 与默认的 SecurityFilterChain。

业务方如果自行定义了 SecurityFilterChain，会覆盖 Starter 的默认行为。



------



### **3. 网关与 Header 约定**

本 Starter 依赖网关写入的 Header。建议使用 auth-core 中的统一常量：

- **Authorization Header**：由网关解析 token
- **USER_ID_HEADER**：透传 userId
- **USER_INFO_HEADER**：透传 LoginUser（建议 **JSON + Base64(URL safe)**）



网关侧建议统一使用 HeaderUtils.serializeUserInfoToHeader(loginUser) 生成 Header。

下游侧由 Starter 统一使用 HeaderUtils.parseUserInfoFromHeader(header) 解析。



------



## **🔧 使用方式**



### **1. 方法级鉴权**

你原有的写法可以保持不变：

```java
@PreAuthorize("hasAuthority('USER')")
@GetMapping("/me")
public UserProfileVO me() {
    String userId = AuthHelper.currentUserId();
    return userProfileService.getByUserId(userId);
}
```

这也是你选择保留 Spring Security 的关键收益。**不需要重写一套自定义权限注解。**



------



### **2. 获取当前用户**

Starter 提供统一的帮助类：

```java
LoginUser user = AuthHelper.currentUser();
String userId = AuthHelper.currentUserId();
```

业务服务从此不再关心 Header 解析细节。



------



## **📦 自动配置说明**

Starter 默认的核心思路是：

**网关负责入口白名单与登录拦截，本 Starter 只恢复上下文，不重复做入口级鉴权。**



因此默认的 SecurityFilterChain 建议采用：

- 关闭 CSRF
- 不使用 Session
- URL 层 **permitAll**
- 启用方法级权限注解
- 在 UsernamePasswordAuthenticationFilter 之前插入 HeaderAuthFilter



如果你希望某个业务服务在 URL 层也做额外限制，只需要在该服务中自定义 SecurityFilterChain 即可覆盖默认策略。



------



## **📦 模块结构说明**

```yaml
wenziyue-auth-starter
├── config/
│   └── WenziyueAuthAutoConfiguration.java   # 自动配置（SecurityFilterChain + Filter 注册）
├── filter/
│   └── HeaderAuthFilter.java               # 核心过滤器：解析 USER_INFO_HEADER -> SecurityContext
├── helper/
│   └── AuthHelper.java                     # 获取当前用户的统一入口
└── META-INF/
    └── spring.factories                    # Spring Boot 自动装配入口
```



------



## **📄 版本说明**

- 要求：Spring Boot 2.7.18，JDK 8
- 依赖：wenziyue-auth-core

------



## **🔗 推荐搭配**

- **blog-gateway-service**：

  负责登录、鉴权、白名单、token 续期、Redis 用户缓存校验与 Header 透传。



------





## **📬 联系作者**

如有建议或问题，欢迎提 Issue 或联系作者 😊



------

