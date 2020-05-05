
# 1.3-1.12
get相关的方法增加了一系列可以根据type来获取的参数重载

# 1.2.1-1.12
修复remove的bug

# 1.2-1.12
KQCode的两个of方法支持infix调用，例如 KQCode of "\[CQ:at,qq=123456]"
KQCodeUtils增加`remove`方法和`removeByType`方法, 用来移除文本中的CQ码。
顺手更新一下核心


# 1.1-1.11
`KQCodeUtils` 追加方法`String[] split(String text)`、`String getCq(String text, int index = 0)`、`String[] getCqs(String text)`等若干方法
`KQCode` 追加静态方法`KQCode of(String text)`
# 1.0-1.11
初版