--local function main()
--    local request_method = ngx.var.request_method
--    if request_method == nil then
--        request_method = ""
--    end
--    writerLog("nicai")
--    print(request_method)
--end;
--function writerLog(str)
--    local  time = os.time()
--    local  date = os.date("%Y%m%d",time)
--
--    local file = io.open("E:/ideaIU-2019.2.2.win/lua/src/main/resources/"..tostring(date).."_log.log","a")
--    --ngx.say("/var/www/lua/log/"..tostring(date).."_log.log")
--    --assert(file)
--    file:write(str.."\n")
--    file:close();
--
--end



-- lua 正则
-- Lua 中正则表达式语法上最大的区别，Lua 使用 ‘%’ 来进行转义，而其他语言的正则表达式使用 ‘\’ 符号来进行转义。
-- 其次，Lua 中并不使用 ‘?’ 来表示非贪婪匹配，而是定义了不同的字符来表示是否是贪婪匹配。定义如下：
-- 符号	匹配次数	匹配模式
-- +	匹配前一字符 1 次或多次	非贪婪
-- *	匹配前一字符 0 次或多次	贪婪
-- –	匹配前一字符 0 次或多次	非贪婪
-- ?	匹配前一字符 0 次或1次	仅用于此，不用于标识是否贪婪
-- .	任意字符
-- %a	字母
-- %c	控制字符
-- %d	数字
-- %l	小写字母
-- %p	标点字符
-- %s	空白符
-- %u	大写字母
-- %w	字母和数字
-- %x	十六进制数字
-- %z	代表 0 的字符
--function pattern()
--    local str = " 26% 20% 30%"
--    --local i,j = string.find(str, "%s%d+%%") -- string.find只能匹配一次，要想迭代的话需要手动改变i和j索引，这样太麻烦了，所以这里使用string.gmatch函数
--    --local ret = string.sub(str, i, j)
--    local num = 0
--    for w in string.gmatch(str,"%s%d+%%") do
--        step = string.sub(w, 1, string.len(w) - 1)
--        num = num + tonumber(step)
--    end
--    print(num/3)
--    --print(ret)
--end

-- 返回当前时间的距离1970.1.1.08:00时间的秒数
local time = os.time()
local date = os.date("%Y-%m-%d %H:%M:%S",time)

ngx.say(date)
-- 引入openresty提供的第三方mysql库
local mysql = require("resty.mysql")
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

local sql = "select * from yishen_blackIp where black_time > '".. date.."'"
res,err,errno,sqlstate = db:query(sql)
if not res then
    ngx.say("select error:", err,", errno：",errno,", sqlstate：",sqlstate)
end

for i, row in ipairs(res) do
    for name,value in pairs(row) do
        ngx.say("=======>name:"..name.."；value："..value)
    end
end



























