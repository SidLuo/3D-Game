package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;


/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */

public class Game extends JFrame implements GLEventListener, KeyListener{

    private Terrain myTerrain;
    private Camera myCamera;
    private Avatar myAvatar;
    private Lighting myLight;
    private boolean night;
    private boolean third;
    
    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        myCamera = new Camera(terrain);
        myAvatar = new Avatar(myCamera);
        myLight = new Lighting(myCamera);
        night = false;
        third = true;
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
    	  GLProfile glp = GLProfile.getDefault();
          GLCapabilities caps = new GLCapabilities(glp);
          GLJPanel panel = new GLJPanel(caps);
          panel.addGLEventListener(this);
          panel.addKeyListener(this);
 
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 600);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		if (!night) gl.glClearColor(0.88f, 1f, 1f, 1.0f);
		else gl.glClearColor(.1f, .1f, .1f, 1);
    	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    	gl.glMatrixMode(GL2.GL_MODELVIEW);
    	gl.glLoadIdentity();
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV,GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		// separate SPECULAR colour from the textures
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
		
		// place the camera
        myCamera.setup(gl);
        myLight.draw(gl, myTerrain.getSunlight());
        
		myTerrain.draw(gl);
		if (third) myAvatar.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0f, 0f, 0f, 1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enable depth testing.
		gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        
        // Specify how texture values combine with current surface color values.
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

    	// Turn on OpenGL texturing.
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	
    	//myLight.setup(gl, myTerrain.getSunlight());
		myTerrain.loadTextures(gl);
		myAvatar.loadTexture(gl);
		myCamera.setup(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// tell the camera and the mouse that the screen has reshaped
        GL2 gl = drawable.getGL().getGL2();
        if(height <= 0) height = 1;
        gl.glViewport(0,0,width,height);
        myCamera.setAspect(1.0f * width / height);
        
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			myCamera.move(0.1);
			break;
		case KeyEvent.VK_DOWN:
			myCamera.move(-0.1);
			break;
		case KeyEvent.VK_LEFT:
			myCamera.rotate(-0.1);
			break;
		case KeyEvent.VK_RIGHT:
			myCamera.rotate(0.1);
			break;
		case KeyEvent.VK_EQUALS:
			myCamera.shiftUp(0.1);
			break;
		case KeyEvent.VK_MINUS:
			myCamera.shiftUp(-0.1);
			break;
			
		case KeyEvent.VK_X:
			myCamera.zoomIn(1);
			break;
		case KeyEvent.VK_Z:
			myCamera.zoomIn(-1);
			break;
		case KeyEvent.VK_M:
			third = !third;
			myCamera.changeMode();
			break;
		case KeyEvent.VK_N:
			myLight.switchMode();
			myAvatar.setNight(night);
			night = !night;
			break;
		case KeyEvent.VK_R:
			if(!night)
				myLight.switchSun();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
