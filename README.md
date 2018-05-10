# MybatisPlugin

## 扩展参数插件 AddtionalParameterPlugin
在去数据库特性的时,针对时间数据需要通过sql的参数传入,这样修改地方就很多!为了解决这样的问题可以通过添加一个扩展参数进行解决<br/>
默认只有一个additional_current参数，值为当前时间，如果需要配置更多参数可以通过property方式传入

## 关闭一级缓存 CloseFirstLevelCachePlugin
Mybatis的一级缓存默认是开启的,这个不是每个业务都适合,可以通过插件都形式关闭一级缓存,在关闭一级缓存都时候也不需要穿件缓存key,
 一级缓存中也不在保存任何对象
