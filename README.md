# 项目描述

> project name : distributed-lock
> author : LGZ
> description: java 实现 redis 分布式锁.

## module introduce
| module name | description | else |
| :---: | :--- | :---: |
|  redis001 | 实现基本业务，请求一次接口扣减一个库存，实现抢购逻辑。用ReentrantLock加锁.| something else |
|  redis002 | 和redis001完全相同的代码  | 用 nginx 反代负载均衡，<br/> jmeter 测试高并发情况 |
|  003_release |   |   |

## development environment
- SYSTEM: windows 10
- LANGUAGE: JAVA 1.8
- IDE: IntelliJ IDEA
