package ReflectionTask.testApi;

import ReflectionTask.testApi.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestRunner {

    public static void runTests(Class<?> testClass) throws Exception {
        // Получаем все методы
        Method[] methods = testClass.getDeclaredMethods();

        // Проверяем статические аннотации
        validateSingleAnnotations(methods);

        // Получаем метод BeforeSuite
        Method beforeSuite = findSingleAnnotatedMethod(methods, BeforeSuite.class);

        // Получаем метод AfterSuite
        Method afterSuite = findSingleAnnotatedMethod(methods, AfterSuite.class);

        // Получаем методы BeforeTest
        List<Method> beforeTests = findAnnotatedMethods(methods, BeforeTest.class);

        // Получаем методы AfterTest
        List<Method> afterTests = findAnnotatedMethods(methods, AfterTest.class);

        // Получаем Test методы и сортируем по приоритетам
        List<Method> testMethods = findAnnotatedMethods(methods, Test.class);
        testMethods.sort(Comparator.comparingInt(m -> {
            Test annotation = m.getAnnotation(Test.class);
            return -annotation.priority();
        }));

        // Создаем объект тестируемого класса
        Object instance = testClass.getDeclaredConstructor().newInstance();

        // Выполняем статические метод перед тестом
        invoke(beforeSuite, null);

        // Выполняем методы Test
        for (Method testMethod : testMethods) {
            // Выполняем BeforeTest методы
            for (Method beforeTest : beforeTests) {
                invoke(beforeTest, instance);
              }

            // Check for CsvSource annotation
            CsvSource csvSource = testMethod.getAnnotation(CsvSource.class);
            if (csvSource != null) {
                // Разбираем параметры CSV и выполняем тест
                invokeWithCsvSource(instance, testMethod, csvSource.value());
            } else {
                // Выполняем тесты
                invoke(testMethod, instance);
            }

            // Выполняем AfterTest методы
            for (Method afterTest : afterTests) {
                invoke(afterTest, instance);
            }
        }

        // Выполняем статические метод после теста
        invoke(afterSuite, null);
    }

    private static void invoke(Method method, Object instance) throws InvocationTargetException, IllegalAccessException {
        if (method != null) {
            method.setAccessible(true);
            method.invoke(instance);
        }
    }

    private static void checkSingleAnnotatedMethods(Method[] methods, Class<? extends Annotation> annotation){
        long beforeSuiteCount = Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(annotation))
                .count();
        if (beforeSuiteCount > 1) {
            throw new RuntimeException("Возможен только один метод помеченный аннотацией @" + annotation.getName());
        }
    }

    private static void checkStaticAnnotatedMethods(Method[] methods, Class<? extends Annotation> annotation){
        Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(annotation))
                .forEach(m -> {
                    if (!Modifier.isStatic(m.getModifiers())) {
                        throw new RuntimeException("@BeforeSuite и @AfterSuite методы должны быть статическими");
                    }
                });
    }

    private static void validateSingleAnnotations(Method[] methods) {
        // Проверяем количество методов помеченных аннотациями
        checkSingleAnnotatedMethods(methods, BeforeSuite.class);
        checkSingleAnnotatedMethods(methods, AfterSuite.class);
        // Проверяем что методы статические
        checkStaticAnnotatedMethods(methods, BeforeSuite.class);
        checkStaticAnnotatedMethods(methods, AfterSuite.class);
    }

    private static Method findSingleAnnotatedMethod(Method[] methods, Class<? extends Annotation> annotation) {
        List<Method> found = findAnnotatedMethods(methods, annotation);
        return found.isEmpty() ? null : found.get(0);
    }

    private static List<Method> findAnnotatedMethods(Method[] methods, Class<? extends Annotation> annotation) {
        return Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private static void invokeWithCsvSource(Object instance, Method method, String csv) throws Exception {
        String[] values = csv.split("\\s*,\\s*");
        Class<?>[] paramTypes = method.getParameterTypes();

        if (values.length != paramTypes.length) {
            throw new IllegalArgumentException("Количество значений в формате CSV не совпадает с количеством параметров метода");
        }

        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = convertValue(values[i], paramTypes[i]);
        }

        method.setAccessible(true);
        method.invoke(instance, args);
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == char.class || targetType == Character.class) {
            return value.charAt(0);
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(value);
        } else if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(value);
        }
        throw new IllegalArgumentException("Не поддерживаемый тип параметра: " + targetType);
    }

}
