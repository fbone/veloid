package com.xirgonium.android.veloid.service;

import com.xirgonium.android.veloid.service.ITimerServiceCallback;

interface ITimerServiceMain {
   
    void registerCallback(ITimerServiceCallback cb);
    
    void unregisterCallback(ITimerServiceCallback cb);
    
     int getPid();
}