package world;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The clock records the current time in game, provides formatted output in hhh:mm. 
 * The {@link Clock#tick()} method increments the clock by 10 minutes.
 * It keeps the following information:
 * <ol>
 * <li>the hour, an {@code int}</li>
 * <li>the minute, an {@code int}</li>
 * </ol>
 *
 * @author  Kyle Lei
 * @version  1.1.0
 */
public class Clock {
	private int hour=0;
	public IntegerProperty minute=new SimpleIntegerProperty(0);
	public int getMinute() {
		return minute.get();
	}
	public int getTime() {
		return hour*60+minute.get();
	}
	
	@Override
	public String toString() {
		return String.format("%03d", hour) + ":" + String.format("%02d", minute.get());
	}
	
	/**
	 * Increments the clock by 10 minutes.
	 */
	public void tick(){
		minute.set(minute.get()+10);
		if(minute.get()==60){
			++hour;
			minute.set(0);
		}
	}
}
