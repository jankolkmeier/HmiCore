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
package hmi.faceanimation.model;

import hmi.faceanimation.model.FAP.Direction;
import hmi.faceanimation.model.FAP.Directionality;
import hmi.faceanimation.model.FAP.Unit;
import hmi.util.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A MPEG-4 FA system. This is a static utility class for reading FAPs and and feature points.
 * 
 * Notice: tongue_roll has two feature points, but this is yet to be accounted for.
 * 
 * @author PaulRC
 */
public class MPEG4
{
    static private LinkedHashMap<Integer, FAP> faps;
    static private LinkedHashMap<String, FeaturePoint> fps;

    /**
     * Static block for reading faps and feature points.
     */
    static
    {
        faps = new LinkedHashMap<Integer, FAP>();
        fps = new LinkedHashMap<String, FeaturePoint>();

        // We're going to read the feature points from a file.
        try
        {
            String filename = "feature_points.txt";
            BufferedReader br = new Resources("Humanoids/shared/mpeg4face/").getReader(filename);
            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.length() < 3)
                    continue;
                if (line.substring(0, 1).equals("#"))
                    continue;
                fps.put(line, new FeaturePoint(line));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // We're going to read the FAPs from a file.
        try
        {
            String filename = "facial_animation_parameters.txt";
            BufferedReader br = new Resources("Humanoids/shared/mpeg4face/").getReader(filename);
            String line;
            int index = 0;

            FAP fap;
            HashMap<FAP, Integer> otherSides = new HashMap<FAP, Integer>();

            while ((line = br.readLine()) != null)
            {
                String[] elts = line.split("\t");
                if (elts.length == 7 || elts.length == 8)
                {
                    // Preprocess directionality
                    Directionality directionality = Directionality.NA;
                    if (elts[4].equals("U"))
                        directionality = Directionality.UNIDIRECTIONAL;
                    else if (elts[4].equals("B"))
                        directionality = Directionality.BIDIRECTIONAL;

                    // Preprocess direction
                    Direction direction = null;
                    if (elts[5].equals("concave upward"))
                        direction = Direction.CONCAVE_UPWARD;
                    else
                        direction = Direction.valueOf(elts[5].toUpperCase());

                    int number = Integer.valueOf(elts[0]);
                    fap = new FAP(index++, number, elts[1], elts[2], Unit.valueOf(elts[3]), directionality, direction, getFeaturePoint(elts[6]));
                    faps.put(number, fap);

                    if (elts.length == 8)
                    {
                        otherSides.put(fap, Integer.parseInt(elts[7]));
                    }
                }
                else if (elts.length == 4)
                {
                    int number = Integer.valueOf(elts[0]);
                    faps.put(number, new FAP(index++, number, elts[1], elts[2], getFeaturePoint(elts[3])));
                }
            }

            // Resolve the other sides (left vs. right)
            for (Entry<FAP, Integer> entry : otherSides.entrySet())
            {
                int otherSide = entry.getValue();
                entry.getKey().setOtherSide(faps.get(otherSide));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        StringBuffer retval = new StringBuffer();
        retval.append("[MPEG4, list of Facial Animation Parameters:\n");
        for (FAP fap : faps.values())
        {
            retval.append("\t");
            retval.append(fap.toString());
            retval.append("\n");
        }
        retval.append("]");
        return retval.toString();
    }

    static public HashMap<Integer, FAP> getFAPs()
    {
        return faps;
    }

    static public FAP getFAP(int number)
    {
        return faps.get(number);
    }

    static public HashMap<String, FeaturePoint> getFeaturePoints()
    {
        return fps;
    }

    static public FeaturePoint getFeaturePoint(String fp)
    {
        return fps.get(fp);
    }
}
