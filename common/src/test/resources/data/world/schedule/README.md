# 测试：方块计划刻调度器

本目录存放方块计划刻调度器的单元测试数据，按测试主题组织。当前包含区块数据的（反）序列化数据集（专项约定见 `README_（反）序列化.md`），后续会加入其他主题的测试数据。

被测的两个区块数据实现是：

- 打包实现 `PackedBlockTickDatum`：一条计划刻压进一个 `long`，区块数据以 `queue` 长整型数组保存；
- 装箱实现 `BoxedBlockTickDatum`：一条计划刻是一个 `IScheduledTick` 对象，区块数据以 `entries` 复合标签列表保存。

## 公共约定

- 全部文件为 UTF-8 编码。AI 生成的样例在文件开头带一行 `# Claude Generated`；
- `.snbt` 文件读取时逐行截去 `#` 起的注释，随后各行直接拼接（不补换行），故单个 SNBT 词法单元不可跨行书写。`.yaml` 文件原样交给 YAML 解析器，`#` 是 YAML 自带的注释语法；
- 数字字面量可用现代 SNBT 语法：`u` / `uL` 无符号后缀、`_` 数字分隔符（按每四位分组，对齐中文数级）；
- 若有测试存在与上面冲突的约定，以该测试本身约定为主。

## SNBT 辅助函数

`.snbt` 文件里可以调用 `可测试的计划刻` 用 `@SNBTFunction` 注册的三个函数，免去手写位段。这些函数由 `TestBlockTickDatum` 在 `beforeAll` 阶段注册，继承该基类的测试都可使用。

| 函数 | 签名 | 产出 |
|---|---|---|
| `blockOf` | `blockOf(词块:String)` | 该方块在注册表中的数字 ID（`Int`） |
| `tick` | `tick(delay:Int, 级:Byte, pos:[I;x,y,z], 词块:String)` | V2 打包格式的一个 `Long` 值 |
| `packPosAndPri` | `packPosAndPri(级:Byte, pos:[I;x,y,z])` | V2 装箱格式的 `p` 字段（`Long`） |

- `tick` 的位段是 `delay<<32 | 级<<28 | y<<20 | z<<16 | x<<12 | 方块ID`，校验 `级` 在 0–15、`x` 与 `z` 在 0–15、`y` 在 0–255。`delay` 按无符号读取，超过 `2^31` 的延迟要写 `u` 后缀；
- `packPosAndPri` 的位段是 `级<<40 | x<<36 | z<<32 | y`，校验 `级` 在 0–15、`x` 与 `z` 在 0–15；`y` 占满低 32 位且不作范围校验，因此越界与负数 `y` 都能表达；
- `blockOf` 按 `MockBlocks.BUILDER` 的完整词块映射取 ID，而 YAML 侧的 `块` 字段按 `upon` 指定的空间构造器取方块（缺省 `天圆地方:scheduler`，含基础方块映射与 `猹`）。两侧写不同的词块名会导致方块比较失败。

## data.schema.json

`data.schema.json` 是 `计划刻数据` 对应的 JSON Schema（draft-07），约束 YAML 样例的字段：

- `upon`：字符串，空间构造器 ID，缺省 `天圆地方:scheduler`；
- `time`：整数，缺省 `0`。序列化输入里它是 `baseTime`，即世界总时间；反序列化答案里不参与比较，可省略；
- `ticks`：计划刻列表，每条必填 `x`、`y`、`z`、`块`、`时`，可选 `级`。`时` 是绝对触发刻而非延迟；`级` 取 `计划刻等级` 的十六个枚举名（`危急0` 至 `松弛15`，名字后缀的数字即位段值），缺省 `中上7`。

样例 YAML 首行写 `# yaml-language-server: $schema=../../data.schema.json` 供编辑器实时校验，测试本身不读这一行。

*Claude Generated*
