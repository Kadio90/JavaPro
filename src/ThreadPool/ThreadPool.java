package ThreadPool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool {
    private final LinkedList<Runnable> taskQueue;
    private final List<WorkerThread> workers;
    private final AtomicBoolean isShutdown;

    private class WorkerThread extends Thread {

        public WorkerThread(String name) {
            super(name);
            System.out.println("Создан поток "+ name);
        }

        @Override
        public void run() {
            Runnable task;
            while (!this.isInterrupted()) {
                synchronized (taskQueue) {
                    // Ждем, пока не появится задача
                    if (taskQueue.isEmpty()){
                        try {
                            System.out.println(this.getName() + " ждет задачу!");
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            System.out.println(this.getName() + " ошибка: " + e.getMessage());
                            continue;
                        }
                    }
                    // Если shutdown завершаем поток
                    if (isShutdown.get()) {
                        System.out.println(this.getName() + " завершает работу по Shutdown!");
                        break;
                    }
                    // Берем задачу из очереди
                    task = taskQueue.removeFirst();
                }
                // Выполняем задачу
                try {
                    task.run();
                } catch (Exception e) {
                    System.out.println(this.getName() + "Ошибка выполнения потока: " + e.getMessage());
                }
            }
        }
    }

    public ThreadPool(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Количество потоков должно быть больше 0");
        }
        this.taskQueue = new LinkedList<>();
        this.workers = new LinkedList<>();
        this.isShutdown = new AtomicBoolean(false);

        // Создаем и запускаем рабочие потоки
        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread("Поток-" + i);
            worker.start();
            workers.add(worker);
        }
    }

    public void execute(Runnable task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("После shutdown обработка заданий не возможна!");
        }
        if (task == null) {
            throw new NullPointerException("Задача должна существовать!");
        }
        synchronized (taskQueue) {
            taskQueue.addLast(task);
            taskQueue.notify(); // Будим один из ожидающих потоков
        }
    }

    public void shutdown() {
        System.out.println("Сработал shutdown!");
        isShutdown.set(true);
        synchronized (taskQueue) {
            taskQueue.notifyAll(); // Будим все потоки для проверки состояния shutdown
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (WorkerThread worker : workers) {
            worker.join();
        }
    }
}
