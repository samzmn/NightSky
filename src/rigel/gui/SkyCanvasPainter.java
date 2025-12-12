package rigel.gui;

import java.awt.RadialGradientPaint;
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
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

/**
 *
 * @author hassanzmn
 */
public class SkyCanvasPainter {
    private final Canvas canvas;
    private final GraphicsContext graph;
    private final ModifySkyBeans modifySkyBeans;
    private final Color horizonColor = Color.WHITE;
    private final double asterismLineThickness = 1;

    public SkyCanvasPainter(Canvas canvas, ModifySkyBeans modifySkyBeans) {
        this.canvas = canvas;
        graph = this.canvas.getGraphicsContext2D();
        this.modifySkyBeans = modifySkyBeans;
    }
    
    public double diameter(double magnitude, Transform trans, StereographicProjection projection){ //for stars and planets
        double m1 = ClosedInterval.of(-2, 5).clip(magnitude);
        double scalefactor =  (99 - 17 * m1) / 140;
        double planeSize = projection.applyToAngle(Angle.ofDeg(0.5)) * scalefactor;
        return trans.deltaTransform(planeSize, 0).getX();
    }
    public double diameter(CelestialObject ob, Transform trans, StereographicProjection projection){ //for Sun and Moon
        return projection.applyToAngle(ob.angularSize()) * trans.getMxx();
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
    
    public void drawAsterisms(ObservedSky observedSky, StereographicProjection projection, Transform transform) {
        if (this.modifySkyBeans.isAsterisms() == false) {
            return;
        }
        List<Star> stars = observedSky.stars();
        double[] starPos = observedSky.starPositions();
        transform.transform2DPoints(starPos, 0, starPos, 0, stars.size());
        
        // Asterisms
        graph.setStroke(Color.WHITE);
        graph.setLineWidth(asterismLineThickness);
        Bounds visibleBounds = canvas.getBoundsInLocal();
        for (Asterism asterism : observedSky.asterisms()) {
            List<Integer> starIndices = observedSky.asterismIndexes(asterism);

            Integer starIndex0 = starIndices.get(0);
            double x0 = starPos[2 * starIndex0];
            double y0 = starPos[2 * starIndex0 + 1];
            boolean previousVisible = visibleBounds.contains(x0, y0);

            graph.beginPath();
            graph.moveTo(x0, y0);
            for (Integer starIndex : starIndices.subList(1, starIndices.size())) {
                double x = starPos[2 * starIndex];
                double y = starPos[2 * starIndex + 1];
                boolean currentVisible = visibleBounds.contains(x, y);

                if (previousVisible || currentVisible)
                    graph.lineTo(x, y);
                else
                    graph.moveTo(x, y);
                previousVisible = currentVisible;
            }
            graph.stroke();
        }
    }
    
    public void drawStars(ObservedSky observedSky, StereographicProjection projection, Transform planeToCanvas) {
        List<Star> stars = observedSky.stars();
        double[] starPos = observedSky.starPositions();
        planeToCanvas.transform2DPoints(starPos, 0, starPos, 0, stars.size());
        Bounds visibleBounds = canvas.getBoundsInLocal();
        // Stars
        int i = 0;
        for (Star star : stars) {
            double x = starPos[i++];
            double y = starPos[i++];
            if(!visibleBounds.contains(x, y)) continue;
            double d = diameter(star.magnitude(), planeToCanvas, projection);
            d = d * this.modifySkyBeans.getMagnify();
            Color starColor;
            if (this.modifySkyBeans.isStarsRealColor() == true) {
                starColor = BlackBodyColor.colorForTemperature(star.colorTemperature());
            } else {
                starColor = Color.WHITE;
            }

            drawDisc(x, y, d, starColor);
        }
        
    }
    
    public void drawPlanets(ObservedSky sky, Transform transform, StereographicProjection projection){
        List<Planet> planets = sky.planets();
        double[] planetsPos = sky.planetPositions();
        transform.transform2DPoints(planetsPos, 0, planetsPos, 0, planets.size());
        
        int i = 0;
        for(Planet planet : planets){
            double d = diameter(planet.magnitude(), transform, projection);
            d = d * this.modifySkyBeans.getMagnify();
            drawDisc(planetsPos[i++], planetsPos[i++], d, Color.LIGHTGRAY);  
        }
    }
    
    public void drawSun(ObservedSky sky, Transform transform, StereographicProjection projection){

        Sun sun = sky.sun();
        CartesianCoordinates sunCC = sky.sunPosition();
        Point2D point = transform.transform(new Point2D(sunCC.x(), sunCC.y()));
        double d = diameter(sun, transform, projection);
        d *= this.modifySkyBeans.getMagnify();
        d *= 2; //Scale factor besides the magnify
        RadialGradient sunRadialGradient = RadialGradient.valueOf("center 50% 50%, radius 50%, white 0%, yellow 80%, black 100%");

        graph.setFill(sunRadialGradient);
        graph.fillOval(point.getX() - d / 2, point.getY() - d / 2, d, d);

    }
    
    public void drawMoon(ObservedSky sky, Transform transform, StereographicProjection projection){
        
        Moon moon = sky.moon();
        
        CartesianCoordinates moonCC = sky.moonPosition();
        Point2D point = transform.transform(new Point2D(moonCC.x(), moonCC.y()));
        double x = point.getX();
        double y = point.getY();
        
        double d = diameter(moon, transform, projection);
        d = d * (this.modifySkyBeans.getMagnify() * 2);// The Scale Factor other than magnify attribute
        double r = d/2;
        double f = moon.phase();// between 0 and 1;
        
        double b;
        if(f < 0.5){
            b = 2*f*r;
        }else{
            b = 2 * (f - 0.5) * r;
        }
        
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
        
        graph.setStroke(horizonColor);
        graph.setLineWidth(2);
        graph.strokeOval(center.getX() - radius, center.getY() - radius, diameter, diameter);

        if (this.modifySkyBeans.isVectorsVisable() == true) {
            graph.setFill(horizonColor);
            graph.setTextAlign(TextAlignment.CENTER);
            graph.setTextBaseline(VPos.TOP);
            for (var azDeg = 0; azDeg < 360; azDeg += 45) {
                HorizontalCoordinates horPos = HorizontalCoordinates.ofDeg(azDeg, -0.5);
                CartesianCoordinates planePos = projection.apply(horPos);
                Point2D imagePos = transform.transform(planePos.x(), planePos.y());
                graph.fillText(horPos.azOctantName("N", "E", "S", "W"), imagePos.getX(), imagePos.getY());
            }
        }

    }

    public void drawBackGround() {
        graph.setFill(Color.BLACK);

    }
}
