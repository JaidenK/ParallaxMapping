import java.io.File;
import java.io.FileInputStream;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.input.Keyboard.*;

import king.jaiden.util.*;

public class ParallaxMain extends ApplicationWindow {
	/* 
	 * Note that I use the word "depth" kind of backwards
	 * I should say instead "protrusion" but depth sounds
	 * nicer.
	 */
	
	Texture texture; // the actual texture shown
	Texture map; // a gray-scale image representing depth
	double[][] depth; // the depth of each pixel
	
	double factor;
	
	public static void main(String[] args){
		new ParallaxMain();
	}
	public ParallaxMain(){
		super(new IntCoord(1280,720),// Window dimensions, in pixels
			  70,// FOV
			  "Parallax-inspired depth mapping",// Window title
			  false,// is fullscreen?
			  ApplicationWindow.THREE_DIMENSIONAL);// Matrix mode
	}
	public void init() {
		try{	// assign the texture to the image of the bricks
			texture = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/images/bricks.png")));
			map = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/images/bricks_map.png")));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		// array size is one bigger than the dimensions of the image
		depth = new double[513][513];
		// move the camera away from the origin.
		dst = -256;
		// and a little to the right;
		xPan = 0;
		// and down a little
		yPan = 0;
		
		panMod = 0.1;
		
		factor = 10;
		
		byte[] data = map.getTextureData();
		for(int r = 0; r<512; r++){
			for(int c = 0; c<512; c++){
				int i = r*512+c;
//				System.out.println(i);
				int x = (int)(data[i*3]);
				if(x>=0){
					x-=256;
				}
				depth[r][c] = x;
//				System.out.println((int)data[i*3]);
			}
		}
	}
	public void input(){
		super.input();
		while(Keyboard.next()){
			if(Keyboard.isKeyDown(Keyboard.getEventKey())){
				switch(Keyboard.getEventKey()){
				case KEY_UP:
					factor+=0.2;
					
					break;
				case KEY_DOWN:
					factor-=0.2;
					break;
				}
			}
		}
	}
	public void draw(){
		super.draw();
		glTranslated(-256,-255,0);
		glBindTexture(GL_TEXTURE_2D,texture.getTextureID());
		for(int r = 512; r>0; r--){
			glBegin(GL_TRIANGLE_STRIP);
			for(int c = 0; c <= 512; c++){
				glTexCoord2d(c/512d,1-r/512d);
				glVertex3d(c,r,depth[r][c]/factor);
				glTexCoord2d(c/512d,1-(r-1)/512d);
				glVertex3d(c,r-1,depth[r-1][c]/factor);
			}
			glEnd();
		}
	}
}
