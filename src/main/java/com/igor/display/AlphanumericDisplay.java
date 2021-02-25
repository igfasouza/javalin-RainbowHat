package com.igor.display;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * I2C wrapper class for 14-segment displays powered by an ht16k33 chip, providing methods for
 * displaying common types of data.
 */
public class AlphanumericDisplay extends Ht16k33 {

    private ByteBuffer mBuffer = ByteBuffer.allocate(8);

    /**
     * Create a new driver for a HT16K33 based alphanumeric display connected on the given I2C bus
     * using the {@link Ht16k33#I2C_ADDRESS default I2C address}.
     * @param bus
     * @throws IOException
     * @throws UnsupportedBusNumberException 
     */
    public AlphanumericDisplay(String bus) throws IOException, UnsupportedBusNumberException {
        super(bus);
    }

    /**
     * Create a new driver for a HT16K33 based alphanumeric display connected on the given I2C bus
     * and using the given I2C address.
     * @param bus
     * @param i2cAddress
     * @throws IOException
     * @throws UnsupportedBusNumberException 
     */
    public AlphanumericDisplay(String bus, int i2cAddress) throws IOException, UnsupportedBusNumberException {
        super(bus, i2cAddress);
    }

    /**
     * Clear the display memory.
     */
    public void clear() throws IOException {
        for (int i = 0; i < 4; i++) {
            writeColumn(i, (byte) 0);
        }
    }

    /**
     * Display a character at the given index.
     * @param index index of the segment display
     * @param c character value
     * @param dot state of the dot LED
     */
    public void display(char c, int index, boolean dot) throws IOException {
        int val = Font.DATA[c];
        if (dot) {
            val |= Font.DATA['.'];
        }
        writeColumn(index, (byte) val);
    }

    /**
     * Display a decimal number.
     * @param n number value
     */
    public void display(double n) throws IOException {
        // pad with leading space until we get 5 chars
        // since double always get formatted with a dot
        // and the dot doesn't consume any space on the display.
        display(String.format("%5s", n));
    }

    /**
     * Display an integer number.
     * @param n number value
     */
    public void display(int n) throws IOException {
        // pad with leading space until we get 4 chars
        display(String.format("%4s", n));
    }

    /**
     * Display a string.
     * @param s string value
     */
    public void display(String s) throws IOException {

        mBuffer.clear();
        mBuffer.mark();

        short prevFontData = 0;
        for (char c : s.toCharArray()) {
            byte fontData = (byte) Font.DATA[c];
            if (c == '.' && prevFontData != Font.DATA['.']) {
                mBuffer.reset();
                fontData = (byte)(prevFontData | fontData);
            }
            if (mBuffer.position() == mBuffer.limit()) {
                break;
            }
            mBuffer.mark();
            mBuffer.put(fontData);
            prevFontData = fontData;
        }

        // clear the rest of the display
        while (mBuffer.position() < mBuffer.capacity()) {
            mBuffer.put((byte) 0);
        }

        mBuffer.flip();
        // write display memory.
        for (int i = 0; i < mBuffer.limit() / 2; i++) {
            writeColumn(i, mBuffer.get());
        }
    }
    
}