package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;


public class WordleGame {

    private String answer;

    private int steps;

    private final PrintWriter logger;
    private final WordleDictionary dictionary;
    private final List<String> previousAttempts;
    private final Map<Character, Integer> letters;
    public static final int MAX_ATTEMPTS = 6;
    public static final int WORD_LENGTH = 5;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.logger = logger;
        this.dictionary = dictionary;
        this.previousAttempts = new ArrayList<>();
        this.letters = new LinkedHashMap<>();
    }

    public void startGame() {
        this.answer = dictionary.getRandomWord();
        this.steps = 1;
        logger.println("Игра началась. Загаданное слово: " + answer + "\nМаксимальное количество попыток: " + MAX_ATTEMPTS);
    }

    public int getSteps() {
        return steps;
    }

    public boolean canContinue() {
        return steps <= MAX_ATTEMPTS;
    }

    public boolean checkAnswer(String word) {
        return answer.equals(word);
    }

    public String getAttemptTip(String word) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == answer.charAt(i)) {
                res.append("+");
                updateLettersGuessed(word, i);
            } else if (answer.contains(word.substring(i, i + 1))) {
                res.append("^");
            } else {
                letters.putIfAbsent(word.charAt(i), 0);
                res.append("-");
            }
        }
        previousAttempts.add(word);
        logger.println("Полученная подсказка для слова: " + res);
        return res.toString();
    }

    private void updateLettersGuessed(String word, int index) {
        letters.putIfAbsent(word.charAt(index), 0);
        boolean needToCount = true; //проверка, была ли эта буква отгадана ранее
        for (String attempt : previousAttempts) {
            if (attempt.charAt(index) == word.charAt(index)) {
                needToCount = false;
                break;
            }
        }
        if (needToCount) {
            letters.put(word.charAt(index), letters.get(word.charAt(index)) + 1);
        }
    }

    public void makeMove() {
        steps++;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTip() {
        if (steps == 1) {
            logger.println("Первая попытка - возврат случайного слова");
            return dictionary.getRandomWord();
        }
        Set<String> setWords = new HashSet<>();
        List<String> words = dictionary.getWords();
        //отсеиваем то, где неверное количество отгаданных букв и каких нет в слове
        for (String word: words) {
            if (previousAttempts.contains(word)) {
                continue;
            }
            if (checkSuitability(word)) {
                setWords.add(word);
            }
        }
        logger.println("При первом отсеивании осталось подходящих слов: " + setWords.size());
        // найти те слова, где нужные буквы на нужных местах
        String mask = getMask();
        Iterator<String> iter = setWords.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            if (!matchMask(word, mask)) {
                iter.remove();
            }
        }
        logger.println("При втором отсеивании осталось подходящих слов: " + setWords.size());
        return dictionary.getRandomWordFromSet(setWords);
    }

    private boolean checkSuitability(String word) {
        for (Character letter: letters.keySet()) {
            if ((letters.get(letter) == 0 && word.contains(letter.toString()))
                    || letters.get(letter) > countMatch(word, letter)) {
                return false;
            }
        }
        return true;
    }

    //"маска" для подходящих слов
    private String getMask() {
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < WORD_LENGTH; i++) {
            boolean isGuessed = false;
            for (String attempt : previousAttempts) {
                if (attempt.charAt(i) == answer.charAt(i)) {
                    isGuessed = true;
                    break;
                }
            }
            if (isGuessed) {
                mask.append(answer.charAt(i));
            } else {
                mask.append("*");
            }
        }
        return mask.toString();
    }

    //подсчёт количества буквы в слове
    private int countMatch(String word, char symbol) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == symbol) {
                count++;
            }
        }
        return count;
    }

    //проверка, подходит ли слово под маску
    private boolean matchMask(String word, String mask) {
        for (int i = 0; i < word.length(); i++) {
            if (!(word.charAt(i) == mask.charAt(i) || mask.charAt(i) == '*')) {
                return false;
            }
        }
        return true;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
