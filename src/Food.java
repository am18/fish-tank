

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.*;

public class Food
{
	private int body;
	public float fx, fy, fz; //position of food
	public float radius; //for bounding sphere
	private Fish fish; //keep track of eater
	public boolean eaten=true; //initially no food in the vivarium
  

  public Food( int _x, int _y, int _z)
  {
	  fx = _x;
	  fy = _y;
	  fz = _z;
	  radius = 0.07f;  
  }
  public void getEater(Fish _fish){
	  fish = _fish;  
  }

  public void init( GL2 gl )
  {
	  GLUT glut = new GLUT();
	  //body
	  body = gl.glGenLists(1); 
	  gl.glNewList( body, GL2.GL_COMPILE );
	  	gl.glPushMatrix();
	  	gl.glTranslated(0, 0.1, 0);
	  	gl.glScaled(0.7f,  0.7f,  0.7f);
	  	glut.glutSolidSphere(0.1, 9, 12);
	  	gl.glPopMatrix();
	  gl.glEndList();

  }
 
  public void update( GL2 gl )
  {
	  /**
	   * checks whether food's been eaten or not
	   */
		eat();   
  }
  
  //collision detection
  private void eat(){
	  double distance = Math.sqrt( (fx-fish.xp)*(fx-fish.xp) + (fy-fish.yp)*(fy-fish.yp) + (fz-fish.zp)*(fz-fish.zp));
	  if((distance >(fish.radius + radius)))
		 eaten = false;
	  else
		 eaten = true;
  }
  
  public void draw( GL2 gl )
  {
    gl.glPushMatrix();
    gl.glPushAttrib( GL2.GL_CURRENT_BIT );
    	gl.glColor3f(0.0f, 1.0f, 0.0f); //green
    	gl.glPushMatrix();
    	gl.glTranslatef(fx, fy, fz);
    	gl.glCallList(body);
	 gl.glPopMatrix(); 
	gl.glPopAttrib();
    gl.glPopMatrix();
  }
}
