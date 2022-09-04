package org.howathon.sprinkler;

import java.util.Timer;
import java.util.TimerTask;

public class SprinklerTimerTask extends TimerTask {

    private int i;
    private Timer timer;

    public SprinklerTimerTask(int i, Timer timer) {
        this.i = i;
        this.timer = timer;
    }

    @Override
    public void run() {
        while(i > 0) {
            System.out.println("PinState.HIGH");
            //          output.setState(PinState.HIGH);
            i--;
        }
        System.out.println("PinState.LOW");
        //        output.setState(PinState.LOW);
        timer.cancel();
    }
}
