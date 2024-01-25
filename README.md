# SpringCloud基础学习

## SpringCloud Eureka

pom文件的依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```



- Eureka包含两个组件：Eureka Server和Eureka Client

  <img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202312290920426.png" alt="image-20231229092051280" style="zoom:50%;" />

  

- Eureka Server（这一章实现的功能），提供服务注册与发现
- Service Provider，服务提供方，将自身服务注册到 Eureka Server 上，从而让 Eureka Server 持有服务的元信息，让其他的服务消费方能够找到当前服务
- Service Consumer，服务消费方，从 Eureka Server 上获取注册服务列表，从而能够消费服务
- Service Provider/Consumer 相对于 Server，都叫做 Eureka Client



### 创建优惠券系统搭建

1. 首先创建一个Maven工程，设置完成JDK、Maven版本，由于是父工程只做模块管理，不需要编写代码将src目录下全部删除

2. pom.xml文件中添加配置和依赖

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>com.sorcery.coupon</groupId>
       <artifactId>coupon</artifactId>
       <version>1.0-SNAPSHOT</version>
   
       <name>coupon</name>
       <description>SpringCloud Project For Coupon</description>
   
       <!--父工程下创建了modules会自动添加到此处-->
       <modules>
           <module>coupon-eureka</module>
       </modules>
   
       <!-- 项目的打包类型, 即项目的发布形式, 默认为 jar. 对于聚合项目的父模块来说, 必须指定为 pom -->
       <packaging>pom</packaging>
   
       <parent>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-parent</artifactId>
           <version>2.1.4.RELEASE</version>
       </parent>
   
       <properties>
           <maven.compiler.source>8</maven.compiler.source>
           <maven.compiler.target>8</maven.compiler.target>
           <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
           <!--标记SpringCloud的版本-->
           <spring-cloud.version>Greenwich.RELEASE</spring-cloud.version>
       </properties>
   
       <dependencies>
           <!-- lombok 工具通过在代码编译时期动态的将注解替换为具体的代码,
           IDEA 需要添加 lombok 插件 -->
           <dependency>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
               <version>1.16.18</version>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
               <scope>test</scope>
           </dependency>
       </dependencies>
   
       <!-- 标识 SpringCloud 的版本 -->
       <dependencyManagement>
           <dependencies>
               <dependency>
                   <groupId>org.springframework.cloud</groupId>
                   <artifactId>spring-cloud-dependencies</artifactId>
                   <version>${spring-cloud.version}</version>
                   <type>pom</type>
                   <scope>import</scope>
               </dependency>
           </dependencies>
       </dependencyManagement>
   
       <!-- 配置远程仓库 -->
       <repositories>
           <repository>
               <id>spring-milestones</id>
               <name>Spring Milestones</name>
               <url>https://repo.spring.io/milestone</url>
               <snapshots>
                   <enabled>false</enabled>
               </snapshots>
           </repository>
       </repositories>
   
   </project>
   ```

   

### 搭建Eureka Server

1. 在父工程下创建一个Maven模块

2. pom.xml添加配置和依赖

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
       <parent>
           <groupId>com.sorcery.coupon</groupId>
           <artifactId>coupon</artifactId>
           <version>1.0-SNAPSHOT</version>
       </parent>
   
       <artifactId>coupon-eureka</artifactId>
       <version>1.0-SNAPSHOT</version>
       <packaging>jar</packaging>
   
       <!-- 模块名及描述信息 -->
       <name>coupon-eureka</name>
       <description>Spring Cloud Eureka For Coupon</description>
   
       <properties>
           <maven.compiler.source>8</maven.compiler.source>
           <maven.compiler.target>8</maven.compiler.target>
           <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
       </properties>
   
       <!-- eureka server: 提供服务发现与服务注册 -->
       <dependencies>
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
           </dependency>
       </dependencies>
   
       <!--
       SpringBoot的Maven插件, 能够以Maven的方式为应用提供SpringBoot的支持，可以将
       SpringBoot应用打包为可执行的jar或war文件, 然后以通常的方式运行SpringBoot应用
       -->
       <build>
           <plugins>
               <plugin>
                   <groupId>org.springframework.boot</groupId>
                   <artifactId>spring-boot-maven-plugin</artifactId>
               </plugin>
           </plugins>
       </build>
   
   </project>
   ```

3. src/main/java设置的包下创建启动类

   ```java
   package com.sorcery.coupon;
   
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
   
   /**
    * Eureka启动入口
    *
    * @author jinglv
    * @date 2023/12/29 09:39
    */
   @EnableEurekaServer
   @SpringBootApplication
   public class CouponEurekaApplication {
       public static void main(String[] args) {
           SpringApplication.run(CouponEurekaApplication.class, args);
       }
   }
   ```

   

4. resources目录下新建application.yml文件，具体配置启动说明中具体介绍

5. 启动服务，访问Eureka自带的看板查看信息，地址：http://localhost:xxxx



### Eureka Server启动说明

- Eureka Server单节点配置说明，resources目录下创建application.yml编写如下配置

  ```yml
  spring:
    application:
      name: coupon-eureka
  
  server:
    port: 8000
  
  eureka:
    instance:
      hostname: localhost
    client:
      # 标识是否从Eureka Server获取注册信息，默认是true，如果这是一个单节点Eureka Serve
      # 不需要同步其他节点的数据，设置为false
      fetch-registry: false
      # 是否将自己注册到Eureka Server， 默认是true，由于当前应用是单节点的Eureka Server
      # 需要设置为false
      registry-with-eureka: false
      # 设置Eureka Server所在地址，查询服务和注册服务都需要依赖这个地址
      service-url:
        defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  ```

  

- Eureka Server多节点配置说明，resources目录下创建bootstrap.yml编写如下配置（注：同一工程下，只是为了和application.yml做区分，在SpringBoot配置文件中bootstrap优先级是要比application高，但是在运行的时候需要将application.yml里面内容进行注释）

  ```yml
  spring:
    application:
      name: coupon-eureka
    profiles: server1
  server:
    port: 8000
  eureka:
    instance:
      # 本地host设置 127.0.0.1 server1
      hostname: server1
      # 不允许通过IP地址在一台机器上启动多个实例，默认是true
      prefer-ip-address: false
    client:
      service-url:
        defaultZone: http://server2:8001/eureka/,http://server3:8002/eureka/
  
  ---
  spring:
    application:
      name: coupon-eureka
    profiles: server2
  server:
    port: 8001
  eureka:
    instance:
      # 本地host设置 127.0.0.1 server2
      hostname: server2
      prefer-ip-address: false
    client:
      service-url:
        defaultZone: http://server1:8000/eureka/,http://server3:8002/eureka/
  
  ---
  spring:
    application:
      name: coupon-eureka
    profiles: server3
  server:
    port: 8002
  eureka:
    instance:
      # 本地host设置 127.0.0.1 server3
      hostname: server3
      prefer-ip-address: false
    client:
      service-url:
        defaultZone: http://server1:8000/eureka/,http://server2:8001/eureka/
  
  ```

  Eureka Server多节点配置注意是的，在同一机器上需要再host做下IP映射，是因为不允许在同一IP上启动多个节点，多节点的意义是在于启动一个服务因有些问题挂了，其他的服务还是可以继续运行，如果同一服务多个节点要是机器挂了就全挂了，例如Mac OS/Linux下的/etc/hosts文件加入如下配置

  ```
  # This line is auto added by aTrustAgent, do not modify, or aTrustAgent may unable to work
  127.0.0.1	localhost.sangfor.com.cn
  127.0.0.1	server1
  127.0.0.1	server2
  127.0.0.1	server3
  ```

  服务启动，因为是三个服务，不能直接启动CouponEurekaApplication应用，需要将应用打包为jar包对应三个服务进行启动，启动命令如下：

  ```shell
  java -jar coupon-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server1
  java -jar coupon-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server2
  java -jar coupon-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server3
  ```

  注意：server1、server2、server3相互是有依赖的，三个服务完全启动才可运行正常，其中没有完全启动，只启动其中一个两个是会有异常，这个是正常情况，三个全部启动后异常消失服务均正常启动

  访问Eureka自带的看板查看信息，地址：http://localhost:8000

  <img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202312291104496.png" alt="image-20231229110406339" style="zoom:50%;" />

  



## SpringCloud Zuul

在介绍 Zuul 可以提供的功能之前，先考虑一个问题：微服务系统中往往包含很多个功能不同的子系统或微服务，那么，外部应用怎样去访问各种各样的微服务呢？这也是 Zuul 所要解决的一个主要问题。

在微服务架构中，后端服务往往不直接开放给调用端，而是通过一个服务网关根据请求的url，路由到相应的服务，即实现请求转发，效果如下图所示。

- Zuul是一个API Gateway服务器，本质上是一个Web Servlet应用
- Zuul提供了动态路由、监控等服务，这些功能实现的核心是一系列的filters

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202312291507813.png" alt="image-20231229150749672" style="zoom:50%;" />



自定义Zuul过滤器需要继承ZuulFilter，并实现四个抽象方法

- filterType：对应Zuul生命周期的四个阶段：pre、post、route和error
- filterOrder：过滤器的优先级，数字越小，优先级越高
- shouldFilter：方法返回boolean类型，true时表示是否执行该过滤器的run方法，false则表示不执行
- run： 过滤器的



pom中添加依赖

```xml
<!--
    Eureka 客户端, 客户端向 Eureka Server 注册的时候会提供一系列的元数据信息, 例如: 主机, 端口, 健康检查url等
    Eureka Server 接受每个客户端发送的心跳信息, 如果在某个配置的超时时间内未接收到心跳信息, 实例会被从注册列表中移除
-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<!-- 服务网关 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```





## 搭建网关模型

### 定义抽象过滤器

### 自定义Token校验过滤器

### 自定义限流过滤器

### 自定义访问日志过滤器



## 微服务系统通用配置开发

设计思想

- 通用的代码、配置不应该散落在各个业务模块中，不利于维护与更新
- 一个大的系统，响应对象需要统一外层格式
- 各种业务设计与实现，可能会抛出各种各样的异常，异常信息的收集也应该做到统一



### 统一的响应

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031100664.png" alt="image-20240103110045445" style="zoom:50%;" />





### 统一异常处理

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031355362.png" alt="image-20240103135558260" style="zoom:50%;" />



### 数据表设计

```sql
-- 创建数据库 coupon_data
CREATE DATABASE IF NOT EXISTS coupon_data;
```



- 优惠券模版表：coupon_template

  ```sql
  -- 创建 coupon_template 数据表
  CREATE TABLE IF NOT EXISTS `coupon_data`.`coupon_template` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `available` boolean NOT NULL DEFAULT false COMMENT '是否是可用状态; true: 可用, false: 不可用',
    `expired` boolean NOT NULL DEFAULT false COMMENT '是否过期; true: 是, false: 否',
    `name` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券名称',
    `logo` varchar(256) NOT NULL DEFAULT '' COMMENT '优惠券 logo',
    `intro` varchar(256) NOT NULL DEFAULT '' COMMENT '优惠券描述',
    `category` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券分类',
    `product_line` int(11) NOT NULL DEFAULT '0' COMMENT '产品线',
    `coupon_count` int(11) NOT NULL DEFAULT '0' COMMENT '总数',
    `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
    `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建用户',
    `template_key` varchar(128) NOT NULL DEFAULT '' COMMENT '优惠券模板的编码',
    `target` int(11) NOT NULL DEFAULT '0' COMMENT '目标用户',
    `rule` varchar(1024) NOT NULL DEFAULT '' COMMENT '优惠券规则: TemplateRule 的 json 表示',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `name` (`name`)
  ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='优惠券模板表';
  ```

  

  说明：

  1. 表中的各个字段及其类型声明
  2. name是唯一键
  3. category和user_id建立了单列索引

- （用户）优惠券：coupon

  ```sql
  -- 创建 coupon 数据表
  CREATE TABLE IF NOT EXISTS `coupon_data`.`coupon` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `template_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联优惠券模板的主键',
    `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '领取用户',
    `coupon_code` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券码',
    `assign_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '领取时间',
    `status` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券的状态',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_user_id` (`user_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='优惠券(用户领取的记录)';
  ```

  说明：

  1. 表中的各个字段及其类型声明
  2. template_id和user_id建立了单列索引

### 缓存设计

优惠券码缓存

- 说明：
  1. 优惠券系统使用Redis做缓存，所以，都是KV类型
  2. Key需要有意义，且不能与原有的Key冲突
  3. 优惠券码需要永久存在，不设过期时间

​		key = 前缀（coupon_template_code_ + 后缀（优惠券模板数据表主键））

​		value类型：list

- 说明：

  1. 优惠券系统使用Redis做缓存，所以，都是KV类型
  2. Key需要有意义，且不能与原有的Key冲突
  3. 用户优惠券信息根据状态（未使用、已使用、已过期）分为三类
  4. 用户数据保存在MySQL中，且数据量大，不适合长时间驻留在Redis中，需要设置过期时间

  key=前缀（`user_coupon_usable_`、`user_coupon_userd_`、`user_coupon_expired_`+后缀（优惠券模版数据表主键））

  value类型：hash（key：优惠券id；value：优惠券信息）



### 架构设计

SpringCloud微服务组件架构

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401041558529.png" alt="image-20240104155816247" style="zoom:50%;" />

功能微服务设计

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401041600460.png" alt="image-20240104160004391" style="zoom:50%;" />



## 微服务调用组件

### SpringCloud Ribbon 和 Feign

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401121021472.png" alt="image-20240112102135257" style="zoom:50%;" />

- Ribbon包括了两个部分：负载均衡算法 + app_name转具体的ip:port

  <img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401121023072.png" alt="image-20240112102303007" style="zoom:50%;" />

- Feign：定义接口，并在接口上添加注解，消费者通过调用接口的形式进行服务消费

  <img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401121024986.png" alt="image-20240112102450921" style="zoom:50%;" />



### SpringCloud Hystrix

服务雪崩是熔断器解决的核心问题

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401121028314.png" alt="image-20240112102805237" style="zoom:50%;" />

Hystix的三个特性：断路器机制、Fallback、资源隔离

- **断路器机制**：当Hystix Command请求后端服务失败数量超过一个阈值比例（默认50%），断路器就会切换到开路状态
- **Fallback**：降级回滚策略
- **资源隔离**：不同的微服务调用使用不同的线程池来管理

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401121033186.png" alt="image-20240112103316037" style="zoom:50%;" />



#### coupon-distribution优惠券分发模块

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401151530427.png" alt="image-20240115153035527" style="zoom:50%;" />



#### coupon-settlement优惠券结算模块

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401191019869.png" alt="image-20240119101930654" style="zoom:50%;" />



