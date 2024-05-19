## 基于java的反爬虫项目
- 学完java后做的一个小项目练练手，在此分享一下。

### 技术栈
本反爬虫系统将采取B/S架构，遵从MVC规范，使用SSM框架（即SpringMVC、Spring、Mybatis整合的简称），websocket、echarts和bootstrap等技术，实现服务端的用户请求数据与主机状态信息的采集，爬虫的识别与拦截，前后端的实时通信，数据的可视化等功能。

### 系统功能模块
1. 用户模块：在该模块下，用户可以通过前台的web界面，在输入正确的用户名、密码之后，点击登录按钮登录系统。若登陆账户为system账户，则可以通过该模块对普通账户的信息进行增删查改。
2.  数据采集模块：在该模块下，系统会自动周期性的对服务器状态数据和用户请求数据进行收集，以便于后期的数据分析。 
3. 规则管理模块：在该模块下，用户可以通过前台的web页面进行可视化的反爬虫规则配置管理，包括referer规则配置、userAgent规则配置、keyPages规则配置、封禁时间规则配置和数据分析间隔规则配置。
4. 流程管理模块：在该模块下，用户可以通过前台的web页面进行可视化的流程策略配置管理，以便在合适的场景下采用恰当的流程策略。
5. 实时计算模块：在该模块下，系统会自动的将由数据采集模块收集得到的数据结合反爬虫策略，进行爬虫的识别，实时地剔除爬虫流量，保证服务的正常提供。 
6. 数据持久化模块：在该模块下，系统将由数据采集模块收集得到的数据、用户制定的反爬虫规则以及反爬结果等信息持久化到数据库中，以便进行爬虫分析以及后期的数据展示。该模块由系统自动的定时完成，无需用户手动操作。
7. 实时监控模块：在该模块下，用户可以清晰方便地看到服务器的状态信息与链路流量转发情况。
8. 数据可视化模块：在该模块下，系统可以将服务器状态信息与链路流量信息制作成图表，向用户展示不同时间内爬虫对系统稳定性的影响。

### 项目部署
- 部署环境：
  - Centos7
  - apache-tomcat-9.0.29
  - mysql-5.5.36-win32
  - openresty-1.13.6.1
  - JDK 1.8.0_191
  - Lua 5.1.4
- 部署步骤
  - 在mysql数据库中先创建一个名为graduationproject的数据库，然后运行 附件/表创建.sql文件 中的SQL语句，创建数据表。
  - 在centos7中安装openresty服务器，然后将系统的lua脚本部署在openresty服务器中并对该服务器的nginx.conf文件进行配置，启动服务器，脚本即可自动运行,nginx.conf文件配置的示例如下：
  ![nginx.conf配置](https://raw.githubusercontent.com/02SWD/AntiCrawler/main/%E9%99%84%E4%BB%B6/nginx%E9%85%8D%E7%BD%AE.png?token=GHSAT0AAAAAACSOXDXHCMKVKQYNZJTDSCTCZSJXJZA)
  - 将项目源代码打包成war包之后，部署在tomcat服务器中，运行服务器即可打开主系统。
  - 在登录页面，管理员账号默认为：system/root，而普通用户需要管理员手动添加在进行登录。

### 系统架构
![系统架构](https://raw.githubusercontent.com/02SWD/AntiCrawler/main/%E9%99%84%E4%BB%B6/%E7%B3%BB%E7%BB%9F%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97.png?token=GHSAT0AAAAAACSOXDXGU4YNLAHW5DEWW26MZSJXJ6Q)















