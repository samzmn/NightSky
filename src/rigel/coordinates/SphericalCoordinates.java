package rigel.coordinates;

import rigel.math.Angle;
/**
 *
 * @author hassanzmn
 */
abstract class SphericalCoordinates {
    private double longitude;
    private double latitude;
    
    SphericalCoordinates(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    protected double lon(){
        return longitude;
    }
    
    protected double lonDeg(){
        return Angle.toDeg(longitude) ;
    }
    
    protected double lat(){
        return latitude;
    }
    
    protected double latDeg(){
        return Angle.toDeg(latitude) ;
    }
    
    @Override
    public final int hashCode(){
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final boolean equals(Object o){
        throw new UnsupportedOperationException();
    }
}
