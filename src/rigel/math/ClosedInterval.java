package rigel.math;

import java.util.Locale;
import static rigel.Preconditions.checkArgument;
/**
 *
 * @author hassanzmn
 */
public final class ClosedInterval extends Interval{
    
    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    public static ClosedInterval of(double low, double high) {
        checkArgument(low < high);
        return new ClosedInterval(low, high);
    }

    public static ClosedInterval symmetric(double size) {
        checkArgument(size > 0);
        return new ClosedInterval(-size/2, size/2);
    }
    
    public double clip(double x) {
        if (x > high()) {
            return high();
        } else if( x < low()) {
            return low();
        }
        return x;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%.2f, %.2f]", low(), high());
    }

    @Override
    public boolean contains(double x) {
        return x>=low() && x<=high();
    }

}
