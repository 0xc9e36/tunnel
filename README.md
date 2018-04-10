## tunnel是什么
* 这是一个简单的，为了自己用的，类似花生壳的，内网穿透工具。

```
				=========================									=========================
				|			|			|							 		|			|			|
				|	外网	|			| <--------------------------------	|			|	内网	|
				|			|	S2C		| 	1.启动后注册到Server			|	S2C-----+-----------+------------> http server		
http/https		|			|			| ----analyze http reqiest-------->	|			|			|		:	(Tomcat/Nginx...)
	|			|			|			|	2.Server将请求传给Client，		|			|			|		:
  Nginx-------->|  Tunnel	|===========|	     带[requestid]。			|===========|  Tunnel	|		:
				|  Server	|			|									|			|  Client	|		:
				|			|			| <---[requestid] http response----	|			|			|		:
				|			|	C2S		| 	3.Client将结果传给Server		|	C2S		|			|		:
				|			|			|									|			|			|		:
				|			|			|									|			|			|		:
				=========================									=========================		:
					http request pool											 ^							:			
					|	requestid	|											 |---------------------------
					-----------------
					|	browser con	|

```				



## 为什么会有（不是有花生壳、ngrock么）
* 首先我用的花生壳，付费的。
* 不稳定，特别是在改域名映射ip后，总会突然访问不了。
* 今天早上打020 62219018电话，每次都是等好久，上厕所回来都还没接通，一等接通，这查查那查查，什么都不改，又好了！
* 有时候特别卡，特别卡，特别卡。特别是文件上传。
* 花生壳客服解决不了，没办法。换线路也没效果。
* 所以自己写了个类似的。

## 先说清楚tunnel
* 请求处理超过10秒的，会404。机制和内网穿透还是不一样的，10秒钟的超时是代码写死的。文件下载不算。
* 会有一些小bug，但是对于需要零时调试下内网，特别是微信公众号开发这样的需求，没什么问题，我们的微信公众号调试已经全从花生壳切到tunnel。
* 发现bug，欢迎提交，一有时间我肯定及时改掉。

## 怎么用
* tunnel-common  这是tunnel-server和tunnel-client的mvn依赖。
* 在公网上布置好tunnel-server，可以直接监听80端口，也可以放在nginx之后，nginx转发80端口到tunnel-server上。
* 然后就简单了！在你需要穿透的内网里，启动tunnel-client就好。
* 没有花生壳的单点登录的限制。


## 配置说明
tunnel-server的sys.properties配置：
```
#http服务端口，可以直接80，也可以接在nginx之后，这里写的6660，就是接受nginx转发的。
http_server_port=6660
#客户端注册使用的端口
register_port=6661
#客户端汇报结果端口
reply_port=6663

```
tunnel-client的sys.properties配置
```
#服务端地址，公网的服务地址
server_ip=121.*.*.*
#客户端注册端口，保持和tunnel-server的配置一致
register_port=6661
#客户端汇报结果端口，保持和tunnel-server的配置一致
reply_port=6663
#客户端的名称，全局唯一
name=test4online
#需要登记的域名
host_ary=n1.xxxx.com,n2.xxxx.com
n1.xxxx.com=localhost:9090
n2.xxxx.com=localhost:80
```

## 如果是上面这样配置
tunnel-server不是监听80端口，那么nginx里针对n1.xxxx.com和n2.xxxx.com要配置下，转发到tunnel-server的6660端口，这是nginx自身的配置，这里不多讲了。
