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
package hmi.tts;

import java.io.IOException;

/**
 * Generic interface to a TTSGenerator
 * @author welberge
 */
public interface TTSBridge
{
    /**
     * Speaks out the sentence, does the appropriate callbacks, fills out visime,
     * @param text the text or script to speak
     */
    TTSTiming speak(String text) throws TTSException;

    /**
     * Generates a file containing the spoken sentence, does the apropiate
     * callbacks, fills out visime, phoneme, bookmark and word lists.
     * 
     * @param text
     *            the text or script to speak
     * @throws FileNotFoundException
     * @throws IOException
     */
    TTSTiming speakToFile(String text, String filename) throws IOException, TTSException;

    /**
     * Gets the timing of the text, fills out visime, phoneme,bookmark and word
     * lists
     * 
     * @param text
     *            the text or script to get the timing of
     */
    TTSTiming getTiming(String text) throws TTSException;
}
