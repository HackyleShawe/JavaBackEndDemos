
-- 与v1的区别：当前时间戳、请求唯一ID都通过入参传递，v1是自己生成的

-- KEYS[1] : 限流key
-- ARGV[1] : 当前时间戳（毫秒）
-- ARGV[2] : 窗口大小（毫秒）
-- ARGV[3] : 最大请求数
-- ARGV[4] : 请求唯一ID

local key = KEYS[1]

local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
local requestId = ARGV[4]

-- 1. 删除窗口外的数据
redis.call(
    'ZREMRANGEBYSCORE',
    key,
    0,
    now - window
)

-- 2. 获取当前窗口请求数
local count = redis.call('ZCARD', key)

-- 3. 判断是否超过限流
if count >= limit then
    return 0
end

-- 4. 写入当前请求
redis.call(
    'ZADD',
    key,
    now,
    requestId
)

-- 5. 设置过期时间
redis.call(
    'PEXPIRE',
    key,
    window
)

return 1

