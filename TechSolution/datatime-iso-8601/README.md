**前后端交互中时间的格式化与解析，将会面临哪些问题?**


在前后端交互中，我们常遇到涉及时间处理的一些问题，例如：
- 交互中的时间格式怎么约定？是字符串，还是时间戳
- 怎么解析URL或查询参数中的时间？
- 怎么解析请求体中的时间？
- 时间解析失败了，有什么异常处理机制？
- 响应给前端之前怎么格式化时间？
- 前后端传递的时间怎么表达时区？

本文将先介绍前后端交互中的时间规范应该怎么设计，再介绍如何通过代码实现。


# **规范约定**

我们先讨论一些有关时间的基础知识，以及约定一些规范。我们可能会遇到那些问题？

-   时间标准问题：使用GMT，或是Timestamp，还是UTC？
-   时间格式问题：使用ISO 8601，还是Unix Timestamp？
-   时区问题(Time Zone)：服务器和客户端时区不一致，导致用户看到的时间与实际时间不一致
-   时间精度问题：业务场景是否需要精确到毫秒？考虑适用Timestamp或者带毫秒的ISO 8601
-   跨语言/跨框架的兼容性问题

## **时间标准问题**

在现实世界的时间，主要有UTC、GMT时间标准。其中UTC ≈ GMT（实际技术上有细微差异），我们可以认为他们相等。

**UTC（Coordinated Universal Time，协调世界时）** 是目前全球统一的时间标准、世界统一时间、世界标准时间

-   根据原子钟来计算时间，更加精确，由于现在世界上最精确的原子钟50亿年才会误差1秒，可以说非常精确。
-   协调世界时区表示相对 UTC 的偏移量。例如，中国与标准的UTC时间前面的8小时，故UTC+8。
-   时区会使用“Z”来表示。又由于Z在无线电联络中使用“Zulu”作代称，协调世界时也会被称为＂Zulu time"。

**GMT（Greenwish Mean Time，格林威治标准时间）**

-   根据地球的自转和公转来计算时间，将位于伦敦郊区的皇家格林尼治天文台这个地方的时间设为标准参照时间，因为本初子午线被定义在通过那里的经线。
-   例如，当此地的时间为0时0分，则东八区的时间为8时0分。

**计算机时间标准：Unix 时间戳（Epoch Time）**

-   起点：1970-01-01 00:00:00 UTC

-   表示方式：从起点到现在的秒数/毫秒数

-   特性：

    -   基于 UTC（世界协调时间）
    -   不受时区影响
    -   所有系统统一基准时间
    -   前后端、跨国系统非常适合使用
    -   本质是一个整数：秒（10位）、毫秒（13位）、微秒（16位）

-   优势：

    -   存储简单（一个 long）
    -   排序方便（天然可比较大小）
    -   适合数据库索引
    -   跨系统、语言统一

**总结：现实世界的时间标准是UTC，** **计算机时间标准是Unix 时间戳（Epoch Time）。**


## **时间格式问题**

在我们日常开发中，前后端交互的时间可能会面临一些问题：

-   使用Date、LocalDateTime响应，前端显示的时间偏移了8小时
-   时间存库容易出错。前端传递浏览器本地的时间，后端直接落库，导致多个时区的时间混乱，无法比较。
-   前后端格式化时间时，容易出错。例如后端如果不小心使用YYYY，输出基于周（Week-Based-Year）的年份

**两类时间格式：** IS0 8601、Unix Timestamp

**Unix Timestamp**

-   秒级、毫秒级
-   优势：精度高、结构简单、适合传输
-   劣势：不包含时区信息、不易读、单位容易搞错
-   适用场景：对时间精度要求比较高

**IS0 8601**

-   是什么？由 International Organization for Standardization 制定的国际时间日期表示标准。

-   它的核心目标是？用统一、无歧义、可排序的方式表示时间。

-   它解决了哪些些问题？

    -   02/03/2026 是 2 月 3 日还是 3 月 2 日？
    -   时间有没有时区
    -   跨国系统怎么统一？

-   优势：明确包含时区信息；可读性好

-   适用场景：通用

**IS0 8601的两类格式：**

-   标准UTC：YYYY-MM-DDTHH:mm:ssZ

    -   Z表示0时区
    -   例如：你当地的时间为"2026-02-27T20:44:44+08:00"，使用这种格式表示为"2026-02-27T12:44:44Z"，把东八区的时间偏移量归零了。

-   带偏移量：YYYY-MM-DDTHH:mm:ss±HH:mm

    -   比UTC快的时区：2026-02-27T20:44:44+08:00，北京时间
    -   比UTC慢的时区：2026-02-27T20:44:44-05:00，美国东部时间

**总结：前后端交互中** **统一使用IS0 8601** **来约定时间格式。**



## **时区问题**

**忽略时区可能导致那些技术问题？**

-   时间语义不明确：无法判断时间是 UTC、服务器时间还是用户本地时间。
-   直接出现 8 小时偏差：前后端默认时区不同导致时间整体偏移。例如：后端在中国服务器（UTC+8）生成：2026-03-03T10:00:00。前端浏览器默认按 UTC 解析，显示变成：18:00:00。
-   不同浏览器解析结果不同：无时区时间在不同环境下可能按本地或 UTC 解析，Node.js版本不同结果不同。
-   时间存库容易出错。前端传递浏览器本地的时间，后端直接落库，导致多个时区的时间混乱，直接导致时间错误，影响后续业务逻辑。
-   国际化的项目多地区部署时全部错乱。例如，测试环境在中国（UTC+8），生产环境在新加坡（UTC+8），后来迁移到美国（UTC-5）。

**忽略时区可能导致的业务问题举例**

-   跨天统计类业务出错，某天的订单量、销售量、营收统计出错
-   会员到期时间提前或延后
-   促销活动、秒杀活动，在某些地区提前 8 小时开启，用户提前下单成功，库存被抢光，投诉大量产生
-   金融 / 支付系统的T+1 结算逻辑错误
-   时间混乱，时间看起来“倒流”

**为什么我们平常开发没有关注过时区？**

-   业务系统都是国内的（东八区），时间的产生和存储都默认是UTC+8
-   一旦业务系统需要提供国际化服务，时间立刻会错乱。想想一下，服务部署在UTC+8，UTC-5、UTC+9等时区的请求发来，不带时区，则服务器按照UTC+8的时间处理，是不是会错乱

**结论：前后端交互中，在国际业务的项目中一定要携带时区，使用IS0 8601时间标准。**



## **数据库存储问题**

还有一个最关键的问题，时间怎么存储呢？

想象一下这种场景：多个时区的请求传递进来，后端服务器应该怎么处理这些多个时区的数据呢？

解决办法其实很简单，就是后端统一，只存储UTC时间。响应给前端也可以只响应UTC时间，前端会根据UTC时间自动转换到本地时间。

不管怎样，核心要求就是一定要带时区，只要带了时区，就可以避免很多时间错乱问题。


说到这里，我们不禁回忆我们平常在写项目代码时，似乎从来没有关注过时区的问题，甚至不知道UTC，代码里几乎不会使用Instant，一招LocalDateTime吃遍天，为什么？——因为业务只在国内营运，不跨时区，我们默认就为UTC+8。另外提一下，中国的标准时间是北京时间（UTC+8），即时你在新疆最西部，你使用的时间依然是北京时间。

所以，如果你的业务系统是跨国（跨时区）的，一定要考虑时区的存储和读取时的转换。

以MySQL为例，对比TIMESTAMP和DATETIME的差异。

|           |                                    |                          |
| --------- | ---------------------------------- | ------------------------ |
| **对比维度**  | **TIMESTAMP**                      | **DATETIME**             |
| 存储本质      | Unix 时间戳（UTC）                      | 字符串形式的日期时间               |
| 是否受时区影响   | ✅ 会自动转换存入是会自动转为UTC标准时间不同时区查询，显示会不同 | ❌ 不做时区转换就是单纯的字符串         |
| 存储范围      | 1970-01-01 ~ 2038-01-19有2038年问题   | 1000-01-01 ~ 9999-12-31 |
| 是否适合跨时区系统 | ✅ 非常适合                             | ❌ 不推荐                    |
| 存储空间      | 4字节                                | 5~8字节                   |
| 默认值支持     | 默认支持 CURRENT_TIMESTAMP             | 5.6之后才支持                 |
| Java API  | Instant / OffsetDateTime           | LocalDateTime            |

**总结**

-   **跨国跨时区系统，用TIMESTAMP。注意，TIMESTAMP有2038年的问题，如果系统运行会超过此时间，考虑使用BIGINT存取UTC毫秒时间戳。**
-   单一时区系统，单纯展示时间，用DATETIME


# **代码实现**

看了上面的规范约定，相信你对时间的格式已经有了一定的认识。接下来我们开始写代码环节来实现我们上文中的约定。

**我们后端在开发中常会遇到这些问题**
-   怎么解析URL或查询参数(@RequestParam、@PathVariable)中的时间？
-   怎么解析表单中的时间？
-   怎么解析请求体中的时间？
-   时间解析失败了，有什么异常处理机制？
-   响应前怎么格式化时间？

**前端也** **面临着这些问题**
-   怎么解析后端响应的时间？
-   怎么格式化时间传递给后端？

## **Java对时间的处理**

Java有2套时间的解决方案API，分别是java.util包和java.time包。JDK8 引入了全新的时间 API（java.time 包），彻底解决了旧 API（Date、Calendar）混乱、线程不安全、时区难用的问题。

**java.util包的时间处理重要API**
-   Date：自 1970-01-01 00:00:00 UTC 起的毫秒数
-   Calendar：用于时间字段的计算（年、月、日、时等）
-   SimpleDateFormat：格式化和解析时间
-   currentTimeMillis()：获取系统毫秒数

**这些API都存在那些问题？**
-   线程不安全：例如：SimpleDateFormat
-   时区处理隐式：Date 内部存 UTC 毫秒，但 toString() 输出本地时区，跨服务器显示不同
-   时间计算繁琐：Date 不能直接加减天、月、年，必须用 Calendar，API 冗长且易错。月份从 0 开始，容易出现 off-by-one 错误。
-   时间计算有误差，本质是毫秒（Long型）运算，可能导致精度不够、Long型溢出

**java.time** **：日期时间基础包**
-   time.chrono：提供对不同的日历系统的访问
-   time.format：格式化和解析时间和日期
-   time.temporal：时间日期的调整
-   time.zone：包含时区支持的类

**关键常量类**
-   Instant：UTC时间
-   Clock：获取某个时区的瞬时
-   本地日期时间：LocalDate、LocalTime、LocalDateTime
-   时区
    -   ZoneId（时区ID）
    -   ZoneOffset（时区偏移量）
    -   OffsetTime（带偏移的时间，不含日期）
    -   OffsetDateTime（带偏移量的时间）
    -   ZonedDateTime（带时区的时间）
-   格式化：DateTimeFormatter

**总结：推荐使用JDK8的时间API，UTC时间用Instant，带偏移量的时间用OffsetDateTime。**

## **Web浏览器对时间的处理**

一般来说，Web浏览器对时间的处理有如下规则，

**时间解析**
-   带时区的（Z、±偏移量），转换为为本地时间显示
-   不带时区的，默认其为本地时间，不做转换，直接显示

**时间生成：** 默认生成本地时间，可转换为UTC时间

```javascript
// 解析：自动时区转换
// 给一个UTC时间，浏览器会自动转换为本地时间
new Date("2026-02-27T12:00:00Z") //输出：Fri Feb 27 2026 20:00:00 GMT+0800 (中国标准时间)
new Date("2026-02-27T12:00:00+07:00") //输出：Fri Feb 27 2026 13:00:00 GMT+0800 (中国标准时间)

// 生成
new Date() //默认生成本地时间
new Date().toISOString() //将本地时间输出ISO 8601时间格式
```

**浏览器Date对象原理**
-   自 1970-01-01T00:00:00Z 起的毫秒数（UTC），也就是 Unix 时间戳。
-   这和Java 的 Instant、MySQL 的 TIMESTAMP本质是一样的。


## **单时区本地系统**

**什么是单时区本地系统？**
-   系统只服务某个时区的用户，例如国内的几乎所有业务系统
-   服务器部署在中国
-   不涉及跨国业务
-   时区固定为：Asia/Shanghai
-   不考虑夏令时（中国目前无 DST）

**整体设计**
-   数据库用DateTime
-   后端用LocalDateTime
-   前后端交互用时间字符串，例如："2026-03-04 13:00:00"

**解析入参**
-   依赖框架：Spring MVC
-   功能：接收前端传的日期字符串（如2024-05-20），转成Date/LocalDateTime
-   怎么解析URL、查询参数(@RequestParam、@PathVariable)、表单中的时间？@DateTimeFormat
-   怎么解析请求体中的时间？@JsonFormat+@DateTimeFormat
-   时间解析失败了，有什么异常处理机制？MethodArgumentTypeMismatchException、HttpMessageNotReadableException

**格式化出参**
-   依赖框架：Jackson
-   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
-   功能：把后端的Date/LocalDateTime转成指定格式的字符串返回给前端

**为什么Date可以被解析？** Spring Boot 默认给 Date 配置了默认时间格式化器。

**为什么** **解析请求体中的** **LocalDateTime需要** **@JsonFormat** **+** **@DateTimeFormat** **？**
-  LocalDateTime 默认只支持 ISO-8601 格式（yyyy-MM-ddTHH:mm:ss）
-  而我们定义的是（yyyy-MM-dd HH:mm:ss），跟ISO-8601的格式差了一个T，所以报错了解析失败
-  在接收请求体中，我们先把此字段的时间格式化（所以@JsonFormat），再交给@DateTimeFormat解析


### **解析查询参数、表单时间**

**解析URL、查询参数(@RequestParam、@PathVariable)**

```java
@GetMapping("/parseDate")
public ApiResponse<String> parseDate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
    return ApiResponse.ok(date.toString());
}
@GetMapping("/parseLocalDate")
public ApiResponse<String> parseLocalDate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return ApiResponse.ok(date.toString());
}

@GetMapping("/parseDateTime")
public ApiResponse<String> parseDateTime(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
    return ApiResponse.ok(date.toString());
}
@GetMapping("/parseLocalDateTime")
public ApiResponse<String> parseLocalDateTime(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date) {
    return ApiResponse.ok(date.toString());
}
```

**解析表单中的时间**
```java
@Data
public class LocalDatetimeFormAddDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
}

@GetMapping("/parseDateByFrom")
public ApiResponse<LocalDatetimeVo> parseDateByFrom(LocalDatetimeFormAddDto dto) {
    LocalDatetimeVo vo = new LocalDatetimeVo();
    vo.setDateTime(dto.getDateTime());
    vo.setLocalDateTime(dto.getLocalDateTime());

    return ApiResponse.ok(vo);
}
```

**解析请求体中的时间**
```java
@Data
public class LocalDatetimeBodyAddDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    /**
     * 为什么要先@JsonFormat？
     * LocalDateTime 默认只支持 ISO-8601 格式（yyyy-MM-ddTHH:mm:ss）
     * 而我们定义的是（yyyy-MM-dd HH:mm:ss），跟ISO-8601的格式差了一个T，所以报错了解析失败
     * 在接收请求体中，我们先把此字段的时间格式化（所以@JsonFormat），再交给@DateTimeFormat解析
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
}

@PostMapping("/parseDateByBody")
public ApiResponse<LocalDatetimeVo> parseDateByBody(@RequestBody LocalDatetimeBodyAddDto dto) {
    LocalDatetimeVo vo = new LocalDatetimeVo();
    vo.setDateTime(dto.getDateTime());
    vo.setLocalDateTime(dto.getLocalDateTime());

    return ApiResponse.ok(vo);
}
```

### **异常处理**
解析URL、查询参数(@RequestParam、@PathVariable)、表单中的时间出现了错误，Spring 内部会抛出：
```java
MethodArgumentTypeMismatchException
  └── cause: DateTimeParseException
```

```java
/**
 * URL或查询参数(@RequestParam、@PathVariable)中的时间，解析失败抛出异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ApiResponse<?> handleTimeParseException(MethodArgumentTypeMismatchException ex) {
    log.error("出现MethodArgumentTypeMismatchException异常：", ex);
    return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "请求参数中时间格式错误");
}
```

解析请求体中的时间出现错误，Spring 会抛出：
```java
HttpMessageNotReadableException
  └── cause: DateTimeParseException
```

```java
/**
 * 请求体（如JSON）中的时间，解析失败时抛出异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(HttpMessageNotReadableException.class)
public ApiResponse<?> handleJsonParseException(HttpMessageNotReadableException ex) {
    log.error("出现HttpMessageNotReadableException异常：", ex);
    return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "请求体中的时间格式错误");
}
```

### 全局配置
```yml
# application.yml
spring:
  # 1. 全局配置入参日期格式（对应@DateTimeFormat）
  mvc:
    format:
      date: yyyy-MM-dd
      date-time: yyyy-MM-dd HH:mm:ss
    # 2. 全局配置出参日期格式（对应@JsonFormat）
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    # 适配LocalDateTime/LocalDate（Java8时间类型）
    deserialization:
      adjust-dates-to-context-time-zone: true
    serialization:
      write-dates-as-timestamps: false
```


## **跨时区系统**

**整体设计** **：** 传输必须带时区，存储、计算必须使用 UTC

-   数据库用Timestamp、BIGINT（时间戳的毫秒）
-   后端用Instant、OffsetDateTime
-   前后端交互用ISO 8601格式的时间字符串，例如："2026-03-04T05:00:00Z"、"2026-03-04T05:00:00+08:00"

**解析入参：**

-   确定入参格式：

    -   标准UTC：YYYY-MM-DDTHH:mm:ssZ
    -   带偏移量：YYYY-MM-DDTHH:mm:ss±HH:mm

-   依赖框架：Spring MVC

-   功能：接收前端传的IOS 8601格式的日期，转成OffsetDateTime、Instant

-   怎么解析URL、查询参数(@RequestParam、@PathVariable)、表单中的时间？@DateTimeFormat

-   怎么解析请求体中的时间？@DateTimeFormat

-   时间解析失败了，有什么异常处理机制？MethodArgumentTypeMismatchException、HttpMessageNotReadableException

**格式化出参**

-   依赖框架：Jackson
-   @JsonFormat，注意需要自定义pattern和timezone
-   功能：把后端的Date/LocalDateTime转成指定格式的字符串返回给前端

### **数据库设计**

**数据库用BIGINT存储UTC时间**

-   存入：now().toEpochMilli();

-   读取：ofEpochMilli(ts).atOffset(ZoneOffset.of("+08:00");

-   优势：

    -   绝对时间线：和时区完全解耦；数据库里永远是 UTC。
    -   排序性能极好，纯数字排序，比字符串更快
    -   跨语言兼容，所有语言都兼容时间戳
    -   不受数据库时区影响。MySQL 的 TIMESTAMP 会受 server time_zone 影响。
    -   不受服务器时区、JVM 默认时区、夏令时的影响

-   劣势：

    -   可读性差，需要转换：FROM_UNIXTIME(create_time/1000)
    -   不能直接用数据库时间函数，需要转换：DATE(FROM_UNIXTIME(create_time/1000))

```sql
DROP TABLE IF EXISTS datetime_demo;
CREATE TABLE datetime_demo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    time_stamp TIMESTAMP DEFAULT NULL COMMENT '时间戳、UTC',
    epoch_milli BIGINT DEFAULT NULL COMMENT 'UTC标准时间的毫秒数，可解决Timestamp的2038问题'
);
```

### **主要流程**

**第一步：前端发送**
```json
{
  "odt" : "2026-03-05T20:00:00+08:00",
  "instant" : "2026-03-05T10:00:00Z"
}
```

**第二步：后端用OffsetDateTime、Instant接收**

转换为Instant：offsetDateTime.toInstant();


**第三步：数据库存储**

Instant可直接存到Timestamp的字段中；

Instant转换为UTC毫秒存储：instant.toEpochMilli();

**第四步：查询返回**

Instant instant = Instant.ofEpochMilli(epochMilli);

OffsetDateTime odf = instant.atOffset(ZoneOffset.of("+08:00"))

后端返回：
```json
[{
  "odt": "2026-03-05T18:00:00+08:00",
  "instant": "2026-03-05T10:00:00Z"
},
{
  "odt": "2026-03-05T20:00:00+08:00",
  "instant": "2026-03-05T12:00:00Z"
}]
```

### **解析查询参数时间**

**解析URL、查询参数(@RequestParam、@PathVariable)**

```java
/**
 * 接收带偏移量的时间
 * DateTimeFormat必须指定iso = DateTimeFormat.ISO.DATE_TIME，表明接收的是ISO-8601格式的时间
 * 例如：2026-03-04T20:14:07+08:00
 * pattern表示不用ISO-8601格式的时间，而是自定义时间格式：yyyy-MM-dd HH:mm:ssXXX，XXX 表示时区偏移
 * 例如：2026-03-04 20:14:07+08:00
 * 注意：pattern 和 iso 不应该同时使用，如果指定了 pattern，iso 会被忽略。
 */
@GetMapping("/parseOffsetDateTime")
public ApiResponse<NationDatetimeVo> parseOffsetDateTime(@RequestParam("datetime")
                                               @DateTimeFormat( //pattern = "yyyy-MM-dd HH:mm:ssXXX",
                                                       iso = DateTimeFormat.ISO.DATE_TIME)
                                               OffsetDateTime date) {
    NationDatetimeVo vo = new NationDatetimeVo();
    vo.setOdt(date);
    vo.setInstant(date.toInstant());

    return ApiResponse.ok(vo);
}

/**
 * 接收UTC时间
 * DateTimeFormat必须指定iso = DateTimeFormat.ISO.DATE_TIME，表明接收的是ISO-8601格式的时间
 * 例如：2026-03-04T12:14:07Z
 * pattern表示不用ISO-8601格式的时间，而是自定义时间格式：yyyy-MM-dd HH:mm:ssZ，XXX 表示时区偏移
 * 例如：2026-03-04 12:14:07Z
 * 注意：pattern 和 iso 不应该同时使用，如果指定了 pattern，iso 会被忽略。
 */
@GetMapping("/parseInstant")
public ApiResponse<NationDatetimeVo> parseInstant(@RequestParam("datetime")
                                        @DateTimeFormat(//pattern = "yyyy-MM-dd HH:mm:ssZ", Instant 不建议配 pattern
                                                iso = DateTimeFormat.ISO.DATE_TIME)
                                        Instant date) {
    NationDatetimeVo vo = new NationDatetimeVo();
    vo.setOdt(date.atOffset(ZoneOffset.of("+08:00")));
    vo.setInstant(date);
    return ApiResponse.ok(vo);
}
```

### **解析表单时间**
```java
@Data
public class NationDatetimeFormAddDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime odt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant instant;
}

/**
 * 接收表单中带偏移量、UTC时间
 */
@GetMapping("/parseByFrom")
public ApiResponse<NationDatetimeVo> parseByFrom(NationDatetimeFormAddDto dto) {
    NationDatetimeVo vo = new NationDatetimeVo();
    vo.setOdt(dto.getOdt());
    vo.setInstant(dto.getInstant());

    return ApiResponse.ok(vo);
}
```

### **解析请求体中的时间**
```java
@Data
public class NationDatetimeBodyAddDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime odt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant instant;
}

/**
 * 接收请求体中带偏移量、UTC时间
 */
@PostMapping("/parseByBody")
public ApiResponse<List<NationDatetimeVo>> parseByBody(@RequestBody NationDatetimeBodyAddDto dto) {

    if(dto.getInstant() != null) {
        DatetimeDemo demo = new DatetimeDemo();
        demo.setTimeStamp(dto.getInstant());
        demo.setEpochMilli(dto.getInstant().toEpochMilli());
        datetimeDemoMapper.insert(demo);
    }

    if(dto.getOdt() != null) {
        DatetimeDemo demo = new DatetimeDemo();
        demo.setTimeStamp(dto.getOdt().toInstant());
        demo.setEpochMilli(dto.getOdt().toInstant().toEpochMilli());
        datetimeDemoMapper.insert(demo);
    }

    List<NationDatetimeVo> datetimeVos = new ArrayList<>();
    List<DatetimeDemo> datetimeDemos = datetimeDemoMapper.selectList(Wrappers.<DatetimeDemo>lambdaQuery());
    for (DatetimeDemo datetimeDemo : datetimeDemos) {
        Instant timeStamp = datetimeDemo.getTimeStamp();
        Long epochMilli = datetimeDemo.getEpochMilli();

        NationDatetimeVo vo = new NationDatetimeVo();
        vo.setOdt(timeStamp.atOffset(ZoneOffset.of("+08:00")));
        vo.setInstant(timeStamp);
        datetimeVos.add(vo);

        vo = new NationDatetimeVo();
        vo.setOdt(Instant.ofEpochMilli(epochMilli).atOffset(ZoneOffset.of("+08:00")));
        vo.setInstant(Instant.ofEpochMilli(epochMilli));
        datetimeVos.add(vo);
    }

    return ApiResponse.ok(datetimeVos);
}
```

### **全局配置**

单时区系统可以在 yml 中通过 spring.mvc.format 和 spring.jackson.time-zone 统一配置时间格式。

但在多时区系统中，不能通过固定时区配置来处理时间，否则会丢失客户端时区信息。

多时区系统应该使用 OffsetDateTime 或 Instant，传输 ISO-8601 格式，后端统一使用 UTC 处理和存储。

所以，不能在yml中统一配置跨时区系统的格式化和解析参数。


# **结尾**

好的，现在回答我们在开头提出的问题。

在规范约定中，

-   时间标准问题：Timestamp和UTC搭配使用
-   时间格式问题：交互可读用ISO 8601，保存传输用Unix Timestamp
-   时区问题(Time Zone)：跨时区的系统一定要考虑时区

在代码实现中，

-   怎么解析URL或查询参数(@RequestParam、@PathVariable)中的时间？@DateTimeFormat
-   怎么解析表单中的时间？实体类字段上加@DateTimeFormat
-   怎么解析请求体中的时间？实体类字段上加@JsonFormat、@DateTimeFormat
-   时间解析失败了，有什么异常处理机制？MethodArgumentTypeMismatchException、HttpMessageNotReadableException
-   响应前怎么格式化时间？@JsonFormat
-   前端直接使用后端格式化后的时间字符串，在向后端传递时需要先格式化为约定的时间字符串

本文结束。