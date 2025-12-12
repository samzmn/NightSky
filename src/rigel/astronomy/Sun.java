package rigel.astronomy;

import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EquatorialCoordinates;
import java.util.Objects;
/**
 *
 * @author hassanzmn
 */
public final class Sun extends CelestialObject {
    private final float meanAnomaly;
    private final EclipticCoordinates eclipticPos;
    
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Sun", equatorialPos, angularSize, (float)-26.7);
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }
    
    public EclipticCoordinates eclipticPos(){
        return this.eclipticPos;
    }
    
    public double meanAnomaly(){
        return this.meanAnomaly;
    }
}
