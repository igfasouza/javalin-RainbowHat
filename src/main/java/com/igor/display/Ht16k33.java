package com.igor.display;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.io.IOException;


public class Ht16k33{

    private static final String TAG = Ht16k33.class.getSimpleName();

    /**
     * Default I2C slave address.
     */
    public static final int I2C_ADDRESS = 0x70;

    private static final int HT16K33_CMD_SYSTEM_SETUP = 0x20;
    private static final int HT16K33_OSCILLATOR_ON = 0b0001;
    private static final int HT16K33_OSCILLATOR_OFF = 0b0000;
    private static final int HT16K33_CMD_DISPLAYSETUP = 0x80;
    private static final int HT16K33_DISPLAY_ON = 0b0001;
    private static final int HT16K33_DISPLAY_OFF = 0b0000;
    private static final int HT16K33_CMD_BRIGHTNESS = 0xE0;

    /**
     * The maximum brightness level for this display
     */
    public static final int HT16K33_BRIGHTNESS_MAX = 0b00001111;

    /** Top segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_TOP = 1;
    /** Right top segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_RIGHT_TOP = 1 << 1;
    /** Right bottom segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_RIGHT_BOTTOM = 1 << 2;
    /** Bottom segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_BOTTOM = 1 << 3;
    /** Left bottom segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_LEFT_BOTTOM = 1 << 4;
    /** Left top segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_LEFT_TOP = 1 << 5;
    /** Center left segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_CENTER_LEFT = 1 << 6;
    /** Center right segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_CENTER_RIGHT = 1 << 7;
    /** Diagonal left top segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_DIAGONAL_LEFT_TOP = 1 << 8;
    /** Center top segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_CENTER_TOP = 1 << 9;
    /** Diagonal right top segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_DIAGONAL_RIGHT_TOP = 1 << 10;
    /** Diagonal left bottom segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_DIAGONAL_LEFT_BOTTOM = 1 << 11;
    /** Center bottom segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_CENTER_BOTTOM = 1 << 12;
    /** Diagonal right bottom segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_DIAGONAL_RIGHT_BOTTOM = 1 << 13;
    /** Dot segment bit. Useful for ORing together segments to display. */
    public static final short SEGMENT_DOT = 1 << 14;

    private I2CDevice mDevice;

    /**
     * Create a new driver for a HT16K33 peripheral connected on the given I2C bus using the
     * {@link #I2C_ADDRESS default I2C address}.
     * @param bus
     * @throws UnsupportedBusNumberException 
     */
    public Ht16k33(String bus) throws IOException, UnsupportedBusNumberException {
        this(bus, I2C_ADDRESS);
    }

    /**
     * Create a new driver for a HT16K33 peripheral connected on the given I2C bus and using the
     * given I2C address.
     * @param bus
     * @param i2cAddress
     * @throws UnsupportedBusNumberException 
     */
    public Ht16k33(String bus, int i2cAddress) throws IOException, UnsupportedBusNumberException {
    	I2CBus pioService = I2CFactory.getInstance(I2CBus.BUS_1);
    	mDevice = pioService.getDevice(i2cAddress);
    }

    /**
     * Enable oscillator and LED display.
     * @throws IOException
     */
    public void setEnabled(boolean enabled) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not opened");
        }
        int oscillator_flag = enabled ? HT16K33_OSCILLATOR_ON : HT16K33_OSCILLATOR_OFF;
        mDevice.write(1, new byte[]{(byte) (HT16K33_CMD_SYSTEM_SETUP | oscillator_flag)});
        int display_flag = enabled ? HT16K33_DISPLAY_ON : HT16K33_DISPLAY_OFF;
        mDevice.write(1, new byte[]{(byte) (HT16K33_CMD_DISPLAYSETUP | display_flag)});
    }

    /**
     * Set LED display brightness.
     * @param value brigthness value between 0 and {@link #HT16K33_BRIGHTNESS_MAX}
     */
    public void setBrightness(int value) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not opened");
        }
        if (value < 0 || value > HT16K33_BRIGHTNESS_MAX) {
            throw new IllegalArgumentException("brightness must be between 0 and " +
                    HT16K33_BRIGHTNESS_MAX);
        }
        mDevice.write(1, new byte[]{(byte) (HT16K33_CMD_BRIGHTNESS | value)});
    }

    /**
     * Set LED display brightness.
     * @param value brigthness value between 0 and 1.0f
     */
    public void setBrightness(float value) throws IOException {
        int val = Math.round(value * HT16K33_BRIGHTNESS_MAX);
        setBrightness(val);
    }
    
    public void writeColumn(int column, byte data) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not opened");
        }
        mDevice.write(column * 2, data);
    }

}