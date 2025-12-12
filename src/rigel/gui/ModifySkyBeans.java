package rigel.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author hassanzmn
 */
public class ModifySkyBeans {

    private final BooleanProperty starsRealColor = new SimpleBooleanProperty();
    private final BooleanProperty asterisms = new SimpleBooleanProperty();
    private final IntegerProperty magnify = new SimpleIntegerProperty();
    private final BooleanProperty vectorsVisable = new SimpleBooleanProperty();

    public ModifySkyBeans() {
    }

    public ModifySkyBeans(boolean starsRealColor, boolean asterisms) {
        this.starsRealColor.setValue(starsRealColor);
        this.asterisms.setValue(asterisms);
        this.magnify.setValue(1);
        this.vectorsVisable.setValue(true);
    }

    public IntegerProperty getMagnifyProperty() {
        return this.magnify;
    }
    public BooleanProperty getStarsRealColorProperty() {
        return this.starsRealColor;
    }

    public BooleanProperty getAsterismsProperty() {
        return this.asterisms;
    }

    public BooleanProperty getVectorsVisableProperty() {
        return this.vectorsVisable;
    }

    public boolean isVectorsVisable() {
        return vectorsVisable.getValue();
    }

    public void setVectorsVisable(boolean vectorsVisable) {
        this.vectorsVisable.setValue(vectorsVisable);
    }

    public int getMagnify() {
        return magnify.get();
    }

    public void setMagnify(int magnify) {
        this.magnify.setValue(magnify);
    }

    public boolean isStarsRealColor() {
        return starsRealColor.get();
    }

    public void setStarsRealColor(boolean starsRealColor) {
        this.starsRealColor.set(starsRealColor);
    }

    public boolean isAsterisms() {
        return asterisms.get();
    }

    public void setAsterisms(boolean asterisms) {
        this.asterisms.set(asterisms);
    }

}
