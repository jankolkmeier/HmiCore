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
/* 
 */

package hmi.graphics.colladatest;

import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;


/**
 * Settings for a normal, i.e. non-shadow,  render pass
 */
public class RenderpassState implements GLRenderObject {

 

   /**
    * Creates a new RenderpassState
    */
   public RenderpassState() {
   }

   /**
    * initializes various OpenGL state variables
    */
   public void glInit(GLRenderContext gl) {

   }

   /**
    * callback function that must be called regularly, in essence
    * every time a new frame is rendered.
    * It clears the necessary OpenGL buffers (color, depth, ..)
    */
   public void glRender(GLRenderContext gl) {
      gl.glEnable(GLC.GL_DEPTH_TEST);
      gl.glEnable(GLC.GL_LIGHTING);
//      gl.glEnable(GLC.GL_TEXTURE_2D);
      gl.glDisable(GLC.GL_BLEND);     
      gl.glDisable(GLC.GL_STENCIL_TEST);
   } 
  
            
}
