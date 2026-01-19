package ru.yandex.practicum;

public class InvalidWordFormatException extends RuntimeException {
    public InvalidWordFormatException(String message) {
        super("Некорректный ввод: " + message + "; Слово должно состоять из 5 русских букв");
    }
}
