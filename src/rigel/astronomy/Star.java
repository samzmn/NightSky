package rigel.astronomy;

import rigel.math.ClosedInterval;
import rigel.coordinates.EquatorialCoordinates;
/**
 *
 * @author hassanzmn
 */
public final class Star extends CelestialObject {
    private final int hipparcosId;
    private final float colorIndex;
    
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);
        if (hipparcosId<0) throw new IllegalArgumentException("hippacrosId is negetive");
        if (!ClosedInterval.of(-0.5, 5.5).contains(colorIndex)) throw new IllegalArgumentException("colorId out of bound");
        this.hipparcosId = hipparcosId;
        this.colorIndex = colorIndex;
    }
    
    public int hipparcosId(){
        return this.hipparcosId;
    }
    
    public int colorTemperature(){
        double T = 4600 * ((1/(0.92*colorIndex+1.7))+(1/(0.92*colorIndex+0.62))) ;
        return (int)T;
    }
}
