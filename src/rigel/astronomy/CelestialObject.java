package rigel.astronomy;

import rigel.coordinates.EquatorialCoordinates;
import java.util.Objects;
/**
 *
 * @author hassanzmn
 */
public abstract class CelestialObject {
    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;
    
    protected CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        if (angularSize < 0) throw new IllegalArgumentException("Angular Size is Negetive");
        
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }
    
    public String name(){
        return name;
    }
    
    public double angularSize(){
        return angularSize;
    }
    
    public double magnitude(){
        return magnitude;
    }
    
    public EquatorialCoordinates equatorialPos(){
        return equatorialPos;
    }
    
    public String info(){
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
