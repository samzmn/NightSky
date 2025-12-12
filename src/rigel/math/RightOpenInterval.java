package rigel.math;

import static rigel.Preconditions.checkArgument;
import java.util.Locale;

/**
 *
 * @author hassanzmn
 */
public class RightOpenInterval extends Interval {
    
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    public static RightOpenInterval of(double low, double high) {
        checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    public static RightOpenInterval symmetric(double size) {
        checkArgument(size > 0);
        return new RightOpenInterval(-size/2, size/2);
    }
    
    public double reduce(double x) {
        double a = low();
        double b = high();
        return a + (x-a) - (b-a)*Math.floor((x-a)/(b-a));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%.2f, %.2f[", low(), high());
    }

    @Override
    public boolean contains(double x) {
        return x>=low() && x<high();
    }

}
