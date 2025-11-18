# Health Track Personal Wellness Platform

Health Track个人健康平台 - Phase 3实现

## 项目简介

Health Track是一个个人健康管理平台，允许用户跟踪健康指标、管理医疗预约、创建和参与健康挑战等。

## 技术栈

- **后端**: Java Servlets + JSP
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **Web服务器**: Apache Tomcat
- **前端**: HTML5, CSS3, JavaScript (JSTL)

## 项目结构

```
assign3/
├── sql/
│   ├── create_tables.sql      # 数据库表结构
│   ├── create_triggers.sql     # 数据库触发器
│   └── populate_data.sql       # 样本数据
├── src/
│   └── main/
│       ├── java/com/healthtrack/
│       │   ├── model/          # 实体类
│       │   ├── dao/             # 数据访问层
│       │   ├── servlet/         # 控制器
│       │   └── util/            # 工具类
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml      # Web配置
│           ├── css/             # 样式文件
│           ├── js/              # JavaScript文件
│           └── jsp/              # JSP页面
├── docs/
│   └── project_d3.md            # 项目文档
├── pom.xml                      # Maven配置
└── README.md                    # 本文件
```

## 环境要求

- Java JDK 11 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 9.0+ 或使用Maven Tomcat插件

## 安装步骤

### 1. 数据库设置

1. 启动MySQL服务器

2. 创建数据库（可选，脚本会自动创建）:
```sql
CREATE DATABASE healthtrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 执行SQL脚本（按顺序）:
```bash
mysql -u root -p < sql/create_tables.sql
mysql -u root -p < sql/create_triggers.sql
mysql -u root -p < sql/populate_data.sql
```

或者直接在MySQL客户端中执行：
```sql
source sql/create_tables.sql;
source sql/create_triggers.sql;
source sql/populate_data.sql;
```

### 2. 配置数据库连接

编辑 `src/main/java/com/healthtrack/util/DBConnection.java`，修改数据库连接信息：

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/healthtrack?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
private static final String DB_USER = "root";        // 修改为你的MySQL用户名
private static final String DB_PASSWORD = "root";   // 修改为你的MySQL密码
```

### 3. 构建项目

使用Maven构建项目：

```bash
mvn clean package
```

这将生成 `target/health-track.war` 文件。

### 4. 部署应用

#### 方式1: 使用Maven Tomcat插件（推荐用于开发）

```bash
mvn tomcat7:run
```

应用将在 `http://localhost:8080/healthtrack` 运行。

#### 方式2: 部署到Tomcat服务器

1. 将 `target/health-track.war` 复制到Tomcat的 `webapps/` 目录
2. 启动Tomcat服务器
3. 访问 `http://localhost:8080/health-track`

## 使用说明

### 登录系统

1. 访问应用首页
2. 使用以下Health ID之一登录：
   - HT001, HT002, HT003, HT004, HT005
   - HT006, HT007, HT008, HT009, HT010

### 主要功能

1. **Account Info**: 查看和编辑个人信息、管理邮箱/手机号、关联医疗服务提供者
2. **Book Appointment**: 预约医疗服务提供者，查看和管理预约
3. **Create Challenge**: 创建健康挑战，添加参与者
4. **Monthly Summary**: 查看月度健康汇总和统计信息
5. **Search Records**: 根据多种条件搜索健康记录

## 数据库说明

### 主要表结构

- **USER**: 用户基本信息
- **EMAIL**: 用户邮箱（多值）
- **PHONE**: 用户手机号（0..1）
- **PROVIDER**: 医疗服务提供者
- **APPOINTMENT**: 医疗预约
- **CHALLENGE**: 健康挑战
- **MONTHLY_SUMMARY**: 月度健康汇总

### 关键约束

- 每个用户最多一个手机号
- 每个用户可以有多个邮箱
- 每个用户最多一个主治医生（is_primary=true）
- 预约取消需提前至少24小时
- 家庭组至少需要2名活跃成员
- 邀请的target_email和target_phone互斥

## 开发说明

### 添加新功能

1. 在 `model/` 包中创建实体类
2. 在 `dao/` 包中创建数据访问对象
3. 在 `servlet/` 包中创建控制器
4. 在 `webapp/jsp/` 中创建JSP页面

### 代码规范

- 使用Java命名规范
- 添加必要的注释
- 处理异常情况
- 验证用户输入

## 故障排除

### 数据库连接问题

- 检查MySQL服务是否运行
- 验证数据库连接信息是否正确
- 确认数据库已创建且表已初始化

### 编译错误

- 确保Java版本为11或更高
- 检查Maven依赖是否正确下载
- 清理并重新构建: `mvn clean install`

### 运行时错误

- 检查Tomcat日志文件
- 确认所有依赖已正确部署
- 验证数据库连接配置

## 测试数据

系统已预填充以下测试数据：

- 10个用户账户
- 8个医疗服务提供者
- 10个预约记录
- 6个健康挑战
- 3个家庭组
- 15条月度汇总记录

## 贡献者

- 肖轶伟 2025202110009
- 蒋硕 2025202110005
- 刘仲翔 2025202110004

## 许可证

本项目仅用于学术目的。

## 联系方式

如有问题，请联系项目组成员。

