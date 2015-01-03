package com.life.util;

public class BigInt {
    public int[] digits = new int[130];
    public boolean isNeg;
    public BigInt()
    {
        int i;
        for (i = 0; i < 130; i++)
        {
            digits[i] = 0;
        }
        isNeg = false;
    }

}
