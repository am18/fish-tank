

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.*;

public class Shark
{
  private int body;
  private int tail;
  private int fin1;
  private int fin2;
  private int dorsal_fin; //modeled with OpenGL SolidCone
  private float angle;
  private float tailangle;
  private float tail_direction;
  private float finangle;
  private float fin_direction;
  private double dir_x, dir_y, dir_z;
  private float speed;
  private float scale;
  private Fish prey; //keep track of prey
  private double dx, dy, dz; //predator-prey potentials
  private double back_wall, front_wall, top_wall, bottom_wall,left_wall, right_wall; // wall potentials 
  public float x, y, z; //position values for shark
  public float radius; //for bounding sphere

  public Shark( int _x, int _y, int _z, float _scale)
  {
	  x = _x;
	  y = _y;
	  z = _z;
	  angle = 0;
	  tailangle = 0;
	  tail_direction = 1;
	  finangle = 0;
	  fin_direction = 1;
	  speed = 0.005f;
	  body = 0;
	  tail = 0;
	  fin1 = 0;
	  fin2 = 0;
	  dorsal_fin = 0;
	  dir_x = 0.03;
	  dir_y = 0.01;
	  dir_z = 0.02;
	  scale = _scale;
	  radius = 0.3f*scale;//based on openGL initialization
  }
  
  public void getFish(Fish _prey){
	  prey = _prey;  
  }

  public void init( GL2 gl )
  {
	  GLUT glut = new GLUT();
	  //body
    body = gl.glGenLists(1); 
    gl.glNewList( body, GL2.GL_COMPILE );
    	gl.glPushMatrix();
    		gl.glTranslated(0, 0, 0.2*1.3f);
    		gl.glScaled(0.3f,  0.5f,  1.3f);
    		glut.glutSolidSphere(0.2, 36, 18);
    	gl.glPopMatrix();
    gl.glEndList();
    
    //tail
   tail = gl.glGenLists(1);
    gl.glNewList( tail, GL2.GL_COMPILE );
    	gl.glPushMatrix();
    		gl.glTranslated(0, 0, -0.2*0.3f);
    		gl.glScaled(0.2f,  0.5f,  0.3f);
    		glut.glutSolidSphere(0.2, 36, 18);
    	gl.glPopMatrix();
    gl.glEndList();
    
  //fin1
    fin1 = gl.glGenLists(1);
     gl.glNewList( fin1, GL2.GL_COMPILE );
     	gl.glPushMatrix();
     		gl.glTranslated(0.2*0.3f, 0, 0.2*1.3f);
     		gl.glScaled(0.45f,  0.1f,  0.22f);
     		glut.glutSolidSphere(0.2, 36, 18);
     	gl.glPopMatrix();
     gl.glEndList();
     
   //fin2
     fin2 = gl.glGenLists(1);
     gl.glNewList( fin2, GL2.GL_COMPILE );
     	gl.glPushMatrix();
     		gl.glTranslated(-0.2*0.3f, 0, 0.2*1.3f);
     		gl.glScaled(0.45f,  0.1f,  0.22f);
     		glut.glutSolidSphere(0.2, 36, 18);
     	gl.glPopMatrix();
     gl.glEndList();
     
     //dorsal fin
     dorsal_fin = gl.glGenLists(1);
     gl.glNewList( dorsal_fin, GL2.GL_COMPILE );
     	gl.glPushMatrix();
     		gl.glTranslated(0, 0.2*0.5f, 0.17f);
     		gl.glScaled(0.1f,  0.44f,  0.3f);
     		glut.glutSolidCone(0.2, 0.4, 36, 18);
     	gl.glPopMatrix();
     gl.glEndList();

  } 
  public void update( GL2 gl )
  {
	  /**
	   * animates the shark such that
	   * (1) it moves within world limits
	   * (2) it flaps its fins and tail, with dormant dorsal fin 
	   */
    flap();
    swim();
  }
  
  public void flap( )
  {
    tailangle += 2*tail_direction;
    if(tailangle > 20 ||tailangle < -20)
    	tail_direction = -tail_direction;
    
    finangle +=1*fin_direction;
    if(finangle > 10 ||finangle < -10)
    	fin_direction = -fin_direction;
  }
 
  private void swim(){
	  
	  //gradient of potential functions
	  dz = (-2)*(z - prey.zp)*Math.exp(-( (x - prey.xp)*(x - prey.xp) + (y - prey.yp)*(y - prey.yp)  + (z - prey.zp)*(z - prey.zp) ) );
	  front_wall = 2*(z-2)*Math.exp(-( (z-2)*(z-2) ) );
	  back_wall = 2*(z+2)*Math.exp(-( (z+2)*(z+2) ) );
	  z = (float)(z + speed*dir_z + dz*speed + front_wall*speed + back_wall*speed);
	  
	  dy = (-2)*(y - prey.yp)*Math.exp(-( (x - prey.xp)*(x - prey.xp) + (y - prey.yp)*(y - prey.yp)  + (z - prey.zp)*(z - prey.zp) ) );
	  bottom_wall = 2*(y+2)*Math.exp(-( (y +2)*(y +2) ) );
	  top_wall = 2*(y-2)*Math.exp(-( (y -2)*(y -2) ) );   
	  y = (float)(y + speed*dir_y + dy*speed + top_wall*speed + bottom_wall*speed);
	  
	  dx = (-2)*(x - prey.xp)*Math.exp(-( (x - prey.xp)*(x - prey.xp) + (y - prey.yp)*(y - prey.yp)  + (z - prey.zp)*(z - prey.zp) ) );
	  right_wall = 2*(x-2)*Math.exp(-( (x-2)*(x-2) ) );
	  left_wall = 2*(x+2)*Math.exp(-( (x+2)*(x+2) ) );
	  x = (float)(x  + speed*dir_x + dx*speed + right_wall*speed + left_wall*speed);
	  //summation of all gradients multiplied by significance
	  
	  
	  

  }

  public void draw( GL2 gl )
  {
    gl.glPushMatrix();
    gl.glPushAttrib( GL2.GL_CURRENT_BIT );
    	gl.glColor3f( 0.5f, 0.5f, 0.5f); // gray
    	gl.glPushMatrix();
    	gl.glScalef(scale, scale,scale);
    	gl.glTranslatef(x, y, z);
    	gl.glRotated(angle, 0, 1, 0);
    	gl.glCallList(body);
	    	gl.glPushMatrix();
	    		gl.glRotated(tailangle, 0, 1, 0);
	    		gl.glCallList(tail);
	    	gl.glPopMatrix();
	    	gl.glPushMatrix();
    			gl.glRotated(finangle, 0, 0, 1);
    			gl.glCallList(fin1);
    		gl.glPopMatrix();
    		gl.glPushMatrix();
    			gl.glRotated(-finangle, 0, 0, 1);
    			gl.glCallList(fin2);
    		gl.glPopMatrix();
    		gl.glPushMatrix();
				gl.glCallList(dorsal_fin);
			gl.glPopMatrix();
    	gl.glPopMatrix();
	gl.glPopAttrib();
    gl.glPopMatrix();
  }
}
