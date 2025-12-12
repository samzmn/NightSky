package rigel.astronomy;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import rigel.math.Angle;
import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EclipticToEquatorialConversion;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public enum SunModel implements CelestialObjectModel<Sun>{
    SUN(279.557208, 283.112438, 0.016705, 0.533128);
    
    private final double E;
    private final double W;
    private final double e;
    private final double teta0;
    
    private SunModel(double E, double W, double e, double teta0){
        this.E = Angle.ofDeg(E);
        this.W = Angle.ofDeg(W);
        this.e = e;
        this.teta0 = Angle.ofDeg(teta0);
    }
    
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double M = (TAU/365.242191) * daysSinceJ2010 + E - W ;
        double V = M + 2*e*sin(M);
        double L = V + W;
        
        double angularSize = teta0 * ( (1+e*cos(V)) / (1-e*e) );
        
        L = Angle.normalizePositive(L);
        return new Sun(EclipticCoordinates.of(L, 0), eclipticToEquatorialConversion.apply(EclipticCoordinates.of(L, 0)), (float)angularSize, (float)M);
    }
    
}
