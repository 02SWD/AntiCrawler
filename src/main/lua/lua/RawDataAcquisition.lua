--- 用户请求的原始数据，采集脚本

-- 开启服务器的保护机制（当前活跃用户连接数小于1000时采集数据，大于1000就不采集数据）
-- 最大的用户数阈值
local maxUserNumber = 1000
-- 获取当前活跃用户连接数
local activeUserNumber = ngx.var.connections_active
-- 当前活跃用户连接数小于1000时采集数据（开始）
if tonumber(activeUserNumber) < tonumber(maxUserNumber) then
    -- 定义数据库关闭方法
    local function close_db(db)
        if not db then
            return
        end
        db:close()
    end

    -- 引入openresty提供的第三方mysql库
    local mysql = require("resty.mysql")

    --- 采集原始数据(获取用于支撑爬虫识别的数据)
    -- 1. 获取客户端ip
    local remote_addr = ngx.var.remote_addr
    --ngx.say("remote_addr："..remote_addr.."\n")
    --ngx.say("remote_addr："..type(remote_addr).."\n")
    if remote_addr == nil then
        remote_addr = ""
    end
    -- 2. 获取请求方法
    local request_method = ngx.var.request_method
    if request_method == nil then
        request_method = ""
    end
    -- 3. 获取服务端ip
    local server_addr = ngx.var.server_addr
    if server_addr == nil then
        server_addr = ""
    end
    -- 4. 获取referer
    local http_referer = ngx.var.http_referer
    if http_referer == nil then
        http_referer = ""
    end
    -- 5. 获取UserAgent
    local http_userAgent = ngx.var.http_user_agent
    if http_userAgent == nil then
        http_userAgent = ""
    end
    -- 6. 获取访问时间：这里我为了简单起见，就不从服务器这里获取时间了（还要解决数据类型不匹配的问题），而是使用sql语句的now()函数在mysql中获取时间并插入
    --local time_local = ngx.var.time_local
    --if time_local == nil then
    --    time_local = ""
    --end
    -- 7. 获取访问时间（ISO格式）
    --local time_iso8601 = ngx.var.time_iso8601
    --if time_iso8601 == nil then
    --    time_iso8601 = ""
    --end
    -- 8. 请求中当前URI（不带请求参数，参数位于$args），可以不同于浏览器传递的 $request_uri 的值，它可以通过内部重定向
    local uri = ngx.var.uri
    if uri == nil then
        uri = ""
    end
    -- 9. 获取请求行(包含了get参数)
    local request = ngx.var.request
    if request == nil then
        request = ""
    end
    -- 10. 获取请求体
    ngx.req.read_body()
    local request_body = ngx.var.request_body
    if request_body == nil then
        request_body = ""
    end

    --- 将数据写进mysql数据库的 yishen_rawdata 表中
    -- 创建实例
    local db,err = mysql:new()
    if not db then
        ngx.say("new mysql error：",err)
    end
    -- 设置超时时间
    db:set_timeout(1000)
    -- 设置mysql数据库连接信息
    local props = {
        host = "192.168.31.108",
        port = "3306",
        database = "graduationproject",
        user = "root",
        password = "1454"
    }
    -- 获取连接
    local res,err,errno,sqlstate = db:connect(props)
    if not res then
        ngx.say("connect to mysql error：",err,", errno：", errno, ", sqlstate：", sqlstate)
    end
    -- 将原始数据存储mysql数据库中
    -- 1. 先将即将插入的数据转义一下，以防止sql注入
    local qu_remote_addr = ngx.quote_sql_str(remote_addr)
    local qu_request_method = ngx.quote_sql_str(request_method)
    local qu_server_addr = ngx.quote_sql_str(server_addr)
    local qu_http_referer = ngx.quote_sql_str(http_referer)
    local qu_http_userAgent = ngx.quote_sql_str(http_userAgent)
    local qu_uri = ngx.quote_sql_str(uri)
    local qu_request = ngx.quote_sql_str(request)
    local qu_request_body = ngx.quote_sql_str(request_body)
    -- 2. 编写sql语句
    local sql1 = "insert into yishen_rawdata(raw_remoteAddress,raw_requestMethod,raw_serverAddress,raw_referer,raw_userAgent,raw_uri,raw_request,raw_requestBody,raw_localTime) values "
    local sql2 = "("..qu_remote_addr..","..qu_request_method..","..qu_server_addr..","..qu_http_referer..","..qu_http_userAgent..","..qu_uri..","..qu_request..","..qu_request_body..",now())"
    local insert_sql = sql1..sql2
    -- 3. 执行插入操作
    res,err,errno,sqlstate = db:query(insert_sql)
    if not res then
        ngx.say("insert rows error：",err,", errno：",errno,", sqlstate：",sqlstate)
        return close_db(db)
    end
    -- 当前活跃用户连接数小于1000时采集数据（结束）
end




























