--- 服务器状态采集
--- 运行该脚本需要linux存在命令：top，df，iostat，hostname

local function linuxGain()

    -- 用于存储cpu和内存使用情况
    local system_info
    -- 用户存储disk的使用情况
    local disk_info
	-- 用于存储diskIo情况
	local diskIo_info
	-- 用于存储服务器的主机名
	local hostname

    -- Lua 调用 Shell
    local function execute_cmd(cmd)
        local t = io.popen(cmd)
        local ret = t:read("*all")
        return ret
    end


    -- 匹配指定模式的字符串
    local function string_match(str, patten)
        local i, j = string.find(str, patten)
        local ret = string.sub(str, i, j)
        return ret
    end


    -- 匹配指定模式字符串中的数字
    local function string_match_num(str, patten)
        local ret = string_match(str,patten)
        local i, j = string.find(ret, "[0-9]+%.*[0-9]*")
        local num = string.sub(ret, i, j)
        return num
    end


    -- 获取系统状况原始数据
    local function get_system_info()
        -- 1. 执行 top并过滤第一次无用输出，将cpu，内存的原始数据存储system_info变量中
        if system_info == nil or system_info == "" then
            local cmd = "top -bn 2 -i -c -d 0.1"
            local output = execute_cmd(cmd)
            -- top result fisrt line is not correct on linux, use second line
            local i, j = string.find( output, "%s\ntop.*" )
            local ret = string.sub(output, i, j)
            system_info = ret
        end
        -- 2. 执行 df -hT 命令，并将disk原始数据存入disk_info中
        if disk_info == nil or disk_info == "" then
            local cmd = "df -hT"
            local output = execute_cmd(cmd)
            disk_info = output
        end
        -- 3. 执行 iostat -d -x | awk '{print $14}' 命令，并将diskIo原始数据存入 diskIo_info 中
		if diskIo_info == nil or diskIo_info == "" then
			local cmd = "iostat -d -x | awk '{print $14}'"
			local output = execute_cmd(cmd)
			diskIo_info = output
		end
		-- 4. 执行hostname命令，以获取服务器的主机名
		if hostname == nil or hostname == "" then
			local cmd = "hostname"
			local output = execute_cmd(cmd)
			hostname = output
		end
    end


    -- 解析原始数据，获取 CPU 使用率
    local function get_cpu_usage()
        local cpu_user = string_match_num(system_info, "[0-9]+%.?[0-9]*%sus,")
        local cpu_system = string_match_num(system_info, "[0-9]+%.?[0-9]*%ssy,")
        local cpu_nice = string_match_num(system_info, "[0-9]+%.?[0-9]*%sni,")
        local cpu_idle = string_match_num(system_info, "[0-9]+%.?[0-9]*%sid,")
        local cpu_wait = string_match_num(system_info, "[0-9]+%.?[0-9]*%swa,")
        local cpu_hardware_interrupt = string_match_num(system_info, "[0-9]+%.?[0-9]*%shi,")
        local cpu_software_interrupt = string_match_num(system_info, "[0-9]+%.?[0-9]*%ssi,")
        local cpu_steal_time = string_match_num(system_info, "[0-9]+%.?[0-9]*%sst")
        local cpu_total = cpu_user + cpu_nice + cpu_system + cpu_wait + cpu_hardware_interrupt + cpu_software_interrupt + cpu_steal_time + cpu_idle
        local cpu_time = cpu_user + cpu_nice + cpu_system + cpu_wait + cpu_hardware_interrupt + cpu_software_interrupt + cpu_steal_time
        local cpu_usage = cpu_time / cpu_total
        return cpu_usage
    end


    -- 解析原始数据，获取 内存 使用率
    local function get_mem_usage()
        local mem_total = string_match_num(system_info, "Mem[%d%p%s]*[0-9]+%stotal")
        local mem_used = string_match_num(system_info, "free[%d%p%s]*[0-9]+%sused")
        local mem_usage = mem_used  / mem_total
        return mem_usage
    end


    -- 解析原始数据，获取 磁盘 使用率
    local function get_disk_usage()
        -- 记录磁盘个数
        local diskNum = 0
        -- 记录所有磁盘总使用率
        local num = 0
        for w in string.gmatch(disk_info,"%s%d+%%") do
            local step = string.sub(w, 1, string.len(w) - 1)
            num = num + tonumber(step)
            diskNum = diskNum + 1
        end
        return num/diskNum
    end

	-- 解析原始数据，获取 磁盘IO （一秒中有百分之多少的时间用于I/O操作，%util接近100%,说明产生的I/O请求太多,I/O系统已经满负载,该磁盘可能存在瓶颈）
	local function get_diskIo_usage()
		-- 记录磁盘个数
		local diskNum = 0
		-- 记录所有磁盘IO率
		local num = 0
		-- string.find只能匹配一次，要想迭代的话需要手动改变i和j索引，这样太麻烦了，所以这里使用string.gmatch函数
		for w in string.gmatch(diskIo_info, "\n%d+%.%d+") do
			step = string.sub(w,1,string.len(w))
			num = num + tonumber(step)
			diskNum = diskNum + 1
		end
		return num/diskNum
	end

	-- 定义数据库关闭方法
	local function close_db(db)
		if not db then
			return
		end
		db:close()
	end

	-- 获取无服务状态信息
	get_system_info()
	local cpuUsage = string.format("%.2f",get_cpu_usage())
	local memUsage = string.format("%.2f",get_mem_usage())
	local diskUsage = string.format("%.2f",get_disk_usage())
	local diskIoUsage = string.format("%.2f",get_diskIo_usage())
	

	-- 引入openresty提供的第三方mysql库
    local mysql = require("resty.mysql")
	-- 创建mysql实例
	local db,err = mysql:new()
	if not db then
		ngx.say("new mysql error:",err)
	end
	-- 设置超时时间
	db:set_timeout(1000)
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
	-- 将原始数据存储到mysql数据库中
	-- 因为这里的数据直接来自于系统，所以这里就不进行转义了
	-- 又因为 yishen_serverStatus 表与 yishen_server 表具有外键关联，所以我们先要查看 yishen_server 表中是否已具有该服务器基本信息，
	-- 若没有，则先向yishen_server表中插入该服务器基本信息后，再将原始数据写入yishen_serverStatus表
	local queryServerSql = "select * from yishen_server where server_hostname = "..ngx.quote_sql_str(hostname)
	local res,err,errno,sqlstate = db:query(queryServerSql)
	-- https://www.cnblogs.com/njucslzh/archive/2013/02/02/2886876.html
	if next(res) == nil then
		-- 如果没有server信息，则向yishen_server表中插入该服务器基本信息
		local createServerSql = "insert into yishen_server(server_hostname,server_time) values ("..ngx.quote_sql_str(hostname)..",now())"
		local res,err,errno,sqlstate = db:query(createServerSql)
		if not res then
			ngx.say("create to mysql error：",err,", errno：", errno, ", sqlstate：", sqlstate)
		end
	end
	-- 若该服务器基本信息已存在，则将原始数据插入yishen_serverStatus表
	-- 1. 从yishen_server表中查找本服务器主机名对应的主键id，以便将id值插入yishen_serverStatus表的外键ser_foreignKeyId中
	local foreignKeyIdSql = "select server_id from yishen_server where server_hostname = "..ngx.quote_sql_str(hostname)
	local res,err,errno,sqlstate = db:query(foreignKeyIdSql)
	local foreignKeyId;
	for i, row in ipairs(res) do
		for name, value in pairs(row) do
			foreignKeyId = value
		end
	end
	-- 2. 将数据插入yishen_serverStatus表中
	local insertStatusSql1 = "insert into yishen_serverStatus(ser_cpu,ser_memory,ser_disk,ser_diskIo,ser_createTime,ser_foreignKeyId) values"
	local insertStatusSql2 = "("..cpuUsage..","..memUsage..","..diskUsage..","..diskIoUsage..",now(),"..foreignKeyId..")"
	local insertStatusSql = insertStatusSql1..insertStatusSql2
	local res,err,errno,sqlstate = db:query(insertStatusSql)
	if not res then
		ngx.say("insert to mysql：",err,", errno：",errno,", sqlstate",sqlstate)
	end
	close_db(db)
end

-- 设置定时器
local delay = 5  -- 每5秒执行一次
local new_timer = ngx.timer.at
local log = ngx.log
local ERR = ngx.ERR
local check

check = function(premature)
	if not premature then
		-- do the health check or other routine work
		linuxGain()
		local ok, err = new_timer(delay, check)
		if not ok then
			log(ERR, "failed to create timer: ", err)
			return
		end
	end
end

if 0 == ngx.worker.id() then
	local ok, err = new_timer(delay, check)
	if not ok then
		log(ERR, "failed to create timer: ", err)
		return
	end
end
















