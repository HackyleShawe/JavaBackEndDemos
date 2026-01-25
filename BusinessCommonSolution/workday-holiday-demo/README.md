# 需求分析

**需求分析**

- 休息日
  - 判定某天是否为周末、节假日
  - 计算某天的 N 个休息日前、后的日期。例如：3个休息日后。应用场景不是很多
  - 计算某天开始，到下N个休息日的日期。例如：下一个休息日
  - 计算两个日期之间的休息日的天数
- 工作日
  - 判定某天是否为法定工作日
  - 计算某天的 N 个工作日前、后的日期。例如：5个工作日后
  - 计算某天开始，到下N个工作日的日期。例如：下一个工作日
  - 计算两个日期之间的工作日的天数
- 批量判定
- 管理端
  - 新增 / 修改节假日
  - 批量导入节假日（每年一次）



# 技术方案

**技术方案**

- 纯代码方案：不可行。因为每年的节假日安排都不一样，受国务院管理（中国大陆，不含港澳台）
- 使用表记录
  - 只记录休息日（周六周日、节假日）
    - 需要注意的是，不一定周六周日一定是休息日
    - 判定时如果在表中查到了数据，就是休息日，否则是工作日。
  - 记录所有日期
    - 通过type区分（工作日、周末、法定节假日、调休工作日）。
    - 如果是调休工作日，可以记录该个工作日调休的是哪一天



**怎么更新休息日信息：**

- 中国大陆（不含港澳台）每年的节假日信息，由国务院在上一年的12月前更新公示
- 手工录入节假日信息
- 定时任务监控gov.cn公告页面变化，自动解析节假日信息

## 只记录休息日

**主要思路：**

*   用一张表专门记录每年的休息日
*   需要注意的是，不一定周六周日一定是休息日
*   判定时如果在表中查到了数据，就是休息日，否则是工作日
*   数据几乎不会变化，在项目初始化时从数据库读取后放入缓存

**表定义：**前缀\_holiday。工作日节假日判定一般为系统级的功能，所以表命名为：sys\_holiday

```sql
CREATE TABLE sys_holiday
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    year     INT         DEFAULT 0 COMMENT '年份，便于快速获取某年的所有节假日',
    holiday     DATE         DEFAULT NULL COMMENT '节假日期',
    description VARCHAR(512) DEFAULT '' COMMENT '节假日说明，例如：周六、周日、春节、国庆节',
    region      VARCHAR(16)  DEFAULT '' COMMENT '国家区域代码，例如CN、TW、HK',

    deleted     BIT          DEFAULT b'0' COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT       DEFAULT 0,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by   BIGINT       DEFAULT 0,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_holiday (holiday)
) COMMENT '休息日信息表';
```

**数据初始化时机？**

- 在节假日安排出来的时候（当年的国庆节后），由人工、辅助工具、API导入
- 如果是人工导入，可以考虑在界面上提供一个日期选择器，按月为一个基本单位，选择出休息日，提交到后端保存；如果没有该月份的数据提交，则默认该月按照标准的周六周日休息来初始化

 

**周六周日为什么也需要保存到表中？**

- 虽然周六周日可以通过Java提供的API实现（LocalDate#getDayOfWeek）
- 但是涉及一个小长假的附近，可能出现周六周日也需要工作的场景，所以必须记录周六周日是休息日的情况

 

**判定是否为休息日（周六周日、节假日）？**

- 日期在sys_holiday中是否存在？存在则立即返回即为是节假日（休息日）
- 查库没有，一定不是节假日？是的，现阶段设计只能如此
- 查往年的日期，没找到，无法判定不是节假日？再按照年份去查库，还是没有则抛出异常：节假日未初始化，请先初始化xx年的节假日信息
- 查未来的日期，没找到，无法判定不是节假日？再按照年份去查库，还是没有则抛出异常：节假日未初始化，请先初始化xx年的节假日信息

 

**如何判定指定日期是否为工作日？**该日期不在节假日表中，则为工作日

 

**缓存设计**

- 数据结构：hash
- Key：sys_holiday:国家区域代码:今年
- HKey: yyyy-MM-dd (String)，使用时需要判断给定日期是否在map中，如果在就是休息日，如果不在就不是
- HVal: HolidayCacheDto (JSON)
- QA
  - 缓存三年？虽然key是今年，但是缓存3年的，防止在今年的年初年尾判断时查库，提升命中率
  - 年份交界处，怎么删除旧的和初始化新的？
  - 旧的过期自动删除，新的根据key自动查库放入缓存
  - 会有短暂这种情况：今年的key中，含有明年的的节假日。但随着过期自动删除，到达明年自动恢复
- 注意：不要使用LocalDate作为Key，因为：Redis序列化时不支持LocalDate，需要额外的配置，可能与其他的序列化配置起冲突

 

**方案评价**

- 优势
  - 只记录休息日，数据体量稍微少一些
  - 初始化数据时比较麻烦
- 劣势
  - 无法表达调休工作日的信息
  - 查库没有，无法判定不是节假日，因为也有可能是没有初始化该年的节假日信息
- 结论：存在较大的设计缺陷，实现复杂度麻烦，不建议使用



## **记录所有日期**

**主要思路**

- 用一张表记录所有日期
- 通过type区分（工作日、周末、法定节假日、调休工作日）。
- 判定时根据在表中查到的数据，进行判断，以及获取具体信息
- 如果是调休工作日，可以记录该个工作日调休的是哪一天
- 数据几乎不会变化，在项目初始化时从数据库读取后放入缓存

 

**表定义：**前缀_calendar。工作日节假日判定一般为系统级的功能，所以表命名为：sys_calendar

```sql
CREATE TABLE sys_calendar
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    year     INT    DEFAULT 0 COMMENT '年份，便于快速获取某年的所有节假日',
    calendar_date     DATE   DEFAULT NULL COMMENT '日期',
    workday    BIT    DEFAULT b'1' COMMENT '0-False-非工作日, 1-True-是工作日',
    type   VARCHAR(20) DEFAULT '' COMMENT '类型：HOLIDAY / WEEKEND / WORKDAY',
    description VARCHAR(512) DEFAULT '' COMMENT '节假日说明，例如：周六、周日、春节、国庆节',
    region      VARCHAR(16)  DEFAULT '' COMMENT '国家区域代码，例如CN、TW、HK',

    deleted     BIT          DEFAULT b'0' COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT       DEFAULT 0,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by   BIGINT       DEFAULT 0,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_calendar_date (calendar_date)
) COMMENT '日历信息表';
```

**判定是否为节假日****？**日期在sys_calendar中是否存在？并且根据workday字段协同判断

 

**数据****初始化时机****？**

- 在节假日安排出来的时候（当年的国庆节后），由人工、辅助工具、API导入
- 如果是人工导入，可以考虑在界面上提供一个日期选择器，按月为一个基本单位，选择出休息日，提交到后端保存；如果没有该月份的数据提交，则默认该月按照标准的周六周日休息来初始化

 

**如何判定指定日期是否为工作日？**该日期是否在日历表中，以及结合是否工作日（workday）判断

 

**判定是否为****休息日（周六周日、****节假日****）****？**

- 该日期是否在日历表中，以及结合是否工作日（workday）判断
- 查库没有，怎么判断是否为休息日？不能判断，直接抛出异常：日历未初始化，请先初始化xx年的节假日信息
- 查往年的日期，没找到，怎么判断？不能判断，直接抛出异常：日历未初始化，请先初始化xx年的节假日信息
- 查未来的日期，没找到，怎么判断？不能判断，直接抛出异常：日历未初始化，请先初始化xx年的节假日信息

**缓存设计**

- 数据结构：hash
- Key：sys_calendar:国家区域代码:年份
- HKey: yyyy-MM-dd (String)
- HVal: HolidayCacheDto (JSON)
- 注意：不要使用LocalDate作为Key，因为：Redis序列化时不支持LocalDate，需要额外的配置，可能与其他的序列化配置起冲突

**方案评价**

- 优势
  - 记录全量日期，方便直接判断
  - 可以表达调休工作日的信息
- 结论：推荐

## 第三方开源工具

https://github.com/NateScarlet/holiday-cn

主要思路

- 把所有的节假日都记录在一个json文件中，后续使用时解析
- 每年发布新的更新日期，需要手动升级依赖版本
- 自定义表并维护节假日更好

```xml
<dependency>
    <groupId>com.github.stuxuhai</groupId>
    <artifactId>cn-holiday</artifactId>
    <version>1.4.0</version>
</dependency>
```

