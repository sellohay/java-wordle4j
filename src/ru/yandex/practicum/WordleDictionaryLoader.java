package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class WordleDictionaryLoader {

    private final Path wordFile;
    private final PrintWriter logger;

    public WordleDictionaryLoader(Path wordFile, PrintWriter logger) {
        this.wordFile = wordFile;
        this.logger = logger;
    }

    public List<String> loadDictionary() throws IOException, SystemException {
        List<String> words = new ArrayList<>();

        if (!Files.exists(wordFile)) {
            logger.println("ОШИБКА: файл словаря не найден: " + wordFile);
            throw new FileNotFoundException("ОШИБКА: файл словаря не найден: " + wordFile.toString());
        }

        try(BufferedReader br = new BufferedReader(new FileReader(wordFile.toFile(), StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.length() == 5) {
                    words.add(line.toLowerCase().replace("ё", "е").trim());
                }
            }
        }
        if (words.isEmpty()) {
            logger.println("ОШИБКА: Словарь пуст");
            throw new SystemException("Словарь пуст");
        }

        logger.println("Словарь успешно загружен, слов: " + words.size());
        return words;
    }

}
