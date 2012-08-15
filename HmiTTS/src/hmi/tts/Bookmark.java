/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.tts;

import net.jcip.annotations.Immutable;

/**
 * Bookmark in speech text. Contains a word description of the word after the
 * bookmark and an offset in ms of the bookmark time relative to the start of
 * the speech text. word is null if the bookmark is at the end of the sentence
 * 
 * @author welberge
 */
@Immutable
public final class Bookmark implements Comparable<Bookmark>
{
    private final String name;

    private final WordDescription word;

    private final int offset;

    public Bookmark(String n, WordDescription w, int o)
    {
        name = n;
        word = w;
        offset = o;
    }

    @Override
    public String toString()
    {
        return "bookmark: " + name + " wordDescription:" + word + "offset: "
                + offset;
    }

    @Override
    public int compareTo(Bookmark o)
    {
        if (offset < o.offset)
            return -1;
        if (offset > o.offset)
            return 1;
        return 0;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Word description of the word after this bookmark, null for none.
     * @return
     */
    public WordDescription getWord()
    {
        return word;
    }

    public int getOffset()
    {
        return offset;
    }

    @Override
    public int hashCode()
    {
        // ensures that equal Bookmarks have equal hash codes
        return offset;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Bookmark)
        {
            return ((Bookmark) o).offset == offset;
        }
        return super.equals(o);
    }
}