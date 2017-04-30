package warriors;
import world.Team;
/**
 * This derived class from {@link Warrior} models a Lion. 
 * If a lion dies in a battle, 
 * its HP before the battle will be transferred to its opponent, i.e. the warrior kills it.
 *
 * @author  Kyle Lei
 * @version  1.0.0
 */
public class Lion extends Warrior{
	/**
	 * Creates a lion warrior with the specified ID, HP, and attack.
	 *
	 * @param inID  the ID of the lion.
	 * @param inHP  the HP of the lion.
	 * @param inAttackV  the attack value of the lion.
	 * @param inTeam  the team of the lion.
	 */
	public Lion(int inID,int inHP,int inAttackV,Team inTeam){
		super(inID,inHP,inAttackV, inTeam);
	}
	/**
	 * @see warrior.toString
	 */
	@Override
	public String toString(){
		return team+" lion "+ID;
	}
}
