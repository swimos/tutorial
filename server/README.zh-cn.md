# Swim 伺服器（Server）

Swim 统一了传统的互不相干的数据库（database），消息代理（message broker），工件管理器（job manager），以及应用服务器（application server）等角色结合成了更为简单的构架。

*其他语言版本：[English](README.md), [简体中文](README.zh-cn.md)*

## 网络代理（Web Agent）

Swim 实现了通用目的的分布式对象模型。这些在模型中的“对象”被称为 **网络代理（Web Agents）**。

[新建一个 class](http://github.com/swimos/tutorial/tree/master/server/src/main/java/swim/tutorial/UnitAgent.java#L13) 继承`swim.api.agent.AbstractAgent` 定义了一个网络代理的 *样板* （虽然在加入[lanes](#lanes)之前不可用）。

浏览 [参考文献](https://swimos.org/concepts/agents/) 获得更多网络代理（Web Agents）详情。

## Lanes

若 Web Agents 是对象，则 **lanes** 在这些对象中担任这些对象的“域”。Lanes 有很多种形态，如：value lanes，map lanes 以及 command lanes。

继续我们的类比，*lane callback* 函数则是Web Agents的 “方法（methods）”。

每种 lane 的类型定义了一套可重写的（默认 no-op）生命周期回调。例如：[发送一条命令消息](#sending-data-do-swim) 到任意的 command lane 会触发 command lane [`onCommand` 回调](http://github.com/swimos/tutorial/tree/master/server/src/main/java/swim/tutorial/UnitAgent.java#L51-L54). 在另一方面, [设置一个 value lane](http://github.com/swimos/tutorial/tree/master/server/src/main/java/swim/tutorial/UnitAgent.java#L53) 会触发 value lane的 `willSet` 回调, 然后更新value lane的值，接着触发value lane 的 [`didSet` 回调](http://github.com/swimos/tutorial/tree/master/server/src/main/java/swim/tutorial/UnitAgent.java#L40-L47)。

浏览 [参考文献](https://swimos.org/concepts/lanes/) 获得更多lanes的更多详情。

## 设置 Swim 伺服器（Server）

Swim server 装载着 **平面（plane）**，plane 提供了Web Agents的运行时并且为信息到达正确的lane规划路径。

Plane 必须在每一种Web Agent 类别上都分别[宣告一个`AgentType`域（field）](http://github.com/swimos/tutorial/tree/master/server/src/main/java/swim/tutorial/TutorialPlane.java#L13-L14)

使用 `ServerLoader` 公用程序 class 来[在 Swim Server 上加载 plane](http://github.com/swimos/tutorial/tree/master/server/src/main/java/swim/tutorial/TutorialPlane.java#L18)。请注意模型必须[`提供` `swim.api.plane.Plane` 与自定义的plane class](http://github.com/swimos/tutorial/tree/master/server/src/main/java/module-info.java#L8)。

浏览 [参考文献](https://swimos.org/concepts) 获得更多详情。

## 将数据填入 Swim Server

每一个Swim 伺服器（Server）都可以从外部程序使用 Swim API 写入和读取。最简单地利用这个API的方式是使用一个**Swim Client** 实例来[发送指令到command lanes](http://github.com/swimos/tutorial/blob/master/server/src/main/java/swim/tutorial/DataSource.java#L40)。

浏览 [参考文献](https://swimos.org/concepts) 获得更多详情。

## 订阅 Swim Server 数据

Swim Client 实例使用 Swim **links** 从 Swim lanes 中获取数据。与links相对应的lanes一样，links也有可重写的回调函数，并且这个函数可以被用来[建造 UI](http://github.com/swimos/tutorial/tree/master/ui/index.html#L111-L133)。

浏览 [参考文献](https://swimos.org/concepts/links/) 获取更多详情。
