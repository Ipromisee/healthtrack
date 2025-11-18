项目交付物2: Health Track个人健康平台- Relational Mapping
组号: 27 小组成员: 肖轶伟 2025202110009 蒋硕 2025202110005 刘仲翔 2025202110004

一、阶段目标
将第1阶段的扩展 EER 模型规范地映射为关系模式。本阶段重点包括:严格执行每个映射步骤、明确记录主外键、候选键及业务约束、说明对阶段1概念模型的修订,以及本阶段遇到的问题与解决方案。

二、映射算法步骤
Step 1: 每个强实体映射为单独表,所有简单属性落表。HealthTrack中, USER、PROVIDER、FAMILY_GROUP、ACTION、INVITATION、MONTHLY_SUMMARY 均按此建立。

Step 2: 弱实体映射为含宿主主键的表,本系统无弱实体。

Step 3: 1:1关系映射,在任一侧添加外键或合并,本系统的USER与PHONE为(0,1)关系,在PHONE 端加 FK并UNIQUE(user_id).

Step 4: 1:N关系映射,在N端加FK,例如ACTION.created_by→ USER, MONTHLY_SUMMARY.user_id USER, APPOINTMENT.provider_id→ PROVIDER.

Step 5: M:N关系映射,创建中间表,如USER_PROVIDER、CHALLENGE_PARTICIPANT、GROUP_MEMBER.

Step 6: 多值属性映射为新表。EMAIL(email, user_id)表:PHONE(phone_number, user_id)表。

Step 7: n>2 关系映射为独立表,主键为全部FK组合,本系统无n元关系。

Step 8: 超类/子类映射采用Option A,即父表+子表,子表主键=父表主键且为外键。ACTION 为父类,APPOINTMENT 和 CHALLENGE 为子类。

三、EER Relational 映射结果(关系模式描述)
根据上述算法,HealthTrack的EER模型被映射为以下关系结构: USER、PROVIDER、EMAIL、PHONE、USER_PROVIDER、ACTION、APPOINTMENT、CHALLENGE、CHALLENGE_PARTICIPANT、INVITATION、FAMILY_GROUP、GROUP_MEMBER、MONTHLY SUMMARY,其中,USER与PROVIDER为核心实体;EMAIL与PHONE 处理多值属性:USER_PROVIDER、CHALLENGE_PARTICIPANT、GROUP_MEMBER 用于处理多对多关系;ACTION是父类表,APPOINTMENT 和 CHALLENGE 是子类表,采用类表继承(Class Table Inheritance).

主要约束包括: USER.health_id、PROVIDER.license_no、EMAIL.email、PHONE.phone_number的唯一性;USER_PROVIDER 保证每用户仅有一个主治医生;APPOINTMENT 取消时需在scheduled_at 前24小时;INVITATION 表的 target_email与target_phone 互斥;FAMILY_GROUP 成员数至少2;MONTHLY_SUMMARY中user_id、year、month 组合唯一。

USER (user_id PK, health_id UNIQUE,……); 候选键: health_id

PROVIDER (provider_id PK, license_no UNIQUE,……); 候选键: license_no

EMAIL (email PK, ..., user_id FK→USER); 候选键: email

PHONE (phone_number PK, ..., user_id FK→USER, UNIQUE(user_id)); 候选键: phone_number

USER_PROVIDER (user_provider_id PK, user_id FK, provider_id FK, is_primary, ..., UNIQUE(user_id, provider_id), 并以“生成列+唯一索引”约束“每用户最多一个主治”)

ACTION (action_id PK, action_type, created_by FK→USER, created_at)

APPOINTMENT (action_id PK FK→ACTION, provider_id FK→PROVIDER, scheduled_at, consultation_type, status, ...)

CHALLENGE (action_id PK FK→ACTION, goal, start_date, end_date, status)

CHALLENGE_PARTICIPANT (challenge_participant_id PK, action_id FK→CHALLENGE, user_id FK→USER, ..., UNIQUE(action_id, user_id))

INVITATION (invitation_id PK, invitation_type, target_email, target_phone, initiated_by FK→USER, action_id FK→CHALLENGE, XOR约束)

FAMILY_GROUP (group_id PK, ..., created_by FK→USER)

GROUP_MEMBER (group_member_id PK, group_id FK→FAMILY_GROUP, user_id FK→USER, role, ..., UNIQUE(group_id, user_id))

MONTHLY_SUMMARY (monthly_summary_id PK, user_id FK→USER, year, month, total_steps, total_appointments, ..., UNIQUE(user_id, year, month))

四、主要约束与触发器
预约取消触发器: 取消时cancel_reason 不能为空且cancel_time早于scheduled_at 24小时。

邀请触发器: 若expires_at为空,则默认设置为 initiated_at+15天。

家庭组成员触发器: 删除或更新成员时确保活跃成员数不少于2。

主治医生唯一约束: 通过虚拟列 user_id_primary + 唯一索引实现。

五、遇到的困难与解决方案
条件唯一(Primary Provider) 问题: MySQL 不支持部分唯一索引,通过虚拟列+唯一索引或触发器实现。

联系方式建模问题: 同时支持多邮箱和单手机号,EMAIL独立表、PHONE表中加 UNIQUE(user_id).

专化一致性问题: 采用Option A并辅以触发器确保 ACTION与子类——对应。

取消窗口限制问题: 利用BEFORE UPDATE 触发器+SIGNAL 报错机制实现。

家庭组人数约束问题: 利用DELETE/UPDATE 触发器动态计算活跃成员数量,确保不少于2。