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
/*
 * VObject.java
 */

package hmi.animation;

/**
 * VObjects represent &quot;virtual objects&quot;, used within virtual environments.
 * VObjects can have an id, an sid, and a name. An id is meant to uniquely identify some VObject.
 * An sid (scoped id) is assumed to be unique only amongst children of the same parent VObject.
 * A name is a "friendly" name, not required to be unique.
 */
public interface VObject 
{

   /**
    * VObject.Predicates are Objects that implement a boolean test on VObjects,
    * in the form of their "valid" method. If some VObject vo satifies
    * some predicate pred, then pred.valid(vo) should yield "true".
    */
   public interface Predicate {
      boolean valid(VObject obj);
   } 
       
   
   /* ******************  ID/SID/NAME ************************/
   
   /** Sets the id for this VObject.    */
   void setId(String id) ;
   
   /** Sets the sid for this VObject.   */
   void setSid(String sid) ;
   
   /** Sets the name for this VObject.  */
   void setName(String name) ;
   
   
   /** Returns an interned String that specifies the id.   */
   String getId() ;
   
   /** Returns an interned String that specifies the sid.  */
   String getSid() ;
   
   /** Returns an interned String that specifies the name. */
   String getName() ;


   /* ******************  TRANSFORMATION ************************/
   
   /** Returns the translation vector in the form of a float array of length three  */
   void getTranslation(float[] ta) ;
  
   /** Like getTranslation(ta), but starting at the specified offset  */
   void getTranslation(float[] ta, int offset) ;
 

   /** Sets the translation vector from a float array of length three.  */
   void setTranslation(float[] ta);
   
   /** Like setTranslation(ta), but starting at the specified offset.   */
   void setTranslation(float[] ta, int offset) ;
   
   
   /** Sets the current translation vector from three floats.  */
   void setTranslation(float tx, float ty, float tz) ;
   
   
   /**
    * Sets the rotation quaternion from a float array of length four.
    * The order is (s, x, y, z), where s is the real part, x, y, z are the imaginary parts.
    */
   void setRotation(float[] ra) ;
   
   /**
    * Like setRotation(ra), but starting at the specified offset.
    */
   void setRotation(float[] ra, int offset) ;
   
   /**
    * Returns the rotation quaternion in a float array of length four.
    * The order is (s, x, y, z), where s is the scalar part, x, y, z are the imaginary parts.
    */
   void getRotation(float[] ra) ;
   
   /**
    * Like getRotation(ra), but starting at the specified offset.
    */   
   void getRotation(float ra[], int offset) ;
   
   
   /**
    * Sets the rotation quaternion from four floats.
    * qs is the real part, qx, qy, qz the imaginary parts.
    */
   void setRotation(float qs, float qx, float qy, float qz) ;
   
  
   /**
    * Sets the rotation quaternion derived from a rotation
    * <em>axis</em> (ax, ay, az) and a rotation <em>angle</em> angle, specified in radians.
    * The axis need not have length one. If all parameters are zero, 
    * the rotation is set to the identity quaternion (1,0,0,0).
    */
   void setAxisAngle(float ax, float ay, float az, float angle);
   
  
   /** Sets the  scale vector from a float array of lebgth three.  */
   void setScale(float[] sa) ;
   
   /** Like setScale(sa), but starting at the specified offset */
   void setScale(float[] sa, int saIndex) ;
   
   
   /** Returns the  scale vector in a float array of length three.  */
   void getScale(float[] sa) ;
   
   /** Like getScale(sa), but starting at the specified offset.  */   
   void getScale(float sa[], int offset) ;
   
   
   /* ******************  PHYSICS ************************/
   
   /** Returns the  velocity vector in the form of a float array of length three.  */
   void getVelocity(float[] va) ;
  
   /** Like getVelocity(va), but starting at the specified offset. */
   void getVelocity(float[] va, int offset) ;


   /** Sets the  velocity vector from a float array of length three.  */
   void setVelocity(float[] va);
   
   /** Like setVelocity(va), but starting at the specified offset. */
   void setVelocity(float[] va, int offset) ;
   
   /** Sets the  velocity vector from three floats.   */
   void setVelocity(float vx, float vy, float vz) ;
   
   /** Returns the angular velocity vector in the form of a float array of length three.*/
   void getAngularVelocity(float[] wa) ;
  
   /** Like getAngularVelocity(wa), but starting at the specified offset. */
   void getAngularVelocity(float[] wa, int offset) ;
  

   /** Sets the angular velocity vector from a float array of length three.  */
   void setAngularVelocity(float[] wa);
   
   /** Like setAngularVelocity(wa), but starting at the specified offset. */
   void setAngularVelocity(float[] wa, int offset) ;


   /** Sets the angular velocity vector from three floats.  */
   void setAngularVelocity(float wx, float wy, float wz) ;
  
}
