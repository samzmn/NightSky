package rigel.gui;

import java.time.*;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;

/**
 *
 * @author hassanzmn
 */
public final class TimeAnimator extends AnimationTimer {
    private final ObjectProperty<TimeAccelerator> accelerator;
    private final BooleanProperty running;
    
    private final DateTimeBean dateTimeBean;
    private long realStart;
    private ZonedDateTime simulatedStart;
    
    public TimeAnimator(DateTimeBean dateTimeBean) {
        this.dateTimeBean = dateTimeBean;
        this.accelerator = new SimpleObjectProperty<>();
        this.running = new SimpleBooleanProperty(false);
    }
    
    @Override
    public void start() {
        realStart = 0;
        simulatedStart = null;
        running.set(true);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        running.set(false);
    }

    public void toggle() {
        if (isRunning()) stop();
        else start();
    }
    
    @Override
    public void handle(long realNow) {
        if (simulatedStart == null) {
            realStart = realNow;
            simulatedStart = dateTimeBean.getZonedDateTime();
        } else {
            long elapsedRealNs = realNow - realStart;
            dateTimeBean.setZonedDateTime(accelerator.get().adjust(simulatedStart, elapsedRealNs));
        }
    }
    
    public ObjectProperty<TimeAccelerator> acceleratorProperty() {
        return accelerator;
    }
     public TimeAccelerator getAccelerator() {
        return accelerator.get();
    }
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.set(accelerator);
    }
    
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }
    public boolean isRunning() {
        return running.get();
    }
    
    
}
