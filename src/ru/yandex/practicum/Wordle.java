package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class Wordle {

    public static final String WORD_FILE = "words_ru.txt";
    public static final String LOG_FILE = "logs.txt";


    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(Paths.get(LOG_FILE).toFile()); Scanner sc = new Scanner(System.in)) {
            logger.println("Старт игры Wordle");

            //загрузка словаря
            Path wordFile = Paths.get(WORD_FILE);
            WordleDictionaryLoader loader = new WordleDictionaryLoader(wordFile, logger);
            List<String> words;
            try {
                words = loader.loadDictionary();
            } catch (SystemException | IOException e) {
                System.out.println(e.getMessage());
                logger.println("ОШИБКА: " + e.getMessage());
                return;
            }

            //создание игры
            WordleDictionary dict = new WordleDictionary(words, logger);
            WordleGame game = new WordleGame(dict, logger);
            game.startGame();

            //игровой цикл
            while (true) {
                System.out.println("Попытка " + game.getSteps() + ". Введите слово (5 букв):");
                logger.println("Попытка " + game.getSteps());
                String word = sc.nextLine();

                //запрос подсказки
                if (word.trim().isEmpty()) {
                    word = game.getTip();
                    System.out.println("Предложенная подсказка: " + word);
                    logger.println("Игрок запросил подсказку. Предложенное слово: " + word);
                }

                //проверка слова на корректность
                try {
                    word = dict.normalizeWord(word);
                    dict.validateWordFormat(word);
                    dict.wordInDictionary(word);

                    if (game.checkAnswer(word)) {
                        System.out.println("Поздравляем, вы угадали слово! Число попыток: " + game.getSteps());
                        logger.println("Пользователь отгадал слово за " + game.getSteps() + ". Игра закончена");
                        break;
                    }

                    game.makeMove();
                    System.out.println(game.getAttemptTip(word));
                } catch (InvalidWordFormatException e) {
                    logger.println("Пользователь ввел слово \"" + word + "\" неверного формата. Попытка не засчитана");
                    System.out.println("Попытка не засчитана: слово должно состоять из 5 русских букв. Повторите попытку.");
                    continue;
                } catch (WordNotFoundInDictionaryException e) {
                    logger.println("Пользователь ввел слово \"" + word + "\", которого нет в словаре. Попытка не засчитана");
                    System.out.println("Попытка не засчитана: такого слова нет в словаре. Повторите попытку.");
                    continue;
                }

                //если истекло число попыток
                if (!game.canContinue()) {
                    System.out.println("Попытки закончились:( Загаданное слово: " + game.getAnswer());
                    logger.println("Пользователь использовал все попытки, слово не отгадано. Игра закончена!");
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println("ОШИБКА при работе с файлами: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ОШИБКА: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
