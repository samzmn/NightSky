package design;

import java.io.IOException;
import java.io.InputStream;
import rigel.math.*;
import rigel.coordinates.*;
import rigel.astronomy.*;
import java.util.*;
import static java.lang.Math.*;
import javafx.scene.canvas.*;
import javafx.scene.transform.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.scene.image.Image;

/**
 *
 * @author hassanzmn
 */
public class SkyPainter {
    private final Canvas canvas;
    private final GraphicsContext graph;
    
    private final double diameterScaleFactor = 8;
    private final double asterismLineWidth = 0.5;
    private final Color asterismLineColor = Color.WHITE;
    private final double horizonLineWidth = 5;
    private final Color horizonLineColor = Color.BLACK;
    
    public SkyPainter(Canvas canvas){
        this.canvas = canvas;
        graph = this.canvas.getGraphicsContext2D();
    }
    
    private double[] circleAllowed(StereographicProjection projection, Transform transform){
        HorizontalCoordinates horizonCoordinates = HorizontalCoordinates.of(0, 0);
        CartesianCoordinates planeCenter = projection.circleCenterForParallel(horizonCoordinates);
        double planeRadius = projection.circleRadiusForParallel(horizonCoordinates);
        
        Point2D center = transform.transform(planeCenter.x(), planeCenter.y());
        double radius = transform.deltaTransform(planeRadius, 0).getX();
        
        double[] needed = new double[3];
        needed[0]= center.getX();
        needed[1]= center.getY();
        needed[2]= radius;
        return needed;
    }
    
    private double diameter(double magnitude, Transform trans, StereographicProjection projection){ //for stars and planets
        double m1 = ClosedInterval.of(-2, 5).clip(magnitude);
        double scalefactor =  (99 - 17 * m1) / 140;
        double planeSize = projection.applyToAngle(Angle.ofDeg(0.5)) * scalefactor;
        return trans.deltaTransform(planeSize, 0).getX() * diameterScaleFactor;
    }
    private double diameter(CelestialObject ob, Transform trans, StereographicProjection projection){ //for Sun and Moon
        return projection.applyToAngle(ob.angularSize()) * trans.getMxx() * diameterScaleFactor;
    }
    private void drawDisc(double centerX, double centerY, double diameter, Color color) {
        var radius = diameter / 2;
        graph.setFill(color);
        graph.fillOval(centerX - radius, centerY - radius, diameter, diameter);
    }
    private void drawDisc(Point2D center, double diameter, Color color) {
        drawDisc(center.getX(), center.getY(), diameter, color);
    }
    
    public void clear() {
        graph.setFill(Color.BLACK);
        graph.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void planeBackground(Color bgColor, StereographicProjection projection, Transform transform){
        double[] planeCircle = circleAllowed(projection, transform);
        double diameter = 2 * planeCircle[2];
        graph.setFill(bgColor);
        graph.fillOval(planeCircle[0] - planeCircle[2], planeCircle[1] - planeCircle[2], diameter, diameter);
    }
    
    public void drawAsterisms(ObservedSky observedSky, StereographicProjection projection, Transform transform){
        double[] harizon = circleAllowed(projection, transform);
        double deltaX, deltaY;
        
        List<Star> stars = observedSky.stars();
        double[] starPos = observedSky.starPositions();
        transform.transform2DPoints(starPos, 0, starPos, 0, stars.size());
        
        // Asterisms
        graph.setStroke(asterismLineColor);
        graph.setLineWidth(asterismLineWidth);
        
        for (Asterism asterism : observedSky.asterisms()) {
            List<Integer> starIndices = observedSky.asterismIndexes(asterism);

            Integer starIndex0 = starIndices.get(0);
            double x0 = starPos[2 * starIndex0];
            double y0 = starPos[2 * starIndex0 + 1];
            deltaX = abs(x0 - harizon[0]);
            deltaY = abs(y0 - harizon[1]);
            boolean previousVisible = !(sqrt(pow(deltaX, 2)+pow(deltaY, 2)) > harizon[2]);

            graph.beginPath();
            graph.moveTo(x0, y0);
            for (Integer starIndex : starIndices.subList(1, starIndices.size())) {
                double x = starPos[2 * starIndex];
                double y = starPos[2 * starIndex + 1];
                deltaX = abs(x - harizon[0]);
                deltaY = abs(y - harizon[1]);
                boolean currentVisible = !(sqrt(pow(deltaX, 2)+pow(deltaY, 2)) > harizon[2]);

                if (previousVisible && currentVisible)
                    graph.lineTo(x, y);
                else
                    graph.moveTo(x, y);
                previousVisible = currentVisible;
            }
            graph.stroke();
        }
    }
    
    public void drawStars(ObservedSky observedSky, StereographicProjection projection, Transform planeToCanvas) {
        double[] harizon = circleAllowed(projection, planeToCanvas);
        double deltaX, deltaY;
        
        List<Star> stars = observedSky.stars();
        double[] starPos = observedSky.starPositions();
        planeToCanvas.transform2DPoints(starPos, 0, starPos, 0, stars.size());
        
        // Stars
        int i = 0;
        for (Star star : stars) {
            double x = starPos[i++];
            double y = starPos[i++];
            deltaX = abs(x - harizon[0]);
            deltaY = abs(y - harizon[1]);
            if(sqrt(pow(deltaX, 2)+pow(deltaY, 2)) > harizon[2]) continue;
            double d = diameter(star.magnitude(), planeToCanvas, projection);
            Color c = StarColor.colorForTemperature(star.colorTemperature());
            drawDisc(x, y, d, c);
        }
        
    }
    
    public void drawPlanets(ObservedSky sky, Transform transform, StereographicProjection projection){
        double[] harizon = circleAllowed(projection, transform);
        double deltaX, deltaY;
        
        List<Planet> planets = sky.planets();
        double[] planetsPos = sky.planetPositions();
        transform.transform2DPoints(planetsPos, 0, planetsPos, 0, planets.size());
        
        int i = 0;
        for(Planet planet : planets){
            double x = planetsPos[i++];
            double y = planetsPos[i++];
            deltaX = abs(x - harizon[0]);
            deltaY = abs(y - harizon[1]);
            if(sqrt(pow(deltaX, 2)+pow(deltaY, 2)) > harizon[2]) continue;
            double d = diameter(planet.magnitude(), transform, projection);
            drawDisc(x, y, d, Color.LIGHTGRAY);  
        }
    }
    
    public void drawSun(ObservedSky sky, Transform transform, StereographicProjection projection){
        double[] harizon = circleAllowed(projection, transform);
        double deltaX, deltaY;
        
        Sun sun = sky.sun();
        CartesianCoordinates sunCC = sky.sunPosition();
        Point2D point = transform.transform(new Point2D(sunCC.x(), sunCC.y()));
        double d = diameter(sun, transform, projection);
        d *= 2;
        deltaX = abs(point.getX() - harizon[0]);
        deltaY = abs(point.getY() - harizon[1]);
        if(sqrt(pow(deltaX, 2)+pow(deltaY, 2)) > harizon[2]) return;
        
        Stop stop1 = new Stop(0, Color.WHITE);
        Stop stop2 = new Stop(1, Color.YELLOW);
        RadialGradient rg = new RadialGradient(0, .01, point.getX(), point.getY(), d, false, CycleMethod.NO_CYCLE, stop1, stop2);
        graph.setFill(rg);
        graph.fillOval(point.getX()-d/2, point.getY()-d/2, d, d);
        //drawDisc(point, d*2.2, Color.YELLOW.deriveColor(0, 1, 1, 0.25));
        //drawDisc(point, d+2, Color.YELLOW);
        //drawDisc(point, d, Color.WHITE);
    }
    
    public void drawMoon(ObservedSky sky, Transform transform, StereographicProjection projection){
        double[] horizon = circleAllowed(projection, transform);
        double deltaX, deltaY;
        
        Moon moon = sky.moon();
        
        CartesianCoordinates moonCC = sky.moonPosition();
        Point2D point = transform.transform(new Point2D(moonCC.x(), moonCC.y()));
        double x = point.getX();
        double y = point.getY();
        
        deltaX = abs(x - horizon[0]);
        deltaY = abs(y - horizon[1]);
        if(sqrt(pow(deltaX, 2)+pow(deltaY, 2)) > horizon[2]) return;
        
        double d = diameter(moon, transform, projection);
        d *= 3;// The Scale Factor
        double r = d/2;
        double f = moon.phase();// between 0 and 1;
        
        double b;
        if(f < 0.5){
            b = 2*f*r;
        }else{
            b = 2 * (f - 0.5) * r;
        }
        b = r - b;
        double moonAge = moon.Age(); // between 0 and 2PI
        
        //Color dark = Color.gray(0.15);
        Color dark = Color.BLACK;
        
        
        Image img = null;
        try(InputStream in = this.getClass().getResourceAsStream("/moon.png")) {
            img = new Image(in);
        } catch (IOException e) {
        }
        
        graph.drawImage(img, x-r, y-r, d, d);
        
        graph.setFill(dark);
        
        if(moonAge>=0 && moonAge<PI/2){
            graph.beginPath();
            graph.arc(x, y, b, r, 270, 180);
            graph.arc(x, y, r, r, 90, 180);
            graph.closePath();
            graph.fill();
        }else if(moonAge>=PI/2 && moonAge<PI) {
            graph.beginPath();
            graph.arc(x, y, b, r, 90, 180);
            graph.arc(x, y, r, r, 270, -180);
            graph.closePath();
            graph.fill();
        } else if(moonAge>=PI && moonAge<(3*PI/2)){
            graph.beginPath();
            graph.arc(x, y, b, r, 90, -180);
            graph.arc(x, y, r, r, 270, 180);
            graph.closePath();
            graph.fill();
        }else {
            graph.beginPath();
            graph.arc(x, y, b, r, 90, 180);
            graph.arc(x, y, r, r, 270, 180);
            graph.closePath();
            graph.fill();
        }
        
    }
    
    public void drawHorizon(StereographicProjection projection, Transform transform){
        HorizontalCoordinates horizonCoordinates = HorizontalCoordinates.of(0, 0);
        CartesianCoordinates planeCenter = projection.circleCenterForParallel(horizonCoordinates);
        double planeRadius = projection.circleRadiusForParallel(horizonCoordinates);

        Point2D center = transform.transform(planeCenter.x(), planeCenter.y());
        double radius = transform.deltaTransform(planeRadius, 0).getX();
        double diameter = 2 * radius;
        
        graph.setStroke(horizonLineColor);
        graph.setLineWidth(horizonLineWidth);
        graph.strokeOval(center.getX() - radius, center.getY() - radius, diameter, diameter);
        /*
        graph.setFill(horizonLineColor);
        graph.setTextAlign(TextAlignment.CENTER);
        graph.setTextBaseline(VPos.TOP);
        for (var azDeg = 0; azDeg < 360; azDeg += 45) {
            HorizontalCoordinates horPos = HorizontalCoordinates.ofDeg(azDeg, -0.5);
            CartesianCoordinates planePos = projection.apply(horPos);
            Point2D imagePos = transform.transform(planePos.x(), planePos.y());
            graph.fillText(horPos.azOctantName("N", "E", "S", "W"), imagePos.getX(), imagePos.getY());
        }
        */
    }
}
