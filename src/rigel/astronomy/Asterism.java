package rigel.astronomy;

import java.util.List;

/**
 *
 * @author hassanzmn
 */
public final class Asterism {
    private final List<Star> stars;
    
    public Asterism(List<Star> stars) {
        if (stars.isEmpty()) {
            throw new IllegalArgumentException("the list is empty!");
        }
        this.stars = List.copyOf(stars);
    }
    
    public List<Star> stars() {
        return this.stars;
    }
}
