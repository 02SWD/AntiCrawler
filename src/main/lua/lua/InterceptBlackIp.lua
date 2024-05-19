--- 该lua脚本用于布置在服务端，用于根据mysql数据库中的ip黑名单拦截爬虫ip
--获取客户端的ip
local remote_addr = ngx.var.remote_addr
if remote_addr == nil then
    remote_addr = ""
end

-- 引入openresty提供的第三方mysql库
local mysql = require("resty.mysql")
-- 创建实例
local db,err = mysql:new()
if not db then
    ngx.say("new mysql error:",err)
end

-- 设置超时时间
db:set_timeout(1000)
-- 设置mysql数据库连接信息
local props = {
    host = "127.0.0.1",
    port = "3306",
    database = "graduationproject",
    user = "root",
    password = "1454"
}

-- 获取连接
local res,err,errno,sqlstate = db:connect(props)
if not res then
    ngx.say("connect to mysql error：",err,", errno：",errno,", sqlstate：", sqlstate)
end

-- 返回当前时间的距离1970.1.1.08:00时间的秒数
local time = os.time()
-- 格式化时间
local date = os.date("%Y-%m-%d %H:%M:%S",time)
-- 查询仍处于封禁状态下的ip
local sql = "select * from yishen_blackIp where black_time > '".. date.."'"
res,err,errno,sqlstate = db:query(sql)
if not res then
    ngx.say("select error:", err,", errno：",errno,", sqlstate：",sqlstate)
end

-- 将客户端ip与查询出来的爬虫ip一一对比，若匹配成功，说明该ip为爬虫ip，直接返回403
for i, row in ipairs(res) do
    for name,value in pairs(row) do
        if value == remote_addr then
            return ngx.exit(ngx.HTTP_FORBIDDEN) -- 直接返回403
        end
    end
end
















