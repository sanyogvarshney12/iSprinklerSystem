package org.howathon.sprinkler;

import com.pi4j.io.gpio.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SprinklerSystemApplication extends AbstractVerticle {

    public static void main(String[] args) {
        System.out.println("iSprinklerSystem server started");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new SprinklerSystemApplication());
    }

    @Override
    public void start() {
        System.out.println("iSprinklerSystem Started");
        GpioController gpioController = GpioFactory.getInstance();
        GpioPinDigitalOutput output = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_15,
                "Motor", PinState.LOW);
        Router router = Router.router(vertx);
        router.get("/").handler(routingContext -> {
            System.out.println("Welcome to iSprinklerSystem");
            routingContext.response().end("Welcome to iSprinklerSystem");
        });
        router.get("/").handler(routingContext -> {
            System.out.println("iSprinklerSystem Motor Started");
            output.setState(PinState.HIGH);
            routingContext.response().end("iSprinklerSystem Motor Started");
        });
        router.get("/turnoff").handler(routingContext -> {
            System.out.println("iSprinklerSystem Motor stopped");
            output.setState(PinState.LOW);
            routingContext.response().end("iSprinklerSystem Motor stopped");
        });
        router.get("/timer/:time").handler(routingContext -> {
            Timer timer = new Timer();
            System.out.println("iSprinklerSystem Motor started for "+routingContext.pathParam("time"));
            int i = Integer.parseInt(routingContext.pathParam("time"));
            SprinklerTimerTask task = new SprinklerTimerTask(i, timer, output);
            task.run();
            routingContext.response().end("iSprinklerSystem Motor stopped");
        });
        router.get("/scheduler/:timestamp/:time").handler(routingContext -> {
            String timestampString = routingContext.pathParam("timestamp");
            Timestamp timestamp = Timestamp.valueOf(timestampString);
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            ZonedDateTime nextRun = now.withHour(dateTime.getHour())
                    .withMinute(dateTime.getMinute()).withSecond(dateTime.getSecond());
            if(now.compareTo(nextRun) > 0) {
                nextRun = nextRun.plusDays(1);
            }
            Duration duration = Duration.between(now, nextRun);
            long initialDelay = duration.getSeconds();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Turning on motor in Schedule");
                    Timer timer = new Timer();
                    int i = Integer.parseInt(routingContext.pathParam("time"));
                    SprinklerTimerTask task = new SprinklerTimerTask(i, timer, output);
                    task.run();
                }
            }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        });
        vertx.createHttpServer().requestHandler(router).listen(1212 );
    }
}
