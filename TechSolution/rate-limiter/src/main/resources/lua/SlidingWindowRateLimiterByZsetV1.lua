-- 滑动窗口计数器限流（Lua脚本）

-- 实现思路：按请求时间依次暂存每次的请求。在判定是否限流时，直接计算当前时间到60s之前的所有请求量。
-- 数据结构：使用zset，score：时间戳，member：请求唯一标识

-- KEYS[1]：限流标识（如：/api/activity/list:192.168.1.1）
-- ARGV[1]：限流阈值（如：100次/分钟）
-- ARGV[2]：窗口时间（如：60000毫秒）
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local now = redis.call('time')[1] * 1000-- 当前时间戳（毫秒）

-- 1. 删除过期的请求（窗口之外的数据）
redis.call('zremrangebyscore', key, 0, now - window)

-- 2. 统计当前窗口内的请求数
local count = redis.call('zcard', key)

-- 3. 判断是否超出阈值
if count < limit then
    -- 未超出，新增当前请求（UUID作为唯一标识，避免重复计数）
    redis.call('zadd', key, now, tostring(redis.call('uuid')()))
    -- 设置过期时间，避免key冗余
    redis.call('expire', key, math.ceil(window / 1000) + 1)
    return 1-- 允许通过
end

return 0-- 拒绝通过
