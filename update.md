# 1.8.0
### BETA.1
- `KQCodeUtils`中的所有**数组**返回值均更替为**列表** (例如`split`)

- 增加接口定义`CodeBuilder<T>`及其默认实现类, 以实现通过更明了高效的方式构建一个任意参数的CQ码。
具体使用方式可去参考文档或代码注释。
- `KQCodeUtils`增加用于获取`CodeBuilder`的对应方法

- 优化`KQCodeUtils`内部分api以可以实现更高效的代码协同 (例如`split`)
- 调整部分文件以及类的命名。一般不会对使用造成影响
- 调整`KQCodeUtils`内部分方法的实现逻辑

- 隐藏`MapKQCode`与`MutableMapKQCode`的构造函数并以工厂方法替代
- 隐藏`KQCodeUtils`中不应存在的字段实体

### BETA.2
- `MutableKQCode`的type变更为可变类型
- 标注部分方法为`static`

### BETA.3
- `CQEncoder` 与 `CQDecoder` 中可空与非空分离
- 简单优化`MQCode`与`KQCode`的相互转化

### release
- 固定版本


# 1.7.0
- 追加一个工具类`KQCodeJsonUtils`，支持将消息或者CQ码转化为Json风格的map数据。


# 1.6.1
- 修复`KQCode.of(...)`中可能会将CQ码字符串的参数二次转义的问题。现在增加了一个`decode: Boolean`参数来控制是否反转义，且默认为true。

# 1.6.0
- 修复`MQCodeUtils`中无法解析无参数mirai码的bug（例如atAll）
- 为KQCode增加一些单例实例: `AtAll`、`Rps`、`Dice`、`Shake`、`Anonymous`、`AnonymousCompulsory`
- 版本号中不再携带核心版本号



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