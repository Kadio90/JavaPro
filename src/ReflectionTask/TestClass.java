package ReflectionTask;


import ReflectionTask.testApi.annotations.*;

public class TestClass {
    @BeforeSuite
    static void beforeAll() {
        System.out.println("BeforeSuite - запускается один раз перед всеми тестами");
    }

    @AfterSuite
    static void afterAll() {
        System.out.println("AfterSuite - запускается один раз после всех тестов");
    }

    @BeforeTest
    void beforeEach() {
        System.out.println("BeforeTest - запускается перед каждым тестом");
    }

    @AfterTest
    void afterEach() {
        System.out.println("AfterTest - запускается после каждого теста");
    }

    @Test(priority = 1)
    void highPriorityTest() {
        System.out.println("Запускается с низким приоритетом (priority=1)");
    }

    @Test // default priority=5
    void mediumPriorityTest() {
        System.out.println("Запускается с приоритетом по умолчанию (priority=5)");
    }

    @Test(priority = 10)
    void lowPriorityTest() {
        System.out.println("Запускается с высоким приоритетом (priority=10)");
    }

    @Test // default priority=5
    @CsvSource("42, Hello, true")
    void testWithParameters(int number, String text, boolean flag) {
        System.out.printf("Параметризированный тест: %d, '%s', %b%n", number, text, flag);
    }
}
