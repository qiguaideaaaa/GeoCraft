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
    public static final String COMMON_ANSWER_FILE_EXT = "ans";

    private ClassGraphUtils(){}

    public static void findInputs(final @Nonnull String dataDir,
                                  final @Nonnull String fileExt,
                                  final @Nonnull IOBiConsumer<ScanResult, Resource> forEachInput){
        try (final @Nonnull ScanResult scan = new ClassGraph().acceptPaths(dataDir).scan()){
            findInputs(fileExt,scan,forEachInput);
        }
    }

    @SuppressWarnings("resource")
    public static void findInputs(final @Nonnull String fileExt,
                                  final @Nonnull ScanResult scan,
                                  final @Nonnull IOBiConsumer<ScanResult, Resource> forEachInput){
        scan.getResourcesWithExtension(fileExt)
                .filter(r -> r.getPath().endsWith(".in."+fileExt))
                .forEach(in -> {
                    try {
                        forEachInput.accept(scan,in);
                    } catch (final @Nonnull Exception e) {
                        Assertions.fail("error occurred when processing "+in.getPath(),e);
                    }
                });
    }

    @Nonnull
    public static Resource getAnswerByInput(final @Nonnull String inputFileExt,final @Nonnull ScanResult scan,final @Nonnull Resource in){
        return getAnswerByInput(inputFileExt,inputFileExt,scan,in);
    }

    @Nonnull
    public static Resource getAnswerByInput(final @Nonnull String inputFileExt,final @Nonnull String ansFileExt,final @Nonnull ScanResult scan,final @Nonnull Resource in){
        final String ansPre = "."+COMMON_ANSWER_FILE_EXT;
        final String ansSuf = ansFileExt.isEmpty()?"":"."+ansFileExt;
        final String outPath = in.getPath().replaceAll("\\.in\\."+inputFileExt+"$", ansPre+ansSuf);
        return scan.getResourcesWithPath(outPath).get(0);
    }

    @Nonnull
    public static Stream<String> getLinesWithoutYAMLComments(final @Nonnull Resource resource) throws IOException {
        final ArrayList<String> lines = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) lines.add(line.split("#",2)[0]);
        }
        return lines.stream();
    }

    @Nonnull
    public static Stream<String> getLinesWithoutJSONComments(final @Nonnull Resource resource) throws IOException {
        final ArrayList<String> lines = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) lines.add(line.split("//",2)[0]);
        }
        return lines.stream();
    }
}
