package ru.yandex.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.InvalidWordFormatException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionaryException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dict;
    private PrintWriter logger;
    private List<String> words;

    @BeforeEach
    public void setUp() {
        logger = new PrintWriter(System.out);
        words = List.of("трава", "метод", "стена", "пурга", "весло");
        dict = new WordleDictionary(words, logger);
    }

    @Test
    public void testDictionaryLoader() throws IOException {
        Path wordFile = Paths.get("words_ru.txt");
        WordleDictionaryLoader loader = new WordleDictionaryLoader(wordFile, logger);
        List<String> wordsList = loader.loadDictionary();
        Assertions.assertNotNull(wordsList);
        assertFalse(wordsList.isEmpty());
    }

    @Test
    public void testLoaderFileNotFound() {
        Path wordFile = Paths.get("words_nonexistent.txt");
        WordleDictionaryLoader loader = new WordleDictionaryLoader(wordFile, logger);
        FileNotFoundException ex = assertThrows(FileNotFoundException.class, () -> {
            loader.loadDictionary();
        });
        assertTrue(ex.getMessage().contains("ОШИБКА: файл словаря не найден: " + wordFile.toString()));
    }

    @Test
    public void testWordInDictionary() {
        assertDoesNotThrow(() -> {
            dict.wordInDictionary("метод");
        });
    }

    @Test
    public void testWordNotFoundInDictionary() {
        assertThrows(WordNotFoundInDictionaryException.class, () -> {
            dict.wordInDictionary("бедро");
        });
        assertThrows(NullPointerException.class, () -> {
            dict.wordInDictionary(null);
        });
    }

    @Test
    public void testWordFormat() {
        assertDoesNotThrow(() -> {
            dict.validateWordFormat("трава");
            dict.validateWordFormat("бедро");
            dict.validateWordFormat("весна");
        });
    }

    @Test
    public void testInvalidWordFormat() {
        assertThrows(InvalidWordFormatException.class, () -> {
            dict.validateWordFormat("верность");
        });
        assertThrows(InvalidWordFormatException.class, () -> {
            dict.validateWordFormat("abcde");
        });
        assertThrows(InvalidWordFormatException.class, () -> {
            dict.validateWordFormat("каша1");
        });
        assertThrows(NullPointerException.class, () -> {
            dict.validateWordFormat(null);
        });
    }

    @Test
    public void testNormalizeWord() {
        assertEquals("весны", dict.normalizeWord("Вёсны"));
        assertEquals("пятка", dict.normalizeWord("пЯтКа"));
        assertEquals("мираж", dict.normalizeWord("  МИРАЖ    "));
        assertThrows(NullPointerException.class, () -> {
            dict.normalizeWord(null);
        });
    }

    @Test
    public void testGetRandomWordFromSet() {
        Set<String> set = new HashSet<>(words);
        System.out.println(set.size());
        assertNotNull(dict.getRandomWordFromSet(set));
        assertTrue(dict.getWords().contains(dict.getRandomWordFromSet(set)));
    }

    @Test
    public void testStartGame() {
        WordleGame game = new WordleGame(dict, logger);
        game.startGame();
        assertTrue(dict.getWords().contains(game.getAnswer()));
        assertEquals(1, game.getSteps());
    }

    @Test
    public void testGetAttemptTip() {
        WordleGame game = new WordleGame(dict, logger);
        game.setAnswer("весло");

        assertEquals("-----", game.getAttemptTip("панки"));
        assertEquals("++--+", game.getAttemptTip("ведро"));
        assertEquals("^+---", game.getAttemptTip("сечка"));
    }

    @Test
    public void testGetComputerTip() {
        WordleGame game = new WordleGame(dict, logger);
        game.setAnswer("метод");

        game.getAttemptTip("сплав");
        game.getAttemptTip("тальк");

        String suggestedWord = game.getTip();
        assertTrue(dict.getWords().contains(suggestedWord));
        assertFalse(suggestedWord.contains("с"));
        assertFalse(suggestedWord.contains("а"));
        assertFalse(suggestedWord.contains("ь"));
        assertFalse(suggestedWord.contains("к"));
    }

    @Test
    public void getComputerTipWithGuessedLetter() {
        WordleGame game = new WordleGame(dict, logger);
        game.setAnswer("трава");

        game.getAttemptTip("стена");
        String suggestedWord = game.getTip();
        assertTrue(dict.getWords().contains(suggestedWord));
        assertTrue(suggestedWord.contains("а"));
        assertEquals('а', suggestedWord.charAt(4));
    }

}
