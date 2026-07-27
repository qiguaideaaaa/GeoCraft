# 测试：镍-SNBT-函数调用

## 测试描述

本数据集验证 SNBT 函数（用 `@SNBTFunction` 注册的 `SNBTOperation`）在 SNBT 表达式中的完整调用路径：从字符串经 `SNBTReader` 解析、`SNBTOperations.resolve` 重载决议，到函数求值产出 NBT 标签。

覆盖的内容：

- 内置函数的调用语义：`bool`、`uuid`（单参 / 双参）、`concat`（复合标签 / 字节数组 / 整型数组 / 长整型数组 / 列表 / 字符串六种重载）；
- 函数调用的嵌套：函数结果作为另一函数的实参、作为列表元素、位于深层复合标签内；
- 语法形态：参数与键周围的空白、尾随逗号、空参列表（本数据集只含空参决议失败的用例，成功解析的正例见 `Scan/scan.yaml`）；
- 错误语义：
  - 未定义函数名、参数个数不符、参数类型无重载可匹配——以上是决议失败，报 OPT_UNDEFINED；
  - 函数体抛出 `NickelRuntimeException`——报 OPT_FAILED，如 UUID 格式非法。

以上错误在没有命令分支上下文时，最终都以 `NickelRuntimeException` 的形式抛出。

## 样例格式

数据文件是 YAML（UTF-8），每个文件的根节点含一个 `cases` 列表，列表里每项是一个用例：

- `name`：字符串，用例名，作为 JUnit 参数化报告里的显示名；
- `input`：字符串，含函数调用的 SNBT 复合标签文本，是被测输入；
- `expected`：字符串，可选，不含函数调用的 SNBT 复合标签文本，解析后与 `input` 的解析结果做结构相等比较；
- `error`：布尔值，可选，默认 `false`。取 `true` 时表示解析 `input` 应当抛出 `NickelRuntimeException`，此时不填 `expected`。

各文件按被测内容分组：`bool.yaml`、`uuid.yaml`、`concat.yaml`、`嵌套与语法.yaml`、`错误.yaml`。

## 样例 #1

### 输入 #1

```yaml
cases:
  - name: bool-整数零
    input: '{r:bool(0)}'
    expected: '{r:0b}'
```

## 样例 #2

### 输入 #2

```yaml
cases:
  - name: 错误-uuid非法格式
    input: '{r:uuid("bad")}'
    error: true
```

## 说明/提示

### 样例 1 解释：

`bool` 接受一个 NUMBER（`NBTPrimitive`）参数，`getLong()` 为 0 时返回 `0b`，否则返回 `1b`。输入里的 `0` 解析为 `NBTTagInt`，沿继承链匹配 NUMBER 形参，求值结果是 `0b`。

### 样例 2 解释：

`uuid` 的函数体对非法 UUID 字符串抛出 `NickelRuntimeException`，`SNBTReader.invokeFunction` 捕获后以 OPT_FAILED 语义 panic。

### 其他现状行为

- `bool` 的取整语义随实参类型而异：`NBTTagDouble.getLong()` 走 `Math.floor`（`-0.9d` → `-1` → `1b`），`NBTTagFloat.getLong()` 走强转截断（`-0.9f` → `0` → `0b`）。
- `concat(LIST,LIST)` 的混型拼接期望产出异构列表（非复合值以空键包裹成复合标签），该语义待实现，数据集暂不含混型用例，见 `TestSNBTBuiltinOperations.concatListMixedTypeTest`（@Disabled）。
- 双参 `uuid` 的键可以是空串：`setUniqueId("",...)` 产出裸 `Most` / `Least` 键。
- 重载决议先于函数体执行：`uuid("a","b","c")` 因参数个数不符报 OPT_UNDEFINED，走不到 UUID 格式校验。
- 函数名必须与左括号紧邻：`bool (1)` 里空白结束无引号 token，`bool` 读成无引号字符串，随后在期望闭花括号处读到 `(` 而报错——这是语法错误，不是决议失败。引号字符串不构成函数名（`{a:"bool"(1)}` 报错，见 `ReadError/`）。
- 函数调用只在值位置被识别，键位置的 `bool(1)` 会在期望冒号处报错（见 `ReadError/`）。
