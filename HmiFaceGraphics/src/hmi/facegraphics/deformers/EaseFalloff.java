/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hmi.facegraphics.deformers;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EaseFalloff
{
    private EaseFalloff(){}
    
    static public enum Method
    {
        SIMPLE, SMOOTH
    };

    static private Method method = Method.SIMPLE;
    static private float smoothLeft = 0.2f;
    static private float smoothRight = 0.2f;

    static public void setMethod(Method method)
    {
        EaseFalloff.method = method;
    }

    static public void setSmoothLeft(float smoothLeft)
    {
        EaseFalloff.smoothLeft = smoothLeft;
    }

    static public void setSmoothRight(float smoothRight)
    {
        EaseFalloff.smoothRight = smoothRight;
    }

    static float getFalloff(float ease, float distanceRatio)
    {
        if (method == Method.SIMPLE)
            return getSimpleFalloff(ease, distanceRatio);
        else if (method == Method.SMOOTH)
            return getSmoothFalloff(ease, distanceRatio);
        else
            return 0;
    }

    static float getSimpleFalloff(float ease, float distanceRatio)
    {
        // float pv = principalValue(mapEaseToExponent(ease), distanceRatio);
        float retval = 1f - principalValue(mapEaseToExponent(ease), distanceRatio);
        return retval;
    }

    static float getSmoothFalloff(float ease, float distanceRatio)
    {
        float pv = principalValue(mapEaseToExponent(ease), distanceRatio);
        float retval = 1f - smooth(distanceRatio, pv);
        return retval;
    }

    /*
     * smoothLeft(w, x, y) = (tanh(x/(.5*w)*pi-pi)/2+.5) * y
     */
    static private float smoothLeft(float x, float y)
    {
        return (float) ((Math.tanh(x / (0.5 * smoothLeft) * Math.PI - Math.PI) / 2.0 + 0.5) * y);
    }

    /*
     * smoothRight(w, x, y) = (tanh((x-(1-w))/(.5*w)*pi-pi)/2+.5) * ((1-y) + y)
     */
    static private float smoothRight(float x, float y)
    {
        return (float) ((Math.tanh((x - (1.0 - smoothRight)) / (0.5 * smoothRight) * Math.PI - Math.PI) / 2.0 + 0.5) * (1 - y) + y);
    }

    /*
     * smooth(l, r, x, y) = smoothLeft(l, x, smoothRight(r, x, y))
     */
    static private float smooth(float x, float y)
    {
        return smoothLeft(x, smoothRight(x, y));
    }

    static float mapEaseToExponent(float ease)
    {
        /*
         * We are going to map [-1:0] to [0:1] and <0:1] to <1:6]
         */

        if (ease <= 0)
            return ease + 1f;
        else if (ease > 0)
            return ease * 5f + 1f;
        else
            return 0;
    }

    /*
     * The 'normal' unsmoothed curve.
     */
    static float principalValue(float exponent, float distanceRatio)
    {
        return (float) Math.pow(distanceRatio, exponent);
    }

    public static void main(String[] args)
    {
        NumberFormat formatter = new DecimalFormat("0.00");

        for (float x = 0f; x < 1.05f; x += 0.05f)
        {
            float pv = principalValue(1, x);
            System.out.println("x: " + formatter.format(x) + ", normal: " + formatter.format(pv) + ", smooth left: "
                    + formatter.format(smoothLeft(x, pv)) + ", smooth right: " + formatter.format(smoothRight(x, pv)) + ", smooth both: "
                    + formatter.format(smooth(x, pv)));
        }
    }
}