local current = redis.call("GET", KEYS[1])

if not current then
    redis.call("SET", KEYS[1], "1", "EX", ARGV[2])
    return 1
end

if tonumber(current) >= tonumber(ARGV[1]) then
    return -1
end

redis.call("SET", KEYS[1], tostring(tonumber(current) + 1))
return tonumber(current) + 1
