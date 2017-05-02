package gui;

import javafx.beans.property.BooleanProperty;
import warriors.Dragon;
import warriors.Iceman;
import warriors.Lion;
import warriors.Ninja;
import warriors.Warrior;
import warriors.Wolf;
import world.Clock;
import world.Headquarter;
import world.Team;
import world.WarriorType;
import world.World;

public class SPWorld extends World {
	public volatile boolean task_should_exit = false;
	boolean haveSpawnedThisRound = false;
	BooleanProperty shouldUpdateBlue;
	BooleanProperty SPend;

	public SPWorld(int[][] param,BooleanProperty sub,BooleanProperty se) {
		super(param[0][0], param[0][1], param[0][2]);
		setHP(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
		setAttack(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
		shouldUpdateBlue=sub;
		SPend=se;
	}

	@Override
	protected void spawn() {
		Warrior blueNew = hq[1].spawnWarrior();
		if (blueNew != null) {
			blueNew.location = cities.length + 1;// blue HQ
			hq[1].warriorInHQ.add(blueNew);
			shouldUpdateBlue.set(true);
		}
	}

	int requestSpawn(WarriorType.type t) {
		if (clock.getMinute() != 0 || haveSpawnedThisRound)
			return Def.mNotRightTime;
		Warrior ret = null;
		Headquarter rhq = hq[0];
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
		ret.location = 0;// red HQ
		rhq.warriorInHQ.add(ret);
		haveSpawnedThisRound = true;
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
				haveSpawnedThisRound = false;
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

	Clock getClock() {
		return clock;
	}

	int getPlayerLE() {
		return hq[0].lifeElements.get();
	}

	protected void checkVictory() {
		if (redHQOccupierCount == 2 || blueHQOccupierCount == 2) {
			end.set(true);
		}
		// TODO: update map here
	}
}
