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
public final class EclipticCoordinates extends SphericalCoordinates{
    
    private EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    public static EclipticCoordinates of(double lon, double lat) {
        if (!RightOpenInterval.of(0, TAU).contains(lon)) throw new IllegalArgumentException("longitude coordinates out of bound 0 , 2PI");
        if (!ClosedInterval.of(-TAU/4, TAU/4).contains(lat)) throw new IllegalArgumentException("latitude coordinates out of bound PI/2 , PI/2");
        return new EclipticCoordinates(lon, lat);
    }
    
    public static EclipticCoordinates ofDeg(double lon, double lat) {
        if (!RightOpenInterval.of(0, 360).contains(lon)) throw new IllegalArgumentException();
        if (!ClosedInterval.of(-90, 90).contains(lat)) throw new IllegalArgumentException();
        return new EclipticCoordinates(Angle.ofDeg(lon), Angle.ofDeg(lat));
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
        return String.format(Locale.ROOT, "(λ = %.4f°, β = %.4f°)", lonDeg(), latDeg());
    }
}
