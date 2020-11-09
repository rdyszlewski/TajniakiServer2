package com.parabbits.tajniakiserver.shared.timer;

import java.io.IOException;

public interface ITimerCallback {
    void onFinish() throws IOException;
    void onTick(Long time);
}
