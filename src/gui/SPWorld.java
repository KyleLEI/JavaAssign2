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
import world.World;

public class SPWorld extends World {
	public volatile boolean task_should_exit=false;
	public SPWorld(int[][] param) {
		super(param[0][0], param[0][1], param[0][2]);
		setHP(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
		setAttack(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
	}

	@Override
	protected void spawn() {
		Warrior blueNew = hq[1].spawnWarrior();
		if (blueNew != null) {
			blueNew.location = cities.length + 1;// blue HQ
			hq[1].warriorInHQ.add(blueNew);
//			System.out.println(clock + " " + blueNew + " born");
		}
	}

	int requestSpawn(WarriorType.type t) {
		if(clock.getMinute()!=0) return Def.mNotRightTime;
		Warrior ret=null;
		Headquarter rhq = hq[0];
		int ID = rhq.spawnIndex+1;
		switch (t) {
		case ICEMAN:
			if (rhq.lifeElements >= type.getHP(WarriorType.type.ICEMAN)) {
				ret = new Iceman(ID, type.getHP(WarriorType.type.ICEMAN), type.geAttack(WarriorType.type.ICEMAN),
						Team.red);
				rhq.lifeElements -= type.getHP(WarriorType.type.ICEMAN);
				rhq.spawnIndex++;
			}
			else return Def.mNotEnoughLE;
			break;
		case LION:
			if (rhq.lifeElements >= type.getHP(WarriorType.type.LION)) {
				ret = new Lion(ID, type.getHP(WarriorType.type.LION), type.geAttack(WarriorType.type.LION),
						Team.red);
				rhq.lifeElements -= type.getHP(WarriorType.type.LION);
				rhq.spawnIndex++;
			}
			else return Def.mNotEnoughLE;
			break;
		case WOLF:
			if (rhq.lifeElements >= type.getHP(WarriorType.type.WOLF)) {
				ret = new Wolf(ID, type.getHP(WarriorType.type.WOLF), type.geAttack(WarriorType.type.WOLF),
						Team.red);
				rhq.lifeElements -= type.getHP(WarriorType.type.WOLF);
				rhq.spawnIndex++;
			}
			else return Def.mNotEnoughLE;
			break;
		case NINJA:
			if (rhq.lifeElements >= type.getHP(WarriorType.type.NINJA)) {
				ret = new Ninja(ID, type.getHP(WarriorType.type.NINJA), type.geAttack(WarriorType.type.NINJA),
						Team.red);
				rhq.lifeElements -= type.getHP(WarriorType.type.NINJA);
				rhq.spawnIndex++;
			}
			else return Def.mNotEnoughLE;
			break;
		case DRAGON:
			if (rhq.lifeElements >= type.getHP(WarriorType.type.DRAGON)) {
				ret = new Dragon(ID, type.getHP(WarriorType.type.DRAGON), type.geAttack(WarriorType.type.DRAGON),
						Team.red);
				rhq.lifeElements -= type.getHP(WarriorType.type.DRAGON);
				rhq.spawnIndex++;
			}
			else return Def.mNotEnoughLE;
			break;
		}
		ret.location = 0;// red HQ
		rhq.warriorInHQ.add(ret);
		return Def.mSpawnSuccess;
		
	}

	@Override
	public void run() {
		while (clock.getTime() <= T&&!task_should_exit) {
			switch (clock.getMinute()) {
			case 0:
				spawn();
				break;
			case 10:
				move();
				if (end)
					return;// end game (victory)
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
				e.printStackTrace();
			}
		}
		return;// end game (time out / stopped)
	}

}
