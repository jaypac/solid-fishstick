package com.indfinvestor.app.nav.amfi.bulkload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

@AllArgsConstructor
@Slf4j
public class AmfiFilePartitioner implements Partitioner {

    private String inputFile;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (StringUtils.isBlank(inputFile)) {
            log.error("inputFile parameter is required");
            throw new IllegalArgumentException("inputFile parameter is required");
        }

        Path path = Paths.get(inputFile);
        List<String> filePaths;
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Path does not exist: " + inputFile);
        } else if (Files.isDirectory(path)) {
            // Handle directory case
            log.info("Input is a directory : {}", inputFile);
            // Get all file paths from the directory
            try (Stream<Path> stream = Files.list(path)) {
                filePaths = stream.filter(Files::isRegularFile)
                        .map(Path::toAbsolutePath)
                        .map(Path::toString)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Error reading directory: " + inputFile, e);
                throw new RuntimeException("Error reading directory: " + inputFile, e);
            }
        } else if (Files.isRegularFile(path)) {
            // Handle file case
            log.info("Input is a file : {}", inputFile);
            // Single file case - create a list with just this file
            filePaths = List.of(path.toAbsolutePath().toString());
        } else {
            // Neither a regular file nor a directory (could be a symbolic link, etc.)
            log.error("Input is neither a regular file nor a directory {}", inputFile);
            throw new IllegalArgumentException("Input is neither a regular file nor a directory " + inputFile);
        }

        Map<String, ExecutionContext> partitions = new HashMap<>(gridSize);
        for (int i = 0; i < filePaths.size(); i++) {
            ExecutionContext context = new ExecutionContext();
            context.putString("fileName", filePaths.get(i));
            context.putInt("partitionIndex", i);
            partitions.put("partition" + i, context);
        }

        return partitions;
    }
}
