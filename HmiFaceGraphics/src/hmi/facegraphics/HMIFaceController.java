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
package hmi.facegraphics;

import hmi.faceanimation.FaceController;
import hmi.faceanimation.model.FAP;
import hmi.faceanimation.model.MPEG4;
import hmi.faceanimation.model.MPEG4Configuration;
import hmi.graphics.opengl.scenegraph.GLScene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The FaceController is the access point for deforming the face of an avatar, just like VJoints are the accesspoint for deforming its body.
 * 
 * This implementation controls the face of an avatar displayed in the HMI rendering environment.
 * 
 * 
 */
public class HMIFaceController implements FaceController
{

    /** Used to control morph targets... */
    private GLScene theGLScene;
    private Collection<String> possibleFaceMorphTargetNames = new ArrayList<String>();

    /** store the current config, for add- and remove-mpeg4configuration */
    private MPEG4Configuration currentConfig = new MPEG4Configuration();
    private GLHead glHead;

    private ArrayList<HMIFaceControllerListener> listeners = new ArrayList<HMIFaceControllerListener>();

    /**
     * This constructor needs to get a link to the face and its morph targets! Maybe the GLScene, for setting morph targets, and some of Ronald Paul's
     * stuff, for controlling FAPs
     */
    public HMIFaceController(GLScene gls, GLHead h)
    {
        theGLScene = gls;
        glHead = h;
    }

    public synchronized void setMPEG4Configuration(MPEG4Configuration config)
    {
        currentConfig = config;
        notifyFaceControllerListeners();
    }

    public synchronized void addMPEG4Configuration(MPEG4Configuration config)
    {
        currentConfig.addValues(config);
        notifyFaceControllerListeners();
    }

    public synchronized void removeMPEG4Configuration(MPEG4Configuration config)
    {
        currentConfig.removeValues(config);
        notifyFaceControllerListeners();
    }

    public Collection<String> getPossibleFaceMorphTargetNames()
    {
        return possibleFaceMorphTargetNames;
    }

    public void setPossibleFaceMorphTargetNames(Collection<String> names)
    {
        possibleFaceMorphTargetNames = names;
    }

    /**
     * NOTE: this function should be called in some synchronisation; to ensure that the values are not changed during the copy method!
     */
    public synchronized void copy()
    {
        if (glHead != null)
        {
            // send the new MPEG4 configuration
            for (FAP fap : MPEG4.getFAPs().values())
            {
                Integer value = currentConfig.getValue(fap.getIndex());
                if (value == null) continue;
                glHead.getDeformer(fap).setValue(value);
            }
            glHead.deformWhenScheduled();
        }

        // on copy, send the current morph targets to the face...
        String[] targetNames = new String[oldDesiredMorphTargets.size()];
        float[] targetWeights = new float[oldDesiredMorphTargets.size()];
        int i = 0;
        for (String targetName : oldDesiredMorphTargets.keySet())
        {
            targetNames[i] = targetName;
            targetWeights[i] = oldDesiredMorphTargets.get(targetName);
            i++;
        }
        theGLScene.removeMorphTargets(targetNames, targetWeights);
        targetNames = new String[desiredMorphTargets.size()];
        targetWeights = new float[desiredMorphTargets.size()];
        i = 0;
        for (String targetName : desiredMorphTargets.keySet())
        {
            targetNames[i] = targetName;
            targetWeights[i] = desiredMorphTargets.get(targetName);
            i++;
        }
        theGLScene.addMorphTargets(targetNames, targetWeights);
        oldDesiredMorphTargets = new HashMap<String, Float>(desiredMorphTargets);
    }

    /*
     * ====================================================== Methods and attributes for maintaining a set of desired morph targets. - Add and remove
     * weight from each target using addMorphTargets() and removeMorphTargets() - have GLScene execute the current set of targets and weights by
     * calling doMorph() ==================================================
     */

    /**
     * The set of morph targets to be set by doMorph, maintained through addMorphTargets and removeMorphTargets
     */
    private HashMap<String, Float> oldDesiredMorphTargets = new HashMap<String, Float>();
    private HashMap<String, Float> desiredMorphTargets = new HashMap<String, Float>();

    /** Add given weights for given morph targets to the list of desired targets */
    public synchronized void addMorphTargets(String[] targetNames, float[] weights)
    {
        float w = 0;
        for (int i = 0; i < targetNames.length; i++)
        {
            w = weights[i];
            Float fl = desiredMorphTargets.get(targetNames[i]);
            if (fl != null)
            {
                w += fl.floatValue();
            }
            if (w == 0)
            {
                desiredMorphTargets.remove(targetNames[i]);
            }
            else
            {
                desiredMorphTargets.put(targetNames[i], new Float(w));
            }
        }
    }

    @Override
    public void setMorphTargets(String[] targetNames, float[] weights)
    {
        for (int i = 0; i < targetNames.length; i++)
        {
            desiredMorphTargets.put(targetNames[i], new Float(weights[i]));
        }
    }

    /** Remove given weights for given morph targets from the list of desired targets */
    public synchronized void removeMorphTargets(String[] targetNames, float[] weights)
    {
        float w = 0;
        for (int i = 0; i < targetNames.length; i++)
        {
            w = -weights[i];
            Float fl = desiredMorphTargets.get(targetNames[i]);
            if (fl != null)
            {
                w += fl.floatValue();
            }
            if (w == 0)
            {
                desiredMorphTargets.remove(targetNames[i]);
            }
            else
            {
                desiredMorphTargets.put(targetNames[i], new Float(w));
            }
        }
    }

    public void addFaceControllerListener(HMIFaceControllerListener fcl)
    {
        synchronized (listeners)
        {
            listeners.add(fcl);
        }
    }

    public void removeFaceControllerListener(HMIFaceControllerListener fcl)
    {
        synchronized (listeners)
        {
            listeners.remove(fcl);
        }
    }

    private void notifyFaceControllerListeners()
    {
        synchronized (listeners)
        {
            for (HMIFaceControllerListener fcl : listeners)
            {
                fcl.setMPEG4Configuration(currentConfig);
            }
        }
    }

}