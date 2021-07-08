package ass2.spec;

import com.jogamp.opengl.GL2;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    private double treeHeight = 1;
    private int shaderProgram;
    private Texture leavesTexture;
    private Texture trunkTexture;
    
    // material properties
    private static final float[] TRUNK_AMBIENT = {0.55f, 0.65f, 0.31f, 1.0f};
    private static final float[] TRUNK_DIFFUSE = {0.5f, 0.5f, 0.5f, 1};
    private static final float[] TRUNK_SPECULAR = {0.6f, 0.6f, 0.6f, 1};
    private static final float TRUNK_PHONG = 20;
    
    // Materials and Color of Leaves
    private static final float[] LEAVES_AMBIENT = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] LEAVES_DIFFUSE = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] LEAVES_SPECULAR = {0.4f, 0.4f, 0.4f, 1};
    private static final float LEAVES_PHONG = 20f;
    
    private static final String TRUNK = "textures/trunk.jpg";
  	private static final String LEAVES = "textures/leaves_texture.jpg";
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
    public void draw(GL2 gl) {
    	int maxStacks = 20;
	    int maxSlices = 24;
    	
    	
    	gl.glPushMatrix();
    	//Use the shader.
    	gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "textureMap"), 0);
        int bitangentId = gl.glGetAttribLocation(shaderProgram, "bitangent");
        gl.glUseProgram(shaderProgram);
        
        //Set material properties
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, TRUNK_AMBIENT, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, TRUNK_DIFFUSE, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, TRUNK_SPECULAR, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, TRUNK_PHONG);
        gl.glTranslated(myPos[0], myPos[1], myPos[2]);
		
    	gl.glPushMatrix();
    		
    		gl.glBindTexture(GL2.GL_TEXTURE_2D, trunkTexture.getTextureId());
    		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            //double texTop = HEIGHT / CYLINDER_RADIUS;
            double text = 2;
            double angle = 0;
            double radius = 0.1;
            for (int i = 0; i <= maxSlices; i++) {
                double x = 0.1 * Math.cos(angle);
                double z = radius * Math.sin(angle);
                gl.glNormal3d(Math.cos(angle), 0, Math.sin(angle));
                gl.glTexCoord2d((double) 2 * i / maxSlices, 0);
                gl.glVertex3d(x, 0, z);
                gl.glTexCoord2d((double) 2 * i / maxSlices, text);
                gl.glVertex3d(x, treeHeight, z);
                gl.glVertexAttrib3d(bitangentId, 0, 1, 0);
                angle += 2 * Math.PI / maxSlices;
            }
            gl.glEnd();

            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
            //gl.glUseProgram(0);
		gl.glPopMatrix();
		
		//Set material properties
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, LEAVES_AMBIENT, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, LEAVES_DIFFUSE, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, LEAVES_SPECULAR, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, LEAVES_PHONG);
        
		gl.glPushMatrix();
			radius = 0.34;
			gl.glTranslated(0, treeHeight + radius - .05, 0);
			
			// Set current texture
			gl.glBindTexture(GL2.GL_TEXTURE_2D, leavesTexture.getTextureId());
			double deltaT;
	    	
	    	deltaT = 0.5/maxStacks;
	    	int ang;  
	    	int delang = 360/maxSlices;
	    	double x1,x2,z1,z2,y1,y2;
	    	
	    	for (int i = 0; i < maxStacks; i++) { 
	    		double t = -0.25 + i*deltaT;
	    		
	    		gl.glBegin(GL2.GL_TRIANGLE_STRIP); 
	    		for (int j = 0; j <= maxSlices; j++)  
	    		{  
	    			ang = j*delang;
	    			x1=radius * r(t)*Math.cos((double)ang*2.0*Math.PI/360.0); 
	    			x2=radius * r(t+deltaT)*Math.cos((double)ang*2.0*Math.PI/360.0); 
	    			y1 = radius * getY(t);

	    			z1=radius * r(t)*Math.sin((double)ang*2.0*Math.PI/360.0);  
	    			z2= radius * r(t+deltaT)*Math.sin((double)ang*2.0*Math.PI/360.0);  
	    			y2 = radius * getY(t+deltaT);

	    			double normal[] = {x1,y1,z1};

	    			MathUtil.normalise3d(normal);    

	    			gl.glNormal3dv(normal,0);  
	    			double tCoord = 1.0/maxStacks * i; //Or * 2 to repeat label
	    			double sCoord = 1.0/maxSlices * j;
	    			gl.glTexCoord2d(sCoord,tCoord);
	    			gl.glVertex3d(x1,y1,z1);
	    			normal[0] = x2;
	    			normal[1] = y2;
	    			normal[2] = z2;

	    			
	    			MathUtil.normalise3d(normal);    
	    			gl.glNormal3dv(normal,0); 
	    			tCoord = 1.0/maxStacks * (i+1); //Or * 2 to repeat label
	    			gl.glTexCoord2d(sCoord,tCoord);
	    			gl.glVertex3d(x2,y2,z2);

	    		}; 
	    		gl.glEnd(); 
	    	}
			
		gl.glPopMatrix();
		gl.glPopMatrix();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		gl.glUseProgram(0);
    }
    
    public void setTexture(GL2 gl) {
    	trunkTexture = new Texture(gl, TRUNK, ".jpg", true);
        leavesTexture = new Texture(gl, LEAVES, ".jpg", true);

        try {
            shaderProgram = Shader.initShaders(gl, "shaders/PhongVertexTex.glsl", "shaders/PhongFragmentTex.glsl");
        } catch (Exception e) {
        	System.err.println("Error while loading shader");
			e.printStackTrace();
			System.exit(1);
        }
    }
    
    double r(double t){
    	double x  = Math.cos(2 * Math.PI * t);
        return x;
    }
    
    double getY(double t){
    	
    	double y  = Math.sin(2 * Math.PI * t);
        return y;
    }
}
