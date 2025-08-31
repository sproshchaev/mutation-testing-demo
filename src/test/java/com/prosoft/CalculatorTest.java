package com.prosoft;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private final Calculator calc = new Calculator();

    @Test
    void testAdd() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void testSubtract() {
        assertEquals(1, calc.subtract(3, 2));
    }

    @Test
    void testIsPositive() {
        assertTrue(calc.isPositive(5));
        assertFalse(calc.isPositive(-3));
        assertFalse(calc.isPositive(0));
    }

    // ❌ Нет теста для classify!
    // Это специально — чтобы увидеть "выживших мутантов"

    @Test
    void testIsEvenAndPositive() {
        assertTrue(calc.isEvenAndPositive(4));  // 4 — чётное и положительное
        // ❌ Нет проверки на отрицательные чётные числа!
        // ❌ Нет проверки на граничные случаи (0, 2, -2)
    }

}