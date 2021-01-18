# (nginx 配置)
[nginx 下载地址](http://nginx.org/en/download.html)
安装使用教程请自行百度/谷歌
```
    upstream my_server{
        server localhost:1111 weight=5 max_fails=2 fail_timeout=30s;
	    server 127.0.0.1:2222 weight=5 max_fails=2 fail_timeout=30s;
    }
    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        location /myserver/ {
            proxy_set_header    Host    $host;
            proxy_set_header    X-Real-IP    $remote_addr;
            proxy_set_header    X-Forwarded-For    $proxy_add_x_forwarded_for;
            rewrite ^/myserver/(.*)$ /$1 break;
            proxy_pass    http://my_server;
        }
     # …… 其他默认配置省略
    }
```

# ( jemeter 请求地址)
[jemeter 下载地址](https://jmeter.apache.org/download_jmeter.cgi)
安装使用教程请自行百度/谷歌
```
http://localhost/myserver/buy
```
