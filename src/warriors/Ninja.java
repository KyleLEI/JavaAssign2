package warriors;
import world.Team;
/**
 * This derived class from {@link Warrior} models a ninja. 
 * It will never fight back.
 *
 * @author  Kyle Lei
 * @version  1.0.0
 */
public class Ninja extends Warrior {
	/**
	 * Creates a ninja warrior with the specified ID, HP, and attack.
	 *
	 * @param inID  the ID of the ninja.
	 * @param inHP  the HP of the ninja.
	 * @param inAttackV  the attack value of the ninja.
	 * @param inTeam  the team of the ninja.
	 */
	public Ninja(int inID,int inHP,int inAttackV,Team inTeam){
		super(inID,inHP,inAttackV,inTeam);
	}
	
	/**
	 * @see warrior.toString
	 */
	@Override
	public String toString(){
		return team+" ninja "+ID;
	}
	
	/**
	 * Since a ninja never fights back, its counter inflicts no damage.
	 *
	 * @param enemy  the enemy warrior target
	 */
	@Override
	public void counter(Warrior enemy) throws Death,Cheer{
		if(enemy instanceof Dragon){throw new Cheer((Dragon)enemy);}
	}
}
