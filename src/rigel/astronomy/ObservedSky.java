package rigel.astronomy;

import rigel.coordinates.EquatorialToHorizontalConversion;
import rigel.coordinates.EclipticToEquatorialConversion;
import rigel.coordinates.CartesianCoordinates;
import rigel.coordinates.StereographicProjection;
import rigel.coordinates.EquatorialCoordinates;
import rigel.coordinates.GeographicCoordinates;
import static rigel.astronomy.PlanetModel.*;
import java.time.ZonedDateTime;
import java.util.*;
import static java.lang.Math.*;
import static java.util.Collections.unmodifiableMap;
import java.util.function.Function;

/**
 *
 * @author hassanzmn
 */

public class ObservedSky {
    private enum Kind {SUN, PLANET, MOON, STAR}

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> planets;
    private final StarCatalogue starCatalog;

    private final Map<Kind, double[]> positions;
    
    public ObservedSky(ZonedDateTime when, GeographicCoordinates geographic, StereographicProjection stereo, StarCatalogue catalog){
        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);
        EclipticToEquatorialConversion eclipToEquo = new EclipticToEquatorialConversion(when);
        
        starCatalog = catalog;
        
        sun = SunModel.SUN.at(daysSinceJ2010, eclipToEquo);
        moon = MoonModel.MOON.at(daysSinceJ2010, eclipToEquo);
        planets = new ArrayList<>(7);
        Iterator<PlanetModel> planetModelIt = List.of(MERCURY, VENUS, MARS, JUPITER,SATURN,URANUS,NEPTUNE).iterator();
        while (planetModelIt.hasNext()) {
            planets.add(planetModelIt.next().at(daysSinceJ2010, eclipToEquo));
        }
        
        Function<EquatorialCoordinates, CartesianCoordinates> combinedProjection = 
                new EquatorialToHorizontalConversion(when, geographic).andThen(stereo);
        EnumMap<Kind, double[]> positions = new EnumMap<>(Map.of(
                Kind.SUN, computePositions(combinedProjection, List.of(sun)),
                Kind.MOON, computePositions(combinedProjection, List.of(moon)),
                Kind.PLANET, computePositions(combinedProjection, planets),
                Kind.STAR, computePositions(combinedProjection, starCatalog.stars())));
        
        this.positions = unmodifiableMap(positions);
        
    }
    
    private static double[] computePositions(Function<EquatorialCoordinates, CartesianCoordinates> projection,
                                             List<? extends CelestialObject> objects) {
        double[] positions = new double[2 * objects.size()];
        int i = 0;
        for (CelestialObject object : objects) {
            CartesianCoordinates position = projection.apply(object.equatorialPos());
            positions[i++] = position.x();
            positions[i++] = position.y();
        }
        return positions;
    }
    
    public Sun sun(){
        return sun;
    }
    
    public CartesianCoordinates sunPosition(){
        double[] pos = positions.get(Kind.SUN);
        return CartesianCoordinates.of(pos[0], pos[1]);
    }
    
    public Moon moon(){
        return moon;
    }
    
    public CartesianCoordinates moonPosition(){
        double[] pos = positions.get(Kind.MOON);
        return CartesianCoordinates.of(pos[0], pos[1]);
    }
    
    public List<Planet> planets(){
        return planets;
    }
    
    public double[] planetPositions(){
        double[] pos = positions.get(Kind.PLANET);
        return Arrays.copyOf(pos, pos.length);
    }
    
    public List<Star> stars(){
        return starCatalog.stars();
    }
    
    public double[] starPositions(){
        double[] pos = positions.get(Kind.STAR);
        return Arrays.copyOf(pos, pos.length);
    }
    
    public Set<Asterism> asterisms(){
        return starCatalog.asterisms();
    }
    
    public List<Integer> asterismIndexes(Asterism asterism){
        return starCatalog.asterismIndexes(asterism);
    }
    
    public CelestialObject objectClosestTo(double x, double y, double maxDistance){
        var closestDistance = maxDistance;
        var closestKind = (Kind) null;
        var closestIndex = -1;

        for (Map.Entry<Kind, double[]> entry : positions.entrySet()) {
            Kind kind = entry.getKey();
            double[] positionS = entry.getValue();
            for (int i = 0; i < positionS.length; i += 2) {
                var dX = abs(x - positionS[i]);
                var dY = abs(y - positionS[i + 1]);
                if (dX < closestDistance && dY < closestDistance) {
                    var d = hypot(dX, dY);
                    if (d < closestDistance) {
                        closestKind = kind;
                        closestIndex = i >> 1;
                        closestDistance = d;
                    }
                }
            }
        }
        return (closestKind != null)? object(closestKind, closestIndex) : null;
    }
    
    private CelestialObject object(Kind kind, int index) {
        switch (kind) {
            case SUN -> {
                return sun;
            }
            case MOON -> {
                return moon;
            }
            case PLANET -> {
                return planets().get(index);
            }
            case STAR -> {
                return stars().get(index);
            }
            default -> throw new Error();
        }
    }
    
}
