package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class Camera {
	private float[] pos;
	private float[] bound;
	private float aspectRatio;
	private double angle;
	private Terrain terrain;
	private float shift;
	private float zoom;
	private boolean third;
	public Camera(Terrain terrain) {
		this.terrain = terrain;
		pos = new float[] {0, terrain.altitude(0, 0), 0};
		angle = Math.PI/2;
		bound = new float[] {(float) terrain.size().width - 1, 
				(float) terrain.size().height - 1};
		shift = 0.1f;
		aspectRatio = 0.9f;
		third = true;
		zoom = 30f;
	}
	
	public float[] getPos() {
		return pos;
	}
	
	public void setPos(float[] pos) {
		this.pos = pos;
	}
	
	public void setAspect(float aspect) {
		this.aspectRatio = aspect;
	}
	
	public float getAspect() {
		return aspectRatio;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public float getShift() {
		return shift;
	}
	
	public void setShift(float shift) {
		this.shift = shift;
	}
	
	public float getZoom() {
		return zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
 	
	public void setup(GL2 gl) {
		float lookAt[] = new float[3];
		float center[] = new float[3];
		lookAt[0] = pos[0] + (float) Math.cos(angle);
		lookAt[1] = pos[1] - shift;
		lookAt[2] = pos[2] + (float) - Math.sin(angle);
		
		
		
		
		if (!third) {
			center[0] = pos[0] - (float) Math.cos(angle);
			center[1] = pos[1] + shift;
			center[2] = pos[2] - (float) -Math.sin(angle);
		}
		else {
			center[0] = pos[0] - (float) Math.cos(angle) * 6;
			center[1] = pos[1] + shift;
			center[2] = pos[2] - (float) -Math.sin(angle) * 6;
		}
		GLU glu = new GLU();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();	
		
		glu.gluLookAt(center[0], center[1], center[2], lookAt[0], lookAt[1], lookAt[2], 0, 1, 0);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(zoom, aspectRatio, .1f, 20f);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
	
	public void reshape(GL2 gl, int x, int y, int width, int height) {
        
        // match the projection aspect ratio to the viewport
        // to avoid stretching
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        double top, bottom, left, right;
        
        if (width > height) {
            double aspect = (1.0 * width) / height;
            top = 1.0;
            bottom = -1.0;
            left = -aspect;
            right = aspect;            
        } else {
            double aspect = (1.0 * height) / width;
            top = aspect;
            bottom = -aspect;
            left = -1;
            right = 1;                        
        }        
        GLU myGLU = new GLU();
        // coordinate system (left, right, bottom, top)
        myGLU.gluOrtho2D(left, right, bottom, top);  
	}
	
	public void move(double direction){
		float inc[] = new float[] {(float) (Math.cos(angle) * direction), 
				(float) (-Math.sin(angle) * direction)};
		if (pos[0] + inc[0] >= bound[0] ||
				pos[2] + inc[1] >= bound[1] || 
				pos[0] + inc[0] < 0 ||
				pos[2] + inc[1] < 0)
			return;

		pos[0] += inc[0];
		pos[1] = terrain.altitude(pos[0], pos[2]);
		pos[2] += inc[1];
	}
	
	public void rotate(double angle){
		this.angle -= angle;
	}
	
	public void changeMode(){
		third = !third;
	}
	
	public void shiftUp (double shift) {
		if (this.shift + shift > 2 || this.shift + shift < -1)
			return;

		this.shift += shift;
	}
	
	public void zoomIn (double zoom) {
		if (this.zoom + zoom > 90 || this.zoom + zoom < 10)
			return;

		this.zoom += zoom;
	}
}
