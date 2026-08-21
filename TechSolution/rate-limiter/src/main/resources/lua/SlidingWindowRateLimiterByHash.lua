-- 滑动窗口计数器限流（Lua脚本）

--主要思路：把大窗口切成多个小桶，Field：桶开始时间，Value：该桶请求次数。判定是否限流时，计算当前时间之前的60s内所有的桶中的计数。
--数据结构：Key：限流维度；Field：时间戳，Value：当前桶的请求数

-- KEYS[1] : 限流key
-- ARGV[1] : 当前时间戳（秒）
-- ARGV[2] : 窗口大小（秒）
-- ARGV[3] : 桶大小（秒）
-- ARGV[4] : 最大请求数

local key = KEYS[1]

local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local bucketSize = tonumber(ARGV[3])
local limit = tonumber(ARGV[4])

-- 找到当前时间属于哪个桶
local currentBucket = math.floor(now / bucketSize) * bucketSize

-- 最早有效时间
local minTime = now - window

-- 获取所有桶
local buckets = redis.call('HGETALL', key)

local total = 0

-- 当前时间 到 前 60s（窗口大小）的所有小桶的请求量，并且删除超过前60s的桶
for i = 1, #buckets, 2 do

    local bucketTime = tonumber(buckets[i])
    local count = tonumber(buckets[i + 1])

    -- 删除过期桶
    if bucketTime < minTime then
        redis.call(
            'HDEL',
            key,
            buckets[i]
        )
    else
        total = total + count
    end
end

-- 判断是否限流
if total >= limit then
    return 0
end

-- 当前桶 +1
redis.call(
    'HINCRBY',
    key,
    tostring(currentBucket),
    1
)

-- 设置过期时间
redis.call(
    'EXPIRE',
    key,
    window
)

return 1

