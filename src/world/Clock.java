package world;
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
 * @version  1.0.0
 */
public class Clock {
	private int hour=0;
	private int minute=0;
	public int getMinute() {
		return minute;
	}
	public int getTime() {
		return hour*60+minute;
	}
	
	@Override
	public String toString() {
		return String.format("%03d", hour) + ":" + String.format("%02d", minute);
	}
	
	/**
	 * Increments the clock by 10 minutes.
	 */
	public void tick(){
		minute+=10;
		if(minute==60){
			++hour;
			minute=0;
		}
	}
}
