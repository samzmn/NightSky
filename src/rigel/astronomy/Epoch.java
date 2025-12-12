package rigel.astronomy;

import java.time.*;
import static java.time.temporal.ChronoUnit.MILLIS;

/**
 *
 * @author hassanzmn
 */
public enum Epoch {
    J2000(LocalDateTime.of(2000, Month.JANUARY, 1, 12, 0, 0, 0)),
    J2010(LocalDateTime.of(2010, Month.JANUARY, 1,0,0,0,0).minusDays(1)) ;
    
    LocalDateTime epoch;
    
    private Epoch(LocalDateTime date){
        this.epoch = date;
    }
    
    public LocalDateTime getDate(){
        return epoch;
    }
    
    public double daysUntil(ZonedDateTime when) {
        //double e1 = epoch.until(when, MILLIS);
        double e = epoch.atOffset(ZoneOffset.UTC).until(when, MILLIS);
        /*
        double year = when.getYear() - epoch.getYear();
        double day = when.getDayOfYear() - epoch.getDayOfYear();
        double hour = when.getHour() - epoch.getHour();
        double min = when.getMinute() - epoch.getMinute();
        double sec = when.getSecond() - epoch.getSecond();
        double nsec = when.getNano() - epoch.getNano();
        return year*365 + day + hour/24 + min/(24*60) + sec/(24*60*60) + nsec/(24*60*60*1000000000);
        */
        return e/(24*60*60*1000);
    }
    
    public double julianCenturiesUntil(ZonedDateTime when) {        
        return daysUntil(when)/36525;
    }
}
