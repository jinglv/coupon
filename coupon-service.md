# 优惠券系统实现

## 业务思想-template（优惠卷模板）模块

实现了什么样的功能，用到了什么样的技术和方案

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031714059.png" alt="image-20240103171400668" style="zoom:50%;" />

给优惠券模版生成“优惠券码”并保存到Redis中（list）

优惠券码在一个服务实例中预先生成的，并放到Redis中，这样做的目的有：

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031723886.png" alt="image-20240103172301730" style="zoom:50%;" />

清理过期的优惠券模版

优惠券模版规定是使用期限，有两种过期策略

1. template模块自己的定期清理策略
2. 使用template模块的其他模块自己校验（因为策略1存在延迟）



## 业务思想-distribution（优惠券分发）模块

### **功能一：根据用户id和优惠券状态查找用户优惠券记录**

说明：

1. 由于不涉及用户系统，用户id不做校验，需要fake
2. 优惠券状态有三类：可用的、已使用的、过期的（未被使用的）
3. 用户数据都存储于Redis之中
4. 除获取用户的优惠券之外，还有**延迟的过期处理策略**

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031730949.png" alt="image-20240103173029898" style="zoom:50%;" />

### **功能二：根据用户id查找当前可以领取的优惠券模版**

说明：

1. 优惠券模版从template模块除获取（**熔断兜底策略**）
2. 根据优惠券的领取限制，比对当前用户所拥有的优惠券，做出判断

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031734681.png" alt="image-20240103173453632" style="zoom:50%;" />

### **功能三：用户领取优惠券**

说明：

1. 优惠券模板从template模块处获取（熔断兜底策略）
2. 根据优惠券的领取显示，比对当前用户所拥有的优惠券，判断是否可以领取
3. 从Redis中获取优惠券
4. 优惠券写入MySQL和Redis

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031737781.png" alt="image-20240103173743718" style="zoom:50%;" />

### **功能四：结算（核销）优惠券**

说明：

1. 校验需要结算的优惠券是否是“合法的（属于用户&&可用）”
2. 利用settlement模块计算结算数据
3. 如果是核销，需要写入数据库

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031740997.png" alt="image-20240103174040927" style="zoom:50%;" />



## 业务思想-settlement（结算）模块

### 功能：根据优惠券类型结算优惠券

说明：

1. 优惠券是分类的，不同类的优惠券有不同的计算方法
2. 不同类的优惠券可以组合，所以，也需要有不同的计算方法

<img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202401031748999.png" alt="image-20240103174831929" style="zoom:50%;" />

mysql -h rm-uf6024x0jupp4zc0s.mysql.rds.aliyuncs.com -u metersphere -p

hf3guTd3sk6Hdfh

mysql -h rm-uf6024x0jupp4zc0s.mysql.rds.aliyuncs.com -u metersphere -p