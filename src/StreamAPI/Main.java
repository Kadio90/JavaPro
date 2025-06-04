package StreamAPI;

import StreamAPI.Examples.Employee;
import StreamAPI.Examples.Position;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        //---------------------------------------------------------------
        List<Integer> integerList = List.of(5, 2, 10, 9, 4, 3, 10, 1, 13);
        //Найдите в списке целых чисел 3-е наибольшее число (пример: 5 2 10 9 4 3 10 1 13 => 10)
        System.out.println("---1---");
        integerList.stream()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .ifPresent(System.out::println);

        //---------------------------------------------------------------
        //Найдите в списке целых чисел 3-е наибольшее «уникальное» число (пример: 5 2 10 9 4 3 10 1 13 => 9, в отличие от прошлой задачи здесь разные 10 считает за одно число)
        System.out.println("---2---");
        integerList.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .ifPresent(System.out::println);

        //---------------------------------------------------------------
        List<Employee> employees = List.of(
                new Employee("Иван", 30, Position.ENGINEER),
                new Employee("Петр", 25, Position.MANAGER),
                new Employee("Сергей", 35, Position.ENGINEER),
                new Employee("Анна", 28, Position.ENGINEER),
                new Employee("Олег", 27, Position.ENGINEER),
                new Employee("Алексей", 40, Position.DIRECTOR)
        );

        //---------------------------------------------------------------
        //Имеется список объектов типа Сотрудник (имя, возраст, должность), необходимо получить список имен 3 самых старших сотрудников с должностью «Инженер», в порядке убывания возраста
        System.out.println("---3---");
        employees.stream().filter(employee -> Position.ENGINEER.equals(employee.getPosition()))
                .sorted(Comparator.comparing(Employee::getAge).reversed())
                .limit(3)
                .map(Employee::getName)
                .forEach(System.out::println);

        //---------------------------------------------------------------
        //Имеется список объектов типа Сотрудник (имя, возраст, должность), посчитайте средний возраст сотрудников с должностью «Инженер»
        System.out.println("---4---");
        employees.stream()
            .filter(employee -> Position.ENGINEER.equals(employee.getPosition())) // Фильтруем только инженеров
            .mapToInt(Employee::getAge)
            .average()
            .ifPresent(System.out::println);

        //---------------------------------------------------------------
        List<String> stringList = List.of("abc", "ab", "abcdf", "a", "adcfg", "bc", "b", "cde");
        //Найдите в списке слов самое длинное
        System.out.println("---5---");

        stringList.stream()
                .max(Comparator.comparing(String::length))
                .ifPresent(System.out::println);

        //---------------------------------------------------------------
        //Имеется строка с набором слов в нижнем регистре, разделенных пробелом.
        //Постройте хеш-мапы, в которой будут хранится пары: слово - сколько раз оно встречается во входной строке
        System.out.println("---6---");
        String splitString = "a ab abcd abcdef ab a abcde";
        Map<String, Long> countWordMap = Arrays.stream(splitString.split(" "))
                .collect(Collectors.groupingBy(str -> str, Collectors.counting()));
        System.out.println(countWordMap);

        //---------------------------------------------------------------
        //Отпечатайте в консоль строки из списка в порядке увеличения длины слова, если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок
        System.out.println("---7---");
        stringList.stream()
                .sorted(Comparator.comparing(String::length))
                .forEach(System.out::println);

        //---------------------------------------------------------------
        String[] strings = {"ab abc abcd abcde abcdef", "ba cda dcba edcba fedcba", "hi hik hiklm hiklmo hiklmnop"};
        //Имеется массив строк, в каждой из которых лежит набор из 5 слов, разделенных пробелом,
        // найдите среди всех слов самое длинное, если таких слов несколько, получите любое из них
        System.out.println("---8---");
        Arrays.stream(strings)
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .max(Comparator.comparingInt(String::length))
                .ifPresent(System.out::println);
    }
}