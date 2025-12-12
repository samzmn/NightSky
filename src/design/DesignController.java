package design;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.*;
import javafx.collections.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import javax.imageio.ImageIO;
import rigel.astronomy.*;
import rigel.coordinates.*;

/**
 * FXML Controller class
 *
 * @author hassanzmn
 */
public class DesignController implements Initializable {
    private DateTimeBeanDesign dateTimeBean;
    private ObserverLocationBeanDesign observerLocationBean;
    private ViewingParametersBeanDesign viewingParametersBean;
    private SkyManager canvasManager ;
    private Map<String, String> cityCoordinatesMap;
    private ObservableList<String> cityCoordinateSearchKeyList;
    private StarCatalogue starCatalog;
    private FeaturesBean features;
    
    @FXML
    private GridPane locationGridPane;
    @FXML
    private Canvas skyCanvas;
    @FXML
    private Canvas backgroundCanvas;
    @FXML
    private TextField lonText;
    @FXML
    private TextField latText;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeText;
    @FXML
    private TextArea mainText;
    @FXML
    private ComboBox<String> searchLocationComboBox;
    @FXML
    private Button createBtn;
    @FXML
    private CheckBox moonCheckBox;
    @FXML
    private CheckBox constellationCheckbox;
    @FXML
    private CheckBox setCoordinateCheckBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try(InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv");
             InputStream astr = getClass().getResourceAsStream("/asterisms.txt"))
        {
            StarCatalogue.Builder scb = new StarCatalogue.Builder().loadFrom(
                hs,
                HygDatabaseLoader.INSTANCE);
            scb.loadFrom(astr, AsterismLoader.INSTANCE);

            starCatalog = scb.build();
        }catch(IOException ex){
            System.out.println("Error");
        }
        
        ZonedDateTime when = ZonedDateTime.now();
        dateTimeBean = new DateTimeBeanDesign();
        dateTimeBean.setZonedDateTime(when);

        observerLocationBean = new ObserverLocationBeanDesign();
        observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(51.3347, 35.7219));
        
        viewingParametersBean = new ViewingParametersBeanDesign();
        viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(180,90));
        viewingParametersBean.setFieldOfViewDeg(185);
        
        
        //skyCanvas.setWidth(410);
        //skyCanvas.setHeight(410);
        features = new FeaturesBean();
        features.setShowMoon(true);
        
        //shoots the sky to canvas
        new SkyManager(skyCanvas,
                            starCatalog, 
                            dateTimeBean,
                            observerLocationBean,
                            viewingParametersBean,
                            features);
        //*****************************//
        
        locationCoordinateSet();
        dateTimeZoneSet();
        
        
        
        
        mainText.setTextFormatter(new TextFormatter<>(change -> {
                                        if (change.isAdded()) {
                                            String newText = change.getControlNewText();
                                            int i = 0;
                                            for(char c: newText.toCharArray()){
                                                if ( c =='\n') i++;
                                            }
                                            if (i > 3) change.setText("");
                                        }
                                        return change;
                                    }));
        mainText.textProperty().addListener( o ->{
            background(backgroundCanvas, 5, 10);
        });
        background(backgroundCanvas, 5, 10);
        latText.textProperty().addListener(cl->{
            background(backgroundCanvas, 5, 10);
        });
        lonText.textProperty().addListener(cl->{
            background(backgroundCanvas, 5, 10);
        });
        
        //searchLocationComboBox setup
        setupSearchLocationComboBox();
        
        moonCheckBox.selectedProperty().bindBidirectional(features.showMoonProperty());
        constellationCheckbox.selectedProperty().bindBidirectional(features.showConstellationPropery());
        
        setCoordinateCheckBox.selectedProperty().addListener(cl -> {
                    locationGridPane.disableProperty().set(!setCoordinateCheckBox.isSelected());
                    searchLocationComboBox.disableProperty().set(setCoordinateCheckBox.isSelected());
        });
        
    }
    //************************End of initialize Method************************
    
    @FXML
    private void coordinateChanged(ActionEvent ae){
        searchLocationComboBox.setValue(null);
    }
    
    @FXML
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    private void createBtnAction(ActionEvent event){
        Canvas bgCanvas = new Canvas();
        bgCanvas.setWidth(2400);
        bgCanvas.setHeight(3200);
        background(bgCanvas, 25, 50);
        
        Canvas saveCanvas = new Canvas();
        saveCanvas.setHeight(2000);
        saveCanvas.setWidth(2000);
        new SkyManager(saveCanvas, starCatalog, dateTimeBean, observerLocationBean, viewingParametersBean, features);
        
        WritableImage fxImage = saveCanvas.snapshot(null, null);
        bgCanvas.getGraphicsContext2D().drawImage(fxImage, 200, 200);
        WritableImage backgoundImage = bgCanvas.snapshot(new SnapshotParameters(), null);
        
        BufferedImage swingImage = SwingFXUtils.fromFXImage(backgoundImage, null);
        try {
            ImageIO.write(swingImage, "png", new File("f:\\sky.png"));
        } catch (IOException ex) {
        }
    }
    
    //*******NOT fxml Methods*******
    private void setupSearchLocationComboBox(){
        this.cityCoordinatesMap = observerLocationBean.getCityCoordinatesMap();
        cityCoordinateSearchKeyList = FXCollections.observableArrayList();
        cityCoordinateSearchKeyList.addAll(cityCoordinatesMap.keySet());
        searchLocationComboBox.setItems(cityCoordinateSearchKeyList);
        searchLocationComboBox.setValue("Tehran, Iran");
        
        searchLocationComboBox.getEditor().setOnKeyTyped(ke -> {
            if (ke.getCharacter().isBlank()) {
                searchLocationComboBox.setValue(searchLocationComboBox.getItems().get(0));
                return;
            }
            
            searchLocationComboBox.setValue(null);
            
            Pattern searchPattern = Pattern.compile(searchLocationComboBox.getEditor().getText(), Pattern.CASE_INSENSITIVE);
            ObservableList<String> newItemList = FXCollections.observableArrayList();
            for (String eachItem : cityCoordinateSearchKeyList){
                Matcher match = searchPattern.matcher(eachItem);
                if (match.find()){
                    newItemList.add(eachItem);
                }
            }
            
            searchLocationComboBox.setItems(newItemList);System.out.println("5");
            searchLocationComboBox.show();System.out.println("6");
        });
        searchLocationComboBox.setOnAction(ae -> {
            if (searchLocationComboBox.getValue() == null) {
                return;
            }
        });
        searchLocationComboBox.valueProperty().addListener(cl->{
            background(backgroundCanvas, 5, 10);
        });
        searchLocationComboBox.getSelectionModel().selectedItemProperty().addListener((o)->{
            if(searchLocationComboBox.getSelectionModel().getSelectedItem() != null){
                String str = cityCoordinatesMap.get(searchLocationComboBox.getSelectionModel().getSelectedItem());
                if (str==null) {
                    searchLocationComboBox.setValue(null);
                    return;
                }
                String[] decomposed = str.split(",");
                dateTimeBean.setZone(ZoneId.of(decomposed[0]));
                String[] coordinate = decomposed[1].split(";");
                observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(Double.parseDouble(coordinate[1]), Double.parseDouble(coordinate[0])));
            }
        });
    }
    
    private void background(Canvas canvas, double lineWidth, double offset){
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        
        ctx.setFill(Color.WHITE);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(lineWidth);
        ctx.strokeRect(offset, offset, canvas.getWidth()-(2*offset), canvas.getHeight()-(2*offset));
        
        ctx.setFill(Color.BLACK);
        ctx.setFont(Font.font("arial", lineWidth*3));
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.TOP);
        
        ctx.fillText(mainText.getText(), canvas.getWidth()/2, canvas.getHeight()*0.72);
        
        ctx.setFont(Font.font("arial", lineWidth*2));
        String info = "Night Sky Design\n";
        if(searchLocationComboBox.getValue()!=null) info += searchLocationComboBox.getValue() +"\n";
        info += dateTimeBean.getDate().toString() + "\n";
        info += latText.getText() + "º N , " + lonText.getText() + "º E";
        ctx.fillText(info, canvas.getWidth()/2, canvas.getHeight()*0.85);
    }
    
    private void locationCoordinateSet(){
        NumberStringConverter nsc = new NumberStringConverter("#0.00");
        UnaryOperator<TextFormatter.Change> lonFilter = change -> {
            try{
                String newText = change.getControlNewText();
                double newLonDeg = nsc.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLonDeg(newLonDeg)? change : null;
            }catch(Exception e){ return null; }
        };
        TextFormatter<Number> lonTextFormatter = new TextFormatter<>(nsc, 0, lonFilter);
        lonText.setTextFormatter(lonTextFormatter);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());
        
        UnaryOperator<TextFormatter.Change> latFilter = change -> {
            try{
                String newText = change.getControlNewText();
                double newLonDeg = nsc.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLatDeg(newLonDeg)? change : null;
            }catch(Exception e){ return null; }
        };
        TextFormatter<Number> latTextFormatter = new TextFormatter<>(nsc, 0, latFilter);
        latText.setTextFormatter(latTextFormatter);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());
    }
    
    private void dateTimeZoneSet(){
        datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());
        
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(stringConverter);
        timeText.setTextFormatter(timeFormatter);
        timeFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());        
    }
    
    
}
