package gui;

import warriors.Dragon;
import warriors.Iceman;
import warriors.Lion;
import warriors.Ninja;
import warriors.Warrior;
import warriors.Wolf;
import world.Headquarter;
import world.Team;
import world.WarriorType;

public class MPWorld extends SPWorld {
	private boolean redHaveSpawnedThisRound = false;
	private boolean blueHaveSpawnedThisRound = false;

	public MPWorld(int[][] param) {
		super(param);
	}

	protected void spawn() {
	}// no automatic spawning

	int requestSpawn(WarriorType.type t,Team team) {
		boolean haveSpawnedThisRound=(team==Team.red?redHaveSpawnedThisRound:blueHaveSpawnedThisRound);
		if (clock.getMinute() != 0 || haveSpawnedThisRound)
			return Def.mNotRightTime;
		Warrior ret = null;
		Headquarter rhq = (team==Team.red?hq[0]:hq[1]);
		int ID = rhq.spawnIndex + 1;
		switch (t) {
		case ICEMAN:
			if (rhq.lifeElements.get() >= type.getHP(WarriorType.type.ICEMAN)) {
				ret = new Iceman(ID, type.getHP(WarriorType.type.ICEMAN), type.geAttack(WarriorType.type.ICEMAN),
						Team.red);
				rhq.lifeElements.set(rhq.lifeElements.get() - type.getHP(WarriorType.type.ICEMAN));
				rhq.spawnIndex++;
			} else
				return Def.mNotEnoughLE;
			break;
		case LION:
			if (rhq.lifeElements.get() >= type.getHP(WarriorType.type.LION)) {
				ret = new Lion(ID, type.getHP(WarriorType.type.LION), type.geAttack(WarriorType.type.LION), Team.red);
				rhq.lifeElements.set(rhq.lifeElements.get() - type.getHP(WarriorType.type.LION));
				rhq.spawnIndex++;
			} else
				return Def.mNotEnoughLE;
			break;
		case WOLF:
			if (rhq.lifeElements.get() >= type.getHP(WarriorType.type.WOLF)) {
				ret = new Wolf(ID, type.getHP(WarriorType.type.WOLF), type.geAttack(WarriorType.type.WOLF), Team.red);
				rhq.lifeElements.set(rhq.lifeElements.get() - type.getHP(WarriorType.type.WOLF));
				rhq.spawnIndex++;
			} else
				return Def.mNotEnoughLE;
			break;
		case NINJA:
			if (rhq.lifeElements.get() >= type.getHP(WarriorType.type.NINJA)) {
				ret = new Ninja(ID, type.getHP(WarriorType.type.NINJA), type.geAttack(WarriorType.type.NINJA),
						Team.red);
				rhq.lifeElements.set(rhq.lifeElements.get() - type.getHP(WarriorType.type.NINJA));
				rhq.spawnIndex++;
			} else
				return Def.mNotEnoughLE;
			break;
		case DRAGON:
			if (rhq.lifeElements.get() >= type.getHP(WarriorType.type.DRAGON)) {
				ret = new Dragon(ID, type.getHP(WarriorType.type.DRAGON), type.geAttack(WarriorType.type.DRAGON),
						Team.red);
				rhq.lifeElements.set(rhq.lifeElements.get() - type.getHP(WarriorType.type.DRAGON));
				rhq.spawnIndex++;
			} else
				return Def.mNotEnoughLE;
			break;
		}
		ret.location =  (team==Team.red?0:6);//  HQ
		rhq.warriorInHQ.add(ret);
		if(team==Team.red)
			redHaveSpawnedThisRound = true;
		else blueHaveSpawnedThisRound=true;
		return Def.mSpawnSuccess;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(Def.delayMs);
		} catch (InterruptedException e) {
		}
		while (clock.getTime() <= T && !task_should_exit && !end.get()) {
			switch (clock.getMinute()) {
			case 0:
				spawn();
				break;
			case 10:
				redHaveSpawnedThisRound = false;
				blueHaveSpawnedThisRound = false;
				move();
				break;
			case 20:
				produceLE();
				break;
			case 30:
				takeLE(true);
				break;
			case 40:
				combat();
				break;
			case 50:
				report();
			}
			clock.tick();
			try {
				Thread.sleep(Def.delayMs);
			} catch (InterruptedException e) {
			}
		}
		end.set(true);
	}
}
