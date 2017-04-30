package world;

import java.util.LinkedList;

import warriors.Dragon;
import warriors.Iceman;
import warriors.Lion;
import warriors.Ninja;
import warriors.Warrior;
import warriors.Wolf;

/**
 * The Headquarter class stores how many life elements are left in the
 * headquarter. It records the following information:
 * <ol>
 * <li>life elements in the HQ, an {@code int}</li>
 * <li>the team of this HQ, an {@code enum} {@link Team}</li>
 * <li>the spawn sequence of the HQ</li>
 * </ol>
 * 
 * @author Kyle Lei
 * @version 1.0.0
 *
 */
public class Headquarter {
	public LinkedList<Warrior> warriorInHQ;
	public int lifeElements = 0;
	private Team team;
	public int spawnIndex = 0;
	public WarriorType type;

	Headquarter(int inLife, Team inTeam) {
		lifeElements = inLife;
		team = inTeam;
		warriorInHQ = new LinkedList<Warrior>();
	}

	/**
	 * Initialize the spawn sequence with initial HP and attack of each type of
	 * warrior.
	 * 
	 * @param inType
	 *            initialized WarriorType enum
	 */
	public void initialize(WarriorType inType) {
		type = inType;
	}

	/**
	 * Outputs number of elements + team headquarter
	 */
	@Override
	public String toString() {
		return lifeElements + " elements in " + team + " headquarter";
	}

	/**
	 * Spawn warrior in specific sequence. Hard code is used here, duh.
	 * 
	 * @return the reference to the spawned warrior
	 */
	public Warrior spawnWarrior() {
		Warrior ret = null;
		int ID = spawnIndex+1;
		int TypeIndex = spawnIndex % 5;
		if (team == Team.red) {
			switch (TypeIndex) {
			case 0:
				if (lifeElements >= type.getHP(WarriorType.type.ICEMAN)) {
					ret = new Iceman(ID, type.getHP(WarriorType.type.ICEMAN), type.geAttack(WarriorType.type.ICEMAN),
							Team.red);
					lifeElements -= type.getHP(WarriorType.type.ICEMAN);
					++spawnIndex;
				}
				break;
			case 1:
				if (lifeElements >= type.getHP(WarriorType.type.LION)) {
					ret = new Lion(ID, type.getHP(WarriorType.type.LION), type.geAttack(WarriorType.type.LION),
							Team.red);
					lifeElements -= type.getHP(WarriorType.type.LION);
					++spawnIndex;
				}
				break;
			case 2:
				if (lifeElements >= type.getHP(WarriorType.type.WOLF)) {
					ret = new Wolf(ID, type.getHP(WarriorType.type.WOLF), type.geAttack(WarriorType.type.WOLF),
							Team.red);
					lifeElements -= type.getHP(WarriorType.type.WOLF);
					++spawnIndex;
				}
				break;
			case 3:
				if (lifeElements >= type.getHP(WarriorType.type.NINJA)) {
					ret = new Ninja(ID, type.getHP(WarriorType.type.NINJA), type.geAttack(WarriorType.type.NINJA),
							Team.red);
					lifeElements -= type.getHP(WarriorType.type.NINJA);
					++spawnIndex;
				}
				break;
			case 4:
				if (lifeElements >= type.getHP(WarriorType.type.DRAGON)) {
					ret = new Dragon(ID, type.getHP(WarriorType.type.DRAGON), type.geAttack(WarriorType.type.DRAGON),
							Team.red);
					lifeElements -= type.getHP(WarriorType.type.DRAGON);
					++spawnIndex;
				}
				break;
			}
		} else {
			switch (TypeIndex) {
			case 0:
				if (lifeElements >= type.getHP(WarriorType.type.LION)) {
					ret = new Lion(ID, type.getHP(WarriorType.type.LION), type.geAttack(WarriorType.type.LION),
							Team.blue);
					lifeElements -= type.getHP(WarriorType.type.LION);
					++spawnIndex;
				}
				break;
			case 1:
				if (lifeElements >= type.getHP(WarriorType.type.DRAGON)) {
					ret = new Dragon(ID, type.getHP(WarriorType.type.DRAGON), type.geAttack(WarriorType.type.DRAGON),
							Team.blue);
					lifeElements -= type.getHP(WarriorType.type.DRAGON);
					++spawnIndex;
				}
				break;
			case 2:
				if (lifeElements >= type.getHP(WarriorType.type.NINJA)) {
					ret = new Ninja(ID, type.getHP(WarriorType.type.NINJA), type.geAttack(WarriorType.type.NINJA),
							Team.blue);
					lifeElements -= type.getHP(WarriorType.type.NINJA);
					++spawnIndex;
				}
				break;
			case 3:
				if (lifeElements >= type.getHP(WarriorType.type.ICEMAN)) {
					ret = new Iceman(ID, type.getHP(WarriorType.type.ICEMAN), type.geAttack(WarriorType.type.ICEMAN),
							Team.blue);
					lifeElements -= type.getHP(WarriorType.type.ICEMAN);
					++spawnIndex;
				}
				break;
			case 4:
				if (lifeElements >= type.getHP(WarriorType.type.WOLF)) {
					ret = new Wolf(ID, type.getHP(WarriorType.type.WOLF), type.geAttack(WarriorType.type.WOLF),
							Team.blue);
					lifeElements -= type.getHP(WarriorType.type.WOLF);
					++spawnIndex;
				}
				break;
			}
		}
		return ret;
	}
	
	public void addLE(int inLE){
		lifeElements+=inLE;
	}
	
	public int rewardLE(){
		if(lifeElements>=8){
			lifeElements-=8;
			return 8;
		}
		else return 0;
	}
}
