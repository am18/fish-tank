

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.util.*;

public class Vivarium
{
  private Tank tank;
  private Fish fish;
  private Shark shark;
  private Food food;
  private int add=0; //to keep track of food

  public Vivarium()
  {
    tank = new Tank( 4.0f, 4.0f, 4.0f ); 
    fish =  new Fish(0, 0, 0, 1);
    shark = new Shark(1, 1, 1, 1.5f);
    food = new Food(0,-2,0);
    
    fish.getShark(shark);
    shark.getFish(fish);
    food.getEater(fish);
    
  }
  //function to keep track of whether user has pressed 'F/f'
  public void getadd(){
		  add+=1;
		  food.eaten = false;
		  fish.getFood(food);
  }

  public void init( GL2 gl )
  {
    tank.init( gl );
    fish.init( gl );
    shark.init( gl );
    food.init(gl);
  }

  public void update( GL2 gl ){
	  if (fish.death==false){ //if the fish hasn't been eaten
		  tank.update( gl );
		  fish.update( gl );
		  shark.update( gl );
		  if (food.eaten==false){food.update(gl);}
	  }
	  else{
		  tank.update( gl );
		  shark.update(gl);
	  }
  }

  public void draw( GL2 gl ){   
    if (fish.death==false){
    	tank.draw( gl );
    	fish.draw( gl );
    	shark.draw( gl );
    	if(add!=0 &&(food.eaten==false))
        	food.draw(gl);
    }
    else{
    	tank.draw( gl );
    	shark.draw( gl );
    	if(add!=0 &&(food.eaten==false)){
    		food.draw(gl);}
    }
   
  }
}
