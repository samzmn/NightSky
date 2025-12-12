package rigel.gui;

import java.time.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author hassanzmn
 */
public final class DateTimeBean {
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final ObjectProperty<ZoneId> zone = new SimpleObjectProperty<>();
    
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    public LocalDate getDate() {
        return date.get();
    }
    public void setDate(LocalDate date) {
        this.date.set(date);
    }
    
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }
    public LocalTime getTime() {
        return time.get();
    }
    public void setTime(LocalTime time) {
        this.time.set(time);
    }
    
    public ObjectProperty<ZoneId> zoneProperty() {
        return zone;
    }
    public ZoneId getZone() {
        return zone.get();
    }
    public void setZone(ZoneId zoneID) {
        this.zone.set(zoneID);
    }
    
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    public void setZonedDateTime(ZonedDateTime newZonedDateTime) {
        setDate(newZonedDateTime.toLocalDate());
        setTime(newZonedDateTime.toLocalTime());
        setZone(newZonedDateTime.getZone());
    }
}
