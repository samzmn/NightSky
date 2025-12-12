package design;

import java.io.*;
import static java.nio.charset.StandardCharsets.US_ASCII;
import java.util.*;
import rigel.coordinates.GeographicCoordinates;
import javafx.beans.property.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
/**
 *
 * @author hassanzmn
 */
public class ObserverLocationBeanDesign {
    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObservableValue<GeographicCoordinates> coordinates;
    private Map<String, String> cityMap;
    
    private Map<String, String> idZoneCoordinateMap;
    private Map<String, String> cityCoordinatesMap;
    
    
    public ObserverLocationBeanDesign() {
        this.lonDeg = new SimpleDoubleProperty();
        this.latDeg = new SimpleDoubleProperty();
        this.coordinates = Bindings.createObjectBinding(() ->
                        GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()),
                lonDeg,
                latDeg);
        /*
        try(BufferedReader input =new BufferedReader(new InputStreamReader( getClass().getResourceAsStream("/geonames-all-cities-with-a-population-1000.csv"), US_ASCII))){
            cityMap = new TreeMap<>();
            String line = input.readLine();       
            while ((line = input.readLine())!= null) {
                String[] seprated = line.split(",");
                String key = "";
                if (seprated.length>5){
                    key = seprated[0] + ", " + seprated[1].substring(1);
                }
                //if (key.startsWith("\"")) key = key.substring(1, key.length()-1);
                String value = seprated[seprated.length - 3] +","+ seprated[seprated.length - 2] +","+ seprated[seprated.length - 1];
                cityMap.put(key, value);
            }
        }catch(IOException ex){
            System.out.println("Error");
        }
        */
        try(BufferedReader input =new BufferedReader(new InputStreamReader( getClass().getResourceAsStream("/geonames-all-cities-final.csv"), US_ASCII))){
            cityCoordinatesMap = new TreeMap<>();
            String line = input.readLine();   
            while ((line = input.readLine())!= null) {
                String[] seprated = line.split(",");
                String key , value;
                
                key = seprated[0] +", "+ seprated[1].split(";")[0];
                value = seprated[2] + "," + seprated[3];
                cityCoordinatesMap.put(key, value);
            }
        }catch(IOException ex){
            System.out.println("Error");
        }
        
    }
    
    public Map<String, String> getCityCoordinatesMap(){
        return this.cityCoordinatesMap;
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
