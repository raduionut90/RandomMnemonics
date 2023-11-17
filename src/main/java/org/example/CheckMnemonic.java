package org.example;

import org.example.worker.MonitorThread;
import org.example.worker.RejectedExecutionHandlerImpl;
import org.example.worker.WorkerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;


public class CheckMnemonic {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckMnemonic.class);
    private static final AppConfig APP_CONFIG = AppConfig.getInstance();
    private static final int QUEUE_CAPACITY = 10000; // Alegeți o capacitate adecvată

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        LOGGER.info("Thread_number: {} rate_minutes: {}", APP_CONFIG.getThreadNumber(), APP_CONFIG.getRateMinutes());

        //RejectedExecutionHandler implementation
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        //Get the ThreadFactory implementation to use
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //creating the ThreadPoolExecutor
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(10,
                100,
                10,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                rejectionHandler);

        //start the monitoring thread
        MonitorThread monitor = new MonitorThread(executorPool, 3);
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();

        while (!Thread.interrupted()) {
            List<String> mnemonic = MnemonicGenerator.generateMnemonic();
            String mnemonicString = String.join(" ", mnemonic);
            WorkerThread worker = new WorkerThread(mnemonicString);
            executorPool.execute(worker);
//            String address = future.get();
//            MnemonicProcessor.checkBalance(address, mnemonicString);
        }
        Thread.sleep(30000);
        //shut down the pool
        executorPool.shutdown();
        //shut down the monitor thread
        Thread.sleep(5000);
        monitor.shutdown();

    }

}
