package rigel.coordinates;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import rigel.math.RightOpenInterval;
import rigel.math.Interval;
import rigel.math.Angle;
import rigel.math.ClosedInterval;
import java.util.Locale;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public final class HorizontalCoordinates extends SphericalCoordinates{
    
    private HorizontalCoordinates(double azimuth, double altitude) {
        super(azimuth, altitude);
    }
    
    public static HorizontalCoordinates of(double az, double alt) {
        if (!RightOpenInterval.of(0, TAU).contains(az)) throw new IllegalArgumentException();
        if (!ClosedInterval.of(-TAU/4, TAU/4).contains(alt)) throw new IllegalArgumentException();
        return new HorizontalCoordinates(az, alt);
    }
    
    public static HorizontalCoordinates ofDeg(double az, double alt) {
        if (!RightOpenInterval.of(0, 360).contains(az)) throw new IllegalArgumentException();
        if (!ClosedInterval.of(-90, 90).contains(alt)) throw new IllegalArgumentException();
        return new HorizontalCoordinates(Angle.ofDeg(az), Angle.ofDeg(alt));
    }
    
    public double az() {
        return lon();
    }
    
    public double azDeg() {
        return lonDeg();
    }
    public double alt() {
        return lat();
    }
    
    public double altDeg() {
        return latDeg();
    }
    
    private static final Interval AZ_INTERVAL = RightOpenInterval.of(0, TAU);
    public String azOctantName(String n, String e, String s, String w) {
        int octant = (int) Math.round(8 * (az() - AZ_INTERVAL.low()) / AZ_INTERVAL.size());
        switch (octant) {
            case 8, 0 -> {
                return n;
            }
            case 1 -> {
                return n+e;
            }
            case 2 -> {
                return e;
            }
            case 3 -> {
                return s+e;
            }
            case 4 -> {
                return s;
            }
            case 5 -> {
                return s+w;
            }
            case 6 -> {
                return w;
            }
            case 7 -> {
                return n+w;
            }
            default -> throw new Error();
        }
    }
    
    public double angularDistanceTo(HorizontalCoordinates that) {
        double az1 = this.az();
        double alt1 = this.alt();
        double az2 = that.az();
        double alt2 = that.alt();
        
        return acos(sin(alt1)*sin(alt2) + cos(alt1)*cos(alt2)*cos(az1-az2));
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az = %.4f°, alt = %.4f°)", azDeg(), altDeg());
    }
}
