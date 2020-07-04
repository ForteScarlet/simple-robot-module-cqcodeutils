# 1.5.1-1.15
- 修复`KQCodeUtils.remove(msg [,...])`的时候，如果msg是不存在CQ码的长度大于等于5的消息，则会遗失第一个字符的问题。


# 1.5.0-1.15
- `KQCode`变为参数不可变类，并增加一个参数可变的子类`MutableKQCode`, 可通过`KQCode`实例的`mutable()`方法进行转化或直接构建`MutableKQCode`实例。
- 简单更新优化KQCode的DSL构建方式:
`param = "key" to "value"` 可以简写成 `this["key"] = "value"`了。
- 增加接口`CodeTemplate<T>`定义一些模板方法，例如`at(...)`、`image(...)`等。提供两个默认的实现类，具体查看文档。
- 代码追加copyright信息

# 1.4.1-1.13
- 修复`split(...)`方法的bug
- 增加一个`Msgs`类，用于拼接`KQCode`、`MQCode`、字符串消息等内容。Msgs的toString会默认在他们中间追加一个空格。



# 1.4-1.13
- 追加`MQCodeUtils`以支持对Mirai码的简易解析与支持。
- 增加一些针对参数的空值判断

# 1.3-1.12
- get相关的方法增加了一系列可以根据type来获取的参数重载

# 1.2.1-1.12
- 修复remove的bug

# 1.2-1.12
- KQCode的两个of方法支持infix调用，例如 KQCode of "\[CQ:at,qq=123456]"
- KQCodeUtils增加`remove`方法和`removeByType`方法, 用来移除文本中的CQ码。
- 顺手更新一下核心


# 1.1-1.11
- `KQCodeUtils` 追加方法`String[] split(String text)`、`String getCq(String text, int index = 0)`、`String[] getCqs(String text)`等若干方法
- `KQCode` 追加静态方法`KQCode of(String text)`
# 1.0-1.11
- 初版