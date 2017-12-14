package com.bj.cachedthreadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

public class CacheThreadPool {
    public static void main(String[] args) {
        ExecutorService threadFactory = Executors.newCachedThreadPool();
        threadFactory.submit();
//        threadFactory.ex();
    }
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {

            int c = ctl.get();
            //获取线程池的状态，
            int rs = runStateOf(c);
            //按照不同的状态来区分执行，线程池状态值大于关闭也就不会接受新的任务了，如果线程池的状态不是运行态或者是
            // 线程池是关闭态但是传入的Runable不是null或者任务队列不是null应该继续
            // 也就是说线程池是关闭态但是任务队列中还有任务，或者有新的命令传入
            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
                    ! (rs == SHUTDOWN &&
                            firstTask == null &&
                            ! workQueue.isEmpty()))
                return false;

            for (;;) {
                //判断线程池中的线程个数。
                int wc = workerCountOf(c);
                //线程池中的线程数已超过了上限或者
                // 是想作为核心线程池的线程但是线程池的核心线程池已经满了。
                // 不作为核心线程池但是已经是最大的线程数目了。都返回false
                if (wc >= CAPACITY ||
                        wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                //满足条件然后CAS的方式来增加线程池的线程数目标识
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                //重新判断状态如果不是运行态然后重试
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        ThreadPoolExecutor.Worker w = null;
        try {
            //创建一个线程，然后
            final ReentrantLock mainLock = this.mainLock;
            w = new ThreadPoolExecutor.Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                //获取线程池的共有锁
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.

                    int c = ctl.get();
                    int rs = runStateOf(c);
                    //
                    if (rs < SHUTDOWN ||
                            (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        //新创建的线程池放入到hashset中
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        //设置线程池添加成功
                        workerAdded = true;
                    }
                } finally {
                    //释放锁
                    mainLock.unlock();
                }
                if (workerAdded) {
                    //启动线程
                    t.start();
                    //设置任务启动成功！
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }
//    public void execute(Runnable command) {
//        //首先判断传入的线程是不是null，没什么好说的
//        if (command == null)
//            throw new NullPointerException();
//        //这里的ctl是什么呢？ private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));这是一个原子操作类，
//        //线程池里面对这个原子操作类做出了，比较清晰的解释的。我大概在这里说一下吧。这个整数有一共32位，其中最高三位用来，存储线程池的状态。低29位用来存储线程池活跃的线程数目。
//        //线程池的状态一共有以下几种情况
//        //正在运行，能够接受新任务，以及对应添加的任务进行处理
//        //private static final int RUNNING    = -1 << COUNT_BITS;
//        //关闭，不接受新的任务但是可以处理已经添加的任务
//        //private static final int SHUTDOWN   =  0 << COUNT_BITS;
//        //停止，不接受新任务，但是也不会处理已经添加的任务。
//        //private static final int STOP       =  1 << COUNT_BITS;
//        //整理，所有的任务都已经处理结束线程池的状态。这个时候线程池会执行一个函数terminated（）现在模拟人的处理都是空的
//        //private static final int TIDYING    =  2 << COUNT_BITS;
//        //线程池彻底的终止。整理状态调用terminated函数以后就会变成这个状态了
//        //private static final int TERMINATED =  3 << COUNT_BITS;
//
//
//        int c = ctl.get();
//        //首先会获取一下正在工作的线程数量是不是大于核心线程池的大小
//        if (workerCountOf(c) < corePoolSize) {
//            //吐过小于核心线程池的大小，就直接创建新的线程来处理这个任务
//            if (addWorker(command, true))
//                return;
//            //如果添加任务失败就重新获取状态
//            c = ctl.get();
//        }
//        //获取状态以后，如果线程池还是在运行态，尝试将新的任务放入到队列当中去。
//        if (isRunning(c) && workQueue.offer(command)) {
//            int recheck = ctl.get();
//            //如果成功入队的话，我们需要重新的判断一下状态，因为从上一次check、到现在可能会有线程死亡，或者线程池关闭了。如果线程池关闭了我们就拒绝这个任务，如果有线程死亡我们就创建新的线程
//            if (! isRunning(recheck) && remove(command))
//                reject(command);
//            else if (workerCountOf(recheck) == 0)
//                addWorker(null, false);
//        }
//        //创建线程失败了，就拒绝任务就好了！
//        else if (!addWorker(command, false))
//            reject(command);
//    }



           if (rs >= SHUTDOWN
            if(
                    (rs != SHUTDOWN }||
                     firstTask != null ||
                workQueue.isEmpty()}
            return false;
}
