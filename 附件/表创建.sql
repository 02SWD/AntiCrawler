use graduationproject;
-- 创建用户表
CREATE TABLE yishen_user(
	id INT PRIMARY KEY auto_increment,
	username VARCHAR(20) UNIQUE NOT NULL,
	`password` VARCHAR(256) NOT NULL,
	telephone VARCHAR(20) DEFAULT "-1",
	email VARCHAR(256) DEFAULT "*@*",
	createDate datetime NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建referer过滤规则表（白名单，只有满足该referer的才允许通过）
CREATE TABLE yishen_referer(
	ref_id INT PRIMARY KEY auto_increment,
	ref_pattern VARCHAR(256) NOT NULL COMMENT "这里的正则是java正则"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建user-agent过滤规则表（黑名单）
CREATE TABLE yishen_userAgent(
	ua_id INT PRIMARY KEY auto_increment,
	ua_pattern VARCHAR(256) NOT NULL COMMENT "这里的正则是java正则"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建原始数据表
CREATE TABLE yishen_rawData(
	raw_id INT PRIMARY KEY auto_increment,
	raw_remoteAddress VARCHAR(256) NOT NULL COMMENT "客户端ip",
	raw_requestMethod VARCHAR(256) NOT NULL,
	raw_serverAddress VARCHAR(256) NOT NULL COMMENT "服务端ip",
	raw_referer VARCHAR(256) NOT NULL,
	raw_userAgent VARCHAR(256) NOT NULL,
	raw_uri VARCHAR(256) NOT NULL,
	raw_request VARCHAR(256) NOT NULL COMMENT "请求行（包含了get参数）",
	raw_requestBody text NOT NULL COMMENT "请求体（包含了post数据）",
	raw_localTime datetime NOT null COMMENT "访问时间"
	-- raw_localIso datetime COMMENT "访问时间ISO格式"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建流程策略表
CREATE TABLE yishen_process(
	pro_id INT PRIMARY KEY auto_increment,
	pro_name VARCHAR(100) UNIQUE NOT NULL,
	pro_date datetime NOT NULL,
	pro_ipSum INT NOT NULL COMMENT "x分钟内，某ip访问总量",
	pro_ipSumThreshold INT NOT NULL DEFAULT 100 COMMENT "x分钟内，某ip访问总量的阈值",
	pro_ipKeysum INT NOT NULL COMMENT "x分钟内，某ip对于关键页面的访问总量",
	pro_ipKeysumThreshold INT NOT NULL DEFAULT 100 COMMENT "x分钟内，某ip对于关键页面的访问总量的阈值",
	pro_ipInterval INT NOT NULL COMMENT "x分钟内，某ip访问间隔",
	pro_ipIntervalThreshold INT NOT NULL DEFAULT 100 COMMENT "x分钟内，某ip访问间隔的阈值",
	pro_ipKeyinterval INT NOT NULL COMMENT "x分钟内，某ip对关键页面的访问间隔",
	pro_ipKeyintervalThreshold INT NOT NULL DEFAULT 100 COMMENT "x分钟内，某ip对关键页面的访问间隔的阈值",
	pro_ipUseragent INT NOT NULL COMMENT "x分钟内，某ip的useragent种类数",
	pro_ipUseragentThreshold INT NOT NULL DEFAULT 100 COMMENT "x分钟内，某ip的useragent种类数的阈值",
	pro_finalThreshold INT NOT NULL DEFAULT 100 COMMENT "最终阈值",
	pro_enable INT NOT NULL DEFAULT 0 COMMENT "表示该策略是否已经被启用，process策略只能启用一个，0 代表未启用，1 代表已启用"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建服务器表
CREATE TABLE yishen_server(
	server_id INT PRIMARY KEY auto_increment,
	server_hostname VARCHAR(255) UNIQUE NOT NULL COMMENT "该服务器的主机名",
	server_time datetime NOT NULL COMMENT "该服务器创建的时间"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建服务器状态表
CREATE TABLE yishen_serverStatus(
	ser_id INT PRIMARY KEY auto_increment,
	ser_cpu FLOAT NOT NULL COMMENT "cpu占用率",
	ser_memory FLOAT NOT NULL COMMENT "内存占用率",
	ser_disk FLOAT NOT NULL COMMENT "磁盘占用率",
	ser_diskIo FLOAT NOT NULL COMMENT "磁盘IO率",
	ser_createTime datetime NOT NULL COMMENT "该服务器状态的时间",
	ser_foreignKeyId INT NOT NULL COMMENT "外键，值为yishen_server表的server_id字段中的值",
	CONSTRAINT `FK_Reference_1` FOREIGN KEY (`ser_foreignKeyId`) REFERENCES `yishen_server` (server_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建ip黑名单
CREATE TABLE yishen_blackIp(
	black_id INT PRIMARY KEY auto_increment,
	black_ip VARCHAR(256) NOT NULL COMMENT "加入黑名单的ip地址",
	black_timeStart datetime NOT NULL COMMENT "该ip加入黑名单时的时间",
	black_time datetime NOT NULL COMMENT "直到该时间时，ip解封"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建封禁时间间隔表
CREATE TABLE yishen_blackTime(
	black_id INT PRIMARY KEY auto_increment,
	black_timeInterval INT NOT NULL COMMENT "要封禁的时间间隔，时间单位：小时"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建关键页面表
CREATE TABLE yishen_keyPages(
	keyPages_id INT PRIMARY KEY auto_increment,
	keyPages_pattern VARCHAR(256) NOT NULL COMMENT "关键页面正则,这里的正则是java正则"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 查询原始数据的时间间隔（即：查询 now()-interval_time ~ now() 之内的原始数据，单位：秒）
CREATE TABLE yishen_AnalysisInterval(
	interval_id INT PRIMARY KEY auto_increment,
	interval_time INT NOT NULL COMMENT "查询原始数据的时间间隔（即：查询 now()-interval_time ~ now() 之内的原始数据，单位：秒）"
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 初始有一个默认的管理员账户：system/root
INSERT INTO yishen_user (username,`password`,telephone,email,createDate)VALUES('system','4813494d137e1631bba301d5acab6e7bb7aa74ce1185d456565ef51d737677b2',-1,"*@*",now());

-- 初始状态下系统会创建并启用一个process默认策略
INSERT INTO yishen_process (pro_name,pro_date,pro_ipSum,pro_ipSumThreshold,pro_ipKeysum,pro_ipKeysumThreshold,pro_ipInterval,pro_ipIntervalThreshold,pro_ipKeyinterval,pro_ipKeyintervalThreshold,pro_ipUseragent,pro_ipUseragentThreshold,pro_finalThreshold,pro_enable) values('process', now(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);

-- 默认的封禁时间为6小时
INSERT INTO yishen_blackTime (black_timeInterval) VALUES (6);

-- 默认的分析间隔为60秒
INSERT INTO yishen_AnalysisInterval (interval_time) VALUES (60)










