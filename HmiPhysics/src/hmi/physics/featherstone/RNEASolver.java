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
package hmi.physics.featherstone;

import hmi.math.Quat4f;
import hmi.math.SpatialInertiaTensor;
import hmi.math.SpatialTransform;
import hmi.math.SpatialVec;
import hmi.math.Vec3f;

/**
 * Recursive Newton-Euler inverse dynamics solver, 
 * based on 
 * Rigid Body Dynamics Algorithms
 * Roy Featherstone
 * 2007
 * 
 * Solves for a branch of ball joints, does not handle external forces (yet)
 * 
 * Further work (contributions are welcome): 
 *  - support for hinge/universal joints
 *  - support for external forces
 * @author Herwin van Welbergen 
 */
public class RNEASolver
{
    private float vi[];
    private float ai[];
    private float X[];
    private float r[];
    private float iX0[]   = new float[13];
    private int size = 0;
    private float[] tempv = new float[6];
    private float[] tempv2 = new float[6];
    private float[] cwi = new float[6];
    private float I[];
    private float F[];
    private float tempq[] = new float[4];
    private int lambda[] = null;
    
    /**
     * Constructor
     * Convention: joint 0 is always the root joint
     * @param joints number of joints
     * @param translations joint-to-joint translation vectors (an array of at least joint*3 length)
     * @param spatialI spatial inertia tensors (an array of at least joint*13 length)
     * @param parent the parent array, providing the parent of each joint, -1 for root
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public RNEASolver(int joints, float[] translations, float[] spatialI, int[] parent)
    {
        vi = new float[(joints+1)*6];
        ai = new float[(joints+1)*6];
        F  = new float[joints*6];
        X  = new float[joints*12];
        I  = spatialI;
        size = joints+1;
        r = translations;        
        lambda = parent;
    }
    
    /**
     * Constructor
     * @param joints number of joints
     * @param translations joint-to-joint translation vectors (an array of at least joint*3 length)
     * @param spatialI spatial inertia tensors (an array of at least joint*13 length)
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public RNEASolver(int joints, float[] translations, float spatialI[])
    {
        vi = new float[(joints+1)*6];
        ai = new float[(joints+1)*6];
        F  = new float[joints*6];
        X  = new float[joints*12];
        I  = spatialI;
        size = joints+1;
        r = translations;        
    }
    
    /**
     * Solves for the forces on each joint
     * @param f         output: spatial force on each joint
     * @param v0        spatial velocity of the base frame
     * @param a0        spatial acceleration of the base frame 
     * @param qi        quaternion joint rotations
     * @param wi        local joint angular velocities
     * @param diffwi    local joint angular accelerations
     */
    public void solve(float f[], float[]v0, float[]a0, float qi[], float[]wi, float[]diffwi)
    {
        SpatialVec.set(vi,v0);
        SpatialVec.set(ai,a0);
        
        //construct transforms
        for(int i=0;i<size-1;i++)
        {
            Quat4f.conjugate(tempq,0,qi,i*4);
            //iXlambdai = XjXt(i)
            SpatialTransform.setFromQuat4fVec3f(X,i*12,tempq,0,r,i*3);
        }        
        cwi[3]=0;
        cwi[4]=0;
        cwi[5]=0;
        
        //vi = iXlambdai vlambdai + [wi 
        //                           0]
        for(int i=1;i<size;i++)
        {
            SpatialTransform.transformMotion(vi,i*6,X,(i-1)*12,vi,(lambda[i-1]+1)*6);
            vi[i*6]+=wi[(i-1)*3];
            vi[i*6+1]+=wi[(i-1)*3+1];
            vi[i*6+2]+=wi[(i-1)*3+2];
        }
        
        //ai = iX_lambdai a_lambdai + vi x [wi  + [diffwi
        //                                  0]      0]        
        for(int i=1;i<size;i++)
        {
            SpatialTransform.transformMotion(ai,i*6,X,(i-1)*12,ai,(lambda[i-1]+1)*6);
            Vec3f.set(cwi,0,wi,(i-1)*3);
            SpatialVec.cross(tempv,0, vi,i*6, cwi,0);
            SpatialVec.add(ai,i*6,tempv,0);
            ai[i*6]+=diffwi[(i-1)*3];
            ai[i*6+1]+=diffwi[(i-1)*3+1];
            ai[i*6+2]+=diffwi[(i-1)*3+2];
        }
        
        SpatialTransform.set(iX0,X);
        //Fi = Ii ai + vi x* (Ii * vi) - iX0 * fx
        for(int i=1;i<size;i++)
        {
            //Ii * ai
            SpatialInertiaTensor.transformSpatialVec(f,(i-1)*6, I,(i-1)*13, ai,i*6);
            
            //Ii * vi
            SpatialInertiaTensor.transformSpatialVec(tempv,0, I,(i-1)*13, vi,i*6);
            
            //vi x* Ii * vi
            SpatialVec.crossForce(tempv2,0, vi,i*6, tempv,0);
            
            //Ii * ai + vi x* Ii * vi
            SpatialVec.add(f,(i-1)*6, tempv2,0);
            
            /* XXX: handle external forces, outline is below
            iX0 = iXi-1 i-1X0
            SpatialTransform.mul(tempX,0, X,(i-1)*12, iX0,0);
            SpatialTransform.set(iX0, tempX);
            SpatialTransform.transformForce(tempFx, iX0, fx);
            */
        }     
        
        //flambdai = flambdai + lambdayiXi fi
        for(int i=size-1;i>1;i--)
        {
            SpatialTransform.transformForceTranspose(tempv,0, X, (i-1)*12, f,(i-1)*6);
            SpatialVec.add(f, (lambda[i-1])*6, tempv, 0);            
        }        
    }
    
    /**
     * Solves for the forces on each joint
     * @param f         output: spatial force on each joint
     * @param v0        spatial velocity of the base frame
     * @param a0        spatial acceleration of the base frame 
     * @param qi        quaternion joint rotations
     * @param wi        local joint angular velocities
     * @param diffwi    local joint angular accelerations
     */
    public void solveChain(float f[], float[]v0, float[]a0, float qi[], float[]wi, float[]diffwi)
    {
        SpatialVec.set(vi,v0);
        SpatialVec.set(ai,a0);
        
        //construct transforms
        for(int i=0;i<size-1;i++)
        {
            Quat4f.conjugate(tempq,0,qi,i*4);
            SpatialTransform.setFromQuat4fVec3f(X,i*12,tempq,0,r,i*3);
        }        
        cwi[3]=0;
        cwi[4]=0;
        cwi[5]=0;
        
        //vi = iXi-1 vi-1 + [wi 
        //                   0]
        for(int i=1;i<size;i++)
        {
            SpatialTransform.transformMotion(vi,i*6,X,(i-1)*12,vi,(i-1)*6);
            vi[i*6]+=wi[(i-1)*3];
            vi[i*6+1]+=wi[(i-1)*3+1];
            vi[i*6+2]+=wi[(i-1)*3+2];
        }
        
        //ai = iXi-1 ai-1 + vi x [wi  + [diffwi
        //                         0]      0]        
        for(int i=1;i<size;i++)
        {
            SpatialTransform.transformMotion(ai,i*6,X,(i-1)*12,ai,(i-1)*6);
            Vec3f.set(cwi,0,wi,(i-1)*3);
            SpatialVec.cross(tempv,0, vi,i*6, cwi,0);
            SpatialVec.add(ai,i*6,tempv,0);
            ai[i*6]+=diffwi[(i-1)*3];
            ai[i*6+1]+=diffwi[(i-1)*3+1];
            ai[i*6+2]+=diffwi[(i-1)*3+2];
        }
        
        SpatialTransform.set(iX0,X);
        //Fi = Ii ai + vi x* (Ii * vi) - iX0 * fx
        for(int i=1;i<size;i++)
        {
            //Ii * ai
            SpatialInertiaTensor.transformSpatialVec(F,(i-1)*6, I,(i-1)*13, ai,i*6);
            
            //Ii * vi
            SpatialInertiaTensor.transformSpatialVec(tempv,0, I,(i-1)*13, vi,i*6);
            
            //vi x* Ii * vi
            SpatialVec.crossForce(tempv2,0, vi,i*6, tempv,0);
            
            //Ii * ai + vi x* Ii * vi
            SpatialVec.add(F,(i-1)*6, tempv2,0);
            
            /* TODO: handle external forces, outline is below
            iX0 = iXi-1 i-1X0
            SpatialTransform.mul(tempX,0, X,(i-1)*12, iX0,0);
            SpatialTransform.set(iX0, tempX);
            SpatialTransform.transformForce(tempFx, iX0, fx);
            */
        }     
        SpatialVec.set(f,(size-2)*6, F,(size-2)*6);
        
        
        
        //fi = iXi+1 fi+1 + Fi
        for(int i=size-3;i>=0;i--)
        {
            SpatialTransform.transformForceTranspose(f,i*6, X, (i+1)*12, f,(i+1)*6);
            SpatialVec.add(f, i*6, F, i*6);
        }        
    }
}