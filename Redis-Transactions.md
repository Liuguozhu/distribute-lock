# redis transactions(redis 事务)

## 事务介绍
- redis 事务是通过 `MULTI`, `EXEC`, `DISCARD`, `WATCH` 这四个命令来完成
- redis 的单个命令都是`原子性`的，所以这里确保事务性的对象是`命令集合`。
- redis 将命令集合序列化并确保处于一事务的`命令集合连续且不被打断`的执行。
- redis `不支持回滚`操作。

## 相关命令
- MULTI

    `用于标记事务块的开始`。

    redis 会将后续的命令逐个放入队列中，然后使用 `EXEC`命令原子化的执行这个命令序列。

    语法：`MULTI`

- EXEC

    在一个`事务中执行所有先前放入队列的命令`，然后恢复正常的连接状态。

    语法：`EXEC`

- DISCARD

    清除所有先前在一个事务中放入队列的命令，然后恢复正常的连接状态。

    语法：`DISCARD`

- WATCH

    当`事务需要按条件执行`时，就要使用这个命令将给定的`键( key )设置为受监控`的`状态`。

    语法：`WATCH key […]`

下面这段代码来源于 [redis官网](https://redis.io/topics/transactions)

更多代码也请点击上面👆链接查阅

```
> MULTI
OK
> INCR foo
QUEUED
> INCR bar
QUEUED
> EXEC
1) (integer) 1
2) (integer) 1

```
加上 WATCH，并且在执行事务之前，没有其他 redis 连接对 监听的这个 key 进行修改。则代码如下
```
> WATCH k1
OK
> MULTI
OK
> SET k1 v1
QUEUED
> SET k2 v2
QUEUED
> EXEC
1) (integer) 1
2) (integer) 1

```
加上 WATCH，如果在执行事务之前，有其他redis连接对监听的这个 key 进行修改 如：`SET k1 111`。则执行结果如下
```
> WATCH k1
OK
> MULTI
OK
> SET k1 v1
QUEUED
> SET k2 v2
QUEUED
> EXEC
(nil)
```
