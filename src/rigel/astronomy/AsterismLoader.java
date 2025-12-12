package rigel.astronomy;

import java.io.*;
import static java.nio.charset.StandardCharsets.US_ASCII;
import java.util.*;

/**
 *
 * @author hassanzmn
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        HashMap<Integer, Star> hipparcosToStar = new HashMap<>();
        for (var star : builder.stars()) {
            if (star.hipparcosId() > 0)
                hipparcosToStar.put(star.hipparcosId(), star);
        }

        try (BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, US_ASCII))) {
            String line = (String)null;
            while ((line = r.readLine()) != null) {
                ArrayList<Star> stars = new ArrayList<>();
                for (String id : line.split(","))
                    stars.add(hipparcosToStar.get(Integer.valueOf(id)));
                builder.addAsterism(new Asterism(stars));
            }
        }
    }
    
}
