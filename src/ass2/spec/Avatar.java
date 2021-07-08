package ass2.spec;

import com.jogamp.opengl.GL2;

import ass2.spec.MathUtil;

public class Avatar {
	private Camera camera;
	
	private double myAngle;
	private boolean torchOn;
	private boolean nightMode;
	
	// material properties
    private static final float[] AMBIENT = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] DIFFUSE = {0.6f, 0.6f, 0.6f, 1};
    private static final float[] SPECULAR = {0.8f, 0.8f, 0.8f, 1};
    private static final float PHONG = 15;
	
    //Texture file information
  	private String TEX_HAT = "textures/metal.jpg";                            // texturing hat
  	private String TEX_SKIN = "textures/skin1.jpg";                           // texturing head
  	private String TEX_EYE = "textures/eye1.jpg";                             // texturing eyes
  	private String TEX_LIGHTBULB_OFF = "textures/lightBulb2.jpg";             // texturing eyes
  	private String TEX_LIGHTBODY = "textures/lightBody1.jpg";                 // texturing eyes
  	private String TEX_LIGHTBULB_ON = "textures/lightBulb5.jpg";              // texturing eyes
  	
  	//Texture data
  	Texture[] myTexture = new Texture[6];
    
    
	public Avatar(Camera c) {
		camera = c;
	}
	
	public void draw(GL2 gl) {
		
		gl.glPushMatrix(); {
			float[] pos = camera.getPos();
			
			if (nightMode) {
				float matAmbAndDifAvatar[] = {0.1f, 0.1f, 0.0f, 1.0f};
		        float matSpecAvatar[] = { 0.3f, 0.3f, 0.3f, 1.0f };
		        float matShineAvatar[] = { 20.0f };
		        float emmSun[] = {0.0f, 0.0f, 0.0f, 1.0f};
		        float matAmbAndDifAvatarBack[] = {0.5f, 0.0f, 0.0f, 1.0f};
		        float matSpecAvatarBack[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		        float matShineAvatarBack[] = { 0.0f };
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmSun,0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatar,0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecAvatar,0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineAvatar,0);
		        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatarBack,0);
		        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_SPECULAR, matSpecAvatarBack,0);
		        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_SHININESS, matShineAvatarBack,0);
			} else {
				// Set current texture
				gl.glEnable(GL2.GL_TEXTURE_GEN_S);	
				gl.glEnable(GL2.GL_TEXTURE_GEN_T);
				
				gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[0].getTextureId());
				// Specify how texture values combine with current surface color values.
		    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE); 	
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, AMBIENT, 0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, DIFFUSE, 0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SPECULAR, 0);
		        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, PHONG);
			}
			
			
			
			
			gl.glTranslatef(pos[0], pos[1] + .2f, pos[2]);
			gl.glRotated(Math.toDegrees(camera.getAngle()), 0, 1, 0);
			
			//Rotate the avatar if key A or D is pressed
			gl.glRotated(myAngle,0,1,0);
			
			//To make the avatar display clearer, lift a little bit.
			gl.glTranslated(0,0.3,0);
			
			/*
			 * Draw a Hat which consist of two cylinders
			 */
			double[] Height = new double[] {0.05,0.35};
			double larerRadius = 0.4;
			drawHat(Height, larerRadius, gl);
			
			/*
			 * Draw a head with two eyes
			 */
			int slices = 64;
			int stacks = 64;
			double headRadius = 0.2;
			double eyeRadius = headRadius/3;     // radius of the eyes is 1/3 of the head
			gl.glPushMatrix(); {
				//Draw the head
				gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[1].getTextureId());
				gl.glTranslated(0, -headRadius, 0);
				drawSphere(headRadius, slices, stacks, gl);
				
				//Draw the eyes
				gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[2].getTextureId());
				//Draw the left eye
				gl.glPushMatrix(); {
					gl.glTranslated(-0.15, -0.01, -0.1);
					drawSphere(eyeRadius, slices, stacks, gl);
				}
				gl.glPopMatrix();
				//Draw the right eye
				gl.glPushMatrix(); {
					gl.glTranslated(-0.15, -0.01, 0.1);
					drawSphere(eyeRadius, slices, stacks, gl);
				}
				gl.glPopMatrix();
			}
			gl.glPopMatrix();
			
			/*
			 * Draw a torch which consists of one sphere and one cylinder
			 */
			double torchSphereRadius = eyeRadius;
			double torchLength = 0.2;
			double torchCylinderRadius = eyeRadius/2;
			gl.glPushMatrix(); {
				gl.glTranslated(0, 0.35+eyeRadius/2, 0);
//				gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[4].getTextureId());
				drawTorch(torchSphereRadius, torchLength, torchCylinderRadius, gl);
			}
			gl.glPopMatrix();
		}
		gl.glPopMatrix();
		
		//Unbind texture and release resources
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
	
	public void rotateAvatar(double angle) {
		myAngle = angle;
	}
	
	public void setTorch(boolean state) {
		torchOn = state;
	}
	
	public void setNight(boolean isNight) {
		nightMode = isNight;
	}
	
	public double getAvatarAngle( ) {
		return myAngle;
	}
	
	/*
	 * Draw a hat which consists of two cylinders
	 */
	public void drawHat(double[] H, double radius, GL2 gl) {
		for (double height : H) {
			double y0 = 0;
			double y1 = height;
			int slices = 128;
			//Bottom circle
			gl.glBegin(GL2.GL_TRIANGLE_FAN); {
				gl.glNormal3d(0, -1, 0);
				gl.glVertex3d(0, y0, 0);
				double angleStep = 2*Math.PI/slices;
				for (int i=0; i<= slices; i++) {
					double theta = i * angleStep;
					double x0 = radius * Math.cos(theta);
					double z0 = radius * Math.sin(theta);
					gl.glVertex3d(x0, y0, z0);
				}
			}
			gl.glEnd();
			
			//Top circle
			gl.glBegin(GL2.GL_TRIANGLE_FAN); {
				gl.glNormal3d(0, 1, 0);
				gl.glVertex3d(0, y1, 0);
				double angleStep = 2*Math.PI/slices;
				for (int i=0; i<= slices; i++) {
					//double theta = i * angleStep;
					double theta = 2*Math.PI - i * angleStep;    //avoid top circle not displaying
					double x1 = radius * Math.cos(theta);
					double z1 = radius * Math.sin(theta);
					gl.glVertex3d(x1, y1, z1);
				}
			}
			gl.glEnd();
			
			// Sides of Cylinder
			gl.glBegin(GL2.GL_QUADS); {
				double angleStep = 2*Math.PI/slices;
				for (int i=0; i<= slices; i++) {
					double theta0 = i * angleStep;
					double theta1 = ((i+1) % slices) * angleStep;
					
					//Calculate vertcies for the quad
					double x0 = radius * Math.cos(theta0);
					double z0 = radius * Math.sin(theta0);
					double x1 = radius * Math.cos(theta1);
					double z1 = radius * Math.sin(theta1);
					//Calculate for face normal for each quad
					//Each face consist of 4 points, which are
					//(x0,y0,z0),(x1,y0,z1),(x1,y1,z1),(x0,y1,z0)
					float[] v1 = new float[] {
							(float)(x0-x1), (float)(y1-y0), (float)(z0-z1),
					};
					float[] v2 = new float[] {
							(float) (x0-x1), 0, (float)(z0-z1),
					};
					float[] normals = MathUtil.crossProduct(v1,v2);
					
					gl.glNormal3f(normals[0], normals[1], normals[2]);
					gl.glVertex3d(x0, y0, z0);
					gl.glVertex3d(x1, y0, z1);
					gl.glVertex3d(x1, y1, z1);
					gl.glVertex3d(x0, y1, z0);		
				}
			}
			gl.glEnd();
			radius -= 0.2;
		}
	}
	
	/*
	 * Draw a sphere
	 */
	public void drawSphere(double headRadius, int slices, int stacks, GL2 gl) {
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
				gl.glVertex3d(headRadius*x2,headRadius*y2,headRadius*z2);
				gl.glNormal3d(x1,y1,z1);
				gl.glVertex3d(headRadius*x1,headRadius*y1,headRadius*z1);
			}
			gl.glEnd();
		}
	}
	
	
	/*
	 * Draw a torch on top of the hat
	 */
	public void drawTorch(double bigR, double length, double smallR, GL2 gl) {
		//Draw the light bulb
		//If torch is on, just light bulb in white color, otherwise draw in yellow color
		if (torchOn) {
			// Set current texture
			gl.glEnable(GL2.GL_TEXTURE_GEN_S);	
			gl.glEnable(GL2.GL_TEXTURE_GEN_T);
			
			// Specify how texture values combine with current surface color values.
	    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE); 	
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, AMBIENT, 0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, DIFFUSE, 0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SPECULAR, 0);
	        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, PHONG);
	        
			gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[5].getTextureId());
			drawSphere(bigR, 64, 64, gl);
		} else {
			gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[3].getTextureId());
			drawSphere(bigR, 64, 64, gl);
		}
		
		//Draw the light body
		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture[4].getTextureId());
		double x0 = 0;
		double x1 = length;
		int slices = 128;
		//Front circle
		gl.glBegin(GL2.GL_TRIANGLE_FAN); {
			gl.glNormal3d(-1, 0, 0);
			gl.glVertex3d(x0, 0, 0);
			double angleStep = 2*Math.PI/slices;
			for (int i=0; i<= slices; i++) {
				double theta = i * angleStep;
				double z0 = smallR * Math.cos(theta);
				double y0 = smallR * Math.sin(theta);
				gl.glVertex3d(x0, y0, z0);
			}
		}
		gl.glEnd();
		
		//Back circle
		gl.glBegin(GL2.GL_TRIANGLE_FAN); {
			gl.glNormal3d(1, 0, 0);
			gl.glVertex3d(x1, 0, 0);
			double angleStep = 2*Math.PI/slices;
			for (int i=0; i<= slices; i++) {
				//double theta = i * angleStep;
				double theta = 2*Math.PI - i * angleStep;    //avoid top circle not displaying
				double z1 = smallR * Math.cos(theta);
				double y1 = smallR * Math.sin(theta);
				gl.glVertex3d(x1, y1, z1);
			}
		}
		gl.glEnd();
		
		// Sides of Cylinder
		gl.glBegin(GL2.GL_QUADS); {
			double angleStep = 2*Math.PI/slices;
			for (int i=0; i<= slices; i++) {
				double theta0 = i * angleStep;
				double theta1 = ((i+1) % slices) * angleStep;
				
				//Calculate vertcies for the quad
				double z0 = smallR * Math.cos(theta0);
				double y0 = smallR * Math.sin(theta0);
				double z1 = smallR * Math.cos(theta1);
				double y1 = smallR * Math.sin(theta1);
				//Calculate for face normal for each quad
				//Each face consist of 4 points, which are
				//(x0,y0,z0),(x1,y0,z1),(x1,y1,z1),(x0,y1,z0)
				float[] v1 = new float[] {
						(float)(x0-x1), (float)(y1-y0), (float)(z0-z1),
				};
				float[] v2 = new float[] {
						(float) (x0-x1), 0, (float)(z0-z1),
				};
				float[] normals = MathUtil.crossProduct(v1,v2);
				
				gl.glNormal3f(normals[0], normals[1], normals[2]);
				gl.glVertex3d(x0, y0, z0);
				gl.glVertex3d(x1, y0, z1);
				gl.glVertex3d(x1, y1, z1);
				gl.glVertex3d(x0, y1, z0);		
			}
		}
		gl.glEnd();
	}
	
	
	public void loadTexture(GL2 gl) {
		myTexture[0] = new Texture(gl,TEX_HAT, "jpg", true);
		myTexture[1] = new Texture(gl,TEX_SKIN, "jpg", true);
		myTexture[2] = new Texture(gl,TEX_EYE, "jpg", true);
		myTexture[3] = new Texture(gl,TEX_LIGHTBULB_OFF, "jpg", true);
		myTexture[4] = new Texture(gl,TEX_LIGHTBODY, "jpg", true);
		myTexture[5] = new Texture(gl,TEX_LIGHTBULB_ON, "jpg", true);
		
        // Specify automatic texture generation for a sphere map.
    	gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP); 
    	gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
    }
}
