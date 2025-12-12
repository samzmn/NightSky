package rigel.coordinates;

import rigel.math.RightOpenInterval;
import rigel.math.Angle;
import rigel.math.ClosedInterval;
import java.util.Locale;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public final class EquatorialCoordinates extends SphericalCoordinates {
    
    private EquatorialCoordinates(double rightAscension, double declination) {
        super(rightAscension, declination);
    }
    
    public static EquatorialCoordinates of(double ra, double dec) {
        if (!RightOpenInterval.of(0, TAU).contains(ra)) throw new IllegalArgumentException("Not in the range [0 , 2pi)");
        if (!ClosedInterval.of(-TAU/4, TAU/4).contains(dec)) throw new IllegalArgumentException("Not in the range [-90 , +90]");
        return new EquatorialCoordinates(ra, dec);
    }
    
    public static EquatorialCoordinates ofDeg(double ra, double dec) {
        if (!RightOpenInterval.of(0, 360).contains(ra)) throw new IllegalArgumentException("Not in the range [0 , 360)");
        if (!ClosedInterval.of(-90, 90).contains(dec)) throw new IllegalArgumentException("Not in the range [-90 , +90]");
        return new EquatorialCoordinates(Angle.ofDeg(ra), Angle.ofDeg(dec));
    }
    
    public double ra() {
        return lon();
    }
    
    public double raDeg() {
        return lonDeg();
    }
    
    public double raHr() {
        return Angle.toHr(ra());
    }
    
    public double dec() {
        return lat();
    }
    
    public double decDeg() {
        return latDeg();
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra = %.4fh, dec = %.4f°)", raHr(), decDeg());
    }
}