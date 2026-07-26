/*
 * Copyright 2025 QiguaiAAAA
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
 * 版权所有 2025 QiguaiAAAA
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

package 清汩萌.天圆地方.世界.沙盒;

import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Assertions;
import 清汩萌.天圆地方.util.ClassGraphUtils;
import 清汩萌.天圆地方.util.IOBiConsumer;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.工具.无参异常;
import 清汩萌.造.格文件;
import 清汩萌.造.空间.词块网格;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author QiguaiAAAA
 */
public class 沙盒测试样例 {
    public final String $名;
    public final @Nonnull 格文件 $格文件;
    public final @Nonnull 词块网格 $网格;

    public 沙盒测试样例(final @Nonnull 格文件 $格文件) {
        this.$格文件 = $格文件;
        this.$名 = $格文件.获取名称();
        this.$网格 = $格文件.获取网格();
        final Map<String,Object> ext = $格文件.获取附加数据();
        Assertions.assertNotNull(ext);
        处理参数(this);
    }

    public static <T> Stream<T> 寻找格样例输入(final @Nonnull String $样例目录, final @Nonnull Function<格文件,T> $处理器){
        final ArrayList<T> cases = new ArrayList<>();
        ClassGraphUtils.寻找样例输入($样例目录,格文件._扩展名_,(ignored, in)-> cases.add($处理器.apply(格文件.解析(in.getURI()))));
        return cases.stream();
    }

    public static void 寻找格样例输入(final @Nonnull String $样例目录, final @Nonnull IOBiConsumer<ScanResult, Resource> $处理器){
        ClassGraphUtils.寻找样例输入($样例目录,格文件._扩展名_,$处理器);
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public static <C extends 沙盒测试样例> C 处理参数(final @Nonnull C $样例){
        final Map<String,Object> $附加参数 = $样例.$格文件.获取附加数据();
        Assertions.assertNotNull($附加参数);
        final Field[] $字段组 = $样例.getClass().getDeclaredFields();
        for(final @Nonnull Field $字段:$字段组){
            final int $修饰符 = $字段.getModifiers();
            if(Modifier.isStatic($修饰符) || Modifier.isFinal($修饰符)) continue;
            if(!$字段.isAnnotationPresent(测试参数.class)) continue;
            final @Nonnull 测试参数 $参 = $字段.getAnnotation(测试参数.class);
            $字段.setAccessible(true);
            final String $名 = $参.键().isEmpty()?$字段.getName():$参.键();
            final @Nullable Optional<Map<String,Object>> $所在YAML对象 = $参.于().isEmpty()?Optional.of($附加参数):
                    Optional.of($附加参数.get($参.于())).filter(o -> o instanceof Map).map(o -> (Map<String, Object>) o);
            try {
                $参.型().解析($样例,$所在YAML对象,$名,$字段);
                天圆地方测试.LOGGER.info("{}:Argument {} is set to {} now",$样例.$名,$字段.getName(),$字段.get($样例));
            } catch (final @Nonnull 无参异常 e){
                if(!$参.可选() && !$字段.isAnnotationPresent(Nullable.class)) throw e;
                天圆地方测试.LOGGER.info("Keep argument {} in {} as Default",$字段.getName(),$样例.$名);
            }catch (final @Nonnull IllegalAccessException e) {
                Assertions.fail(e);
            }
        }
        return $样例;
    }

    @Override
    public String toString() {
        return $名;
    }
}
