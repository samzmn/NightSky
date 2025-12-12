package rigel.astronomy;

import rigel.coordinates.EclipticToEquatorialConversion;

/**
 *
 * @author hassanzmn
 * represents a celestial object model, i.e a way of calculating the characteristics of this object at a given moment.
 * @param <O> represents the type of the objects modeled by the model
 */
public interface CelestialObjectModel<O> {
    public abstract O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
