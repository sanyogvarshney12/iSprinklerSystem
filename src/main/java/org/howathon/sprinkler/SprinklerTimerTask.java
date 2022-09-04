package org.howathon.sprinkler;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import java.util.Timer;
import java.util.TimerTask;

public class SprinklerTimerTask extends TimerTask {

    private int i;
    private Timer timer;
    private GpioPinDigitalOutput output;

    public SprinklerTimerTask(int i, Timer timer, GpioPinDigitalOutput output) {
        this.i = i;
        this.timer = timer;
        this.output = output;
    }

    @Override
    public void run() {
        while(i > 0) {
            System.out.println("PinState.HIGH");
            output.setState(PinState.HIGH);
            i--;
        }
        System.out.println("PinState.LOW");
        output.setState(PinState.LOW);
        timer.cancel();
    }
}
