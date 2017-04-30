package world;
/**
 * This {@code enum} contains the HP and attack of all warrior types. 
 * It acts as a helper when spawning warriors.
 * @author kyle
 * @version 1.0.0
 */
public class WarriorType {
	public enum type{DRAGON,NINJA,ICEMAN,LION,WOLF}
	private int[] HP;
	private int[] attack;
	
	/**
	 * Initialize the types in order of dragon, ninja, iceman, lion and wolf
	 * @param inHP initial HP
	 * @param inAttack initial attack
	 */
	public void initialize(int[] inHP,int[] inAttack){
		HP=new int[5];
		attack=new int[5];
		for(int i=0;i<5;++i){
			HP[i]=inHP[i];
			attack[i]=inAttack[i];
		}
	}
	public int getHP(WarriorType.type type){
		switch(type){
		case DRAGON:
			return HP[0];
		case NINJA:
			return HP[1];
		case ICEMAN:
			return HP[2];
		case LION:
			return HP[3];
		case WOLF:
			return HP[4];
		}
		return 0;
	}
	
	public int geAttack(WarriorType.type type){
		switch(type){
		case DRAGON:
			return attack[0];
		case NINJA:
			return attack[1];
		case ICEMAN:
			return attack[2];
		case LION:
			return attack[3];
		case WOLF:
			return attack[4];
		}
		return 0;
	}
}
