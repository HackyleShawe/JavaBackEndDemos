-- 漏桶算法限流（Lua脚本）

-- 数据结构：key为限流标识，Hash字段包括「剩余容量（remaining）、漏出速率（rate）、上次漏出时间（lastTime）

-- KEYS[1]：限流标识（如：/api/seckill/1001）
-- ARGV[1]：漏桶容量（最大可缓冲请求数）
-- ARGV[2]：漏出速率（次/秒）
-- ARGV[3]：当前时间戳（毫秒）
local key = KEYS[1]
local capacity = tonumber(ARGV[1]) -- 漏桶容量
local rate = tonumber(ARGV[2])     -- 漏出速率（次/秒）
local now = tonumber(ARGV[3])
local interval = 1000 / rate      -- 每次漏出的时间间隔（毫秒）

-- 1. 初始化漏桶信息（remaining：剩余容量，lastTime：上次漏出时间）
local bucket = redis.call('hmget', key, 'remaining', 'lastTime')
local remaining = tonumber(bucket[1]) or capacity -- 初始容量为桶的最大容量
local lastTime = tonumber(bucket[2]) or now

-- 2. 计算从上次漏出到现在，可漏出的请求数
local leakCount = math.floor((now - lastTime) / interval)
-- 3. 更新剩余容量（漏出请求，容量增加；最多不超过桶的最大容量）
remaining = math.min(capacity, remaining + leakCount)
-- 4. 更新上次漏出时间
lastTime = now + (leakCount * interval)

-- 5. 判断是否允许请求通过（有剩余容量则通过，容量-1）
if remaining > 0 then
    remaining = remaining - 1
    redis.call('hmset', key, 'remaining', remaining, 'lastTime', lastTime)
    -- 设置过期时间，避免key冗余
    redis.call('expire', key, math.ceil(capacity / rate) + 1)
    return 1-- 允许通过
end

return 0-- 拒绝通过
