"""
大气存储文件(.atmdat) GUI查看器
代码由Claude AI生成
"""

import tkinter as tk
from tkinter import ttk, filedialog, messagebox, scrolledtext
import struct
import gzip
import zlib
import json
import os
import threading
from typing import Optional, Dict, Any, List, Tuple
from io import BytesIO
import datetime


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

    TAG_NAMES = {
        0: "TAG_End", 1: "TAG_Byte", 2: "TAG_Short", 3: "TAG_Int",
        4: "TAG_Long", 5: "TAG_Float", 6: "TAG_Double", 7: "TAG_Byte_Array",
        8: "TAG_String", 9: "TAG_List", 10: "TAG_Compound", 11: "TAG_Int_Array",
        12: "TAG_Long_Array"
    }

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
        return self.read_string()

    def read_tag_value(self, tag_type: int) -> Any:
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

    def read_list(self) -> Tuple[int, List[Any]]:
        tag_type = self.read_byte()
        length = self.read_int()
        return tag_type, [self.read_tag_value(tag_type) for _ in range(length)]

    def read_compound(self) -> Dict[str, Tuple[int, Any]]:
        compound = {}
        while True:
            tag_type = self.read_byte()
            if tag_type == self.TAG_END:
                break
            name = self.read_tag_name()
            value = self.read_tag_value(tag_type)
            compound[name] = (tag_type, value)
        return compound

    def read_nbt(self) -> Tuple[str, int, Any]:
        tag_type = self.read_byte()
        if tag_type == self.TAG_END:
            return "", tag_type, None
        name = self.read_tag_name()
        value = self.read_tag_value(tag_type)
        return name, tag_type, value


class AtmosphereRegionFileReader:
    """大气区域文件读取器"""

    def __init__(self, filename: str):
        self.filename = filename
        self.STORAGE_ATMOSPHERES_COUNT_LOG = 7
        self.TOTAL_ATMOSPHERES = 1 << (self.STORAGE_ATMOSPHERES_COUNT_LOG * 2)
        self.ATMOSPHERES_PER_ROW = 1 << self.STORAGE_ATMOSPHERES_COUNT_LOG
        self.HEAD_SECTORS = 1 << ((self.STORAGE_ATMOSPHERES_COUNT_LOG - 5) * 2 + 1)
        self.TIME_SECTORS_BEGIN = 1 << ((self.STORAGE_ATMOSPHERES_COUNT_LOG - 5) * 2)

        self.offsets = [0] * self.TOTAL_ATMOSPHERES
        self.timestamps = [0] * self.TOTAL_ATMOSPHERES
        self._load_file()

    def _load_file(self):
        with open(self.filename, 'rb') as f:
            for i in range(self.TOTAL_ATMOSPHERES):
                offset_data = f.read(4)
                if len(offset_data) < 4:
                    break
                self.offsets[i] = struct.unpack('>I', offset_data)[0]

            for i in range(self.TOTAL_ATMOSPHERES):
                timestamp_data = f.read(4)
                if len(timestamp_data) < 4:
                    break
                self.timestamps[i] = struct.unpack('>I', timestamp_data)[0]

    def _get_index(self, x: int, z: int) -> int:
        return x + z * self.ATMOSPHERES_PER_ROW

    def _out_of_bounds(self, x: int, z: int) -> bool:
        return x < 0 or x >= self.ATMOSPHERES_PER_ROW or z < 0 or z >= self.ATMOSPHERES_PER_ROW

    def atmosphere_exists(self, x: int, z: int) -> bool:
        if self._out_of_bounds(x, z):
            return False
        return self.offsets[self._get_index(x, z)] != 0

    def get_atmosphere_data(self, x: int, z: int) -> Optional[bytes]:
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
                return None

            compression_type = struct.unpack('>B', f.read(1))[0]
            compressed_data = f.read(data_length - 1)

            try:
                if compression_type == 1:
                    return gzip.decompress(compressed_data)
                elif compression_type == 2:
                    return zlib.decompress(compressed_data)
                else:
                    return None
            except:
                return None

    def parse_atmosphere_nbt(self, x: int, z: int) -> Optional[Tuple[str, int, Any, int]]:
        raw_data = self.get_atmosphere_data(x, z)
        if raw_data is None:
            return None

        try:
            nbt_reader = NBTReader(raw_data)
            name, tag_type, value = nbt_reader.read_nbt()
            timestamp = self.timestamps[self._get_index(x, z)]
            return name, tag_type, value, timestamp
        except Exception as e:
            return None

    def list_all_atmospheres(self) -> List[Tuple[int, int]]:
        atmospheres = []
        for z in range(self.ATMOSPHERES_PER_ROW):
            for x in range(self.ATMOSPHERES_PER_ROW):
                if self.atmosphere_exists(x, z):
                    atmospheres.append((x, z))
        return atmospheres


class AtmosphereGUI:
    """大气存储文件GUI查看器"""

    def __init__(self, root):
        self.root = root
        self.root.title("大气存储文件查看器")
        self.root.geometry("1200x800")

        self.reader = None
        self.current_file = None

        self.setup_ui()

    def setup_ui(self):
        """设置用户界面"""
        # 菜单栏
        menubar = tk.Menu(self.root)
        self.root.config(menu=menubar)

        file_menu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="文件", menu=file_menu)
        file_menu.add_command(label="打开", command=self.open_file)
        file_menu.add_separator()
        file_menu.add_command(label="导出当前", command=self.export_current)
        file_menu.add_command(label="导出所有", command=self.export_all)
        file_menu.add_separator()
        file_menu.add_command(label="退出", command=self.root.quit)

        # 主框架
        main_frame = ttk.Frame(self.root)
        main_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # 工具栏
        toolbar_frame = ttk.Frame(main_frame)
        toolbar_frame.pack(fill=tk.X, pady=(0, 5))

        ttk.Button(toolbar_frame, text="打开文件", command=self.open_file).pack(side=tk.LEFT, padx=(0, 5))
        ttk.Button(toolbar_frame, text="刷新", command=self.refresh_tree).pack(side=tk.LEFT, padx=(0, 5))

        # 文件信息标签
        self.info_label = ttk.Label(toolbar_frame, text="未打开文件", foreground="gray")
        self.info_label.pack(side=tk.LEFT, padx=(10, 0))

        # 分割面板
        paned = ttk.PanedWindow(main_frame, orient=tk.HORIZONTAL)
        paned.pack(fill=tk.BOTH, expand=True)

        # 左侧面板 - 文件树
        left_frame = ttk.Frame(paned)
        paned.add(left_frame, weight=1)

        ttk.Label(left_frame, text="大气存储结构", font=("Arial", 12, "bold")).pack(pady=(0, 5))

        # 树形控件
        tree_frame = ttk.Frame(left_frame)
        tree_frame.pack(fill=tk.BOTH, expand=True)

        self.tree = ttk.Treeview(tree_frame)
        self.tree.heading("#0", text="结构树", anchor="w")

        tree_scroll = ttk.Scrollbar(tree_frame, orient=tk.VERTICAL, command=self.tree.yview)
        self.tree.configure(yscrollcommand=tree_scroll.set)

        self.tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        tree_scroll.pack(side=tk.RIGHT, fill=tk.Y)

        # 绑定选择事件
        self.tree.bind("<<TreeviewSelect>>", self.on_tree_select)

        # 右侧面板 - 详细信息
        right_frame = ttk.Frame(paned)
        paned.add(right_frame, weight=1)

        ttk.Label(right_frame, text="详细信息", font=("Arial", 12, "bold")).pack(pady=(0, 5))

        # 信息显示区域
        self.detail_text = scrolledtext.ScrolledText(right_frame, wrap=tk.WORD, font=("Consolas", 10))
        self.detail_text.pack(fill=tk.BOTH, expand=True)

        # 状态栏
        self.status_bar = ttk.Label(self.root, text="就绪", relief=tk.SUNKEN)
        self.status_bar.pack(side=tk.BOTTOM, fill=tk.X)

    def open_file(self):
        """打开文件对话框"""
        filename = filedialog.askopenfilename(
            title="选择大气存储文件",
            filetypes=[("大气数据文件", "*.atmdat"), ("所有文件", "*.*")]
        )

        if filename:
            self.load_file(filename)

    def load_file(self, filename):
        """加载文件"""
        try:
            self.status_bar.config(text="正在加载文件...")
            self.root.update()

            self.reader = AtmosphereRegionFileReader(filename)
            self.current_file = filename

            # 更新信息标签
            file_size = os.path.getsize(filename) / 1024  # KB
            atmospheres = self.reader.list_all_atmospheres()
            self.info_label.config(
                text=f"文件: {os.path.basename(filename)} | 大小: {file_size:.1f}KB | 大气数: {len(atmospheres)}",
                foreground="black"
            )

            self.refresh_tree()
            self.status_bar.config(text=f"已加载文件: {os.path.basename(filename)}")

        except Exception as e:
            messagebox.showerror("错误", f"加载文件失败: {str(e)}")
            self.status_bar.config(text="加载失败")

    def refresh_tree(self):
        """刷新树形结构"""
        if not self.reader:
            return

        # 清空树
        for item in self.tree.get_children():
            self.tree.delete(item)

        # 添加根节点
        root_node = self.tree.insert("", "end", text=f"📁 {os.path.basename(self.current_file)}",
                                     values=("file",), tags=("file",))

        # 获取所有大气数据
        atmospheres = self.reader.list_all_atmospheres()

        if not atmospheres:
            self.tree.insert(root_node, "end", text="📄 (无大气数据)", values=("empty",), tags=("empty",))
            return

        # 按区域分组显示
        regions = {}
        for x, z in atmospheres:
            region_x = x // 16
            region_z = z // 16
            region_key = (region_x, region_z)
            if region_key not in regions:
                regions[region_key] = []
            regions[region_key].append((x, z))

        # 添加区域节点
        for (region_x, region_z), coords in regions.items():
            region_text = f"🗺️ 区域 ({region_x}, {region_z}) - {len(coords)} 个大气"
            region_node = self.tree.insert(root_node, "end", text=region_text,
                                           values=("region", region_x, region_z), tags=("region",))

            # 添加大气节点
            for x, z in sorted(coords):
                timestamp = self.reader.timestamps[self.reader._get_index(x, z)]
                time_str = datetime.datetime.fromtimestamp(timestamp).strftime("%Y-%m-%d %H:%M:%S") if timestamp > 0 else "未知"

                atm_text = f"🌤️ 大气 ({x}, {z}) - {time_str}"
                self.tree.insert(region_node, "end", text=atm_text,
                                 values=("atmosphere", x, z), tags=("atmosphere",))

        # 展开根节点
        self.tree.item(root_node, open=True)

    def on_tree_select(self, event):
        """树形控件选择事件"""
        selection = self.tree.selection()
        if not selection:
            return

        item = selection[0]
        values = self.tree.item(item, "values")

        if not values:
            return

        item_type = values[0]

        if item_type == "file":
            self.show_file_info()
        elif item_type == "region":
            region_x, region_z = int(values[1]), int(values[2])
            self.show_region_info(region_x, region_z)
        elif item_type == "atmosphere":
            x, z = int(values[1]), int(values[2])
            self.show_atmosphere_info(x, z)
        else:
            self.detail_text.delete(1.0, tk.END)
            self.detail_text.insert(tk.END, "选择一个项目查看详细信息")

    def show_file_info(self):
        """显示文件信息"""
        if not self.reader:
            return

        atmospheres = self.reader.list_all_atmospheres()
        file_size = os.path.getsize(self.current_file)

        info = f"""文件信息
{'='*50}
文件路径: {self.current_file}
文件大小: {file_size:,} 字节 ({file_size/1024:.2f} KB)

存储格式信息:
- 每行大气数量: {self.reader.ATMOSPHERES_PER_ROW}
- 总大气容量: {self.reader.TOTAL_ATMOSPHERES:,}
- 头部扇区数: {self.reader.HEAD_SECTORS}
- 时间戳扇区起始: {self.reader.TIME_SECTORS_BEGIN}

实际数据:
- 已存储大气数: {len(atmospheres)}
- 存储利用率: {len(atmospheres)/self.reader.TOTAL_ATMOSPHERES*100:.2f}%

坐标范围:
- X: 0 ~ {self.reader.ATMOSPHERES_PER_ROW-1}
- Z: 0 ~ {self.reader.ATMOSPHERES_PER_ROW-1}
"""

        self.detail_text.delete(1.0, tk.END)
        self.detail_text.insert(tk.END, info)

    def show_region_info(self, region_x, region_z):
        """显示区域信息"""
        if not self.reader:
            return

        atmospheres = self.reader.list_all_atmospheres()
        region_atmospheres = [(x, z) for x, z in atmospheres
                              if x // 16 == region_x and z // 16 == region_z]

        info = f"""区域信息
{'='*50}
区域坐标: ({region_x}, {region_z})
覆盖范围: X({region_x*16}~{region_x*16+15}), Z({region_z*16}~{region_z*16+15})
大气数量: {len(region_atmospheres)}

包含的大气坐标:
"""

        for i, (x, z) in enumerate(sorted(region_atmospheres)):
            timestamp = self.reader.timestamps[self.reader._get_index(x, z)]
            time_str = datetime.datetime.fromtimestamp(timestamp).strftime("%Y-%m-%d %H:%M:%S") if timestamp > 0 else "未知"
            info += f"  {i+1:2d}. ({x:3d}, {z:3d}) - {time_str}\n"

        self.detail_text.delete(1.0, tk.END)
        self.detail_text.insert(tk.END, info)

    def show_atmosphere_info(self, x, z):
        """显示大气详细信息"""
        if not self.reader:
            return

        self.status_bar.config(text=f"正在解析大气数据 ({x}, {z})...")
        self.root.update()

        try:
            result = self.reader.parse_atmosphere_nbt(x, z)
            if result is None:
                self.detail_text.delete(1.0, tk.END)
                self.detail_text.insert(tk.END, f"无法读取坐标 ({x}, {z}) 的大气数据")
                return

            name, tag_type, value, timestamp = result
            time_str = datetime.datetime.fromtimestamp(timestamp).strftime("%Y-%m-%d %H:%M:%S") if timestamp > 0 else "未知"

            info = f"""大气数据详情
{'='*50}
坐标: ({x}, {z})
时间戳: {timestamp} ({time_str})
根标签名: {name}
根标签类型: {NBTReader.TAG_NAMES.get(tag_type, f"未知({tag_type})")}

NBT结构:
{'='*50}
"""

            # 显示NBT结构树
            self.detail_text.delete(1.0, tk.END)
            self.detail_text.insert(tk.END, info)
            self.format_nbt_value(value, tag_type, 0)

        except Exception as e:
            self.detail_text.delete(1.0, tk.END)
            self.detail_text.insert(tk.END, f"解析大气数据时出错: {str(e)}")

        self.status_bar.config(text="就绪")

    def format_nbt_value(self, value, tag_type, indent_level):
        """格式化NBT值为树状结构"""
        indent = "  " * indent_level

        if tag_type == NBTReader.TAG_COMPOUND:
            for key, (child_type, child_value) in value.items():
                type_name = NBTReader.TAG_NAMES.get(child_type, f"未知({child_type})")
                self.detail_text.insert(tk.END, f"{indent}📋 {key} [{type_name}]")

                if child_type in [NBTReader.TAG_COMPOUND, NBTReader.TAG_LIST]:
                    self.detail_text.insert(tk.END, "\n")
                    self.format_nbt_value(child_value, child_type, indent_level + 1)
                else:
                    formatted_value = self.format_simple_value(child_value, child_type)
                    self.detail_text.insert(tk.END, f": {formatted_value}\n")

        elif tag_type == NBTReader.TAG_LIST:
            list_type, items = value
            type_name = NBTReader.TAG_NAMES.get(list_type, f"未知({list_type})")
            self.detail_text.insert(tk.END, f"{indent}📝 列表 [{type_name}] ({len(items)} 项)\n")

            for i, item in enumerate(items):
                self.detail_text.insert(tk.END, f"{indent}  [{i}]")
                if list_type in [NBTReader.TAG_COMPOUND, NBTReader.TAG_LIST]:
                    self.detail_text.insert(tk.END, "\n")
                    self.format_nbt_value(item, list_type, indent_level + 2)
                else:
                    formatted_value = self.format_simple_value(item, list_type)
                    self.detail_text.insert(tk.END, f": {formatted_value}\n")

    def format_simple_value(self, value, tag_type):
        """格式化简单值"""
        if tag_type == NBTReader.TAG_STRING:
            return f'"{value}"'
        elif tag_type in [NBTReader.TAG_BYTE_ARRAY, NBTReader.TAG_INT_ARRAY, NBTReader.TAG_LONG_ARRAY]:
            if len(value) > 10:
                return f"[{len(value)} 项] {value[:5]}...{value[-2:]}"
            return str(value)
        elif tag_type == NBTReader.TAG_FLOAT:
            return f"{value:.6f}f"
        elif tag_type == NBTReader.TAG_DOUBLE:
            return f"{value:.6f}d"
        elif tag_type == NBTReader.TAG_LONG:
            return f"{value}L"
        elif tag_type == NBTReader.TAG_BYTE:
            return f"{value}b"
        elif tag_type == NBTReader.TAG_SHORT:
            return f"{value}s"
        else:
            return str(value)

    def export_current(self):
        """导出当前选中的大气数据"""
        selection = self.tree.selection()
        if not selection:
            messagebox.showwarning("警告", "请先选择一个大气数据")
            return

        item = selection[0]
        values = self.tree.item(item, "values")

        if not values or values[0] != "atmosphere":
            messagebox.showwarning("警告", "请选择一个大气数据")
            return

        x, z = int(values[1]), int(values[2])

        filename = filedialog.asksaveasfilename(
            title="保存大气数据",
            defaultextension=".json",
            filetypes=[("JSON文件", "*.json"), ("所有文件", "*.*")],
            initialvalue=f"atmosphere_{x}_{z}.json"
        )

        if filename:
            try:
                result = self.reader.parse_atmosphere_nbt(x, z)
                if result:
                    name, tag_type, value, timestamp = result
                    data = {
                        "coordinates": [x, z],
                        "timestamp": timestamp,
                        "root_name": name,
                        "root_type": NBTReader.TAG_NAMES.get(tag_type, f"未知({tag_type})"),
                        "data": self.convert_nbt_to_json(value, tag_type)
                    }

                    with open(filename, 'w', encoding='utf-8') as f:
                        json.dump(data, f, indent=2, ensure_ascii=False)

                    messagebox.showinfo("成功", f"数据已导出到: {filename}")
                else:
                    messagebox.showerror("错误", "无法读取大气数据")
            except Exception as e:
                messagebox.showerror("错误", f"导出失败: {str(e)}")

    def export_all(self):
        """导出所有大气数据"""
        if not self.reader:
            messagebox.showwarning("警告", "请先打开一个文件")
            return

        folder = filedialog.askdirectory(title="选择导出目录")
        if not folder:
            return

        atmospheres = self.reader.list_all_atmospheres()
        if not atmospheres:
            messagebox.showinfo("信息", "没有大气数据可以导出")
            return

        # 在新线程中执行导出操作
        threading.Thread(target=self.export_all_worker, args=(folder, atmospheres), daemon=True).start()

    def export_all_worker(self, folder, atmospheres):
        """导出工作线程"""
        try:
            success_count = 0
            total = len(atmospheres)

            for i, (x, z) in enumerate(atmospheres):
                self.root.after(0, lambda i=i, total=total:
                self.status_bar.config(text=f"正在导出 {i+1}/{total}..."))

                try:
                    result = self.reader.parse_atmosphere_nbt(x, z)
                    if result:
                        name, tag_type, value, timestamp = result
                        data = {
                            "coordinates": [x, z],
                            "timestamp": timestamp,
                            "root_name": name,
                            "root_type": NBTReader.TAG_NAMES.get(tag_type, f"未知({tag_type})"),
                            "data": self.convert_nbt_to_json(value, tag_type)
                        }

                        filename = os.path.join(folder, f"atmosphere_{x}_{z}.json")
                        with open(filename, 'w', encoding='utf-8') as f:
                            json.dump(data, f, indent=2, ensure_ascii=False)
                        success_count += 1
                except Exception as e:
                    print(f"导出 ({x}, {z}) 失败: {e}")

            self.root.after(0, lambda: self.status_bar.config(text="导出完成"))
            self.root.after(0, lambda: messagebox.showinfo("完成",
                                                           f"导出完成!\n成功: {success_count}/{total}\n目录: {folder}"))

        except Exception as e:
            self.root.after(0, lambda: messagebox.showerror("错误", f"导出过程中出错: {str(e)}"))
            self.root.after(0, lambda: self.status_bar.config(text="导出失败"))

    def convert_nbt_to_json(self, value, tag_type):
        """将NBT数据转换为JSON兼容格式"""
        if tag_type == NBTReader.TAG_COMPOUND:
            result = {}
            for key, (child_type, child_value) in value.items():
                result[key] = {
                    "type": NBTReader.TAG_NAMES.get(child_type, f"未知({child_type})"),
                    "value": self.convert_nbt_to_json(child_value, child_type)
                }
            return result
        elif tag_type == NBTReader.TAG_LIST:
            list_type, items = value
            return {
                "type": f"List[{NBTReader.TAG_NAMES.get(list_type, f'未知({list_type})')}]",
                "items": [self.convert_nbt_to_json(item, list_type) for item in items]
            }
        elif tag_type in [NBTReader.TAG_BYTE_ARRAY, NBTReader.TAG_INT_ARRAY, NBTReader.TAG_LONG_ARRAY]:
            return list(value)  # 转换为普通列表
        else:
            return value  # 基本类型直接返回


def main():
    """主函数"""
    root = tk.Tk()

    # 设置图标和样式
    try:
        root.state('zoomed')  # Windows上最大化
    except:
        root.attributes('-zoomed', True)  # Linux上最大化

    # 设置窗口最小尺寸
    root.minsize(800, 600)

    # 设置主题
    style = ttk.Style()
    try:
        style.theme_use('clam')  # 使用现代主题
    except:
        pass

    # 创建应用
    app = AtmosphereGUI(root)

    # 如果命令行提供了文件参数，自动加载
    import sys
    if len(sys.argv) > 1 and os.path.exists(sys.argv[1]):
        app.load_file(sys.argv[1])

    # 运行主循环
    root.mainloop()


if __name__ == "__main__":
    main()