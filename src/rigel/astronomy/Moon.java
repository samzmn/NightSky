package rigel.astronomy;

import rigel.coordinates.EquatorialCoordinates;
import java.util.Locale;

/**
 *
 * @author hassanzmn
 */
public final class Moon extends CelestialObject {
    private final double phase;
    private final double moonAge;
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, double phase, double moonAge) {
        super("Moon", equatorialPos, angularSize, magnitude);
        if (phase<0 || phase>1) throw new IllegalArgumentException();
        this.phase = phase;
        this.moonAge = moonAge;
    }
    public double Age(){ //in radian
        return this.moonAge;
    }
    public double phase(){
        return this.phase;
    }
    
    @Override
    public String info(){
        return String.format(Locale.ROOT, "Moon (%.1f)", this.phase*100);
    }
}
