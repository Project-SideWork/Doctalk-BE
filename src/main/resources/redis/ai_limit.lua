local current = redis.call("GET", KEYS[1])

if not current then
    redis.call("SETEX", KEYS[1], ARGV[2], "1")
    return 1
end

if tonumber(current) >= tonumber(ARGV[1]) then
    return -1
end

local newValue = redis.call("INCR", KEYS[1])
if redis.call("TTL", KEYS[1]) == -1 then
    redis.call("EXPIRE", KEYS[1], ARGV[2])
end
return newValue
