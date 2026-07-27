# 测试：镍-SNBT 解析-语法扫描

## 测试描述

本数据集验证 SNBT 扫描器（`SNBTScanner.scanNBTFromInput` / `scanSingleNBTFromInput`）的三态行为。

扫描器与读取器语法同构，但只做语法检查、不构造 NBT 对象，服务于补全场景。它有三种结果：

- **正常（ok）**：合法输入完整走通，不抛任何异常；
- **EOF 信号（eof）**：输入在语法上还没写完（截断）时，抛出 `NickelScanEOFSignal.INSTANCE`。这不是错误，而是"光标处输入未完成"的信号；
- **语法错误（error）**：真正的语法错误仍经 `InputReader.panic` 抛出，测试环境下为 `NickelRuntimeException`。

扫描器不做以下检查：数字合法性、列表类型一致性、空键、空值 token、重复键，也不做函数名决议。因此有些输入在读者侧非法、在扫描侧却正常走通，这些用例记录该宽容行为的现状：

- `{a:00.5}`（前导零的非法数字）；
- `{a:[1,"x"]}`（列表类型不一致）；
- `{:1}`（空键）；
- `{a:}`（缺值）；
- `{a:-}`（孤立负号）；
- `{a:1,a:2}`（重复键）；
- `{a:1.5b}`（后缀错配）；
- `{a:nosuchfunc(1)}`（未注册函数）。

带类型前缀数组（`[B;...]` 等）的合法用例不收录在本数据集里，由 `清汩萌.镍.snbt.TestSNBTScanner` 的用例直接覆盖。

## 样例格式

YAML 用例列表，每个用例的键：

- `name`：用例名；
- `input`：SNBT 输入原文（YAML 单引号书写）；
- `mode`：可选，取 `single` 时经 `scanSingleNBTFromInput` 入口；
- `outcome`：期望结果，取 `ok` / `eof` / `error` 三值之一。

## 样例 #1

### 输入 #1

```yaml
- name: 截断于冒号后
  input: '{a:'
  outcome: eof
```

## 说明/提示

### 样例 1 解释：

输入在键 `a` 的冒号之后被截断。扫描器读值时发现输入耗尽，抛出 `NickelScanEOFSignal`，表示"输入未完成"而非语法错误。
