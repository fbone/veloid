package com.xirgonium.android.veloid.veloid2.timer;

import com.xirgonium.android.veloid.veloid2.timer.ITimerServiceCallback2;

interface ITimerServiceMain2 {
   
    void registerCallback(ITimerServiceCallback2 cb);
    
    void unregisterCallback(ITimerServiceCallback2 cb);
    
     int getPid();
}