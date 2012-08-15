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
 
package hmi.math;

/**
 * Efficient implementation of the 6x6 spatial transform matrix
 * As defined in
 * 
 * Rigid Body Dynamics Algorithms
 * Roy Featherstone
 * 2007
 * 
 * using a 12-element float array
 * @author Herwin van Welbergen
 */
public final class SpatialTransform
{
    public static final int R = 9;    
    
    private SpatialTransform(){}
    
    /**
     * Length of SpatialTransform arrays is 12
     */
    public static final int SPATIALTRANSFORM_SIZE = 12;
    
    /**
     * Returns a new float[12] array with zero components
     */
    public static float[] getSpatialTransform() 
    {
       return new float[SPATIALTRANSFORM_SIZE];
    }
    
    /**
     * Sets the spatial transform
     * @param dst the spatial transform (lenght 12 float array)
     * @param m 3x3 rotation matrix, specified as length 9 float array
     * @param tr 3x1 translation vector
     */
    public static void setFromMat3fVec3f(float[] dst, float[] m, float[] tr)
    {
        for(int i=0;i<9;i++)
        {
            dst[i] = m[i];            
        }
        dst[R] = tr[0];
        dst[R+1] = tr[1];
        dst[R+2] = tr[2];
    }
    
    /**
     * Sets the spatial transform
     * @param dst the spatial transform (lenght 12 float array)
     * @param q quaternion
     * @param tr 3x1 translation vector
     */
    public static void setFromQuat4fVec3f(float[] dst, float[] q, float[] tr)
    {
        Mat3f.setFromQuatScale(dst, q, 1.0f);
        dst[R] = tr[0];
        dst[R+1] = tr[1];
        dst[R+2] = tr[2];
    }
    
    /**
     * Sets the spatial transform from another spatial transform
     */
    public static void set(float[] dst, float[] src)
    {
        System.arraycopy(src, 0, dst, 0, SPATIALTRANSFORM_SIZE);
    }
    
    /**
     * Sets the spatial transform from another spatial transform
     */
    public static void set(float[] dst, int dIndex, float[] src, int srcIndex)
    {
        System.arraycopy(src, srcIndex, dst, dIndex, SPATIALTRANSFORM_SIZE);
    }
    
    /**
     * Sets the spatial transform
     * @param dst the spatial transform (lenght 12 float array)
     * @param q quaternion
     * @param tr 3x1 translation vector
     */
    public static void setFromQuat4fVec3f(float[] dst, int dstIndex, float[] q, int qIndex, float[] tr, int trIndex)
    {
        Mat3f.setFromQuatScale(dst,dstIndex, q,qIndex, 1.0f);
        //Mat3f.transpose(dst,dstIndex);
        dst[dstIndex+R] = tr[trIndex];
        dst[dstIndex+R+1] = tr[trIndex+1];
        dst[dstIndex+R+2] = tr[trIndex+2];
    }
    
    /**
     * Transforms a vec6
     * @param trans the spatial transform
     * @param src the vec6
     * dest = spx(E,r)spv(v,v0)
     */
    public static void transformMotion(float[] dest, float[] trans, float[] src)
    {
        //mv(E * w, E( v - r x w ))
        Mat3f.transform(trans, dest, src);
        
        //v - r x w
        float cx = src[3] - (trans[R+1]*src[2] - trans[R+2] * src[1]);
        float cy = src[4] - (trans[R+2]*src[0] - trans[R]   * src[2]);
        float cz = src[5] - (trans[R]  *src[1] - trans[R+1] * src[0]);
        
        Mat3f.transform(trans, dest, 3, cx,cy,cz);
    }
    
    /**
     * Transforms a spatial motion vector
     * @param trans the spatial transform
     * @param src the vec6
     * dest = spx(E,r)spv(v,v0)
     */
    public static void transformMotion(float[] dest, int destI, float[] trans, int transI, float[] src, int srcI)
    {
        //spv(E w, E( v - r x w))
        Mat3f.transform(trans, transI, dest, destI, src, srcI);
        
        //v - r x w
        float cx = src[3+srcI] - (trans[transI+R+1]*src[srcI+2] - trans[transI+R+2] * src[srcI+1]);
        float cy = src[4+srcI] - (trans[transI+R+2]*src[srcI] - trans[transI+R]   * src[srcI+2]);
        float cz = src[5+srcI] - (trans[transI+R]  *src[srcI+1] - trans[transI+R+1] * src[srcI]);
        
        Mat3f.transform(trans, transI, dest, 3+destI, cx,cy,cz);
    }
    
    /**
     * Transforms a spatial force vector
     * Vec6 src, Vec6 dest, 12 float  spatial transform trans.
     * @param trans the spatial transform
     */
    public static void transformForce(float[] dest, float[] trans, float[] src)
    {
        // trans = (E,r)   (Mat3f E, Vec3f r)
        // src = (n, f)    (Vec3f n Vec3f f)
        //dest = (E * (n - r x f), E * f)  
        
        //save r x f in dest
        Vec3f.cross(dest, 3, trans, R, src, 3);
        
        //n - r x f
        Vec3f.sub(dest, 0, src, 0, dest, 3);
        
        //E(n - r x f)
        Mat3f.transform(trans,0, dest, 3);
        
        //E * f
        Mat3f.transform(trans,0,dest, 3,src,3);
    }
    
    /**
     * Transforms a spatial force vector
     * @param trans the spatial transform
     */
    public static void transformForce(float[] dest, int destIndex, float[] trans, int transIndex, float[] src, int srcIndex)
    {
        //(E * (n - r x f), E * f)
        
        //r x f
        Vec3f.cross(dest, destIndex+3, trans, transIndex+R, src, srcIndex+3);
        
        //n - r x f
        Vec3f.sub(dest, destIndex, src, srcIndex, dest, destIndex+3);
        
        //E * (n - r x f)
        Mat3f.transform(trans,transIndex, dest, destIndex);
        
        //E * f
        Mat3f.transform(trans,transIndex,dest, destIndex+3, src,srcIndex+3);
    }
    
    /**
     * Transforms a spatial motion vector with the transpose
     * @param trans the spatial transform
     * @param src the vec6     
     */
    public static void transformMotionTranspose(float[] dest, float[] trans, float[] src)
    {
        //mv( E^T * w, r x (E^T * w) + E^T * v)
        
        //E^T * w
        Mat3f.transformTranspose(trans, dest, src);
        
        //E^T * v
        Mat3f.transformTranspose( trans,0, dest, 3,src,3);
        
        //E^T * v + r x (E^T * w)
        dest[3] += trans[R+1]*dest[2] - trans[R+2] * dest[1];
        dest[4] += trans[R+2]*dest[0] - trans[R]   * dest[2];
        dest[5] += trans[R]  *dest[1] - trans[R+1] * dest[0];
    }
    
    /**
     * Transforms a spatial motion vector with the transpose
     * @param trans the spatial transform
     * @param src the vec6     
     */
    public static void transformMotionTranspose(float[] dest, int destI, float[] trans, int transI, float[] src, int srcI)
    {
        //mv(E^T * w, r x (E^T * w)+E^T * v)
        
        //E^T * w
        Mat3f.transformTranspose( trans, transI, dest, destI,src, srcI);
        
        //E^T * v
        Mat3f.transformTranspose( trans, transI, dest, 3+destI,src,3+srcI);
        
        //E^T * v + r x (E^T * w)
        dest[destI+3]   += trans[transI+R+1]*dest[destI+2] - trans[transI+R+2] * dest[destI+1];
        dest[destI+4] += trans[transI+R+2]*dest[destI]   - trans[transI+R]   * dest[destI+2];
        dest[destI+5] += trans[transI+R]  *dest[destI+1] - trans[transI+R+1] * dest[destI];
    }
    
    /**
     * Transforms a spatial force vector with the transpose
     */
    public static void transformForceTranspose(float[] dest, float[] trans, float[] src)
    {
        //fv(E^T*n + r x E^T * f, E^T * f)
        
        //E^T * f
        Mat3f.transformTranspose(trans, 0, dest, 0, src, 3);
        float fx = dest[0];
        float fy = dest[1];
        float fz = dest[2];
        
        //r x E^T * f
        Vec3f.cross(dest, 3, trans, R, dest, 0);
        
        //E^T * n
        Mat3f.transformTranspose(trans,dest,src);
        
        //E^T * n + r x E^T * f
        Vec3f.add(dest, 0, dest, 3);
        
        //E^T * f
        Vec3f.set(dest,3,fx,fy,fz);
    }
    
    /**
     * Transforms a spatial force vector with the transpose
     */
    public static void transformForceTranspose(float[] dest, int destIndex, float[] trans, int transIndex, float[] src, int srcIndex)
    {
        //fv(E^T*n + r x E^T * f, E^T * f)
        
        //E^T * f
        Mat3f.transformTranspose( trans, transIndex, dest, destIndex,src, srcIndex+3);
        float fx = dest[destIndex];
        float fy = dest[destIndex+1];
        float fz = dest[destIndex+2];
        
        //r x E^T * f
        Vec3f.cross( dest, destIndex+3,trans, transIndex+R, dest, destIndex);
        
        //E^T * n
        Mat3f.transformTranspose(trans,transIndex,dest,destIndex,src,srcIndex);
        
        //E^T * n + r x E^T * f
        Vec3f.add(dest, destIndex, dest, destIndex+3);
        
        //E^T * f
        Vec3f.set(dest,destIndex+3,fx,fy,fz);
    }
    
    /**
     * Multiplies spatial transforms a and b and stores the result in dest
     * dest is not allowed to be aliased with a or b 
     */
    public static void mul(float[] dest, float[] a, float[] b)
    {
        //spx(E1,r1)spx(E2,r2)=spx(E1E2,r2+E2^-1r1)        
        Mat3f.mul(dest, a, b);
        Mat3f.transformTranspose(b, 0, dest, R,a, R);
        Vec3f.add(dest, R, b, R);
    }
    
    /**
     * Multiplies spatial transforms a and b and stores the result in dest
     * dest is not allowed to be aliased with a or b 
     */
    public static void mul(float[] dest, int dIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        //spx(E1,r1)spx(E2,r2)=spx(E1E2,r2+E2^-1r1)        
        Mat3f.mul(dest, dIndex, a, aIndex, b, bIndex);
        Mat3f.transformTranspose(b, bIndex, dest, dIndex+R,a, aIndex+R);
        Vec3f.add(dest, dIndex+R, b, bIndex+R);
    }
    
    /**
     * Sets the transpose of dst in dst 
     */
    public static void transpose(float[] dst)
    {
        Mat3f.transform(dst, 0, dst, R);
        Vec3f.scale(-1, dst,R);
        Mat3f.transpose(dst);
    }  
    
    /**
     * Sets the transpose of a in dest, dest can not be aliased in a
     */
    public static void transpose(float[] dest, float[] a)
    {
        Mat3f.transform(a, 0, dest, R, a, R);
        Vec3f.scale(-1, dest,R);
        Mat3f.transpose(dest, a);
    } 
    
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon)
    {
        if (!Mat3f.epsilonEquals(a, b, epsilon))return false;
        return Vec3f.epsilonEquals(a, R, b, R, epsilon);
    }
    
    public static String toString(float[] a)
    {
        return Mat3f.toString(a)+Vec3f.toString(a, R);
    }
    
    public static String toString(float[] a, int aIndex)
    {
        return Mat3f.toStringTabbed(a,aIndex)+Vec3f.toString(a, aIndex+R);
    }
    /**
     * The identity matrix.
     */
    public static final float[] ID = new float[] {1f, 0f, 0f,
                                                  0f, 1f, 0f,
                                                  0f, 0f, 1f,
                                                  0f, 0f, 0f};  
    
}