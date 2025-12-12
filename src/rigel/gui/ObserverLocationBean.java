package rigel.gui;

import rigel.coordinates.GeographicCoordinates;
import javafx.beans.property.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
/**
 *
 * @author hassanzmn
 */
public class ObserverLocationBean {
    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObservableValue<GeographicCoordinates> coordinates;
    public ObserverLocationBean(){
        this.lonDeg = new SimpleDoubleProperty();
        this.latDeg = new SimpleDoubleProperty();
        this.coordinates = Bindings.createObjectBinding(() ->
                        GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()),
                lonDeg,
                latDeg);
    }
    
    public ObservableValue<GeographicCoordinates> coordinatesProperty(){
        return this.coordinates;
    }
    public GeographicCoordinates getCoordinates(){
        return this.coordinates.getValue();
    }
    public void setCoordinates(GeographicCoordinates geographic){
        this.lonDeg.set(geographic.lonDeg());
        this.latDeg.set(geographic.latDeg());
    }
    
    public DoubleProperty lonDegProperty(){
        return this.lonDeg;
    }
    public double getLonDeg(){
        return this.lonDeg.getValue();
    }
    
    public DoubleProperty latDegProperty(){
        return this.latDeg;
    }
    public double getLatDeg(){
        return this.latDeg.getValue();
    }
    
}
