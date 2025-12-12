package rigel.coordinates;

import rigel.math.Polynomial;
import rigel.math.RightOpenInterval;
import rigel.math.Angle;
import rigel.astronomy.Epoch;
import java.time.ZonedDateTime;
import java.util.function.Function;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {
    private final double e;
    private final double cose;
    private final double sine;
    
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double T = Epoch.J2000.julianCenturiesUntil(when);
        //the obliquity of the ecliptic in seconds of DMS system and then in degree and then in radian.
        e = Angle.ofDeg(Polynomial.of(0.00181, -0.0006, -46.815, 84381.45).at(T) / 3600);
        cose = Math.cos(e);
        sine = Math.sin(e);
    }
    
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ec) {
        double l = ec.lon();
        double sinl = Math.sin(l);
        double cosl = Math.cos(l);
        double b = ec.lat();
        double sinb = Math.sin(b);
        double cosb = Math.cos(b);
        double a = Math.atan2(sinl*cose - sinb/cosb*sine, cosl);
        a = RightOpenInterval.of(0, TAU).reduce(a);
        double d = Math.asin(sinb*cose+cosb*sine*sinl);
        return EquatorialCoordinates.of(a, d);
    }
    
    
    @Override
    public int hashCode(){
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(Object o){
        throw new UnsupportedOperationException();
    }
}
