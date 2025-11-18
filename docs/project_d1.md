好的，这是 `project_d1_27.pdf` 文件的Markdown格式。

-----

# 项目交付物1: Health Track 个人健康平台- 概念设计

**组号:** 27
**小组成员:** 肖轶伟 2025202110009
蒋硕 2025202110005
刘仲翔 2025202110004

## 一、阶段目标

本阶段目标:对“Health Track个人健康平台”进行全面的需求分析,给出扩展E/R (EER) 概念模式,并在文档中明确关键实体、关系、属性类型、主键、结构性约束、附加数据约束、 建模假设以及设计中遇到的难点和权衡,主要包括以下几个方面:

1.  **需求要点梳理:** 分析项目描述中的功能需求,识别核心的实体、属性和关系。
2.  **设计细节与假设:** 根据需求分析,设计相应的实体及其属性;并依据一些关键假设, 使用传统和(min, max)两种表示法以确定结构性约束。
3.  **EER图:** 根据相关设计方案,描述 Health Track平台设计的EER图。
4.  **附加数据约束:** 明确阐述在设计过程中做出的、超出项目原始描述的假设。
5.  **难点与权衡:** 记录并讨论在此概念设计阶段遇到的挑战及解决方案。

## 二、需求要点

首先,我们对项目描述中的功能需求进行了分析,

1.  **平台定位:** 跟踪健康指标、管理医疗预约、发起/参与保健挑战等。
2.  **账号信息:** 用户注册需提供姓名、唯一Health ID、Email与手机号,每个账号仅允许一个手机号,但可关联多个邮箱;邮箱与手机号都可记录已验证/未验证状态(验证 过程不入模)。
3.  **医疗服务提供者(Provider):** 以唯一的行医执照号标识;用户可关联多名 Provider, 并需指定一名主治:Provider 可为已验证/未验证;可解除关联。
4.  **家庭组(Family Group):** 两个或以上用户组成,便于一方代管另一方的健康信息 (需权限控制)。
5.  **两类“动作(Action)”:** 预约(Appointment)与发起挑战(Challenge).
6.  **预约:** 按执照号或已验证邮箱定位 Provider,记录日期时间,就诊类型(面诊/远程)、 可选 Memo;每笔预约有唯一ID;可在开诊前24小时内取消,取消需记录原因。
7.  **挑战:** 按邮箱/手机号邀请他人,记录目标与起止日期;需记录参与者与其进度;每个挑战唯一ID.
8.  **邀请(Invitation):** 若发送到未注册或未验证的联系方式,15天内注册/验证即接 受,否则过期;需记录发起时间与完成(或过期)时间。
9.  **健康数据组织:** 按月度汇总与查询(如按月步数、按日预约),搜索逻辑由应用实 现。

## 三、Health Track 平台扩展实体-关系图(EER Diagram)

以下是为 Health Track 平台设计的EER图:

## 四、设计细节与假设

### 1\. 实体与属性

**(1) USER**

1.  user\_id【PK】
2.  health\_id【候选键】:简单、单值;唯一(候选键)。
3.  full\_name
4.  account status
5.  created\_at

**(2) EMAIL:** 多值(对用户而言);带状态。

1.  email【PK】
2.  is\_verified
3.  verified\_at
4.  user\_id【FK→USER】

**(3) PHONE:** 对用户至多一个;带状态与时间戳。

1.  phone\_number【PK】
2.  is\_verified
3.  verified\_at
4.  user\_id【FK→USER】

**(4) PROVIDER**

1.  provider\_id【PK】
2.  license\_no【候选键】
3.  provider\_name
4.  is\_verified
5.  verified\_at

**(5) USER\_PROVIDER:** 连接/解绑与主治标记。

1.  user\_provider\_id【PK】
2.  user\_id【FK】
3.  provider\_id【FK】
4.  is\_primary
5.  link\_status
6.  linked\_at
7.  unlinked\_at

**(6) ACTION:** 专化父类。

1.  action\_id【PK】
2.  action\_type {Appointment, Challenge}
3.  created\_by【FK→USER】
4.  created\_at

**(7) APPOINTMENT**

1.  action\_id【PK/且FK→ACTION】
2.  provider\_id【FK】
3.  scheduled\_at
4.  consultation\_type {InPerson|Virtual}
5.  memo
6.  status {Scheduled, Cancelled}
7.  cancel\_reason
8.  cancel\_time

**(8) CHALLENGE**

1.  action\_id【PK/ FK→ACTION】
2.  goal
3.  start\_date
4.  end\_date
5.  status {Draft|Active|Completed, Cancelled, Expired}

**(9) CHALLENGE\_PARTICIPANT**

1.  challenge\_participant\_id【PK】
2.  action\_id【FK→CHALLENGE】
3.  user\_id【FK→USER】
4.  progress\_value
5.  progress\_unit
6.  updated\_at
7.  participant\_status {Invited|Joined|Declined|Removed}

**(10) INVITATION**

1.  invitation\_id【PK】
2.  invitation\_type {Challenge|DataShare}
3.  target\_email
4.  target\_phone
5.  initiated\_at
6.  expires\_at
7.  completed\_at
8.  status {Pending|Accepted|Expired, Cancelled}
9.  to\_new\_user
10. initiated\_by【FK→USER】
11. action\_id【可空 FK→CHALLENGE】
12. XOR: target\_email, target\_phone.

**(11) FAMILY\_GROUP**

1.  group\_id【PK】
2.  group\_name
3.  target\_phone.
4.  created\_by【FK→USER】
5.  created\_at

**(12) GROUP\_MEMBER**

1.  group\_member\_id【PK】
2.  group\_id【FK】
3.  user\_id【FK】
4.  role {Admin, Caregiver|Member}
5.  joined\_at
6.  left\_at

**(13) MONTHLY\_SUMMARY**

1.  monthly\_summary\_id【PK】
2.  user\_id【FK→USER】
3.  year
4.  month
5.  total\_steps【可派生/可存储】
6.  total\_appointments【可派生】
7.  last\_updated
8.  is\_finalized
    (派生属性示例: total\_appointments 可由 APPOINTMENT 按月聚合得出;若考虑性能可 物化并以触发器维护。)

### 2\. 结构约束

| 关系 | 传统表示 | (min,max)表示 |
| :--- | :--- | :--- |
| **USER-EMAIL (has)** | 一(用户)对多(邮箱) 邮箱属唯一用户 | USER(0,N) <br> EMAIL(1,1) |
| **USER-PHONE (has)** | (用户)对零或一(手机号): 手机号属唯一用户 | USER(0.1) <br> PHONE (1.1) |
| **USER-USER\_PROVIDER-PROVIDER** | 多对多。经连接实体 | USER\_PROVIDER (1,1) <br> USER(0,N) <br> PROVIDER(0,N) <br> USER\_PROVIDER (1.1) |
| **USER-ACTION (created\_by)** | 一用户可创建多 Action: Action 必有且仅有一创建者 | USER(0,N) <br> ACTION (1,1) |
| **ACTION (APPOINTMENT, CHALLENGE)** | 总覆盖、互斥专化 | Total, Disjoint |
| **APPOINTMENT-USER (books)** | 用户可有多预约: 每预约一用户 | USER(0,N) <br> APPOINTMENT (1.1) |
| **APPOINTMENT-PROVIDER (attends)** | Provider 可有多预约 每预约-Provider | APPOINTMENT(1,1) <br> PROVIDER(0,N) |
| **CHALLENGE-CHALLENGE\_PARTICIPANT-USER** | 多对多、参与者至少1人 | CHALLENGE (1,N) <br> PARTICIPANT (1,1) <br> USER(0 N) <br> PARTICIPANT (1.1) |
| **CHALLENGE-INVITATION** | 一挑战对多邀请: 每邀请对应—挑战 | CHALLENGE(0,N) <br> INVITATION (1,1) |
| **USER-INVITATION (initiates)** | 一用户可发多邀请: 每邀请一发起者 | USER(0,N) <br> INVITATION (1,1) |
| **FAMILY GROUP-GROUP\_MEMBER-USER** | 组与用户多对多; 每组至少2名 | FAMILY GROUP(2.N) <br> GROUP\_MEMBER(1,1); <br> USER(0,N) <br> GROUP MEMBER(1.1) |
| **USER-MONTHLY\_SUMMARY (aggregates)** | 一用户对多月汇总: 每月汇总对应唯一用户 | (,) <br> MONTHLY SUMMARY(1,1) <br> USER(0,N) |

### 3\. 关键假设

以下假设用于给出明确的(min, max)取值与约束实现方式,保证概念模型的落地。

1.  预约仅在平台已存在的 Provider 上完成(即预约必须落到某个 Provider 实体上), 用执照号或已验证邮箱只是定位该Provider 的不同方式。
2.  用户手机号可不填,但若存在仅允许且必然唯一;因需要验证元数据,手机号与邮 箱各自单独建实体(非简单属性)。
3.  家庭组允许任意多用户,但组内成员数至少为2;权限以组成员上的“角色 (Admin/Caregiver/Member)”表示。
4.  “Action”是完全覆盖且互斥的专化:要么是Appointment,要么是 Challenge (Total Disjoint).
5.  邀请对象使用异或约束:`target_email`、`target_phone`必有其一(不可同时为空或同时非空);过期时间 发起时间+15天(可由触发器/应用层计算存储)。
6.  挑战参与者至少1名(不含或包含发起者均可,本设计允许发起者同时作为参与者)。
7.  用户-Provider 关联表上通过部分唯一约束保证“每个用户最多一个 is\_primary=true的活动关联”.

## 五、附加数据约束

1.  **唯一性与候选键**
    (1) USER.health\_id 唯一(候选键);
    (2) PROVIDER.license\_no 唯一;
    (3) EMAIL.email唯一;
    (4) PHONE.phone\_number 唯一,
2.  **主治医生唯一**
    (1) 在USER\_PROVIDER 上添加部分唯一索引;
    (2) UNIQUE(user\_id) WHERE is\_primary = true.
3.  **手机号/邮箱验证**
    (1) verified\_at 非空 → is\_verified=true (检查约束).
4.  **邀请 XOR与有效期**
    (1) CHECK((target\_email IS NULL) \<\> (target\_phone IS NULL));
    (2) expires\_at = initiated\_at + INTERVAL '15 DAY' (由触发器或应用层保证).
5.  **预约可取消时间窗口**
    (1) 取消时 status='Cancelled' → cancel\_time \<= scheduled\_at - INTERVAL '24 HOUR',并 cancel\_reason 非空,
6.  **家庭组规模**
    (1) GROUP\_MEMBER上用聚合检查/触发器确保每个 group\_id 的有效成员数 ≥ 2.
7.  **挑战参与者**
    (1)每个挑战在CHALLENGE\_PARTICIPANT 中至少1行 participant\_status IN ('Joined', 'Invited').
8.  **专化约束**
    (1) ACTION.action\_type与子表————对应(Total, Disjoint):
    (2) action\_type='Appointment' → 在APPOINTMENT 有同 action\_id;
    (3) action\_type='Challenge' → 在CHALLENGE 有同 action\_id.

## 六、遇到的困难与解决方案

**挑战1:联系方式建模(Email/Phone)**

  * **问题:** 既要支持“多邮箱+单手机号”,又要记录验证状态与时间截,还得保证全局 唯一与规范化(大小写、连字符等)。
  * **困难:** 全库唯一+规范化(lower/去噪)与业务键重复检测;MySQL 正则处理与 历史数据导入。
  * **解决方案:** 独立实体 EMAIL / PHONE: EMAIL.email 全库唯一,PHONE 以 UNIQUE(user\_id)保证 0..1;用触发器做大小写归一与号码清洗;CHECK约束 verified\_at → is\_verified.
  * **方案描述:** 将联系方式抽离为实体,能完整承载“验证状态/时间戳/全局唯一”等 语义,避免在USER 表堆叠可空列与稀疏字段,归一化触发器让导入数据“先净化、再落 库”,减少后续重复与格式问题

**挑战2:主治医生唯一**

  * **问题:** 同一用户在任一时刻仅能有一个主治;还要保留历史关联。
  * **困难:** MySQL 没有“部分唯一”;需要在 is\_primary=1 时才唯一。
  * **解决方案:** 连接实体 USER\_PROVIDER存历史;生成列 user\_id\_primary=CASE WHEN is\_primary THEN user\_id END + UNIQUE(user\_id\_primary)实现“每用户最多一个 主治”。
  * **方案描述:** 把“当前主治”与“历史关联”合并到一张关系表,用条件唯一规避 MySQL 不支持部分唯一的限制;既能保留时态,又让唯一性在数据库层硬性保障

**挑战3:“Action”继承一致性(CTI)**

  * **问题:** 父表 ACTION 与子表类型必须——————对应,且互斥。
  * **困难:** 防止把 action\_id 既插 APPOINTMENT 又插 CHALLENGE.
  * **解决方案:** 父表 ACTION (action\_type) +子表 APPOINTMENT/CHALLENGE(主 键即外键);触发器强制 action\_type一致,必要时再加“互斥”校验。
  * **方案描述:** 采用类表继承(CTI),把通用审计属性放父表、差异放子表。触发器 把EER的Total + Disjoint 语义落地:每条 ACTION必须、且只能属于一个子类,杜绝 “悬空父类”或“多重归属”。