package rigel.astronomy;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import rigel.math.Angle;
import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EclipticToEquatorialConversion;
import java.util.List;
import static rigel.math.Angle.TAU;

/**
 *
 * @author hassanzmn
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {
    MERCURY("Mercury", 0.24085, 75.5671, 77.612, 0.205627, 0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Venus", 0.615207, 272.30044, 131.54, 0.006812, 0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Earth", 0.999996, 99.556772, 103.2055, 0.016671, 0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348, 1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907, 5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturn", 29.310579, 172.398316, 89.567, 0.053853, 9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321, 19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483, 30.1985, 1.7673, 131.879, 62.20, -6.87);
    
    private final String name;
    private final double Tp;
    private final double E;
    private final double W;
    private final double e;
    private final double a;
    private final double i;
    private final double ohm;
    private final double teta0;
    private final double V0;
    
    private double RE;
    private double LE;
    
    public static final List<PlanetModel> ALL = List.of(values());
    
    private PlanetModel(String name, double Tp, double E, double W, double e, double a, double i, double ohm, double teta0, double V0){
        this.name = name;
        this.Tp = Tp;
        this.E = Angle.ofDeg(E);
        this.W = Angle.ofDeg(W);
        this.e = e;
        this.a = a;
        this.i = Angle.ofDeg(i);
        this.ohm = Angle.ofDeg(ohm);
        //this.teta0 = Angle.ofArcsec(teta0);
        this.teta0 = Angle.ofArcsec(teta0);
        this.V0 = V0;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double M = (TAU/365.242191) * (daysSinceJ2010/Tp) + E - W;
        double V = M + 2*e*sin(M);
        double l = V + W;
        double r = (a*(1-e*e)) / (1 + e*cos(V));
        double UI = asin(sin(l-ohm) * sin(i));
        double l1 = atan2(sin(l-ohm) * cos(i), cos(l-ohm)) + ohm;
        double r1 = r * cos(UI);
        
        double ME = (TAU/365.242191) * (daysSinceJ2010/0.999996) + Angle.ofDeg(99.556772) - Angle.ofDeg(103.2055);
        double VE = ME + 2*0.016671*sin(ME);
        RE = (0.999985*(1 - pow(0.016671, 2))) / (1 + 0.016671*cos(VE));
        LE = VE + Angle.ofDeg(103.2055);
        
        double L;
        if (name.equals("Mercure") || name.equals("Venus")) {
            L = PI + LE + atan2(r1*sin(LE-l1), RE-r1*cos(LE-l1));
        } else {
            L = l1 + atan2(RE*sin(l1-LE), r1-RE*cos(l1-LE));
        }
        double B = atan(r1*tan(UI)*sin(L-l1) / (RE*sin(l1-LE)));
        
        double ro = sqrt(RE*RE + r*r - 2*RE*r*cos(l - LE)*cos(UI));
        double angularSize = teta0 / ro;
        
        double F = (1 + cos(L - l)) / 2;
        double magnitude = V0 + 5*Math.log10((r*ro)/Math.sqrt(F));

        L = Angle.normalizePositive(L);
        //B = RightOpenInterval.of(-Math.TAU/4, Math.TAU/4).reduce(B);

        return new Planet(name, eclipticToEquatorialConversion.apply(EclipticCoordinates.of(L, B)), (float)angularSize, (float)magnitude);
    }
    
}
