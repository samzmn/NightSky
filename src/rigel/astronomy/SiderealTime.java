package rigel.astronomy;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import rigel.math.Polynomial;
import rigel.math.RightOpenInterval;
import rigel.math.Angle;
import rigel.coordinates.GeographicCoordinates;
import static java.time.temporal.ChronoUnit.DAYS;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public final class SiderealTime {
    private SiderealTime(){}
    
    public static double greenwich(ZonedDateTime when) {
        when = when.withZoneSameInstant(ZoneOffset.UTC);
        double nano = (double)when.getNano()/(1000000000);
        double t = when.getHour() + (double)when.getMinute()/60 + (double)(when.getSecond()+nano)/(60*60) ;
        double T = Epoch.J2000.julianCenturiesUntil(when.truncatedTo(DAYS));
        double S0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558).at(T);
        S0 = RightOpenInterval.of(0, 24).reduce(S0);
        double S1 = 1.002737909*t;
        return Angle.ofHr(RightOpenInterval.of(0, 24).reduce(S0 + S1));
    }
    
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        double Sg = greenwich(when);
        double l = where.lon();
        return RightOpenInterval.of(0, TAU).reduce(Sg + l);
    }
}
