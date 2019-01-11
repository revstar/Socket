package com.revstar.binderconnectpool;

import android.os.RemoteException;

/**
 * Create on 2019/1/11 14:48
 * author revstar
 * Email 1967919189@qq.com
 */
public class SecurityCenterImpl extends ISecurityCenter.Stub {
    private static final char SECRET_CODE = '^';
    @Override
    public String encrypt(String content) throws RemoteException {

        char []chars=content.toCharArray();
        for (int i=0;i<chars.length;i++){
            chars[i]^=SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
