package com.parabbits.tajniakiserver.shared.timer;

public interface ITimerCallback {
    void onFinish();
    void onTick(Long time);
}
