package com.prosoft;

public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public boolean isPositive(int number) {
        return number > 0;
    }

    public String classify(int number) {
        if (number > 0) {
            return "positive";
        } else if (number < 0) {
            return "negative";
        } else {
            return "zero";
        }
    }

    /**
     * Проверяет, является ли число чётным и положительным.
     */
    public boolean isEvenAndPositive(int number) {
        return number % 2 == 0 && number > 0;
    }

}
