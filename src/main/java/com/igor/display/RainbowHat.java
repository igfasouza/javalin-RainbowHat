package com.igor.display;

import java.io.IOException;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class RainbowHat {


	public static void main(String[] args) {

		try {
			
			AlphanumericDisplay segment = new AlphanumericDisplay("I2C1");
			segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
			segment.display("0000");
			segment.setEnabled(true);
			
		} catch (IOException | UnsupportedBusNumberException e) {
			e.printStackTrace();
		}

	}


}