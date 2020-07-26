package wikipediafetcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskManager {
    private int threadcount;
    private ExecutorService executorservice;

    public TaskManager(int threadcount){

        this.threadcount = threadcount;
        executorservice = Executors.newFixedThreadPool(threadcount);

    }

    public void addTask(Runnable runnable)
    {
        this.executorservice.submit(runnable);
    }

    private int getQueueSize()
    {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) executorservice;
        return executor.getQueue().size();

    }

    public void waitTillQueueIsFreeAndAddTask(Runnable runnable)
    {
        while(getQueueSize() >= threadcount)
        {
            try{
                // Analyse whn this thread is sleeping is printed in output console
                System.out.println("thread is sleeping");
                Thread.currentThread().sleep(100000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        addTask(runnable);
    }



}
