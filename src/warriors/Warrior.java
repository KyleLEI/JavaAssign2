package warriors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import world.Team;

/**
 * This parent class models a warrior. Instantiating directly from this class is
 * discouraged. One should instantiate the derived specific warrior type in this
 * package. The following information is maintained:
 * <ol>
 * <li>the ID of the warrior, an <code>int</code></li>
 * <li>the HP of the warrior, a <code>int</code></li>
 * <li>the attack, an <code>int</code></li>
 * <li>the team, an <code>enum</code></li>
 * </ol>
 *
 * @author Kyle Lei
 * @version 1.2.0
 */
public class Warrior implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9012106594264718805L;
	protected int ID;
	protected int HP;
	protected int attackV;
	protected Team team;
	public int location = 0;
	protected int enemiesKilled = 0;
	volatile protected int steps = 0;

	public int getID() {
		return ID;
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public int getSteps() {
		return steps;
	}

	/**
	 * Returns whether the warrior's HP is less or equal to 0
	 *
	 * @return boolean true equals dead.
	 */
	public boolean isDead() {
		return HP <= 0;
	}

	public int getHP() {
		return HP;
	}

	public int getAttackV() {
		return attackV;
	}

	/**
	 * Receives life element reward from the HQ
	 * 
	 * @param lifeElement
	 *            the amount of rewarded life element
	 */
	public void addHP(int lifeElement) {
		this.HP += lifeElement;
	}

	public Team getTeam() {
		return team;
	}

	/**
	 * Returns the team, name and id of this warrior. To be overridden by all
	 * derived classes
	 *
	 * @return "team+name+id"
	 */
	@Override
	public String toString() {
		return null;
	}

	/**
	 * Returns the warrior's HP and attackV in a formatted String
	 *
	 * @return "with "+HP+" elements and force "+attackV;
	 */
	public String getDetails() {
		return "with " + HP + " elements and force " + attackV;
	}

	/**
	 * Creates a warrior object with the specified ID, HP, and attack.
	 *
	 * @param inID
	 *            the ID of the warrior.
	 * @param inHP
	 *            the HP of the warrior.
	 * @param inAttackV
	 *            the attack value of the warrior.
	 */
	public Warrior(int inID, int inHP, int inAttackV, Team inTeam) {
		this.ID = inID;
		this.HP = inHP;
		this.attackV = inAttackV;
		this.team = inTeam;
	}

	/**
	 * Handles changes accompanied by every move. Should be overridden by
	 * {@link Iceman} to calculate HP reduction
	 *
	 */
	public void beforeMove() {
		steps = steps + 1;
	};

	/**
	 * Active attack, The HP of the target reduces by the attacker’s attack
	 * value. If the target’s HP is dead, a {@link Death} is thrown. Should be
	 * overridden by {@link Wolf} for HP and attack doubling calculation.
	 *
	 * @param enemy
	 *            the enemy warrior target
	 * @throws Death
	 *             if the enemy is killed
	 */
	public void attack(Warrior enemy) throws Death {
		int HPBefore = enemy.HP;
		enemy.HP -= this.attackV;
		if (enemy.isDead()) {
			++enemiesKilled;
			if (enemy instanceof Lion) {
				this.HP += HPBefore;// transfer the HP to the warrior that
									// killed it
			}
			throw new Death(this, enemy);
		}
	}

	/**
	 * 
	 * if the target is not killed, it will fight back instinctively. The attack
	 * value of the fight-back is the half of the attack value of the warriors
	 * conducting it. Should be overridden by {@link Ninja}.</li>
	 *
	 * @param enemy
	 *            the enemy warrior target
	 * @throws Death
	 *             if the enemy is killed
	 * @throws Cheer
	 *             if a {@link Dragon} is taking the attack and survived, then
	 *             cheers
	 */
	public void counter(Warrior enemy) throws Death, Cheer {
		int HPBefore = enemy.HP;
		enemy.HP -= this.attackV / 2;
		// must throw Death first, or death will never be caught because of the
		// Cheer thrown
		if (enemy.isDead()) {
			if (enemy instanceof Lion) {
				this.HP += HPBefore;// transfer the HP to the warrior that
									// killed it
			}
			throw new Death(this, enemy);
		} else if (enemy instanceof Dragon) {
			throw new Cheer((Dragon) enemy);
		}
	}

	public void setHP(int hP) {
		HP = hP;
	}

	public void setAttackV(int attackV) {
		this.attackV = attackV;
	}

	public void setEnemiesKilled(int enemiesKilled) {
		this.enemiesKilled = enemiesKilled;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	
//	private void writeObject(ObjectOutputStream oos) throws IOException {
//		// default serialization
////		oos.defaultWriteObject();
//		System.out.println("Warrior sending");
//		oos.writeInt(HP);
//		oos.writeInt(attackV);
//		oos.writeInt(enemiesKilled);
//		oos.writeInt(steps);
//		System.out.println("Warrior sent");
//	}
//
//	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
////		ois.defaultReadObject();
//		System.out.println("Warrior receiving");
//		HP=ois.readInt();
//		attackV=ois.readInt();
//		enemiesKilled=ois.readInt();
//		steps=ois.readInt();
//		System.out.println("Warrior received steps: "+steps);
//	}
}
