/*
 * Copyright 2026 QGMoe
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2026 QGMoe
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package 清汩萌.天圆地方.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * @author QGMoe
 */
public final class ClassGraphUtils {
    public static final String _一般答案文件扩展名_ = "ans";

    private ClassGraphUtils(){}

    @SuppressWarnings("resource")
    public static void 寻找样例输入(final @Nonnull String $样例目录,
                                    final @Nonnull String $扩展名,
                                    final @Nonnull IOBiConsumer<ScanResult, Resource> $处理器){
        try (final @Nonnull ScanResult scan = new ClassGraph().acceptPaths($样例目录).scan()){
            scan.getResourcesWithExtension($扩展名)
                    .filter(r -> r.getPath().endsWith(".in."+$扩展名))
                    .forEach(r -> {
                        try {
                            $处理器.accept(scan, r);
                        } catch (final @Nonnull Exception e) {
                            Assertions.fail("在处理资源 "+r.getPath() +" 时出现错误",e);
                        }
                    });
        }
    }

    public static void 寻找特定类型文件(final @Nonnull String $样例目录,
                                        final @Nonnull String $扩展名,
                                        final @Nonnull IOBiConsumer<ScanResult, Resource> $处理器){
        try (final @Nonnull ScanResult scan = new ClassGraph().acceptPaths($样例目录).scan()){
            scan.getResourcesWithExtension($扩展名)
                    .forEach(r -> {
                        try {
                            $处理器.accept(scan,r);
                        } catch (final @Nonnull Exception e) {
                            Assertions.fail("在处理资源 "+r.getPath() +" 时出现错误",e);
                        }
                    });
        }
    }

    @Nonnull
    public static Resource 基于样例输入获取答案(final @Nonnull ScanResult $扫描结果, final @Nonnull Resource r){
        return 基于样例输入获取答案(FilenameUtils.getExtension(r.getPath()),$扫描结果,r);
    }

    @Nonnull
    public static Resource 基于样例输入获取一般答案(final @Nonnull ScanResult $扫描结果, final @Nonnull Resource r){
        return 基于样例输入获取答案("",$扫描结果,r);
    }

    @Nonnull
    public static Resource 基于样例输入获取答案(final @Nonnull String $答案扩展名, final @Nonnull ScanResult $扫描结果, final @Nonnull Resource $输入){
        final String $答案二级扩展 = "."+ _一般答案文件扩展名_;
        final String $答案一级扩展 = $答案扩展名.isEmpty()?"":"."+$答案扩展名;
        return 基于资源路径替换得到新资源($扫描结果,$输入,"\\.in\\."+ FilenameUtils.getExtension($输入.getPath()) +"$",$答案二级扩展+$答案一级扩展);
    }

    @Nonnull
    public static Resource 基于样例输入获取指定类型文件(final @Nonnull String $扩展名, final @Nonnull ScanResult $扫描结果, final @Nonnull Resource $输入){
        return 基于资源路径替换得到新资源($扫描结果,$输入,"\\.in\\."+FilenameUtils.getExtension($输入.getPath())+"$",$扩展名.isEmpty()?"":"."+$扩展名);
    }

    @Nonnull
    public static Resource 基于样例文件获取指定类型文件(final @Nonnull String $扩展名, final @Nonnull ScanResult $扫描结果, final @Nonnull Resource $输入){
        return 基于资源路径替换得到新资源($扫描结果,$输入,"\\."+FilenameUtils.getExtension($输入.getPath())+"$",$扩展名.isEmpty()?"":"."+$扩展名);
    }

    @Nonnull
    public static Resource 基于资源路径替换得到新资源(final @Nonnull ScanResult $扫描结果, final @Nonnull Resource $输入, final @Nonnull String regex,final @Nonnull String $替换){
        final String $文件路径 = $输入.getPath().replaceAll(regex, $替换);
        return $扫描结果.getResourcesWithPath($文件路径).get(0);
    }

    @Nonnull
    public static Stream<String> 获取去除YAML风格注释的文本(final @Nonnull Resource resource) throws IOException {
        return 获取去除特定风格注释的文本(resource,"#");
    }

    @Nonnull
    public static Stream<String> 获取去除Java风格注释的文本(final @Nonnull Resource resource) throws IOException {
        return 获取去除特定风格注释的文本(resource,"//");
    }


    @Nonnull
    public static Stream<String> 获取去除特定风格注释的文本(final @Nonnull Resource resource,final @Nonnull String $风格) throws IOException {
        final ArrayList<String> lines = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) lines.add(line.split($风格,2)[0]);
        }
        return lines.stream();
    }
}
