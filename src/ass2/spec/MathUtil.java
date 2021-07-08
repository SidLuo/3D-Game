package ass2.spec;
import java.lang.Math;
/**
 * A collection of useful math methods 
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class MathUtil {

    /**
     * Normalise an angle to the range [-180, 180)
     * 
     * @param angle 
     * @return
     */
    static public float normaliseAngle(float angle) {
        return ((angle + 180.0f) % 360.0f + 360.0f) % 360.0f - 180.0f;
    }

    /**
     * Clamp a value to the given range
     * 
     * @param value
     * @param min
     * @param max
     * @return
     */

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Multiply two matrices
     * 
     * @param p A 3x3 matrix
     * @param q A 3x3 matrix
     * @return
     */
    public static float[][] multiply(float[][] p, float[][] q) {

        float[][] m = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                   m[i][j] += p[i][k] * q[k][j]; 
                }
            }
        }

        return m;
    }

    /**
     * Multiply a vector by a matrix
     * 
     * @param m A 3x3 matrix
     * @param v A 3x1 vector
     * @return
     */
    public static float[] multiply(float[][] m, float[] v) {

        float[] u = new float[3];

        for (int i = 0; i < 3; i++) {
            u[i] = 0;
            for (int j = 0; j < 3; j++) {
                u[i] += m[i][j] * v[j];
            }
        }

        return u;
    }


    public static float[] crossProduct(float[] v1, float[] v2){		
		float[] val = new float[3];
		val[0] = v1[1] * v2[2] - v1[2] * v2[1];
		val[1] = v1[2] * v2[0] - v1[0] * v2[2];
		val[2] = v1[0] * v2[1] - v1[1] * v2[0];
		return val;		
	}
    
    public static double[] normalise2d(double[] v){
    	double length = Math.sqrt(v[0]*v[0] + v[1]*v[1]);
    	double val[] = {v[0]/length,v[1]/length};
		return val;
	}
    
    public static double[] normalise3d(double v[])  
    {  
        double d = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);  
        if (d != 0.0) {  
        	double val[] = {v[0]/d,v[1]/d, v[2]/d}; 
        	return val;
        }
        else return v;  
       
    }
    
    public static double dist(double[] p1, double[] p2) {
        double sum = 0;
        int len = Math.min(p1.length, p2.length);
        for (int i = 0; i < len; i++) {
            sum += (p1[i] - p2[i]) * (p1[i] - p2[i]);
        }
        return Math.sqrt(sum);
    }
    
    public static double[] lerp(double[] p1, double[] p2, double f) {
        int len = Math.min(p1.length, p2.length);
        double[] p = new double[len];
        for (int i = 0; i < len; i++) {
            p[i] = p1[i] + f * (p2[i] - p1[i]);
        }
        return p;
    }
    
}
