package com.revstar.binderconnectpool;

import android.os.RemoteException;

/**
 * Create on 2019/1/11 14:57
 * author revstar
 * Email 1967919189@qq.com
 */
public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
