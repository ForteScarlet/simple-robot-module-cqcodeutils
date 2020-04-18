# 简单的CQ码操作工具类
[![](https://img.shields.io/badge/simple--robot-module-green)](https://github.com/ForteScarlet/simple-robot-core) [![](https://img.shields.io/maven-central/v/io.github.ForteScarlet.simple-robot-module/mod-cqcodeutils)](https://search.maven.org/artifact/io.github.ForteScarlet.simple-robot-module/mod-cqcodeutils)
## **依赖**
1. 依赖导入
### Maven
```xml
<dependency>
    <groupId>io.github.ForteScarlet.simple-robot-module</groupId>
    <artifactId>mod-cqcodeutils</artifactId>
    <version>${version}</version> <!-- 参考版本：1.0-1.11 -->
</dependency>
```

### gradle
```groovy
compile group: 'io.github.ForteScarlet.simple-robot-module', name: 'mod-cqcodeutils', version: '${version}'
```

## **简介**
提供单例工具类`CQDecoder`、`CQEncoder`、`KQCodeUtils`
java中可通过`getInstance()`或`INSTANCE`获取
kotlin中可作为`object`直接使用

提供`KQCode`封装类，且提供与CQCode相互转化的方法。
实现了`Map接口`与`CharSequence`接口。

kotlin下，提供DSL风格的构建方式：
```kotlin
kqCode("CQ码类型") {
        param = "参数1键" to "参数1值"
        param = "参数2键" to "参数2值"
        ...
    }
```
或
```kotlin
kqCode {
        type ="CQ码类型"
        params {
            param = "参数1键" to "参数1值"
            param = "参数2键" to "参数2值"
            ...
        }
    }
```

