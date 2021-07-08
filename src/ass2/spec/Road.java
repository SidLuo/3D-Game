package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Double> myPoints;
    private double myWidth;
    private int shaderProgram;
    private Texture myTexture;
    
    // material properties
    private static final float[] AMBIENT = {0.55f, 0.65f, 0.55f, 1.0f};
    private static final float[] DIFFUSE = {0.5f, 0.5f, 0.5f, 1};
    private static final float[] SPECULAR = {0.6f, 0.6f, 0.6f, 1};
    private static final float PHONG = 18f;
    
    private static final String ROAD = "textures/road.jpg";
    
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return width
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    /**
     * 
     * Get a point on the tangent. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return tangent
     */
   
    public double[] tangent(double t)
    {
    	double[] tan = new double[2];
    	
    	
    	int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
       
        tan[0] = 3*( bt(0, t) *(x1-x0) + bt(1, t) *(x2 - x1) + bt(2, t) *(x3-x2)) ;
        tan[1] = 3*( bt(0, t) *(y1-y0) + bt(1, t) *(y2 - y1) + bt(2, t) *(y3-y2));  
    
    	
    	return tan;	
    }
    
    /**
     * Calculate the Bezier coefficients for tangent
     * 
     * @param i
     * @param t
     * @return
     */
    private double bt(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t);

        case 1:
            return 2 * (1-t) * t;

        case 2:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }

    public void draw(GL2 gl, double alt) {
    	double stride = 0.01;
    	
    	// Set current texture
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
    	// Use shader
        gl.glUseProgram(shaderProgram);
        gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "texUnit"), 0);
        
        //Set material properties
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, AMBIENT, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, DIFFUSE, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SPECULAR, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, PHONG);
        
    	gl.glBegin(GL2.GL_TRIANGLE_STRIP);
    	
    	// Small offset in height so it is on top of the terrain
		double offset = 0.01;
		double w = myWidth/2;
    	for (double i = 0; i < this.size(); i += stride) {
    		
    		double[] p = this.point(i);
    		double[] tangent = this.tangent(i);
    		double[] normal = new double[]{-tangent[1], tangent[0]};
    		
    		//normalize the normal
    		
    		normal = MathUtil.normalise2d(normal);

    		gl.glNormal3d(0, 1,0);
    		gl.glTexCoord2d(-w * normal[0] + p[0], -w * normal[1] + p[1]); 
    		gl.glVertex3d(-w * normal[0] + p[0], alt + offset, -w * normal[1] + p[1]);
    		gl.glTexCoord2d(w * normal[0] + p[0], w * normal[1] + p[1]); 
    		gl.glVertex3d(w * normal[0] + p[0], alt + offset, w * normal[1] + p[1]);
    	}
    	
    	gl.glEnd();
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    	gl.glUseProgram(0);
    	
    }
    
    public void setTexture(GL2 gl) {
    	myTexture = new Texture(gl, ROAD, ".jpg", true);

        try {
            shaderProgram = Shader.initShaders(gl, "shaders/PhongVertexTex.glsl", "shaders/PhongFragmentTex.glsl");
        } catch (Exception e) {
        	System.err.println("Error while loading shader");
			e.printStackTrace();
			System.exit(1);
        }
    }
    	
}
