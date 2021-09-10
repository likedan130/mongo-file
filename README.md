# mongo-file
基于mongodb实现的文件系统，能快速搭建包含GridFs特性的对象存储服务

## 项目简介
  1. 提供基于GridFS的文件系统功能，mongodb特性使其天然支持文件的权限管理、副本集和分片、数据的额外信息存储及索引功能 
  2. 基于thumbnailator提供通用的图片处理功能 

基于以上的特性，程序非常适合正在使用mongodb，对于对象存储业务不涉及超巨量高并发业务的团队

## 快速开始
  * 将项目代码clone到本地
  * 修改resources目录下的application.yml配置文件，根据自己的需要配置web服务和mongodb地址
  * 使用maven进行编译 ```mvn clean package -Dmaven.test.skip=true```
  * 运行程序 ```mvn spring-boot:run```

## 功能
  * /file/**提供基于文件的各类上传、下载、批量下载操作，可对存储的文件附加自定义元数据方便检索查询
  * /image/**提供图片的压缩、缩放、翻转、水印添加等操作
  * /office/**提供office文件的在线预览功能
