# Swim 软件教程

Swim 是建造可规模化扩展的，终端到终端的流式（streaming）应用程序的完整的集成解决方案。
相对于使用单独的消息代理（message brokers），应用伺服器（app servers），和数据库（database），Swim 应用程序只由两部分组成：

- **Swim 伺服器（server）** *内置* 维久性（persistence），通信（messaging），调度（scheduling）， 聚类（clustering），复制（replication），自我测量（introspection），以及安全（security）。

- **操作界面（user interface）** 通过使用Swim的流式UI框架（streaming UI frameworks）视觉化来自Swim伺服器（Server）的实时数据

*其他语言版本：[English](README.md), [简体中文](README.zh-cn.md)*


## Run
* [安装 JDK 9+](https://www.oracle.com/technetwork/java/javase/downloads/index.html).
  * 确保您的`JAVA_HOME`环境变量设置指向您的Java安装地址。
  * 确保您的`PATH`包含`$JAVA_HOME`。

* 从控制台（console）进入	`server` 目录，运行 `./gradlew run` （Windows操作系统运行`.\gradlew.bat run`） 来启动Swim伺服器（server）。

* 然后可在浏览器中分别打开 `ui/chart.html`, `ui/gauge.html`, 和 `ui/pie.html` 即可看到不同的 UI 实时部件。

## 如何运作

浏览 [伺服器（server）](https://github.com/swimos/tutorial/tree/master/server)学习如何建立和处理在Swim伺服器（server）上的数据。

接着，浏览 [操作界面（ui）](https://github.com/swimos/tutorial/tree/master/ui) 来学习与众不同的Swim视觉化数据处理。