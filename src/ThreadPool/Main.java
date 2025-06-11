package ThreadPool;


public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Создаем пул потоков:");
        ThreadPool pool = new ThreadPool(4);
        // Запускаем потоки обработки
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " выполняем задачу " + taskId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Thread.sleep(5000);
        pool.shutdown();
        // Задача после shutdown вызовет исключение IllegalStateException
        //pool.execute(() -> System.out.println(Thread.currentThread().getName() + " выполняем задачу после shutdown"));
        pool.awaitTermination();
        System.out.println("Все задачи завершены");
    }
}