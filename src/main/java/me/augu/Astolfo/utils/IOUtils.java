package me.augu.Astolfo.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.nio.file.Files;

public class IOUtils {
    private final static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static String readFile(String path) {
        try (final FileReader file = new FileReader(path);
             final BufferedReader reader = new BufferedReader(file)
        ) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readConfig(Class<T> config, File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
            Files.write(file.toPath(), gson.toJson(config.newInstance()).getBytes(StandardCharsets.UTF_8));
        }

        return gson.fromJson(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), config);
    }
}