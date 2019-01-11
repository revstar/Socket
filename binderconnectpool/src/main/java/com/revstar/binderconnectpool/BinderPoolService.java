package com.revstar.binderconnectpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Create on 2019/1/11 15:07
 * author revstar
 * Email 1967919189@qq.com
 */
public class BinderPoolService extends Service {

    public static final String TAG="BinderPoolService";
    private Binder mBinderPool=new BinderPool.BinderPoolImpl();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return mBinderPool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
