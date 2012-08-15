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

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;


/** 
 * Defines a set of texturing commands for regular and combiner mode.
 * @author Job Zwiers
 */
public class TextureUnit extends ColladaElement {
 
   // attributes: sid, inherited from ColladaElement,
      
   // child elements:
   private Surface surface;
   private SamplerState samplerState;
   private String texcoord;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public TextureUnit() {
      super();
   }
   
   public TextureUnit(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);    
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, surface);
      appendXMLStructure(buf, fmt, samplerState);
      appendTextElement(buf, "texcoord", texcoord, fmt);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }
   
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Surface.xmlTag()))  {                
                 surface = new Surface(getCollada(), tokenizer);
         } else if (tag.equals(SamplerState.xmlTag()))  {                
                 samplerState = new SamplerState(getCollada(), tokenizer);
         } else if (tag.equals("texcoord"))  {                
                 texcoord = tokenizer.takeTextElement("texcoord"); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("TextureUnit: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(surface);
      addColladaNode(samplerState);
      addColladaNodes(extras);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "texture_unit";
 
   /**
    * The XML Stag for XML encoding
    */
   public static String xmlTag() { return XMLTAG; }
 
   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }

}