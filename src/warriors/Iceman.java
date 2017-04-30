package warriors;
import world.Team;
/**
 * This derived class from {@link Warrior} models an Iceman. 
 * Exactly after it goes every two steps, 
 * its HP reduces by 9 and its attack value increases by 20. 
 * But it will not die because of moving, that is, 
 * when its HP is less or equal to 0 after decreasing by 9, its HP will reduce to 1.
 *
 * @author  Kyle Lei
 * @version  1.0.0
 */
public class Iceman extends Warrior {
	/**
	 * Creates a iceman warrior with the specified ID, HP, and attack.
	 *
	 * @param inID  the ID of the iceman.
	 * @param inHP  the HP of the iceman.
	 * @param inAttackV  the attack value of the iceman.
	 * @param inTeam  the team of the iceman.
	 */
	public Iceman(int inID,int inHP,int inAttackV,Team inTeam){
		super(inID,inHP,inAttackV,inTeam);
	}
	
	int steps=0;
	/**
	 * Reduce HP by 9 and increase attack by 20 every two steps.
	 */
	@Override
	public void beforeMove(){
		++steps;
		if(steps%2==0&&steps!=0){
			HP-=9;
			HP=Math.max(HP, 1);
			attackV+=20;
		}
	}
	
	/**
	 * @see warrior.toString
	 */
	@Override
	public String toString(){
		return team+" iceman "+ID;
	}
}
