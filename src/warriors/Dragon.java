package warriors;
import world.Team;
/**
 * This derived class from {@link Warrior} models a dragon. 
 * It will cheer at the end of the battle, 
 * if it makes an active attack and it is not killed by enemy’s fight-back.
 *
 * @author  Kyle Lei
 * @version  1.0.0
 */
public class Dragon extends Warrior {
	/**
	 * Creates a dragon warrior with the specified ID, HP, and attack.
	 *
	 * @param inID  the ID of the dragon.
	 * @param inHP  the HP of the dragon.
	 * @param inAttackV  the attack value of the dragon.
	 * @param inTeam  the team of the dragon.
	 */
	public Dragon(int inID,int inHP,int inAttackV,Team inTeam){
		super(inID,inHP,inAttackV,inTeam);
	}
	
	/**
	 * @see warrior.toString
	 */
	@Override
	public String toString(){
		return team+" dragon "+ID;
	}
}
