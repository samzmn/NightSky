package rigel.astronomy;

import java.io.*;
import rigel.coordinates.EquatorialCoordinates;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 *
 * @author hassanzmn
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;
    
    private int hipparcos;
    private String name, bayer;
    private float magnitude;
    private float colorIndex;
    
    @Override
    public void load(InputStream inputstream, StarCatalogue.Builder builder) throws IOException{
        //inputstream = Files.newInputStream(Path.of("resources\\hygdata_v3.csv"), StandardOpenOption.READ)
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, US_ASCII)) ){
            String s = br.readLine();
            
            while ( (s = br.readLine()) != null ) {
                String[] prop = s.split(",");
                bayer = !prop[27].equals("")? prop[27] : "?";
                name = !prop[6].equals("")? prop[6] : bayer+" "+prop[29];
                hipparcos = !prop[1].equals("")? Integer.parseInt(prop[1]) : 0;
                magnitude = !prop[13].equals("")? Float.parseFloat(prop[13]) : 0;
                colorIndex = !prop[16].equals("")? Float.parseFloat(prop[16]) : 0;
                
                Star star = new Star(hipparcos, name, 
                        EquatorialCoordinates.of(Double.parseDouble(prop[23]), Double.parseDouble(prop[24])),
                        magnitude, colorIndex);
                builder.addStar(star);
            }
            
        }catch(Exception ex){
            throw new IOException("Error : invalid inputStream in HygDatabaseLoader");
        }
    }
}
