package hu.unideb.inf.barcodescannernew.tasksmanager;

import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class CustomThreadPoolManager {

    private static final CustomThreadPoolManager sInstance;
    private static final int NUMBER_OF_CORES;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private final ExecutorService mExecutorService;
    private final BlockingQueue<Runnable> mTaskQueue;
    private final List<Future> mRunningTaskList;

    private WeakReference<PresenterThreadCallback> presenterThreadCallbackWeakReference;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        sInstance = new CustomThreadPoolManager();
    }

    private CustomThreadPoolManager() {
        mTaskQueue = new LinkedBlockingQueue<>();
        mRunningTaskList = new ArrayList<>();
        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mTaskQueue, new BackgroundThreadFactory());
    }

    public static CustomThreadPoolManager getsInstance() {
        return sInstance;
    }

    public void addCallableMethod(Callable callable){
        Future future = mExecutorService.submit(callable);
        mRunningTaskList.add(future);
    }

    public void addRunnableMethod(Runnable runnable){
        Future future = mExecutorService.submit(runnable);
        mRunningTaskList.add(future);
    }

    public void cancelAllTasks() {
        synchronized (this) {
            mTaskQueue.clear();
            for (Future task : mRunningTaskList) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }
            mRunningTaskList.clear();
        }
    }

    public void setPresenterCallback(PresenterThreadCallback presenterThreadCallback) {
        this.presenterThreadCallbackWeakReference = new WeakReference<>(presenterThreadCallback);
    }

    public void sendResultToPresenter(Message message){
        if(presenterThreadCallbackWeakReference!= null && presenterThreadCallbackWeakReference.get() != null) {
            presenterThreadCallbackWeakReference.get().sendResultToPresenter(message);
        }
    }

    private static class BackgroundThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            int sTag = 1;
            thread.setName("CustomThread" + sTag);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            thread.setUncaughtExceptionHandler(
                    (thread1, ex) -> Log.e("Util.LOG_TAG", thread1.getName() + " szál létrehozása során hiba keletkezett: " + ex.getMessage())
            );
            return thread;
        }
    }
}
