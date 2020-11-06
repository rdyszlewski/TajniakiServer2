package com.parabbits.tajniakiserver.shared.timer;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TimerService {

    private Map<UUID, Timer> timersMap = new HashMap<>();

    public void startTimer(UUID id, long time, ITimerCallback callback){
        if(timersMap.containsKey(id)){
            timersMap.get(id).stop();
        }
        Timer timer = new Timer(new ITimerCallback() {
            @Override
            public void onFinish() {
                callback.onFinish();
                // remove timer after finish job
                timersMap.remove(id);
            }

            @Override
            public void onTick(Long time) {
                callback.onTick(time);
            }
        });
        timer.start(time);
        timersMap.put(id, timer);
    }

    public void stopTimer(UUID id){
        if(timersMap.containsKey(id)){
            Timer timer = timersMap.get(id);
            timer.stop();
            timersMap.remove(id);
        }
    }

    public boolean isTimerRunning(UUID id){
        if(timersMap.containsKey(id)){
            return timersMap.get(id).isRunning();
        }
        return false;
    }
}
