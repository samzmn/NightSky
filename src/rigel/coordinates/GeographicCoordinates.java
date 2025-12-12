package rigel.coordinates;

import rigel.math.RightOpenInterval;
import rigel.math.Angle;
import rigel.math.ClosedInterval;
import java.util.Locale;
/**
 *
 * @author hassanzmn
 */
public final class GeographicCoordinates extends SphericalCoordinates {
    
    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg){
        if (!isValidLonDeg(lonDeg) || !isValidLatDeg(latDeg)) throw new IllegalArgumentException();
        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }
    
    public static boolean isValidLonDeg(double lonDeg) {
        RightOpenInterval roi = RightOpenInterval.of(-180, 180);
        return roi.contains(lonDeg);
    }
    
    public static boolean isValidLatDeg(double latDeg) {
        ClosedInterval ci = ClosedInterval.of(-90, 90);
        return ci.contains(latDeg);
    }
    
    @Override
    public double lon(){
        return super.lon();
    }
    
    @Override
    public double lonDeg(){
        return super.lonDeg();
    }
    
    @Override
    public double lat(){
        return super.lat();
    }
    
    @Override
    public double latDeg(){
        return super.latDeg();
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(long = %.4f°, lat = %.4f°)", lonDeg(), latDeg());
    }
}
