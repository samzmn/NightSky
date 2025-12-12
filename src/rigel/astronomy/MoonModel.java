package rigel.astronomy;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import rigel.math.RightOpenInterval;
import rigel.math.Angle;
import rigel.coordinates.EclipticToEquatorialConversion;
import rigel.coordinates.EclipticCoordinates;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON(91.929336, 130.143076, 291.682547, 5.145396, 0.0549);
    
    private final double l0;
    private final double P0;
    private final double N0;
    private final double i;
    private final double e;
    
    private MoonModel(double l0, double P0, double N0, double i, double e){
        this.l0 = Angle.ofDeg(l0);
        this.P0 = Angle.ofDeg(P0);
        this.N0 = Angle.ofDeg(N0);
        this.i = Angle.ofDeg(i);
        this.e = e;
    }
    
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double Mo = (TAU/365.242191)*daysSinceJ2010 + Angle.ofDeg(279.557208) - Angle.ofDeg(283.112438);
        double Lo = Mo + 2*sin(Mo)*(0.016705) + Angle.ofDeg(283.112438);
        
        double l = Angle.ofDeg(13.1763966)*daysSinceJ2010 + l0;
        double Mm = l - Angle.ofDeg(0.1114041)*daysSinceJ2010 - P0;
        double Ev = Angle.ofDeg(1.2739)*sin(2*(l-Lo) - Mm);
        double Ae = Angle.ofDeg(0.1858)*sin(Mo);
        double A3 = Angle.ofDeg(0.37)*sin(Mo);
        double Mm1 = Mm + Ev - Ae - A3;
        double Ec = Angle.ofDeg(6.2886)*sin(Mm1);
        double A4 = Angle.ofDeg(0.214)* sin(2*Mm1);
        double l1 = l + Ev + Ec - Ae + A4;
        double V = Angle.ofDeg(0.6583)*sin(2*(l1-Lo));
        double l2 = l1 + V;
        double N = N0 - Angle.ofDeg(0.0529539)*daysSinceJ2010;
        double N1 = N - Angle.ofDeg(0.16)*sin(Mo);
        
        double L = atan2(sin(l2-N1)*cos(i), cos(l2-N1)) + N1;
        double B = asin(sin(l2-N1)*sin(i));
        
        double moonAge = l2-Lo;
        moonAge = RightOpenInterval.of(0, TAU).reduce(moonAge);
        double phase = (1 - cos(moonAge)) / 2;
        
        double ro = (1-e*e) / (1 + e*cos(Mm1+Ec));
        double angularSize = Angle.ofDeg(0.5181)/ro;
        
        L = RightOpenInterval.of(0, TAU).reduce(L);
        return new Moon(eclipticToEquatorialConversion.apply(
                EclipticCoordinates.of(L, B)), (float)angularSize, 0, phase, moonAge);
    }
    
}
