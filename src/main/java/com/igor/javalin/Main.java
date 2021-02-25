package com.igor.javalin;

import java.io.IOException;

import com.igor.display.AlphanumericDisplay;
import com.igor.display.Ht16k33;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) throws IOException, UnsupportedBusNumberException {
    	
    	AlphanumericDisplay segment = new AlphanumericDisplay("I2C1");
		segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(8080);

        app.post("/display", ctx -> {
        	System.out.println(ctx.body());
			segment.display(ctx.body());
			segment.setEnabled(true);
            ctx.redirect("index.html");
        });

    }

}