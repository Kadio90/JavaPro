package ThreadPool;


import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Создаем пул потоков:");
        ThreadPool pool = new ThreadPool(4);
        // Запускаем потоки обработки
        for (int i = 0; i < 2; i++) {
            final int taskId = i;
            pool.execute(() -> {
                Random random = new Random();
                System.out.println(Thread.currentThread().getName() + " выполняем задачу 1" + taskId);
                try {
                    int timeSleep = 100 + random.nextInt(1000);
                    System.out.println(Thread.currentThread().getName() + " засыпаю на " + timeSleep);
                    Thread.sleep(timeSleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        Thread.sleep(3000);
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            pool.execute(() -> {
                Random random = new Random();
                System.out.println(Thread.currentThread().getName() + " выполняем задачу 2" + taskId);
                try {
                    int timeSleep = 200 + random.nextInt(1000);
                    System.out.println(Thread.currentThread().getName() + " засыпаю на " + timeSleep);
                    Thread.sleep(timeSleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        Thread.sleep(3000);
        pool.shutdown();
        // Задача после shutdown вызовет исключение IllegalStateException
        //pool.execute(() -> System.out.println(Thread.currentThread().getName() + " выполняем задачу после shutdown"));
        pool.awaitTermination();
        System.out.println("Все задачи завершены");
    }
}