package hmi.graphics.examples;
import java.awt.Component;
import java.util.*;
import hmi.graphics.opengl.*;
import hmi.util.*;
import hmi.math.*;
import hmi.graphics.collada.*;
import hmi.graphics.collada.scenegraph.*;
import hmi.graphics.scenegraph.*;
import hmi.graphics.opengl.*;
import hmi.graphics.opengl.state.*;
import hmi.graphics.opengl.scenegraph.*;
import hmi.graphics.opengl.renderobjects.*;
import hmi.animation.*;
import hmi.graphics.util.*;
import java.io.*;
 
public class ColladaTestScenePupilSizeSetting  implements GLClockedRenderObject {  
   private VGLNode scene;
   private VJoint sceneRoot;
   private OpenGLState openglState;        
   private VJoint bodyNGRootJoint;
   private VJoint bipRootJoint;
   private VJoint avatarRootJoint;
   private VGLNode avatarNode;
   private GLScene glScene;
   private SkeletonInterpolator ski;
   private SkeletonInterpolator adaptedSkel;
   
   private LightBox lights;
   private GLNavigation2 glNav; 
   
   public ColladaTestScenePupilSizeSetting(Component canvas) {       
  
      sceneRoot = new VJoint("sceneRoot");
      sceneRoot.setTranslation(0.0f, 0.0f, 0.0f); // should be used for navigation
      scene = new VGLNode("ShadowScene");
      sceneRoot.addChild(scene.getRoot());
 
      openglState = new OpenGLState();      
      
      lights = new LightBox(3);
      lights.get(0).setDiffuseColor(0.8f, 0.8f, 0.8f);   // The (initial) position of the light
      lights.get(1).setDiffuseColor(0.7f, 0.0f, 0.0f);   // The (initial) position of the light
      lights.get(2).setDiffuseColor(0.0f, 0.0f, 0.8f);   // The (initial) position of the light
      lights.get(0).setSpecularColor(0.1f, 0.1f, 0.1f);   // The (initial) position of the light
     
      lights.get(0).setPosition(1.0f, 1.5f, 1.0f);   // The (initial) position of the light
      lights.get(1).setPosition(0.0f, 1.0f, 0.0f);   // The (initial) position of the light
      lights.get(2).setPosition(8.0f, 2.5f, -3.0f);   // The (initial) position of the light
          
      glNav = new  GLNavigation2(canvas);
   
      lights.setControl(glNav);
      
     // glNav.setPosition(0.0f, 1.65f, 3.0f);   // The (initial) position of the camera 
      glNav.setPosition(0.0f, 1.65f, 0.15f);   // zoomed in on eye region
      glNav.setOrientation(0f, 0.0f, 0f);
       
      glNav.setLinearVelocity(0.7f);
      glNav.setVerticalVelocity(1.0f);
      glNav.setStrafeVelocity(1.0f);
    
      GLTextureLoader.addTextureDirectory("Humanoids/armandia/maps");
     
      GLShaderProgramLoader.clearShaderPool();
      GLShaderProgramLoader.addShaderDirectory("shaders");
 
      //glScene = SceneIO.readGLScene("Humanoids/armandia/dae", "armandia-toplevel.dae", SceneIO.ARMANDIA) ;
      glScene = SceneIO.readGLScene("Humanoids/armandia/bin", "ArmandiaPupilSizeSetting.bin", SceneIO.NONE) ;
     
 
      avatarRootJoint = glScene.getToplevelVJoint();
      glScene.sortGLShapeList();
      GLRenderList shapeList = glScene.getGLShapeList();
      avatarNode = new VGLNode(avatarRootJoint, shapeList);   
      scene.addChild(avatarNode);
 
     GLMaterial leftPupil = glScene.getGLMaterial("Eye_L-mesh", "irisModified");
     if (leftPupil != null) {
         leftPupil.setPupilSize(0.03f); //  original/ummodified: 0.0586f
     }
     
     GLMaterial rightPupil = glScene.getGLMaterial("Eye_R-mesh", "irisModified");
     if (rightPupil != null) {
         rightPupil.setPupilSize(0.05f); //  original/ummodified: 0.0586f
     }
        
   }   
   
   
   public synchronized void initTime(double t) { 
      glNav.initTime(t);  
   }
  
   public synchronized void time(double t) {  
      glNav.time(t);  
      sceneRoot.calculateMatrices();
      glScene.deform();
   }
   
   public synchronized void glInit(GLRenderContext glc) {                   
      openglState.glInit(glc);
      lights.glInit(glc);
      glNav.glInit(glc);
      scene.glInit(glc);
   }
   
 
   public synchronized void glRender(GLRenderContext glc) {  
      openglState.glRender(glc);
      glNav.glRender(glc);
      lights.glRender(glc);
      scene.glRender(glc);
      hmi.graphics.opengl.GLUtil.reportGLErrors(glc);
   }    
}     



