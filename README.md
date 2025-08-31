# 🧪 Мутационное тестирование в Java (на примере PITest + JUnit 5)

Проект демонстрирует **использование мутационного тестирования** в Java с помощью **PITest** и **JUnit 5**.  
Цель — научиться проверять **качество юнит-тестов**, а не только покрытие кода.

> 💡 Обычные тесты проверяют: *«Работает ли код?»*  
> Мутационные тесты проверяют: *«Обнаружат ли тесты ошибку, если код сломается?»*

---

## 📦 1. Зависимости и их назначение

### 🔹 JUnit 5
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```
- **Для чего**: написание и запуск юнит-тестов.
- **Важно**: PITest не работает с JUnit 5 "из коробки" — нужен отдельный плагин.

---

### 🔹 PITest (PIT Mutation Testing)
```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.20.2</version>
</plugin>
```
- **Для чего**: инструмент для мутационного тестирования.
- Генерирует "мутантов" — сломанные версии кода (например, `+` → `-`, `> 0` → `>= 0`).
- Проверяет, **падают ли тесты** при этих изменениях.

---

### 🔹 pitest-junit5-plugin
```xml
<dependency>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-junit5-plugin</artifactId>
    <version>1.2.3</version>
    <scope>test</scope>
</dependency>
```
- **Для чего**: позволяет PITest **работать с JUnit 5**.
- Без него — ошибка: `Please check you have correctly installed the pitest plugin...`.

---

### 🔹 maven-surefire-plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
</plugin>
```
- **Для чего**: запускает юнит-тесты в Maven (`mvn test`).
- Необходим для корректной работы PITest.

---

## ▶️ 2. Как запустить мутационное тестирование

Выполни в терминале в корне проекта:

```bash
mvn clean test compile
```
> Сначала убедимся, что все тесты проходят. Запуск mvn clean test compile вручную перед mutationCoverage — не обязателен, 
> потому что PITest делает это автоматически.  

Затем запусти мутационное тестирование:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

> Первый запуск может занять 1–2 минуты — PITest анализирует каждый возможный "слом" кода.

---

## 📊 3. Как проанализировать результаты

После выполнения PITest создаст **HTML-отчёт**:

```
target/pit-reports/com.prosoft/index.html
```

Открой этот файл в браузере.

[![2025-08-31-13-07-11.png](https://i.postimg.cc/k47q4nxw/2025-08-31-13-07-11.png)](https://postimg.cc/gwtftbt6)

### 🔍 Что смотреть:

| Параметр         | Что означает                                                                                                                                  |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **KILLED**       | ✅ Мутант убит — тесты **обнаружили** ошибку. Отлично!                                                                                        |
| **SURVIVED**     | ❌ Мутант выжил — тесты **не заметили** изменение. Проблема! Нужно услучшить тесты - добавить проверки граничных случаев, негативные сценарии.|
| **NO_COVERAGE**  | ⚠️ Код не покрыт тестами вообще. Нужно добавить тесты.                                                                                        |
| **Memory Error** | 💥 Мутация вызвала падение JVM — редко, но бывает.                                                                                            |

💡 Почему SURVIVED важнее, чем NO_COVERAGE?  
- NO_COVERAGE — очевидная проблема: "здесь вообще нет тестов".  
- SURVIVED — скрытая угроза: тесты есть, кажется, что всё в порядке, но на самом деле они не надёжны.  

| Мутация | Что проверяет | Как поймать |
|--------|--------------|------------|
| `Replaced integer modulus with multiplication` | Проверка чётности | Тест с нечётным числом (`3`) |
| `negated conditional` | Правильность условия | Тест с противоположным значением (`-4`) |
| `changed conditional boundary` | Граничные случаи | Тест на `0`, `1`, `-1` |
| `replaced return with true/false/0/null` | Возврат значений по умолчанию | Негативные тесты |

---

## 📚 Основные типы мутаторов в PITest

Вот **Ключевые мутаторы** из полученного отчёта и их значение:

| Мутатор | Что делает | Пример |
|--------|-----------|-------|
| `CONDITIONALS_BOUNDARY` | Меняет **границы условий** | `> 0` → `>= 0`, `< 10` → `<= 10` |
| `NEGATE_CONDITIONALS` | **Инвертирует** условие | `a > b` → `a <= b`, `==` → `!=` |
| `MATH` | Заменяет **арифметические операции** | `+` → `-`, `*` → `/`, `-` → `+` |
| `INCREMENTS` | Меняет `++`/`--` на `--`/`++` | `i++` → `i--` |
| `INVERT_NEGS` | Меняет знак числа | `x = -y` → `x = y` |
| `PRIMITIVE_RETURNS` | Заменяет **примитивы на 0/false/0.0** | `return 5` → `return 0`, `return true` → `return false` |
| `TRUE_RETURNS` / `FALSE_RETURNS` | Всегда возвращает `true` или `false` | `return x > 0` → `return true` |
| `NULL_RETURNS` | Возвращает `null` вместо объекта | `return new String()` → `return null` |
| `EMPTY_RETURNS` | Возвращает `""`, `[]`, `Collections.empty...` | `return list` → `return new ArrayList<>()` |
| `VOID_METHOD_CALLS` | Удаляет вызов метода `void` | `list.clear()` → (удалено) |

---

⚙️ Как управлять мутаторами в pom.xml
Включение/выключение мутаторов:
```xml
<configuration>
    <!-- Только нужные мутаторы -->
    <mutators>
        <mutator>CONDITIONALS_BOUNDARY</mutator>
        <mutator>NEGATE_CONDITIONALS</mutator>
        <mutator>MATH</mutator>
        <mutator>PRIMITIVE_RETURNS</mutator>
        <mutator>TRUE_RETURNS</mutator>
        <mutator>FALSE_RETURNS</mutator>
    </mutators>
</configuration>
```

Исключить шумные:
```xml
<excludedMutators>
    <excludedMutator>VOID_METHOD_CALLS</excludedMutator>
    <excludedMutator>EMPTY_RETURNS</excludedMutator>
</excludedMutators>
```

---

## ✅ Рекомендации

| Мутатор | Использовать? | Почему |
|--------|----------------|--------|
| `MATH`, `NEGATE_CONDITIONALS`, `CONDITIONALS_BOUNDARY` | ✅ Да | Проверяют ключевую логику |
| `PRIMITIVE_RETURNS`, `TRUE_RETURNS`, `FALSE_RETURNS` | ✅ Да | Проверяют возврат значений |
| `VOID_METHOD_CALLS` | ⚠️ Осторожно | Много шума, особенно с логгерами |
| `EMPTY_RETURNS` | ⚠️ Осторожно | Может быть избыточным |
| `EXPERIMENTAL_*` | ❌ Нет (по умолчанию) | Нестабильны, могут давать ложные срабатывания |

---

### 🔎 Пример анализа

Допустим, в методе:
```java
public String classify(int number) {
    if (number > 0) {
        return "positive";
    } else if (number < 0) {
        return "negative";
    } else {
        return "zero";
    }
}
```

PITest может создать мутанта: `number > 0` → `number >= 0`.  
Если у тебя **нет теста на `classify(0)`**, этот мутант **выживёт** — значит, твой тест не ловит ошибку.

---

## 🛠 4. Какие действия предпринять

### ✅ Если мутант **убит (Killed)**:
- Тесты хорошие.
- Логика покрыта.
- Можно двигаться дальше.

### ❌ Если мутант **выжил (Survived)**:
1. **Посмотри, какой код изменился** (в отчёте — кликни по строке).
2. **Добавь тест**, который ломается при этом изменении.
3. Перезапусти PITest — мутант должен быть убит.

> Пример: если выжил мутант `> 0 → >= 0`, добавь тест:
> ```java
> @Test
> void shouldReturnZeroForZero() {
>     assertEquals("zero", calc.classify(0));
> }
> ```

### ⚠️ Если **нет покрытия (No Coverage)**:
- Метод или ветка кода **вообще не вызываются** в тестах.
- Напиши тест, который покрывает этот участок.

---

## 🎯 Цель: 100% убитых мутантов

Хороший проект стремится к:
- **Максимальное уничтожение мутантов** (не 100% покрытие, а 100% *обнаружение ошибок*).
- Каждый `if`, `return`, оператор сравнения должен быть "проверен на прочность".

---

## 💡 Советы
- Начинай с простых классов (например, `Calculator`).
- Не бойся выживших мутантов — они показывают, **где тесты слабые**.
- Используй мутационное тестирование как **обучение** — оно учит писать лучше тесты.

---

## 🚀 Дальнейшее развитие
- Можно добавить PITest в CI/CD (GitHub Actions, GitLab CI).
- Настроить автоматический запуск при каждом коммите.
- Интегрировать с JaCoCo для анализа покрытия + мутаций.

---

## 📚 Официальная документация
- [PITest Mutators](https://pitest.org/quickstart/mutators/)
- [Configuring Mutators](https://pitest.org/quickstart/maven_plugin/)


