package com.example.myapplication3;

import java.io.Serializable;

public class NativeDataHandler implements Serializable {
    static {
        System.loadLibrary("nativelib");
    }

    private static final long serialVersionUID = 0L;

    private long ptr;

    private native void freePtr(long ptr);


    public void invokeNativeFree() {
        if (ptr != 0) {
            freePtr(ptr);
            ptr = 0;
        }
    }

    protected void finalize() throws Throwable {
        if (ptr != 0) {
            freePtr(ptr);
            ptr = 0;
        }
    }
}