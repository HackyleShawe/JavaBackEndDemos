-- 固定窗口计数器限流（Lua脚本，保证原子性）
-- KEYS[1]：限流标识（如：/api/sms:13800138000）
-- ARGV[1]：限流阈值（如：3次/分钟）
-- ARGV[2]：窗口时间（如：60秒）
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])

-- 1. 获取当前计数器值，不存在则初始化为0
local count = redis.call('get', key)
if not count then
    count = 0
end

-- 2. 判断是否超出阈值
if tonumber(count) < limit then
    -- 未超出，计数器+1，设置过期时间（窗口时间）
    redis.call('incr', key)
    redis.call('expire', key, window)
    return 1-- 允许通过
end

return 0-- 拒绝通过
