package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private List<Other> myOthers;
    private float[] mySunlight;
    private Texture myTexture;
    private int shaderProgram;
    
    // material values
    private static final float[] AMBIENT = {0.55f, 0.65f, 0.31f, 1.0f};
    private static final float[] DIFFUSE = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] SPECULAR = {0.1f, 0.1f, 0.1f, 1f};
    private static final float PHONG = 0.1f * 64;
    
    //Texture file information
  	private static final String GRASS = "textures/grass.jpg";
  	
	
	//Shader file information
	private static final String VERTEX_SHADER = "shaders/PhongVertexTex.glsl";
    private static final String FRAGMENT_SHADER = "shaders/PhongFragmentTex.glsl";
  	
    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        myOthers = new ArrayList<Other>();
        mySunlight = new float[3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(double x, double z) {
		int xl = (int) Math.floor(x);
		int xr = xl + 1;
		int zl = (int) Math.floor(z);
		int zr = zl + 1;
		double[] p = {x, z};
        double[] tl = {xl, zl};
        double[] br = {xr, zr};
        double[] alt;

		if (MathUtil.dist(p, tl) < MathUtil.dist(p, br)) {
            // top left triangle
			double[] p1 = {xl, myAltitude[xl][zr], zr};
            double[] p2 = {xl, myAltitude[xl][zl], zl};
            double[] p3 = {xr, myAltitude[xr][zl], zl};
            double[] i1;
            double[] i2;
            // first interpolate between (p1, p2) and (p1, p3), get i1 and i2

            i1 = MathUtil.lerp(p1, p2, (zr - z) / (zr - zl));
            i2 = MathUtil.lerp(p1, p3, (zr - z) / (zr - zl));
            // then interpolate between (i1, i2)
            if (i2[0] == xl) {
            	alt = i2;
            } else {
            	alt = MathUtil.lerp(i1, i2, (x - xl) / (i2[0] - xl));
            }
		} else {
			// bottom right triangle, process is the same as above
            double[] p1 = {xr, myAltitude[xr][zl], zl};
            double[] p2 = {xl, myAltitude[xl][zr], zr};
            double[] p3 = {xr, myAltitude[xr][zr], zr};
            double[] i1;
            double[] i2;

            i1 = MathUtil.lerp(p1, p2, (z - zl) / (zr - zl));
            i2 = MathUtil.lerp(p1, p3, (z - zl) / (zr - zl));
            
            if (xr == i1[0]) {
            	alt = i1;
            } else {
            	alt = MathUtil.lerp(i1, i2, (x - i1[0]) / (xr - i1[0]));
            }
		}

		return (float) alt[1];
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    
    /**
     * Add an other object
     * 
     * @param x
     * @param z
     */
    
    public void addOther(double x, double z)
    {
    	double y = altitude(x, z);
        Other o = new Other(x, y, z);
        myOthers.add(o);
    }


    public void draw(GL2 gl) {
    	
    	// Set current texture     
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_MIRRORED_REPEAT);
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_MIRRORED_REPEAT);
    	// Use the shader
        gl.glUseProgram(shaderProgram);
        gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "texUnit"), 0);
    	
    	// materials and color stuff here:
    	
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, AMBIENT, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, DIFFUSE, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SPECULAR, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, PHONG);
    
		
    	for (int i = 0; i < (int) mySize.getWidth() - 1; i++) {
    		for (int j = 0; j < (int) mySize.getHeight() - 1; j++) {
    			
    			float[] alt = new float[]{ (float)getGridAltitude(i, j),
    					(float)getGridAltitude(i, j+1),
    					(float)getGridAltitude(i+1, j),
    					(float)getGridAltitude(i+1, j+1)};
    			float[] v1 = { 1,
						(float) (myAltitude[i + 1][j] - myAltitude[i][j]), 0 };
				float[] v2 = { 0,
						(float) (myAltitude[i][j + 1] - myAltitude[i][j]), 1 };
				float[] n = MathUtil.crossProduct(v2, v1);

				gl.glBegin(GL2.GL_TRIANGLES);

				gl.glNormal3f(n[0], n[1], n[2]);
				gl.glTexCoord2f(i, j); 
				gl.glVertex3f(i, alt[0], j);
				
				gl.glTexCoord2f(i, j+1); 
				gl.glVertex3f(i, alt[1], j+1);
				
				gl.glTexCoord2f(i+1, j+1); 
				gl.glVertex3f(i+1, alt[2], j);
				
				float[] v3 = { -1,
						(float) (myAltitude[i][j + 1] - myAltitude[i + 1][j]),
						1 };
				float[] v4 = {
						0,
						(float) (myAltitude[i + 1][j + 1] - myAltitude[i + 1][j]),
						1 };
				n = MathUtil.crossProduct(v3, v4);
				gl.glNormal3f(n[0], n[1], n[2]);
				
				gl.glTexCoord2f(i, j); 
				gl.glVertex3f(i+1, alt[2], j);
				
				gl.glTexCoord2f(i, j+1); 
				gl.glVertex3f(i, alt[1], j+1);
				
				gl.glTexCoord2f(i+1, j+1); 
				gl.glVertex3f(i+1, alt[3], j+1);
				
				
				gl.glEnd();
    		}
    		
    	}
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glUseProgram(0);
        
        for (Tree tree : trees()) {
            tree.draw(gl);
        }

        for (Road road : roads()) {
        	double[] p = road.point(0);
            road.draw(gl, this.altitude(p[0], p[1]));
        }

        for (Other other: myOthers) {
            other.draw(gl);
        }
    }
    
    public void loadTextures(GL2 gl) {
    	myTexture = new Texture(gl, GRASS, ".jpg", true);

		try 
		{
			shaderProgram = Shader.initShaders(gl,VERTEX_SHADER, FRAGMENT_SHADER);   	
		}
		catch (Exception e) {
			System.err.println("Error while loading shader");
			e.printStackTrace();
			System.exit(1);
		}
		for (Road r : myRoads) {
			r.setTexture(gl);
		}
		
		for (Tree t : myTrees) {
			t.setTexture(gl);
		}
		for (Other o: myOthers) {
			o.loadTextures(gl);
		}
	}
    
    
}
