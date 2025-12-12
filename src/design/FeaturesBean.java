package design;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
/**
 *
 * @author hassanzmn
 */
public final class FeaturesBean {
    private final ObjectProperty<Color> skyBgColor = new SimpleObjectProperty();
    private final ObjectProperty<Color> bgColor = new SimpleObjectProperty();
    private final BooleanProperty showMoon = new SimpleBooleanProperty();
    private final BooleanProperty showConstellation = new SimpleBooleanProperty();
    private final ObjectProperty<Image> bgImage = new SimpleObjectProperty<>();
    private final BooleanProperty showTime = new SimpleBooleanProperty();
    private final ListProperty<Integer> boardSize = new SimpleListProperty<>();

    public FeaturesBean() {
        setSkyBgColor(Color.BLACK);
        setBgColor(Color.BLACK);
        setShowMoon(true);
        setShowConstellation(true);
        setBgImage(null);
        setShowTime(false);
    }
    
    public ListProperty<Integer> boardSizeProperty(){
        return this.boardSize;
    }
    public int[] getBoardSize(){
        int[] size = new int[2];
        size[0] = this.boardSize.get(0);
        size[1] = this.boardSize.get(1);
        return size;
    }
    public void setBoardSize(int x, int y){
        this.boardSize.add(x);
        this.boardSize.add(y);
    }
    
    public BooleanProperty showTimeProperty(){
        return this.showTime;
    }
    public boolean getShowTime(){
        return this.showTime.getValue();
    }
    public void setShowTime(boolean show){
        this.showTime.set(show);
    }
    
    public ObjectProperty<Image> bgImageProperty(){
        return this.bgImage;
    }
    public Image getBgImage(){
        return this.bgImage.get();
    }
    public void setBgImage(Image image){
        this.bgImage.set(image);
    }
    
    public BooleanProperty showConstellationPropery(){
        return this.showConstellation;
    }
    public boolean getShowConstellation(){
        return this.showConstellation.get();
    }
    public void setShowConstellation(boolean show){
        this.showConstellation.set(show);
    }
    
    public BooleanProperty showMoonProperty(){
        return this.showMoon;
    }
    public boolean getShowMoon(){
        return this.showMoon.getValue();
    }
    public void setShowMoon(boolean show){
        this.showMoon.set(show);
    }
    
    public ObjectProperty<Color> bgColorProperty() {
        return this.bgColor;
    }
    public Color getBgColor(){
        return this.bgColor.getValue();
    }
    public void setBgColor(Color bgColor) {
        this.bgColor.setValue(bgColor);
    }
    
    public ObjectProperty<Color> skyBgColorProperty(){
        return this.skyBgColor;
    }
    public Color getSkyBgColor(){
        return this.skyBgColor.getValue();
    }
    public void setSkyBgColor(Color color){
        this.skyBgColor.setValue(color);
    }
    
}
