package ass2.spec;

import com.jogamp.opengl.GL2;

public class Lighting {
	private Camera camera;
	private long time; 
	private float[] pos;
	
	private boolean rotate;
	private boolean show;
	private boolean nightMode;
	
	// Global materials
	private float[] sunAmb = {0.1f, 0.1f, 0.1f, 1};
	private float[] sunDiff = { 1.0f, 0.85f, 0.7f, 1 };
	private float[] sunSpec= { 1.0f, 0.85f, 0.7f, 1 };
	private float[] gloAmb = {0.1f, 0.1f, 0.1f, 1};
	
	// Sun texture
	private Texture[] sunTexture = new Texture[2];
	// Sun material properties
    private static final float[] AMBIENT = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] DIFFUSE = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] SPECULAR = {0.8f, 0.8f, 0.8f, 1};
    private static final float PHONG = 15;
	
	public Lighting(Camera camera) {
		this.camera = camera;
		pos = null;
		//time = 0;
		rotate = false;
		show = true;
		nightMode = false;
	}
	
	public void draw(GL2 gl, float[] p) {
		pos = new float[] {p[0], p[1], p[2], 0};
		
		// to rotate it around
		float radius = (float) Math.sqrt(pos[0] * pos[0] + pos[2] * pos[2]);
		float radiusZ = (float) Math.sqrt(radius * radius + pos[1] * pos[1]);
		
		float angle1 = (float) Math.atan(pos[1] / radius);
		float angle2 = (float) Math.atan(pos[2] / pos[0]);
		if (rotate) {
			long current = System.currentTimeMillis(); 
			float diff = (current - time) / 5000f;
			pos[0] = (float) (radius * Math.cos(diff + angle1) * -Math.cos(angle2));
			pos[1] = (float) (radiusZ * Math.sin(diff + angle1));
			pos[2] = (float) (radius * Math.cos(diff + angle1) * -Math.sin(angle2));
			
			float sv = (float)Math.sin(diff + angle1);
			if (sv >= 0) nightMode = false;
			if (sv >= 0.7f) {
				sunDiff[2] = sv;
				sunSpec[2] = sv;
				gl.glClearColor(.012f, sv, 1-sv, 1);
			}
			if (sv >= 0.85f) {
				sunDiff[1] = sv;
				sunSpec[1] = sv;
			}
			if (sv < 0) {
				sv = 0;
				nightMode = true;
			}
		}
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
		if (nightMode) {
			// low sunlight in night mode
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] {0.05f, 0.05f, 0.05f, 1}, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[] {0.05f, 0.05f, 0.05f, 1}, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[] {0.05f, 0.05f, 0.05f, 1}, 0);
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, gloAmb, 0);
            
            // torch light settings
            float[] pos = camera.getPos();
            float angle = (float) camera.getAngle();
            float shift = camera.getShift();
            // attach it to camera
            float[] lightPos = {
                    pos[0] + (float) Math.cos(angle) * 0.01f,
                    pos[1],
                    pos[2] - (float) Math.sin(angle) * 0.01f,
                    1};
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);

            float spotDirection[] = {(float) Math.cos(angle), 0.5f - shift, (float) -Math.sin(angle)};
            gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, 30);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, spotDirection,0);
            gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_EXPONENT, 2);
            gl.glLightf(GL2.GL_LIGHT1, GL2.GL_LINEAR_ATTENUATION, 1.0f);

            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, new float[] {0.9f, 0.9f, 0.9f, 1}, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[] {0.9f, 0.9f, 0.9f, 1}, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, new float[] {0.9f, 0.9f, 0.9f, 1}, 0);
			
		} else {
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sunDiff, 0);
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, sunSpec, 0);
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, sunAmb, 0);
	
			gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, gloAmb, 0);
			
			// turn off the spot light
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, new float[] {0, 0, 0, 1}, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[] {0, 0, 0, 1}, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, new float[] {0, 0, 0, 1}, 0);
			gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
		}
		gl.glPushMatrix();
			sunTexture[0] = new Texture(gl,"textures/sun4.jpg", "jpg", true);
			gl.glTranslated(pos[0], pos[1], pos[2]);
			// Set current texture
			gl.glEnable(GL2.GL_TEXTURE_GEN_S);	
			gl.glEnable(GL2.GL_TEXTURE_GEN_T);
			
			// Specify how texture values combine with current surface color values.
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE); 	
		    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, AMBIENT, 0);
		    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, DIFFUSE, 0);
		    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SPECULAR, 0);
		    gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, PHONG);
		    
		    gl.glBindTexture(GL2.GL_TEXTURE_2D, sunTexture[0].getTextureId());
			radius = .1f;
			int stacks = 64;
			int slices = 64;
			for (int j = 0; j < stacks; j++) {
				double latitude1 = (Math.PI/stacks) * j - Math.PI/2;
				double latitude2 = (Math.PI/stacks) * (j+1) - Math.PI/2;
				double sinLat1 = Math.sin(latitude1);
				double cosLat1 = Math.cos(latitude1);
				double sinLat2 = Math.sin(latitude2);
				double cosLat2 = Math.cos(latitude2);
				gl.glBegin(GL2.GL_QUAD_STRIP);
				for (int i = 0; i <= slices; i++) {
					double longitude = (2*Math.PI/slices) * i;
					double sinLong = Math.sin(longitude);
					double cosLong = Math.cos(longitude);
					double x1 = cosLong * cosLat1;
					double y1 = sinLong * cosLat1;
					double z1 = sinLat1;
					double x2 = cosLong * cosLat2;
					double y2 = sinLong * cosLat2;
					double z2 = sinLat2;
					gl.glNormal3d(x2,y2,z2);
					gl.glVertex3d(radius*x2,radius*y2,radius*z2);
					gl.glNormal3d(x1,y1,z1);
					gl.glVertex3d(radius*x1,radius*y1,radius*z1);
				}
				gl.glEnd();
			}
		gl.glPopMatrix();
	}
	
	public void switchSun() {
		rotate = !rotate;
		time = System.currentTimeMillis();
	}
	
	public void switchMode() {
		nightMode = !nightMode;
		
		time = System.currentTimeMillis();
	}
	
	public void hideSun() {
		if (rotate && show)
			switchSun();
		rotate = !rotate;
	}
}
