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
package hmi.physics.ode;

import hmi.graphics.scenegraph.GMesh;

import org.odejava.GeomTriMesh;

/**
 * Physics utilities
 * @author welberge
 *
 */
public final class Tools
{
    private Tools(){}
    /**
     * Creates a GeomTrimes from the original vertices and normals set in a GenericMesh
     * @param gm the generic mesh
     * @return the new GeomTriMesh
     */
    public static GeomTriMesh getGeomTriMesh(GMesh gm)
    {
        return new GeomTriMesh(gm.getVertexData("mcPosition"), gm.getVertexData("mcNormal"), gm.getIndexData());        
    }
}