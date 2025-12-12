package design;

import rigel.math.*;
import rigel.coordinates.*;
import rigel.astronomy.*;
import java.util.Map;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.beans.binding.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
/**
 *
 * @author hassanzmn
 */
public class SkyManager {
    private static final Map<KeyCode, int[]> VIEW_DIRECTION_CHANGES = Map.of(
            KeyCode.LEFT, new int[]{-10, 0},
            KeyCode.RIGHT, new int[]{10, 0},
            KeyCode.UP, new int[]{0, 5},
            KeyCode.DOWN, new int[]{0, -5}
    );
    private static final ClosedInterval FIELD_OF_VIEW_INTERVAL = ClosedInterval.of(30, 360);
    private static final RightOpenInterval VIEWING_AZ_INTERVAL = RightOpenInterval.of(0, 360);
    private static final ClosedInterval VIEWING_ALT_INTERVAL = ClosedInterval.of(5, 90);

    private static final int MAX_OBJECT_DISTANCE = 10;

    private final Canvas canvas;
    private final SkyPainter painter;

    private final DateTimeBeanDesign dateTimeBean;
    private final ObserverLocationBeanDesign observerLocationBean;
    private final ViewingParametersBeanDesign viewingParametersBean;
    
    private final ObservableDoubleValue mouseAzDeg;
    private final ObservableDoubleValue mouseAltDeg;
    private final ObservableValue<CelestialObject> objectUnderMouse;
    
    private final ObservableValue<StereographicProjection> projection;
    private final ObservableValue<Transform> planeToCanvas;
    private final ObservableValue<ObservedSky> observedSky;
    private final ObjectProperty<Point2D> mousePosition;
    private final ObservableValue<HorizontalCoordinates> mouseHorizontalPosition;
    
    private final FeaturesBean features;
    private final ObservableValue<Boolean> showMoon;
    private final ObservableValue<Boolean> showConstellation;
    
    public SkyManager(Canvas canvas,StarCatalogue starCatalog,
                            DateTimeBeanDesign datetimebean,
                            ObserverLocationBeanDesign observerlocationbean,
                            ViewingParametersBeanDesign viewingparametersbean,
                            FeaturesBean featuresbean) {
        
        this.dateTimeBean = datetimebean;
        this.observerLocationBean = observerlocationbean;
        this.viewingParametersBean = viewingparametersbean;
        this.canvas = canvas;
        this.painter = new SkyPainter(this.canvas);
        this.mousePosition = new SimpleObjectProperty<>(Point2D.ZERO);
        
        projection = Bindings.createObjectBinding(() -> {
                return new StereographicProjection(viewingParametersBean.getCenter());
            }, viewingParametersBean.centerProperty());
        
        observedSky = Bindings.createObjectBinding(() -> new ObservedSky(
                                dateTimeBean.getZonedDateTime(),
                                observerLocationBean.getCoordinates(),
                                projection.getValue(),
                                starCatalog),
                dateTimeBean.dateProperty(),
                dateTimeBean.timeProperty(),
                dateTimeBean.zoneProperty(),
                observerLocationBean.coordinatesProperty(),
                projection);
        
        planeToCanvas = Bindings.createObjectBinding(() -> {
                    double fov = Angle.ofDeg(viewingParametersBean.getFieldOfViewDeg());
                    double width = canvas.widthProperty().get();
                    double height = canvas.heightProperty().get();
                    double scale = width / projection.getValue().applyToAngle(fov);
                    return Transform.translate(width / 2, height / 2)
                            .createConcatenation(Transform.scale(scale, -scale));
                },
                viewingParametersBean.fieldOfViewDegProperty(),
                projection,
                canvas.widthProperty(),
                canvas.heightProperty());
        
        objectUnderMouse = Bindings.createObjectBinding(()->{ 
                try{
                    Point2D point = planeToCanvas.getValue().inverseTransform(mousePosition.get()) ;
                    CelestialObject celestial =  observedSky.getValue().objectClosestTo(
                            point.getX(), 
                            point.getY(),
                            planeToCanvas.getValue().inverseDeltaTransform(MAX_OBJECT_DISTANCE, 0).getX());
                    return celestial;
                }catch (NonInvertibleTransformException e) {
                            return null;
                }
            }, planeToCanvas, mousePosition, observedSky);
        
        mouseHorizontalPosition = Bindings.createObjectBinding(() -> {
                try{
                    Point2D mousePoint = planeToCanvas.getValue().inverseTransform(mousePosition.get());
                    return projection.getValue().inverseApply(
                            CartesianCoordinates.of(mousePoint.getX(), mousePoint.getY()));
                }catch (NonInvertibleTransformException e) {
                    return null;
                }
            }, planeToCanvas, mousePosition, projection);
        
        mouseAzDeg = Bindings.createDoubleBinding(() -> {
                    HorizontalCoordinates mouseHorPos = mouseHorizontalPosition.getValue();
                    return mouseHorPos != null ? mouseHorPos.azDeg() : Double.NaN;
                } , mouseHorizontalPosition);
        
        mouseAltDeg = Bindings.createDoubleBinding(() -> {
                    HorizontalCoordinates mouseHorPos = mouseHorizontalPosition.getValue();
                    return mouseHorPos != null ? mouseHorPos.altDeg() : Double.NaN;
                }, mouseHorizontalPosition);
                
        
        
        this.features = featuresbean;
        this.showMoon = Bindings.createObjectBinding(()->{
            return (Boolean)this.features.getShowMoon() ;
            },
            features.showMoonProperty()
                );
        this.showConstellation = Bindings.createObjectBinding(()-> {
            return this.features.getShowConstellation();
            },
            features.showConstellationPropery()
                );
        /*showMoon.addListener(o -> {
            ObservedSky sky = this.observedSky.getValue();
            StereographicProjection project = this.projection.getValue();
            Transform transform = this.planeToCanvas.getValue();
            painter.drawMoon(sky, transform, project);
        });*/
        setupListeners();
    }
    
    
    private void setupListeners() {
        canvas.setOnMousePressed(me -> {
                if(me.isPrimaryButtonDown()){
                    canvas.requestFocus();
                }
            });
        /*
        canvas.setOnKeyPressed((ke) -> {
                if (! VIEW_DIRECTION_CHANGES.containsKey(ke.getCode()))
                    return;
                int[] dirChange = VIEW_DIRECTION_CHANGES.get(ke.getCode());
                HorizontalCoordinates center = viewingParametersBean.getCenter();
                double newCenterAzDeg = VIEWING_AZ_INTERVAL.reduce(center.azDeg() + dirChange[0]);
                double newCenterAltDeg = VIEWING_ALT_INTERVAL.clip(center.altDeg() + dirChange[1]);
                viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(newCenterAzDeg, newCenterAltDeg));
                ke.consume();
            });
        */
        /*canvas.setOnMouseMoved(me -> {
                mousePosition.setValue(new Point2D(me.getX(), me.getY()));
            });*/
        
        /*canvas.setOnScroll(se -> {
            double dx = se.getDeltaX();
            double dy = se.getDeltaY();
            double d = Math.abs(dx) >= Math.abs(dy) ? dx : dy;
            double newFieldOfView = FIELD_OF_VIEW_INTERVAL.clip(viewingParametersBean.getFieldOfViewDeg() - d);
            viewingParametersBean.setFieldOfViewDeg(newFieldOfView);
        });*/
        drawSky();
        redrawSky(observedSky);
        redrawSky(planeToCanvas);
        redrawSky(showMoon);
        redrawSky(showConstellation);
    }
    
    public void redrawSky(ObservableValue<? extends Object> oV){
        oV.addListener( o -> {
            ObservedSky sky = this.observedSky.getValue();
            StereographicProjection project = this.projection.getValue();
            Transform transform = this.planeToCanvas.getValue();

            //painter.clear(); 
            painter.planeBackground(Color.BLACK, project, transform);
            if (showConstellation.getValue()) painter.drawAsterisms(sky, project, transform);
            painter.drawStars(sky, project, transform);
            painter.drawPlanets(sky, transform, project);
            painter.drawSun(sky, transform, project);
            if (this.showMoon.getValue())  painter.drawMoon(sky, transform, project);
            painter.drawHorizon(project, transform);
        });
    }
    public void drawSky(){
        ObservedSky sky = this.observedSky.getValue();
        StereographicProjection project = this.projection.getValue();
        Transform transform = this.planeToCanvas.getValue();
        //painter.clear();
        painter.planeBackground(Color.BLACK, project, transform);
        painter.drawAsterisms(sky, project, transform);
        painter.drawStars(sky, project, transform);
        painter.drawPlanets(sky, transform, project);
        painter.drawSun(sky, transform, project);
        if (this.showMoon.getValue())   painter.drawMoon(sky, transform, project);
        painter.drawHorizon(project, transform);
        
    }
    public Canvas canvas() {
        return canvas;
    }

    public DateTimeBeanDesign dateTimeBean() {
        return dateTimeBean;
    }

    public ObserverLocationBeanDesign observerLocationBean() {
        return observerLocationBean;
    }

    public ViewingParametersBeanDesign viewingParametersBean() {
        return viewingParametersBean;
    }

    public ObservableDoubleValue mouseAzDegProperty() {
        return mouseAzDeg;
    }

    public ObservableDoubleValue mouseAltDegProperty() {
        return mouseAltDeg;
    }

    public ObservableValue<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }
}
