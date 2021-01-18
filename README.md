# project description (项目描述)

> project name : distributed-lock
> author : LGZ
> description: java 实现 redis 分布式锁.

## module introduce (模块介绍)
前置说明：运行服务之前需要先启动 redis ，请求服务之前需先使用 redis 命令 `set Mi11 100` 初始化商品库存
| module name | description | explain |
| :---: | :--- | :---: |
|  redis001 | 实现基本业务，请求一次接口扣减一个库存，实现抢购逻辑。用 ReentrantLock 加锁。| 单台服务 |
|  redis002 | 和redis001完全相同的代码  | 多台服务<br/> nginx 反代负载均衡<br/> jmeter 测试高并发情况<br/>[nginx和jemeter配置](./NGINX&JEMETER.md) |
|  module003 | …… | …… |
|  module004 | …… | …… |

## development environment (开发环境)
- SYSTEM:  windows 10
- LANGUAGE:  JAVA 1.8
- IDE:  IntelliJ IDEA
- redis
- nginx-1.19.6
- apache-jmeter-5.4