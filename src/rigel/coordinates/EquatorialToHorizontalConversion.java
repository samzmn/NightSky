package rigel.coordinates;

import rigel.math.RightOpenInterval;
import java.time.ZonedDateTime;
import java.util.function.Function;
import rigel.astronomy.SiderealTime;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {
    private final double Sl;
    private double H;
    private double sinH;
    private double cosH;
    private final double fi;
    private final double sinfi;
    private final double cosfi;
    
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        Sl = SiderealTime.local(when, where);
        fi = where.lat();
        cosfi = Math.cos(fi);
        sinfi = Math.sin(fi);
    }
    
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {
        double a = equatorialCoordinates.ra();
        double d = equatorialCoordinates.dec();
        double sind = Math.sin(d);
        double cosd = Math.cos(d);
        H = Sl - a ;
        sinH = Math.sin(H);
        cosH = Math.cos(H);
        
        double h = Math.asin(sind*sinfi+cosd*cosfi*cosH);
        double A = Math.atan2(-1 *cosd * cosfi * sinH, sind-sinfi*Math.sin(h));
        A = RightOpenInterval.of(0, TAU).reduce(A);
        return HorizontalCoordinates.of(A, h);
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
