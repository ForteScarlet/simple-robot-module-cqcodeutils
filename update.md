
# now
KQCode的两个of方法支持infix调用，例如 KQCode of "\[CQ:at,qq=123456]"

# 1.1-1.11
`KQCodeUtils` 追加方法`String[] split(String text)`、`String getCq(String text, int index = 0)`、`String[] getCqs(String text)`等若干方法
`KQCode` 追加静态方法`KQCode of(String text)`
# 1.0-1.11
初版