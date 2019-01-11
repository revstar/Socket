// IBinderPool.aidl
package com.revstar.binderconnectpool;

// Declare any non-default types here with import statements

interface IBinderPool {
   IBinder queryBinder(int binderCode);
}
