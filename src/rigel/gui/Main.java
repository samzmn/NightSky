package rigel.gui;

import rigel.coordinates.GeographicCoordinates;
import rigel.coordinates.HorizontalCoordinates;
import rigel.astronomy.AsterismLoader;
import rigel.astronomy.HygDatabaseLoader;
import rigel.astronomy.CelestialObject;
import rigel.astronomy.StarCatalogue;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.converter.*;
/**
 *
 * @author hassanzmn
 */
public class Main extends Application {
    private DateTimeBean dateTimeBean;
    private ObserverLocationBean observerLocationBean;
    private ViewingParametersBean viewingParametersBean;
    private ModifySkyBeans modifySkyBeans;
    private TimeAnimator timeAnimator;
    private SkyCanvasManager skyCanvasManager;
    private final StarCatalogue starCatalog;

    public Main() throws IOException {
        try(InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv");
             InputStream astr = getClass().getResourceAsStream("/asterisms.txt"))
        {
            StarCatalogue.Builder scb = new StarCatalogue.Builder().loadFrom(
                hs,
                HygDatabaseLoader.INSTANCE);
            scb.loadFrom(astr, AsterismLoader.INSTANCE);

            starCatalog = scb.build();
        }    
    }
    
    public static void main(String args[]){
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        dateTimeBean = new DateTimeBean();
        dateTimeBean.setZonedDateTime(ZonedDateTime.now());
        
        observerLocationBean = new ObserverLocationBean();
        observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(35.7219, 51.3347));
        
        viewingParametersBean = new ViewingParametersBean();
        viewingParametersBean.setCenter( HorizontalCoordinates.ofDeg(180.000000000001, 15));
        viewingParametersBean.setFieldOfViewDeg(100);

        modifySkyBeans = new ModifySkyBeans(true, false);
        modifySkyBeans.setMagnify(1);

        skyCanvasManager = new SkyCanvasManager(starCatalog, dateTimeBean, observerLocationBean, viewingParametersBean, modifySkyBeans);

        timeAnimator = new TimeAnimator(skyCanvasManager.dateTimeBean());
        
        Canvas skyCanvas = skyCanvasManager.canvas();
        Pane skyPane = new Pane(skyCanvas);
        skyCanvas.widthProperty().bind(skyPane.widthProperty());
        skyCanvas.heightProperty().bind(skyPane.heightProperty());

        Pane mainPain = new Pane();

        BorderPane root = new BorderPane(skyPane, controlBar(), null, informationBar(), null);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(new Scene(root));
        stage.setTitle("Night Sky Design");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Icon.jpg")));
        stage.show();
        skyCanvas.requestFocus();
    }
    
    private HBox controlBar(){
        Separator sep1 = new Separator(Orientation.VERTICAL);
        Separator sep2 = new Separator(Orientation.VERTICAL);
        Separator sep3 = new Separator(Orientation.VERTICAL);

        HBox controlBar = new HBox(observationPosition(),
                sep1,
                observationTime(),
                sep2,
                flowOFTime(),
                sep3,
                skyInformation());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }
    
    private HBox observationPosition(){
        Label lon = new Label("Longitude:(°)");
        TextField lontxt = new TextField();
        lontxt.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        NumberStringConverter nsc = new NumberStringConverter("#0.00");
        UnaryOperator<TextFormatter.Change> lonFilter = change -> {
            try{
                String newText = change.getControlNewText();
                double newLonDeg = nsc.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLonDeg(newLonDeg)? change : null;
            }catch(Exception e){ return null; }
        };
        TextFormatter<Number> lonTextFormatter = new TextFormatter<>(nsc, 0, lonFilter);
        lontxt.setTextFormatter(lonTextFormatter);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());
        
        Label lat = new Label("Latitude:(°)");
        TextField lattxt = new TextField();
        lattxt.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        UnaryOperator<TextFormatter.Change> latFilter = change -> {
            try{
                String newText = change.getControlNewText();
                double newLonDeg = nsc.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLatDeg(newLonDeg)? change : null;
            }catch(Exception e){ return null; }
        };
        TextFormatter<Number> latTextFormatter = new TextFormatter<>(nsc, 0, latFilter);
        lattxt.setTextFormatter(latTextFormatter);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());
        
        HBox observationPosition = new HBox(lon, lontxt, lat, lattxt);
        observationPosition.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-right");
        return observationPosition;
    }
    
    private HBox observationTime(){
        Label datelbl = new Label("Date:");
        DatePicker date = new DatePicker();
        date.setStyle("-fx-pref-width: 120");
        date.valueProperty().bindBidirectional(dateTimeBean.dateProperty());
        
        Label hourlbl = new Label("Time:");
        TextField hour = new TextField(LocalTime.now().toString());
        hour.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(stringConverter);
        hour.setTextFormatter(timeFormatter);
        timeFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());        
        
        List<ZoneId> zoneIds = ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .map(ZoneId::of)
                .collect(Collectors.toList());
        ComboBox<ZoneId> zone = new ComboBox<>(FXCollections.observableList(zoneIds));
        zone.setStyle("-fx-pref-width: 180;");
        zone.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());
        
        HBox observationTime = new HBox(datelbl, date, hourlbl, hour, zone);
        observationTime.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");
        for (Node child : observationTime.getChildren())
            child.disableProperty().bind(timeAnimator.runningProperty());
        return observationTime;
    }

    private HBox skyInformation() {
        CheckBox starColorCheckBox = new CheckBox("Stars Real Color");
        CheckBox asterismCheckBox = new CheckBox("Asterisms");
        CheckBox vectorsCheckBox = new CheckBox("Vectors");
        NumberStringConverter nsc = new NumberStringConverter("#0");
        TextField magnifyInt = new TextField();
        magnifyInt.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        UnaryOperator<TextFormatter.Change> latFilter = change -> {
            try {
                String newText = change.getControlNewText();
                double newLonDeg = nsc.fromString(newText).intValue();
                return GeographicCoordinates.isValidLatDeg(newLonDeg) ? change : null;
            } catch (Exception e) {
                return null;
            }
        };
        TextFormatter<Number> magnifyTextFormatter = new TextFormatter<>(nsc, 0, latFilter);
        magnifyInt.setTextFormatter(magnifyTextFormatter);

        magnifyTextFormatter.valueProperty().bindBidirectional(this.modifySkyBeans.getMagnifyProperty());
        starColorCheckBox.selectedProperty().bindBidirectional(this.modifySkyBeans.getStarsRealColorProperty());
        asterismCheckBox.selectedProperty().bindBidirectional(this.modifySkyBeans.getAsterismsProperty());
        vectorsCheckBox.selectedProperty().bindBidirectional(this.modifySkyBeans.getVectorsVisableProperty());

        this.skyCanvasManager.redrawSky(this.modifySkyBeans.getAsterismsProperty());
        this.skyCanvasManager.redrawSky(this.modifySkyBeans.getMagnifyProperty());
        this.skyCanvasManager.redrawSky(this.modifySkyBeans.getStarsRealColorProperty());
        this.skyCanvasManager.redrawSky(this.modifySkyBeans.getVectorsVisableProperty());

        HBox skyModifyBox = new HBox(starColorCheckBox, asterismCheckBox, vectorsCheckBox, magnifyInt);
        skyModifyBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-right");
        return skyModifyBox;
    }
    
    private HBox flowOFTime(){
        try(InputStream fontStream = getClass().getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")){
            ChoiceBox<NamedTimeAccelerator> accelerator = new ChoiceBox<>();
            accelerator.setItems(FXCollections.observableList(List.of(NamedTimeAccelerator.values())));
            accelerator.setValue(NamedTimeAccelerator.TIMES_300);
            timeAnimator.acceleratorProperty().bind(Bindings.select(accelerator.valueProperty(), "accelerator"));
            accelerator.disableProperty().bind(timeAnimator.runningProperty());
            
            Font fontAwesome = Font.loadFont(fontStream, 15);
            
            Button resetBtn = new Button("\uf0e2");
            resetBtn.setFont(fontAwesome);
            resetBtn.setOnAction(eh -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));
            resetBtn.disableProperty().bind(timeAnimator.runningProperty());
            
            Button toggleBtn = new Button();
            toggleBtn.setFont(fontAwesome);
            toggleBtn.textProperty().bind(
                    Bindings.when(timeAnimator.runningProperty().not())
                            .then("\uf04b")
                            .otherwise("\uf04c"));
            toggleBtn.setOnAction(eh -> timeAnimator.toggle());
            
            
            HBox flowOfTime = new HBox(accelerator, resetBtn, toggleBtn);
            flowOfTime.setStyle("-fx-spacing: inherit;");
        return flowOfTime;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    private Pane informationBar(){
        Text fieldOfV = new Text();
        fieldOfV.textProperty().bind(Bindings.format("Field of View : %.1f°",
                skyCanvasManager.viewingParametersBean().fieldOfViewDegProperty()));
        
        Text center = new Text();
        center.textProperty().bind(Bindings.createStringBinding(()-> {
            CelestialObject obj = skyCanvasManager.objectUnderMouseProperty().getValue();
                    return obj == null ? "" : obj.info();
        }, skyCanvasManager.objectUnderMouseProperty()));
        
        Text horizontalText = new Text();
        horizontalText.textProperty().bind(Bindings.format("Azimuth: %.2f , Height: %.2f ",
                skyCanvasManager.mouseAzDegProperty(), skyCanvasManager.mouseAltDegProperty()));
        
        BorderPane infoBar = new BorderPane(center, null, horizontalText, null, fieldOfV);
        infoBar.setStyle("-fx-padding: 4; -fx-background-color: white;");
        return infoBar;
    }
    
}
