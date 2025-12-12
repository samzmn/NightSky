package rigel.astronomy;

import java.io.*;
import java.util.*;

/**
 *
 * @author hassanzmn
 */
public final class StarCatalogue {
    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> asterismIndices;

    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        var starMap = new HashMap<Star, Integer>();
        for (var star : stars)
            starMap.put(star, starMap.size());

        HashMap<Asterism, List<Integer>> asterismInxs = new HashMap<>();
        for (Asterism asterism : asterisms) {
            List<Star> asterismStars = asterism.stars();
            ArrayList<Integer> indices = new ArrayList<Integer>(asterismStars.size());
            for (Star asterismStar : asterismStars) {
                Integer starIndex = starMap.get(asterismStar);
                if (starIndex == null)
                    throw new IllegalArgumentException();
                indices.add(starIndex);
            }
            asterismInxs.put(asterism, Collections.unmodifiableList(indices));
        }

        this.stars = List.copyOf(stars);
        this.asterismIndices = Collections.unmodifiableMap(asterismInxs);
    }

    public List<Star> stars() {
        return stars;
    }

    public Set<Asterism> asterisms() {
        return asterismIndices.keySet();
    }

    public List<Integer> asterismIndexes(Asterism asterism) {
        var indices = asterismIndices.get(asterism);
        if (indices == null)
            throw new IllegalArgumentException();
        return indices;
    }
    
    // class StarCatalogue.Builder
    public final static class Builder {
        private final List<Star> stars;
        private final List<Asterism> asterisms; 
        public Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
        }
        
        public Builder addStar(Star star){
            stars.add(star);
            return this;
        }
        
        public List<Star> stars(){
            return List.copyOf(stars);
        }
        
        public Builder addAsterism(Asterism asterism){
            asterisms.add(asterism);
            return this;
        }
        
        public List<Asterism> asterisms() {
            return List.copyOf(asterisms);
        }
        
        public Builder loadFrom(InputStream inputstream, Loader loader) throws IOException{
            loader.load(inputstream, this);
            return this;
        }
        
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }

    
    
    public interface Loader {
        public abstract void load(InputStream inputstream, Builder builder) throws IOException;
    }
}
