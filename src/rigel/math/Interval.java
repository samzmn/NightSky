package rigel.math;

/**
 *
 * @author hassanzmn
 */
public abstract class Interval {
    private final double lower;
    private final double higher;

    protected Interval(double low, double up) {
            this.lower = low;
            this.higher = up;
    }

    public double low() {
            return lower;
    }

    public double high() {
            return higher;
    }

    public double size() {
            return higher-lower;
    }

    //return true if v is in the range.
    abstract public boolean contains(double v);

    @Override
    public int hashCode(){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o){
        throw new UnsupportedOperationException();
    }
}