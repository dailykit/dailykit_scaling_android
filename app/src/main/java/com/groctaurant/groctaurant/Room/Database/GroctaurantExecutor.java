package com.groctaurant.groctaurant.Room.Database;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Danish Rafique on 03-10-2018.
 */
public class GroctaurantExecutor {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static GroctaurantExecutor sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private GroctaurantExecutor(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static GroctaurantExecutor getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new GroctaurantExecutor(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(500),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}


