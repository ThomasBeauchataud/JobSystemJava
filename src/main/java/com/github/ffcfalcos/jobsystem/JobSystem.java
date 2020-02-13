package com.github.ffcfalcos.jobsystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class JobSystem {

    protected static JobSystem instance;
    protected static int threadNumber = 1;

    protected List<Thread> threads;
    protected Queue<Job> queue;
    protected boolean running;
    protected AtomicInteger x;

    public JobSystem() {
        this.threads = new ArrayList<>();
        this.queue = new LinkedList<>();
        running = false;
    }

    public void start() {
        stop();
        if(threads.size() < threadNumber) {
            while(threads.size() < threadNumber) {
                Thread thread = new Thread(new Handler());
                threads.add(thread);
            }
        } else {
            while (threads.size() > threadNumber) {
                threads.remove(threads.size() - 1);
            }
        }
        running = true;
    }

    public void stop() {
        running = false;
        join();
    }

    public void join() {
        for(Thread thread : this.threads) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void queue(Job job) {
        while (x.compareAndSet(0, 1)) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        queue.add(job);
        x.set(0);
    }

    public static JobSystem getInstance() {
        if(instance == null) {
            instance = new JobSystem();
        }
        return instance;
    }

    public static void setThreadNumber(int number) {
        threadNumber = number;
    }

    class Handler implements Runnable {

        public void run() {
            while(running) {
                if(queue.size() > 0) {
                    while (x.compareAndSet(0, 1)) {
                        try {
                            Thread.sleep(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (queue.size() > 0) {
                        Job job = queue.poll();
                        x.set(0);
                        job.execute();
                    }
                }
            }
        }
    }
}
