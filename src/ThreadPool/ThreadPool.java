package ThreadPool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<WorkerThread> workers;
    private volatile boolean isShutdown;

    private class WorkerThread extends Thread {
        private volatile boolean isRunning = true;

        public WorkerThread(String name) {
            super(name);
            System.out.println("Создан поток "+ name);
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        task.run();
                    }
                } catch (InterruptedException e) {
                    // Поток был прерван во время ожидания задачи
                    if (!isRunning) {
                        break;
                    }
                }
            }
        }

        public void interruptIfIdle() {
            isRunning = false;
            this.interrupt();
        }
    }

    public ThreadPool(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Количество потоков должно быть больше 0");
        }
        this.taskQueue = new LinkedBlockingDeque<>();
        this.workers = new LinkedList<>();
        this.isShutdown = false;

        // Создаем и запускаем рабочие потоки
        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread("Поток-" + i);
            worker.start();
            workers.add(worker);
        }
    }

    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("После shutdown обработка заданий не возможна!");
        }
        if (task == null) {
            throw new NullPointerException("Задача должна существовать!");
        }
        taskQueue.add(task);
    }

    public void shutdown() {
        isShutdown = true;
        for (WorkerThread worker : workers) {
            worker.interruptIfIdle();
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (WorkerThread worker : workers) {
            worker.join();
        }
    }
}
