package design;

import rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.*;

/**
 *
 * @author hassanzmn
 */
public class ViewingParametersBeanDesign {
    private final DoubleProperty fieldOfViewDeg ;
    private final ObjectProperty<HorizontalCoordinates> center;
    public ViewingParametersBeanDesign(){
        this.fieldOfViewDeg = new SimpleDoubleProperty();
        this.center = new SimpleObjectProperty<HorizontalCoordinates>();
    }
    
    
    public DoubleProperty fieldOfViewDegProperty(){
        return this.fieldOfViewDeg;
    }
    public double getFieldOfViewDeg(){
        return this.fieldOfViewDeg.get();
    }
    public void setFieldOfViewDeg(double filedOfView){
        this.fieldOfViewDeg.set(filedOfView);
    }
    
    public ObjectProperty<HorizontalCoordinates> centerProperty(){
        return this.center;
    } 
    public HorizontalCoordinates getCenter(){
        return this.center.getValue();
    }
    public void setCenter(HorizontalCoordinates horizontalCenter){
        this.center.set(horizontalCenter);
    }
}
