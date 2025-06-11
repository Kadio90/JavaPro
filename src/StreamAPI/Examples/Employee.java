package StreamAPI.Examples;


// Класс сотрудника

public class Employee {
    private final String name;
    private int age;
    private Position position;

    public Employee(String name, int age, Position position) {
        this.age = age;
        this.name = name;
        this.position = position;
    }

    public int getAge() {
        return age;
    }

    public Position getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
