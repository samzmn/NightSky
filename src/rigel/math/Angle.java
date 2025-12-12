package rigel.math;

/**
 *
 * @author hassanzmn
 */
public final class Angle {
    public final static double TAU = 2 * Math.PI;
    private  static  final  double DEG_PER_RAD = 360.0 / TAU;
    
    private Angle(){}
    
    public static double normalizePositive(double rad) {
        return RightOpenInterval.of(0, TAU).reduce(rad);
    }
    
    public static double ofArcsec(double sec) {
        return (sec/1296000)*TAU; //360*60*60 = 1296000
    }
    
    public static double ofDMS(int deg, int min, double sec) {
        if (min>=60 || min<0 || sec>=60 || sec<0 || deg<0){
            throw new IllegalArgumentException();
        }
        double d = deg +(double)min/60 + sec/3600;
        return ofDeg(d);
    }
    
    public static double ofDeg(double deg) {
        return (deg/360)*TAU;
        //return Math.toRadians(deg);
    }
    
    public static double toDeg(double rad) {
        return rad * DEG_PER_RAD;
    }
    
    public static double ofHr(double hr) {
        return hr/24 * TAU;
    }
    
    public static double toHr(double rad) {
        return rad/TAU * 24;
    }
    
}
