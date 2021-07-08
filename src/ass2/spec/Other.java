package ass2.spec;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import ass2.spec.Shader;

public class Other {
	
	double pos[];
	private static final String VERTEX_SHADER = "shaders/PhongVertexTex.glsl";
    private static final String FRAGMENT_SHADER_DAY = "shaders/PhongFragmentTex.glsl";
    //private static final String FRAGMENT_SHADER_NIGHT = "shaders/PhongFragmentSpot.glsl";
    private int shaderprogram;
    private int[] shaders;
    private boolean useShaders;
    private int[] bufferIds; //VBO
    
    /*
     * Vertices for wall, roof, door and window
     */
    private float[] vertices = 
    	{
    		//Door
    		0,0,0.5f, 0,0.5f,0.5f, 0,0.5f,0.1f,
    		0,0,0.5f, 0,0.5f,0.1f, 0,0,0.1f,
    		
    		//Window
    		0,0.2f,0.9f, 0,0.5f,0.9f, 0,0.5f,0.6f,
    		0,0.2f,0.9f, 0,0.5f,0.6f, 0,0.2f,0.6f,
    		
    		//Front Face 
    		0,0,0, 0,1,0, 1,1,0, 
    		0,0,0, 1,1,0, 1,0,0,
    			
    		//Back Face  
    		1,0,1, 1,1,1, 0,1,1,
    		1,0,1, 0,1,1, 0,0,1,
    		
    		//Top Face
    		0,1,0, 0,1,1, 1,1,1,
    		0,1,0, 1,1,1, 1,1,0,
    		
    		//Bottom Face
    		0,0,1, 0,0,0, 1,0,0,
    		0,0,1, 1,0,0, 1,0,1,
    		
    		//Left Face  
    		1,0,0, 1,1,0, 1,1,1,
    		1,0,0, 1,1,1, 1,0,1, 
    		
    		//Right Face   
    		0,0,1, 0,1,1, 0,1,0,
    		0,0,1, 0,1,0, 0,0,0,
    		
    		//Left UnderRoof
    		1,1,1, 1,1,0, 1,1.5f,0.5f,
    		
    		//Right UnderRoof
    		0,1,1, 0,1.5f,0.5f, 0,1,0,
    		
    		//Front Roof
    		0,1,0, 0,1.5f,0.5f, 1,1.5f,0.5f,
    		0,1,0, 1,1.5f,0.5f, 1,1,0,
    		
    		//Back Roof
    		1,1,1, 1,1.5f,0.5f, 0,1.5f,0.5f,
    		1,1,1, 0,1.5f,0.5f, 0,1,1,
    	};
    
    /*
     * Normals for wall, roof, door and window
     */
    private float[] normals = 
    	{
    		//Door
    		-1,0,0, -1,0,0, -1,0,0, 
    		-1,0,0, -1,0,0, -1,0,0,
    		
    		//Window
    		-1,0,0, -1,0,0, -1,0,0, 
    		-1,0,0, -1,0,0, -1,0,0,
    		
    		//Front Face
    		0,0,-1, 0,0,-1, 0,0,-1, 
    		0,0,-1, 0,0,-1, 0,0,-1,
    		
    		//Back Face
    		0,0,1, 0,0,1, 0,0,1, 
    		0,0,1, 0,0,1, 0,0,1,
    			
    		//Top Face
    		0,1,0, 0,1,0, 0,1,0, 
    		0,1,0, 0,1,0, 0,1,0,
    		
    		//Bottom Face
    		0,-1,0, 0,-1,0, 0,-1,0, 
    		0,-1,0, 0,-1,0, 0,-1,0,

    		//Left Face
    		1,0,0, 1,0,0, 1,0,0, 
    		1,0,0, 1,0,0, 1,0,0,
    		
    		//Right Face
    		-1,0,0, -1,0,0, -1,0,0,
    		-1,0,0, -1,0,0, -1,0,0,
    		
    		//Left UnderRoof
    		1,0,0, 1,0,0, 1,0,0, 
    		
    		//Right UnderRoof
    		-1,0,0, -1,0,0, -1,0,0, 
    		
    		//Front Roof
//    		1,0,0, 1,0,0, 1,0,0, 
//    		1,0,0, 1,0,0, 1,0,0,
    		0,-0.5f,0.5f, 0,-0.5f,0.5f, 0,-0.5f,0.5f,
    		0,-0.5f,0.5f, 0,-0.5f,0.5f, 0,-0.5f,0.5f,
    		
    		//Back Roof
//    		1,0,0, 1,0,0, 1,0,0, 
//    		1,0,0, 1,0,0, 1,0,0,
    		0,-0.5f,-0.5f, 0,-0.5f,-0.5f, 0,-0.5f,-0.5f,
    		0,-0.5f,-0.5f, 0,-0.5f,-0.5f, 0,-0.5f,-0.5f,
    	};
    
    
    // Texture coordinates 
    private float texCoords[] =  
		{	
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
    		1,0, 1,1, 0,1, 
    		1,0, 0,1, 0,0,
    		
		};
    
    
    //These are not vertex buffer objects, they are just java containers
  	private FloatBuffer  posData= Buffers.newDirectFloatBuffer(vertices);
  	private FloatBuffer normalData = Buffers.newDirectFloatBuffer(normals);
  	private FloatBuffer texData = Buffers.newDirectFloatBuffer(texCoords);
    
    //Texture file information
  	private String TEX_DOOR = "textures/door4.jpg";            // texturing door
  	private String TEX_WINDOW = "textures/window2.jpg";          // texturing window
  	private String TEX_WALL = "textures/brick_texture.jpg";    // texturing wall
  	private String TEX_ROOF = "textures/roof5.jpg";            // texturing roof
  	
  	//Texture data
  	private Texture myTextures[] = new Texture[4];
	
	public Other(double x, double y, double z) {
		pos = new double[]{x,y,z};
		useShaders = true;
	}
	
	
	public void setNight(boolean b) {
		if(b) shaderprogram = shaders[1];
		else shaderprogram = shaders[0];
	}
	
	/**
	 *  Set up vbo information of our object
	 * @param gl
	 */
	private void setUpvbo(GL2 gl) {
		bufferIds = new int[1];
		
		//Generate 1 VBO buffer and get its ID
        gl.glGenBuffers(1,bufferIds,0);
       
   	 	//This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
     
        //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,                  //Type of buffer  
       	        vertices.length * Float.SIZE +  normals.length* Float.SIZE + texCoords.length* Float.SIZE, //size needed
       	        null,                                         //We are not actually loading data here yet
       	        GL2.GL_STATIC_DRAW);                          //We expect once we load this data we will not modify it


        //Actually load the positions data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0,            //From byte offset 0
       		 vertices.length*Float.SIZE,posData);

        //Actually load the normal data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
       		 vertices.length*Float.SIZE,                      //Load after the position data
       		 normals.length*Float.SIZE,normalData);
        
        //Actually load the texcoord data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
       		 vertices.length*Float.SIZE+normals.length*Float.SIZE,  //Load after the position data
       		 texCoords.length*Float.SIZE,texData);
        
      
  	   
	}
	
	/**
	 * Load the necessary textures and shaders for the cube
	 * @param gl
	 */
	
	public void loadTextures(GL2 gl) {
		setUpvbo(gl);
		shaders = new int[2];
		try {
			shaders[0] = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER_DAY);   	
			//shaders[1] = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER_NIGHT); 
	    } catch (Exception e) {
			System.err.println("Error while loading  shader");
			e.printStackTrace();
	        System.exit(1);
		}
		int texUnitLoc = gl.glGetUniformLocation(shaders[0],"texUnit1");
		gl.glUniform1i(texUnitLoc, 0);
		texUnitLoc = gl.glGetUniformLocation(shaders[1],"texUnit1");
		gl.glUniform1i(texUnitLoc, 0);
		 
		shaderprogram = shaders[0];
		
		//Initialize textures
		myTextures[0] = new Texture(gl, TEX_DOOR, ".jpg", true);
		myTextures[1] = new Texture(gl, TEX_WINDOW, ".jpg", true);
		myTextures[2] = new Texture(gl, TEX_WALL, ".jpg", true); 
		myTextures[3] = new Texture(gl, TEX_ROOF, ".jpg", true); 
		 
		System.out.println("myTextures: " + myTextures[0].getTextureId());
		System.out.println("myTextures: " + myTextures[1].getTextureId());
		System.out.println("myTextures: " + myTextures[2].getTextureId());
		System.out.println("myTextures: " + myTextures[3].getTextureId());
	}
	
	
	/**
	 * Debug function to activate/deactivate the use of
	 * out shaders
	 */
	public void switchShader() {
		useShaders = !useShaders;
	}
	
	
	/**
	 * Draw the cube (using shaders and VBO)
	 * @param gl
	 */
	
	public void draw(GL2 gl) {
		//gl.glEnable(GL2.GL_DEPTH_BITS);
		
		if(useShaders) {
			gl.glUseProgram(shaderprogram);
		}
		
		gl.glPushMatrix(); {
			gl.glTranslated(pos[0], pos[1], pos[2]);
			//Settings for the VBO
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		        
		    //Specify locations for the co-ordinates and color arrays.
		  	gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0); //last num is the offset
		  	gl.glNormalPointer(GL.GL_FLOAT, 0, (long)vertices.length*Float.SIZE);
		  	gl.glTexCoordPointer(2,GL.GL_FLOAT, 0, (long)vertices.length*Float.SIZE + normals.length*Float.SIZE);
			
			//Material properties
			float matAmbAndDifAvatar[] = {0.7f, 0.6f, 1.0f, 1.0f};
	        float matSpecAvatar[] = { 0.3f, 0.3f, 0.3f, 1.0f };
	        float matShineAvatar[] = { 100.0f };
	        float emmSun[] = {0.0f, 0.0f, 0.0f, 1.0f};
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmSun,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatar,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecAvatar,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineAvatar,0);
			
	        //Draw the door
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());  
	        gl.glDrawArrays(GL2.GL_TRIANGLES,0,6);
	        
	        //Draw the window
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[1].getTextureId());  
	        gl.glDrawArrays(GL2.GL_TRIANGLES,6,6);
	        
	        //Draw the wall
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[2].getTextureId());  
	        gl.glDrawArrays(GL2.GL_TRIANGLES,12,6*7);
	        
	        //Draw the roof
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[3].getTextureId());  
	        gl.glDrawArrays(GL2.GL_TRIANGLES,54,6*2);
	        
	        //After drawing, disable client state and unbind the buffer to release resources 
	        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		}
		gl.glPopMatrix();
		
		if(useShaders) {
			gl.glUseProgram(0);
		}
	}


}
