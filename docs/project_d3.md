# 项目交付物3: Health Track个人健康平台 - 数据库实现与应用开发

**组号:** 27  
**小组成员:** 肖轶伟 2025202110009  
蒋硕 2025202110005  
刘仲翔 2025202110004

## 一、阶段目标

本阶段目标是将前两阶段的概念设计和关系映射转化为可运行的数据库系统和Web应用程序，主要包括：

1. **数据库模式创建**: 执行SQL脚本创建所有表结构，包括主键、外键、约束和索引
2. **数据填充**: 为所有表插入足够的样本数据，确保数据间的引用完整性
3. **应用系统开发**: 开发完整的Java Web应用，实现所有必需的功能菜单
4. **问题解决**: 记录并解决实施过程中遇到的技术问题

## 二、数据库模式创建

### 2.1 表结构设计

根据Phase 2的关系映射，我们创建了13个表：

1. **USER**: 用户基本信息表
2. **EMAIL**: 用户邮箱表（多值属性）
3. **PHONE**: 用户手机号表（0..1关系）
4. **PROVIDER**: 医疗服务提供者表
5. **USER_PROVIDER**: 用户-提供者关联表（M:N关系）
6. **ACTION**: 动作父表（超类）
7. **APPOINTMENT**: 预约表（ACTION的子类）
8. **CHALLENGE**: 挑战表（ACTION的子类）
9. **CHALLENGE_PARTICIPANT**: 挑战参与者表（M:N关系）
10. **INVITATION**: 邀请表
11. **FAMILY_GROUP**: 家庭组表
12. **GROUP_MEMBER**: 组成员表（M:N关系）
13. **MONTHLY_SUMMARY**: 月度汇总表

### 2.2 关键约束实现

- **主键约束**: 所有表都有自增主键
- **外键约束**: 实现了所有关系的外键引用
- **唯一约束**: health_id, license_no, email, phone_number的唯一性
- **检查约束**: 验证状态一致性、日期范围、月份范围等
- **候选键**: health_id和license_no作为候选键

### 2.3 触发器实现

实现了以下业务规则触发器：

1. **预约取消验证**: 确保取消时间至少提前24小时
2. **邀请过期设置**: 自动设置15天过期时间
3. **家庭组最小成员数**: 确保每组至少2名活跃成员
4. **Action类型一致性**: 确保ACTION与子表类型匹配（Total Disjoint）
5. **邮箱/手机号规范化**: 自动转换为小写和去除格式字符

## 三、数据填充

### 3.1 样本数据规模

- **用户**: 10个用户，包含不同的账号状态
- **邮箱**: 12个邮箱记录，包含已验证和未验证状态
- **手机号**: 9个手机号记录（1个用户无手机号）
- **提供者**: 8个医疗服务提供者
- **用户-提供者关联**: 12条关联记录
- **预约**: 10个预约记录，包含已取消和已预约状态
- **挑战**: 6个挑战记录，包含不同状态
- **挑战参与者**: 15条参与者记录
- **邀请**: 8条邀请记录
- **家庭组**: 3个家庭组
- **组成员**: 8条成员记录
- **月度汇总**: 15条月度汇总记录

### 3.2 数据完整性

所有数据都遵循引用完整性约束，确保：
- 外键引用有效
- 数据间关系正确
- 业务规则得到遵守

## 四、应用系统架构

### 4.1 技术栈

- **后端**: Java Servlets + JSP
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **前端**: HTML5 + CSS3 + JavaScript (JSTL)

### 4.2 项目结构

```
src/main/
├── java/com/healthtrack/
│   ├── model/          # 实体类
│   ├── dao/           # 数据访问层
│   ├── servlet/       # 控制器层
│   └── util/          # 工具类
└── webapp/
    ├── WEB-INF/
    │   └── web.xml    # 配置文件
    ├── css/           # 样式文件
    ├── js/            # JavaScript文件
    └── jsp/           # JSP页面
```

### 4.3 主要功能实现

#### 4.3.1 主菜单功能

1. **Account Info**: 显示和编辑用户个人信息
2. **Book Appointment**: 预约医疗服务提供者
3. **Create Challenge**: 创建健康挑战
4. **Monthly Summary**: 查看月度健康汇总
5. **Search Records**: 搜索健康记录
6. **Sign Out**: 登出系统

#### 4.3.2 账户管理功能

- **修改个人信息**: 更新姓名和账号状态
- **邮箱管理**: 添加/删除邮箱地址
- **手机号管理**: 添加/删除手机号
- **提供者管理**: 添加/删除/设置主治医生

#### 4.3.3 汇总统计功能

- **日期范围内预约总数**: 统计指定日期范围内的预约数量
- **月度健康指标统计**: 计算平均/最小/最大步数
- **最受欢迎挑战**: 显示参与者最多的挑战
- **最活跃用户**: 显示健康记录最多或完成挑战最多的用户

## 五、遇到的问题与解决方案

### 问题1: Action继承一致性约束

**问题描述**: 需要确保ACTION表的action_type与子表（APPOINTMENT/CHALLENGE）一一对应，且互斥。

**解决方案**: 
- 在插入子表前先插入ACTION表，获取生成的action_id
- 使用触发器验证action_type一致性
- 在应用层使用事务确保原子性操作

**实现代码**:
```java
// 在AppointmentDAO和ChallengeDAO中使用事务
conn.setAutoCommit(false);
// 先插入ACTION
// 再插入子表
conn.commit();
```

### 问题2: 预约取消时间窗口验证

**问题描述**: 需要确保取消预约时至少提前24小时。

**解决方案**: 
- 在数据库触发器中使用BEFORE UPDATE触发器
- 检查cancel_time是否早于scheduled_at - 24小时
- 如果不符合条件，使用SIGNAL抛出错误

**实现代码**:
```sql
CREATE TRIGGER trg_appointment_cancel_validation
BEFORE UPDATE ON APPOINTMENT
FOR EACH ROW
BEGIN
    IF NEW.status = 'Cancelled' AND OLD.status != 'Cancelled' THEN
        IF NEW.cancel_time > DATE_SUB(NEW.scheduled_at, INTERVAL 24 HOUR) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Appointment can only be cancelled at least 24 hours before scheduled time';
        END IF;
    END IF;
END
```

### 问题3: 主治医生唯一性约束

**问题描述**: MySQL不支持部分唯一索引（WHERE条件），但需要确保每个用户最多只有一个is_primary=true的提供者。

**解决方案**: 
- 使用生成列（GENERATED COLUMN）存储条件值
- 在生成列上创建唯一索引
- 生成列只在is_primary=true时存储user_id，否则为NULL

**实现代码**:
```sql
ALTER TABLE USER_PROVIDER 
ADD COLUMN user_id_primary INT GENERATED ALWAYS AS 
    (IF(is_primary = TRUE, user_id, NULL)) STORED;

CREATE UNIQUE INDEX uk_primary_provider ON USER_PROVIDER(user_id_primary);
```

### 问题4: 邀请XOR约束

**问题描述**: INVITATION表的target_email和target_phone必须互斥（有且仅有一个非空）。

**解决方案**: 
- 使用CHECK约束实现XOR逻辑
- 在应用层也进行验证

**实现代码**:
```sql
CONSTRAINT chk_invitation_xor CHECK (
    (target_email IS NOT NULL AND target_phone IS NULL) OR 
    (target_email IS NULL AND target_phone IS NOT NULL)
)
```

### 问题5: 家庭组最小成员数约束

**问题描述**: 需要确保每个家庭组至少有2名活跃成员。

**解决方案**: 
- 使用AFTER DELETE和BEFORE UPDATE触发器
- 在删除或更新成员时检查活跃成员数
- 如果少于2人，阻止操作

**实现代码**:
```sql
CREATE TRIGGER trg_family_group_min_members
AFTER DELETE ON GROUP_MEMBER
FOR EACH ROW
BEGIN
    DECLARE active_count INT;
    SELECT COUNT(*) INTO active_count
    FROM GROUP_MEMBER
    WHERE group_id = OLD.group_id AND left_at IS NULL;
    
    IF active_count < 2 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Family group must have at least 2 active members';
    END IF;
END
```

## 六、对Phase 2的修订

### 6.1 数据库设计修订

1. **MONTHLY_SUMMARY表**: 添加了year和month的范围检查约束，确保数据有效性
2. **触发器增强**: 添加了更多业务规则验证触发器
3. **索引优化**: 为常用查询字段添加了索引以提高性能

### 6.2 应用设计补充

1. **会话管理**: 实现了基于HttpSession的用户认证
2. **错误处理**: 添加了统一的错误处理和用户友好的错误消息
3. **数据验证**: 实现了客户端和服务器端的双重验证

## 七、系统运行说明

### 7.1 数据库设置

1. 创建MySQL数据库
2. 执行`sql/create_tables.sql`创建表结构
3. 执行`sql/create_triggers.sql`创建触发器
4. 执行`sql/populate_data.sql`填充样本数据

### 7.2 应用部署

1. 配置`DBConnection.java`中的数据库连接信息
2. 使用Maven构建项目: `mvn clean package`
3. 部署WAR文件到Tomcat服务器
4. 访问应用: `http://localhost:8080/healthtrack`

### 7.3 测试账号

可以使用以下Health ID登录系统：
- HT001, HT002, HT003, HT004, HT005, HT006, HT007, HT008, HT009, HT010

## 八、总结

本阶段成功实现了Health Track个人健康平台的完整数据库系统和Web应用。系统包含了所有必需的功能，并正确处理了各种业务规则和约束。通过使用触发器、约束和事务管理，确保了数据的完整性和一致性。应用界面友好，功能完整，可以满足用户的基本需求。
