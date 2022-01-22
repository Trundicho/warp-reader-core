package de.trundicho.warp.reader.core;

import java.util.Timer;
import java.util.TimerTask;

import de.trundicho.warp.reader.core.controller.WarpUpdater;
import de.trundicho.warp.reader.core.view.api.timer.WarpTimer;

class WarpTimerForTest implements WarpTimer {
    private Timer timer;
    private final Runnable runnable;
    private boolean cancelled = false;

    WarpTimerForTest(WarpUpdater warpUpdater) {
        timer = createTimer();
        runnable = warpUpdater::doNextWarp;

    }

    @Override
    public void doNextWarp(WarpUpdater warpUpdater) {
        warpUpdater.doNextWarp();
    }

    @Override
    public void cancel() {
        timer.cancel();
        cancelled = true;
    }

    @Override
    public void scheduleRepeating(int periodMillis) {
        if(cancelled){
            timer = createTimer();
            cancelled = false;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        timer.schedule(timerTask, periodMillis);
    }

    private Timer createTimer() {
        return new Timer("Timer");
    }
}
