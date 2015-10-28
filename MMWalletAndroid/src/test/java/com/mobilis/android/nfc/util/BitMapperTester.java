package com.mobilis.android.nfc.util;


import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

public class BitMapperTester {
    String TAG = "BitMapperTester";

    @Test
    public void testMenuGTopUpIndex() {
        String menuG = "0012";
        int index = 0;
        boolean menuSet = false;
        BigInteger menuHex = new BigInteger(menuG, 16);
        System.out.println("testMenuGTopUpIndex() " + menuG + "..." + menuHex);


        System.out.println("testMenuGTopUpIndex()" + menuHex.bitCount());
        System.out.println("testMenuGTopUpIndex()" + menuHex.bitLength());
        for (int i = 0; i < menuHex.bitLength(); i++) {
            menuSet = false;
            if (menuHex.testBit(i)) {
                menuSet = true;
            }
            System.out.println("testMenuGTopUpIndex() " + menuG + "..." + menuHex + " testBit " + i + "   " + menuSet);

        }
        //assertEquals(1, hasTopUp);
        assertTrue(menuSet);
    }

   /* @Test
    public void testMenuGTopUpIndex0() {
        String menuG = "0011";
        boolean hasTopUp = BitsMapper.parseBitSet(menuG, 0);
        //assertEquals(1, hasTopUp);
        assertTrue(hasTopUp);
    }*/
}