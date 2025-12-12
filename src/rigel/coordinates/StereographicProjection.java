package rigel.coordinates;

import rigel.math.Angle;
import static java.lang.Math.*;
import java.util.Locale;
import java.util.function.Function;

/**
 *
 * @author hassanzmn
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {
    //private final HorizontalCoordinates center;
    private final double azC;
    private final double altC;
    private final double cosAltC , sinAltC;
    
    public StereographicProjection(HorizontalCoordinates center) {
        //this.center = center;
        this.azC = center.az();
        this.altC = center.alt();
        cosAltC = cos(altC);
        sinAltC = sin(altC);
    }
    
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return CartesianCoordinates.of(0, cosAltC/(sin(hor.alt())+sinAltC));
    }
    
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        return cos(parallel.alt())/(sin(parallel.alt())+sinAltC);
    }
    
    public double applyToAngle(double rad) {
        return 2 * tan(rad/4);
    }
    
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates horizontalCoordinates) {
        double ld = horizontalCoordinates.az() - azC;
        double cosLd = cos(ld);
        double sinF = sin(horizontalCoordinates.alt());
        double cosF = cos(horizontalCoordinates.alt());
        double d = 1 / (1 + sinF*sinAltC + cosF*cosAltC*cosLd);
        
        return CartesianCoordinates.of(
                d*cosF*sin(ld),
                d*(sinF*cosAltC - cosF*sinAltC*cosLd));
    }

    public HorizontalCoordinates inverseApply(CartesianCoordinates cartesianCoordinates) {
        if (cartesianCoordinates.x() == 0 && cartesianCoordinates.y() == 0){
            return HorizontalCoordinates.of(azC, altC);
        }
        
        double rhoSq = cartesianCoordinates.x()*cartesianCoordinates.x()+cartesianCoordinates.y()*cartesianCoordinates.y();
        double ro = sqrt(rhoSq);
        double sinc = (2*ro)/(rhoSq+1);
        double cosc = (1-rhoSq)/(rhoSq+1);
        
        double l = atan2(cartesianCoordinates.x()*sinc, ro*cosAltC*cosc - cartesianCoordinates.y()*sinAltC*sinc) + azC;
        double fi = asin(cosc*sinAltC + (cartesianCoordinates.y()*sinc*cosAltC)/ro);
        
        return HorizontalCoordinates.of(Angle.normalizePositive(l), fi);
    }
    
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "StereographicProjection centered at: (x=%.4f, y=%.4f)", azC, altC);
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
