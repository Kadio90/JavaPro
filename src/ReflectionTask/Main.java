package ReflectionTask;

import ReflectionTask.testApi.TestRunner;

public class Main {

    public static void main(String[] args) throws Exception {
        TestRunner.runTests(TestClass.class);
    }
}