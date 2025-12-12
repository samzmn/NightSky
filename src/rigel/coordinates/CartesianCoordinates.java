package rigel.coordinates;

import java.util.Locale;

/**
 *
 * @author hassanzmn
 */
public final class CartesianCoordinates {
    private final double x, y;
    
    private CartesianCoordinates(double abscissa, double ordinate) {
        this.x = abscissa;
        this.y = ordinate;
    }
    
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }
    
    public double x() {
        return this.x;
    }
    
    public double y() {
        return this.y;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", this.x, this.y);
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
