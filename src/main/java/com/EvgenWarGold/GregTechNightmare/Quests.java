package com.EvgenWarGold.GregTechNightmare;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quests {

    private static boolean registered;

    public static void init() {
        if (registered) {
            return;
        }

        if (GregTechNightmare.CONFIG_DIR == null) return;

        File configDir = new File(GregTechNightmare.CONFIG_DIR, "betterquesting/DefaultQuests");

        if (!configDir.exists()) return;

        File questsDir = new File(configDir, "Quests");
        File questLineDir = new File(configDir, "QuestLines");

        try {
            URI jarFileUri = GregTechNightmare.RESOURCE_URL.toURI();
            String jarPath = "jar:" + jarFileUri.getRawSchemeSpecificPart()
                .split("!")[0];

            try (FileSystem fs = FileSystems.newFileSystem(URI.create(jarPath), Collections.emptyMap())) {
                Path sourceDir = fs.getPath("/assets/gregtechnightmare/Quests");

                if (Files.exists(sourceDir) && Files.isDirectory(sourceDir)) {
                    try (var stream = Files.list(sourceDir)) {
                        stream.forEach(entry -> {
                            String name = entry.getFileName()
                                .toString();

                            try {
                                if (name.equals("Quests") && Files.isDirectory(entry)) {
                                    copyDirectoryRecursive(entry, questsDir);
                                } else if (name.equals("QuestLines") && Files.isDirectory(entry)) {
                                    copyDirectoryRecursive(entry, questLineDir);
                                } else if (name.equals("QuestLinesOrder.txt") && Files.isRegularFile(entry)) {
                                    mergeQuestLinesOrderFile(entry, configDir);
                                }
                            } catch (IOException ignored) {}
                        });
                    }
                }
            }
        } catch (URISyntaxException | IOException ignored) {}

        registered = true;
    }

    private static void mergeQuestLinesOrderFile(Path sourceFile, File configDir) throws IOException {
        File targetFile = new File(configDir, "QuestLinesOrder.txt");

        List<String> jarLines = Files.readAllLines(sourceFile);

        List<String> allLines = new ArrayList<>(jarLines);

        if (targetFile.exists()) {
            List<String> existingLines = Files.readAllLines(targetFile.toPath());
            allLines.addAll(existingLines);
        }

        Files.write(targetFile.toPath(), allLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void copyDirectoryRecursive(Path sourceDir, File targetDir) throws IOException {
        try (var walk = Files.walk(sourceDir)) {
            walk.forEach(sourcePath -> {
                try {
                    Path relativePath = sourceDir.relativize(sourcePath);
                    File targetPath = new File(targetDir, relativePath.toString());

                    if (Files.isDirectory(sourcePath)) {
                        targetPath.mkdirs();
                    } else if (Files.isRegularFile(sourcePath)) {
                        targetPath.getParentFile()
                            .mkdirs();
                        Files.copy(sourcePath, targetPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException ignored) {}
            });
        }
    }
}
