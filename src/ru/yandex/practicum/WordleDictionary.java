package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class WordleDictionary {

    private final List<String> words;
    private final PrintWriter logger;
    private final Random rand;

    public WordleDictionary(List<String> words, PrintWriter logger) {
        this.words = words;
        this.logger = logger;
        this.rand = new Random();
    }

    public String getRandomWord() {
        return words.get(rand.nextInt(words.size()));
    }

    public void wordInDictionary(String word) throws WordNotFoundInDictionaryException {
        if (!words.contains(word)) {
            throw new WordNotFoundInDictionaryException(word);
        }
    }

    public void validateWordFormat(String word) throws InvalidWordFormatException {
        if (word == null || word.length() != 5) {
            throw new InvalidWordFormatException(word);
        }
        for (char c : word.toCharArray()) {
            if (c < 'а' || c > 'я') {
                throw new InvalidWordFormatException(word);
            }
        }
    }

    public String normalizeWord(String word) {
        return word.toLowerCase().replace("ё", "е").trim();
    }

    public List<String> getWords() {
        return words;
    }

    public String getRandomWordFromSet(Set<String> words) {
        String[] array = words.toArray(new String[words.size()]);
        logger.println("Вывод случайного слова для подсказки");
        return array[rand.nextInt(array.length)];
    }
}
