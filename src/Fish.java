

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.*;

public class Fish
{
  private int body;
  private int tail;
  private int fin1;
  private int fin2;
  private float angle;
  private float tailangle;
  private float tail_direction;
  private float finangle;
  private float fin_direction;
  private float speed;
  private float scale;
  private double dir_x, dir_y, dir_z;
  private Shark pred; //keep track of predator position
  private Food food = new Food(0,0,0); //keep track of food position
  private double dx, dy, dz; //prey-predator potential
  private double dx_food, dy_food, dz_food; //food-eater potential
  private double back_wall, front_wall, top_wall, bottom_wall, left_wall, right_wall; // wall potentials
  public float xp,yp,zp; //position values for fish
  public float radius; //for bounding sphere
  public boolean death; //updated in update()
  

  public Fish( int _x, int _y, int _z, float _scale)
  {
	  xp = _x;
	  yp = _y;
	  zp = _z;
	  angle = 0;
	  tailangle = 0;
	  tail_direction = 1;
	  finangle = 0;
	  fin_direction = 1;
	  speed = 0.03f;
	  body = 0;
	  tail = 0;
	  fin1 = 0;
	  fin2 = 0;
	  dir_x = 0.02;
	  dir_y = 0.04;
	  dir_z = 0.01;
	  scale = _scale;
	  radius = (0.299f)*scale; //based on openGL initialization
  }
  
  public void getShark(Shark _predator){
	  pred = _predator;
  }
  
  public void getFood(Food _food){
	  food = _food;
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
  		gl.glScaled(0.3f,  0.1f,  0.2f);
  		glut.glutSolidSphere(0.2, 36, 18);
  	gl.glPopMatrix();
  gl.glEndList();
  
  //fin2
  fin2 = gl.glGenLists(1);
  gl.glNewList( fin2, GL2.GL_COMPILE );
  	gl.glPushMatrix();
  		gl.glTranslated(-0.2*0.3f, 0, 0.2*1.3f);
  		gl.glScaled(0.3f,  0.1f,  0.2f);
  		glut.glutSolidSphere(0.2, 36, 18);
  	gl.glPopMatrix();
  gl.glEndList();
    

  }

  
  
  public void update( GL2 gl )
  {
	  /**
	   * Animates the fish such that
	   * (1) it moves within world limits
	   * (2) it flaps its fins and tail
	   */
		flap();
		swim();
		death();
    
  }
  
  public void flap( )
  {
    tailangle += 2*tail_direction;
    if(tailangle > 20 ||tailangle < -20)
    	tail_direction = -tail_direction;
    
    finangle +=1*fin_direction;
    if(finangle > 8 ||finangle < -8)
    	fin_direction = -fin_direction;
  }
  
  //collision detection
  //fish is alive if distance between prey and predator
  //is greater than sum of radii of bounding spheres
  private void death(){
	  double distance = Math.sqrt( (xp-pred.x)*(xp-pred.x) + (yp-pred.y)*(yp-pred.y) + (zp-pred.z)*(zp-pred.z) );  
	  if( distance > (pred.radius + radius)){ 
		 death = false;
	  }
	  else
		 death = true;
  }
  
  private void swim(){
	  if (food.eaten == false){
		  dz_food = -2*(zp - food.fz)*Math.exp(-( (xp- food.fx)*(xp- food.fx) + (yp - food.fy)*(yp - food.fy)  + (zp - food.fz)*(zp - food.fz) ) );
		  dy_food = -2*(yp - food.fy)*Math.exp(-( (xp- food.fx)*(xp- food.fx) + (yp - food.fy)*(yp - food.fy)  + (zp - food.fz)*(zp - food.fz) ) );
		  dx_food = -2*(xp - food.fx)*Math.exp(-( (xp- food.fx)*(xp- food.fx) + (yp - food.fy)*(yp - food.fy)  + (zp - food.fz)*(zp - food.fz) ) );
	  }
	  else{
		  dz_food = 0;
		  dy_food = 0;
		  dx_food = 0;
	  }
	  
	  //gradient of potential functions
	  dz = 2*(zp - pred.z)*Math.exp(-( (xp- pred.x)*(xp- pred.x) + (yp - pred.y)*(yp - pred.y)  + (zp - pred.z)*(zp - pred.z) ) );	  
	  back_wall = 2*(zp-2)*Math.exp(-( (zp-2)*(zp-2) ) );
	  front_wall = 2*(zp+2)*Math.exp(-( (zp+2)*(zp+2) ) );
	  zp = (float)(zp + dz*speed*1.1 + dz_food*speed*0.8f + speed*dir_z + back_wall*speed*0.9f + front_wall*speed*0.9f);
	  
	  dy = 2*(yp - pred.y)*Math.exp(-( (xp- pred.x)*(xp- pred.x) + (yp - pred.y)*(yp - pred.y)  + (zp - pred.z)*(zp - pred.z) ) ); 
	  bottom_wall = 2*(yp+2)*Math.exp(-((yp +2)*(yp +2)));
	  top_wall = 2*(yp-2)*Math.exp(-((yp -2)*(yp -2)));
	  yp = (float)(yp + dy*speed*1.1 + dy_food*speed*0.8f + speed*dir_y + bottom_wall*speed*0.9f + top_wall*speed*0.9f);
	  
	  dx = 2*(xp - pred.x)*Math.exp(-( (xp- pred.x)*(xp- pred.x) + (yp - pred.y)*(yp - pred.y)  + (zp - pred.z)*(zp - pred.z) ) );   
	  right_wall = 2*(xp - 2)*Math.exp(-( (xp-2)*(xp-2) ) );
	  left_wall = 2*(xp + 2)*Math.exp(-( (xp+2)*(xp+2) ) );

	  //summation of all gradients multiplied by significance
	 
	  
	  
  }

  public void draw( GL2 gl )
  {
    gl.glPushMatrix();
    gl.glPushAttrib( GL2.GL_CURRENT_BIT );
    	gl.glColor3f( 1, 0.5f, 0); // orange
    	gl.glPushMatrix();
    	gl.glScalef(scale, scale,scale);
    	gl.glTranslatef(xp, yp, zp);
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
	    gl.glPopMatrix(); 
	gl.glPopAttrib();
    gl.glPopMatrix();
  }
}
