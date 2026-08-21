-- 数据结构：key为限流标识，Hash字段包括：剩余令牌数、上一次放令牌时间

local key = KEYS[1]
local capacity = tonumber(ARGV[1]) -- 令牌桶容量（最大令牌数）
local rate = tonumber(ARGV[2]) -- 令牌生成速率（个/秒）
local now = tonumber(ARGV[3]) -- 当前时间戳（毫秒）
local interval = 1000 / rate -- 生成一个令牌的时间间隔（毫秒）

-- 初始化令牌桶：hmset key remaining 0 lastTime 0
local bucket = redis.call('hmget', key, 'remaining', 'lastTime')
local remaining = tonumber(bucket[1]) or 0
local lastTime = tonumber(bucket[2]) or 0

-- 计算从上次放令牌到现在，应该生成的令牌数
local generate = math.floor((now - lastTime) / interval)
-- 剩余令牌数 = 最小（令牌桶容量，当前剩余 + 生成的令牌数）
remaining = math.min(capacity, remaining + generate)
-- 更新最后放令牌时间
lastTime = now

-- 有令牌则取出1个
if remaining > 0 then
    remaining = remaining - 1
    redis.call('hmset', key, 'remaining', remaining, 'lastTime', lastTime)
    -- 设置过期时间（避免key冗余）
    redis.call('expire', key, math.ceil(capacity / rate) + 1)
    return 1-- 允许通过
end

return 0-- 拒绝通过
