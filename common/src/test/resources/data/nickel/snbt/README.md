# 测试：镍-SNBT

本目录存放 NickelAPI SNBT 子系统的单元测试数据，按测试主题分子目录，每个子目录一份 README 说明数据格式与约定。

## SNBT 解析数据集（`SNBTReader` / `SNBTScanner`）

以下三个子目录共享同一套用例结构，服务于 SNBT 读取器与扫描器：

- `Read/` —— 合法输入，期望还原出指定的 NBT 结构；
- `ReadError/` —— 非法输入，期望解析抛出异常；
- `Scan/` —— 语法扫描的三态结果（正常 / EOF 信号 / 语法错误）。

## 其他数据集

以下子目录测试各自的主题，用例结构与上面三个不同，格式见各自的 README：

- `matcher/` —— NBT 匹配器与 SNBT 联动的全流程匹配；
- `函数调用/` —— SNBT 函数在表达式中的调用与求值；
- `nbtpath/` —— NBTPath 路径语法的解析与读扫一致性。

## 解析数据集共同约定

本节约定只适用于 `Read/`、`ReadError/`、`Scan/` 三个数据集。

- 数据文件是 UTF-8 编码（无 BOM）的 YAML 文件（`.yaml` 后缀），每个文件是一个用例列表；
- 每个用例至少含两个键：`name`（用例名，中文）与 `input`（SNBT 输入原文）；
- `input` 一律用 YAML 单引号书写，输入里的反斜杠转义原样交给解析器处理；
- 可选键 `mode`：取 `single` 时经 `readSingleNBTFromInput` / `scanSingleNBTFromInput` 入口解析，该入口额外要求复合标签之后是空白或输入结尾；缺省时经 `readNBTFromInput` / `scanNBTFromInput` 入口；
- 期望 NBT 结构（`expected`）用"类型标注节点"描述，格式见 `Read/README.md`；
- 若某个测试有与本节冲突的约定，以该测试自己的约定为准。
