"""
大气存储文件(.atmdat)解析器
代码由Claude AI生成
"""

import struct
import gzip
import zlib
import json
import os
import sys
from typing import Optional, Dict, Any, List, Tuple
from io import BytesIO


class NBTReader:
    """简单的NBT读取器，用于解析Minecraft NBT格式数据"""

    TAG_END = 0
    TAG_BYTE = 1
    TAG_SHORT = 2
    TAG_INT = 3
    TAG_LONG = 4
    TAG_FLOAT = 5
    TAG_DOUBLE = 6
    TAG_BYTE_ARRAY = 7
    TAG_STRING = 8
    TAG_LIST = 9
    TAG_COMPOUND = 10
    TAG_INT_ARRAY = 11
    TAG_LONG_ARRAY = 12

    def __init__(self, data: bytes):
        self.data = BytesIO(data)

    def read_byte(self) -> int:
        return struct.unpack('>b', self.data.read(1))[0]

    def read_short(self) -> int:
        return struct.unpack('>h', self.data.read(2))[0]

    def read_int(self) -> int:
        return struct.unpack('>i', self.data.read(4))[0]

    def read_long(self) -> int:
        return struct.unpack('>q', self.data.read(8))[0]

    def read_float(self) -> float:
        return struct.unpack('>f', self.data.read(4))[0]

    def read_double(self) -> float:
        return struct.unpack('>d', self.data.read(8))[0]

    def read_string(self) -> str:
        length = self.read_short()
        if length < 0:
            return ""
        return self.data.read(length).decode('utf-8')

    def read_byte_array(self) -> List[int]:
        length = self.read_int()
        return [self.read_byte() for _ in range(length)]

    def read_int_array(self) -> List[int]:
        length = self.read_int()
        return [self.read_int() for _ in range(length)]

    def read_long_array(self) -> List[int]:
        length = self.read_int()
        return [self.read_long() for _ in range(length)]

    def read_tag_name(self) -> str:
        """读取标签名称"""
        return self.read_string()

    def read_tag_value(self, tag_type: int) -> Any:
        """根据标签类型读取值"""
        if tag_type == self.TAG_END:
            return None
        elif tag_type == self.TAG_BYTE:
            return self.read_byte()
        elif tag_type == self.TAG_SHORT:
            return self.read_short()
        elif tag_type == self.TAG_INT:
            return self.read_int()
        elif tag_type == self.TAG_LONG:
            return self.read_long()
        elif tag_type == self.TAG_FLOAT:
            return self.read_float()
        elif tag_type == self.TAG_DOUBLE:
            return self.read_double()
        elif tag_type == self.TAG_BYTE_ARRAY:
            return self.read_byte_array()
        elif tag_type == self.TAG_STRING:
            return self.read_string()
        elif tag_type == self.TAG_LIST:
            return self.read_list()
        elif tag_type == self.TAG_COMPOUND:
            return self.read_compound()
        elif tag_type == self.TAG_INT_ARRAY:
            return self.read_int_array()
        elif tag_type == self.TAG_LONG_ARRAY:
            return self.read_long_array()
        else:
            raise ValueError(f"未知的NBT标签类型: {tag_type}")

    def read_list(self) -> List[Any]:
        """读取列表标签"""
        tag_type = self.read_byte()
        length = self.read_int()
        return [self.read_tag_value(tag_type) for _ in range(length)]

    def read_compound(self) -> Dict[str, Any]:
        """读取复合标签"""
        compound = {}
        while True:
            tag_type = self.read_byte()
            if tag_type == self.TAG_END:
                break
            name = self.read_tag_name()
            value = self.read_tag_value(tag_type)
            compound[name] = value
        return compound

    def read_nbt(self) -> Tuple[str, Any]:
        """读取完整的NBT结构"""
        tag_type = self.read_byte()
        if tag_type == self.TAG_END:
            return "", None
        name = self.read_tag_name()
        value = self.read_tag_value(tag_type)
        return name, value


class AtmosphereRegionFileReader:
    """大气区域文件读取器"""

    def __init__(self, filename: str):
        self.filename = filename
        self.STORAGE_ATMOSPHERES_COUNT_LOG = 7
        self.TOTAL_ATMOSPHERES = 1 << (self.STORAGE_ATMOSPHERES_COUNT_LOG * 2)  # 128x128
        self.ATMOSPHERES_PER_ROW = 1 << self.STORAGE_ATMOSPHERES_COUNT_LOG  # 128
        self.HEAD_SECTORS = 1 << ((self.STORAGE_ATMOSPHERES_COUNT_LOG - 5) * 2 + 1)  # 头部扇区数
        self.TIME_SECTORS_BEGIN = 1 << ((self.STORAGE_ATMOSPHERES_COUNT_LOG - 5) * 2)  # 时间戳开始扇区

        self.offsets = [0] * self.TOTAL_ATMOSPHERES
        self.timestamps = [0] * self.TOTAL_ATMOSPHERES

        print(f"文件格式信息:")
        print(f"  总大气数量: {self.TOTAL_ATMOSPHERES}")
        print(f"  每行大气数: {self.ATMOSPHERES_PER_ROW}")
        print(f"  头部扇区数: {self.HEAD_SECTORS}")
        print(f"  时间戳开始扇区: {self.TIME_SECTORS_BEGIN}")

        self._load_file()

    def _load_file(self):
        """加载文件并读取头部信息"""
        with open(self.filename, 'rb') as f:
            # 读取偏移量表
            for i in range(self.TOTAL_ATMOSPHERES):
                offset_data = f.read(4)
                if len(offset_data) < 4:
                    break
                self.offsets[i] = struct.unpack('>I', offset_data)[0]

            # 读取时间戳表
            for i in range(self.TOTAL_ATMOSPHERES):
                timestamp_data = f.read(4)
                if len(timestamp_data) < 4:
                    break
                self.timestamps[i] = struct.unpack('>I', timestamp_data)[0]

    def _get_index(self, x: int, z: int) -> int:
        """根据坐标计算索引"""
        return x + z * self.ATMOSPHERES_PER_ROW

    def _out_of_bounds(self, x: int, z: int) -> bool:
        """检查坐标是否越界"""
        return x < 0 or x >= self.ATMOSPHERES_PER_ROW or z < 0 or z >= self.ATMOSPHERES_PER_ROW

    def atmosphere_exists(self, x: int, z: int) -> bool:
        """检查指定位置是否存在大气数据"""
        if self._out_of_bounds(x, z):
            return False
        return self.offsets[self._get_index(x, z)] != 0

    def get_atmosphere_data(self, x: int, z: int) -> Optional[bytes]:
        """获取指定位置的原始大气数据"""
        if self._out_of_bounds(x, z):
            return None

        index = self._get_index(x, z)
        offset = self.offsets[index]

        if offset == 0:
            return None

        sector_begin = offset >> 8
        sector_count = offset & 0xFF

        with open(self.filename, 'rb') as f:
            f.seek(sector_begin * 4096)
            data_length = struct.unpack('>I', f.read(4))[0]

            if data_length <= 0 or data_length > 4096 * sector_count:
                print(f"警告: 坐标({x}, {z})的数据长度无效: {data_length}")
                return None

            compression_type = struct.unpack('>B', f.read(1))[0]
            compressed_data = f.read(data_length - 1)

            try:
                if compression_type == 1:  # GZIP
                    return gzip.decompress(compressed_data)
                elif compression_type == 2:  # Deflate
                    return zlib.decompress(compressed_data)
                else:
                    print(f"未知的压缩类型: {compression_type}")
                    return None
            except Exception as e:
                print(f"解压缩失败: {e}")
                return None

    def parse_atmosphere_nbt(self, x: int, z: int) -> Optional[Dict[str, Any]]:
        """解析指定位置的NBT大气数据"""
        raw_data = self.get_atmosphere_data(x, z)
        if raw_data is None:
            return None

        try:
            nbt_reader = NBTReader(raw_data)
            name, value = nbt_reader.read_nbt()
            return {
                "root_name": name,
                "data": value,
                "timestamp": self.timestamps[self._get_index(x, z)]
            }
        except Exception as e:
            print(f"NBT解析失败 ({x}, {z}): {e}")
            return None

    def list_all_atmospheres(self) -> List[Tuple[int, int]]:
        """列出所有存在的大气坐标"""
        atmospheres = []
        for z in range(self.ATMOSPHERES_PER_ROW):
            for x in range(self.ATMOSPHERES_PER_ROW):
                if self.atmosphere_exists(x, z):
                    atmospheres.append((x, z))
        return atmospheres

    def export_atmosphere(self, x: int, z: int, output_file: Optional[str] = None) -> bool:
        """导出指定位置的大气数据为JSON格式"""
        nbt_data = self.parse_atmosphere_nbt(x, z)
        if nbt_data is None:
            print(f"坐标({x}, {z})没有大气数据")
            return False

        if output_file is None:
            output_file = f"atmosphere_{x}_{z}.json"

        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(nbt_data, f, indent=2, ensure_ascii=False)
            print(f"大气数据已导出到: {output_file}")
            return True
        except Exception as e:
            print(f"导出失败: {e}")
            return False

    def export_all_atmospheres(self, output_dir: str = "atmospheres") -> int:
        """导出所有大气数据"""
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)

        atmospheres = self.list_all_atmospheres()
        exported_count = 0

        for x, z in atmospheres:
            output_file = os.path.join(output_dir, f"atmosphere_{x}_{z}.json")
            if self.export_atmosphere(x, z, output_file):
                exported_count += 1

        print(f"成功导出 {exported_count}/{len(atmospheres)} 个大气数据")
        return exported_count

    def print_file_info(self):
        """打印文件基本信息"""
        atmospheres = self.list_all_atmospheres()
        print(f"\n文件: {self.filename}")
        print(f"总共发现 {len(atmospheres)} 个大气数据")

        if atmospheres:
            print("\n存在的大气坐标:")
            for i, (x, z) in enumerate(atmospheres):
                timestamp = self.timestamps[self._get_index(x, z)]
                print(f"  [{i+1:3d}] 坐标: ({x:3d}, {z:3d}), 时间戳: {timestamp}")
                if i >= 19:  # 只显示前20个
                    print(f"  ... 还有 {len(atmospheres) - 20} 个")
                    break


def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("用法: python atmosphere_parser.py <atmdat文件路径> [选项]")
        print("选项:")
        print("  --info                只显示文件信息")
        print("  --export-all [目录]   导出所有大气数据到指定目录(默认: atmospheres)")
        print("  --export x z [文件]   导出指定坐标的大气数据")
        print("  --list               列出所有大气坐标")
        return

    atmdat_file = sys.argv[1]

    if not os.path.exists(atmdat_file):
        print(f"错误: 文件 '{atmdat_file}' 不存在")
        return

    try:
        reader = AtmosphereRegionFileReader(atmdat_file)

        # 解析命令行参数
        if len(sys.argv) == 2 or "--info" in sys.argv:
            reader.print_file_info()

        elif "--list" in sys.argv:
            atmospheres = reader.list_all_atmospheres()
            print(f"找到 {len(atmospheres)} 个大气数据:")
            for x, z in atmospheres:
                timestamp = reader.timestamps[reader._get_index(x, z)]
                print(f"  坐标: ({x}, {z}), 时间戳: {timestamp}")

        elif "--export-all" in sys.argv:
            index = sys.argv.index("--export-all")
            output_dir = sys.argv[index + 1] if index + 1 < len(sys.argv) else "atmospheres"
            reader.export_all_atmospheres(output_dir)

        elif "--export" in sys.argv:
            index = sys.argv.index("--export")
            if index + 2 >= len(sys.argv):
                print("错误: --export 需要 x 和 z 坐标参数")
                return

            try:
                x = int(sys.argv[index + 1])
                z = int(sys.argv[index + 2])
                output_file = sys.argv[index + 3] if index + 3 < len(sys.argv) else None

                if not reader.atmosphere_exists(x, z):
                    print(f"坐标({x}, {z})没有大气数据")
                    return

                reader.export_atmosphere(x, z, output_file)

                # 同时在控制台显示数据
                nbt_data = reader.parse_atmosphere_nbt(x, z)
                if nbt_data:
                    print(f"\n坐标({x}, {z})的大气数据:")
                    print(json.dumps(nbt_data, indent=2, ensure_ascii=False))

            except ValueError:
                print("错误: x 和 z 必须是整数")
                return

        else:
            # 默认行为: 显示文件信息并导出所有数据
            reader.print_file_info()
            print("\n是否要导出所有大气数据? (y/n): ", end="")
            choice = input().lower().strip()
            if choice in ['y', 'yes', '是']:
                reader.export_all_atmospheres()

    except Exception as e:
        print(f"处理文件时发生错误: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    main()