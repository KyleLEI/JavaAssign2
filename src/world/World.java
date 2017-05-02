package world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import warriors.Cheer;
import warriors.Death;
import warriors.Dragon;
import warriors.Ninja;
import warriors.Warrior;

/**
 * The world class handles the core game logic, including moving warriors and
 * combat. It keeps the following information:
 * <ol>
 * <li>the world clock, a {@link Clock} object</li>
 * <li>HP and attack of warrior types in the order of dragon, ninja, iceman,
 * lion and wolf, two {@code array} of 5 {@code int}</li>
 * <li>container for cities, an {@code array}</li>
 * <li>headquarters of the two team, an {@code array} of
 * {@link Headquarter}</li>
 * </ol>
 *
 * @author Kyle Lei
 * @version 1.1.0
 */

public class World implements Runnable{
	public Clock clock;
	protected int T;
	protected int[] HP;
	protected int[] attack;
	protected City[] cities;
	public Headquarter[] hq;
	protected WarriorType type;
	public int redHQOccupierCount = 0;
	public int blueHQOccupierCount = 0;
	public BooleanProperty end = new SimpleBooleanProperty(false);
	protected LinkedList<MoveMessage> moves;
	protected LinkedList<Warrior> redAwardee;
	protected LinkedList<Warrior> blueAwardee;

	public World(int inLifeElement, int numberOfCities, int endTime) {
		clock = new Clock();
		cities = new City[numberOfCities];
		for (int i = 0; i < numberOfCities; ++i)
			cities[i] = new City();
		hq = new Headquarter[2];
		hq[0] = new Headquarter(inLifeElement, Team.red);
		hq[1] = new Headquarter(inLifeElement, Team.blue);
		T = endTime;
		moves = new LinkedList<MoveMessage>();
		redAwardee = new LinkedList<Warrior>();
		blueAwardee = new LinkedList<Warrior>();
	}

	public void setHP(int dragon, int ninja, int iceman, int lion, int wolf) {
		HP = new int[] { dragon, ninja, iceman, lion, wolf };
	}

	public void setAttack(int dragon, int ninja, int iceman, int lion, int wolf) {
		attack = new int[] { dragon, ninja, iceman, lion, wolf };
		type = new WarriorType();
		type.initialize(HP, attack);
		hq[0].initialize(type);
		hq[1].initialize(type);
	}

	/**
	 * Spawn and announce warrior according to specified sequence from two HQs.
	 */
	protected void spawn() {
		Warrior redNew = hq[0].spawnWarrior();
		if (redNew != null) {
			redNew.location = 0;// red HQ
			hq[0].warriorInHQ.add(redNew);
			System.out.println(clock + " " + redNew + " born");
		}
		Warrior blueNew = hq[1].spawnWarrior();
		if (blueNew != null) {
			blueNew.location = cities.length + 1;// blue HQ
			hq[1].warriorInHQ.add(blueNew);
			System.out.println(clock + " " + blueNew + " born");
		}
	}

	/**
	 * The method follows the routine of
	 * <ol>
	 * <li>Iterate over HQ and cities from west to east</li>
	 * <li>Iterate over warriors within the location</li>
	 * <li>check if the warrior has been moved in this round</li>
	 * <li>if not, remove it from the current location and move to the next</li>
	 * <li>register that this warrior has been moved this round</li>
	 * </ol>
	 * finally, it checks whether victory has been achieved and announce the
	 * moves in an orderly manner.
	 */
	protected void move() {
		ArrayList<Warrior> moved = new ArrayList<Warrior>();
		Iterator<Warrior> it;
		Warrior w;

		it = hq[0].warriorInHQ.iterator();// handle moves from red HQ
		while (it.hasNext()) {
			w = it.next();
			if (moved.contains(w))
				continue;
			w.beforeMove();
			++w.location;
			it.remove();
			cities[0].warriorInCity.addFirst(w);
			moves.add(new MoveMessage(w));
			moved.add(w);
		}

		if (cities.length != 1) {
			it = cities[0].warriorInCity.iterator();// handle city 1;
			while (it.hasNext()) {
				w = it.next();
				if (moved.contains(w))
					continue;
				if (w.getTeam() == Team.red) {
					w.beforeMove();
					++w.location;
					it.remove();
					cities[1].warriorInCity.addFirst(w);
					moves.add(new MoveMessage(w));
					moved.add(w);
				} else {
					w.beforeMove();
					redHQOccupierCount++;
					it.remove();
					--w.location;
					moves.add(new MoveMessage(w));
					moved.add(w);
				}
			}

			for (int i = 1; i < cities.length - 1; ++i) {// handle regular city
				// moves
				it = cities[i].warriorInCity.iterator();
				while (it.hasNext()) {
					w = it.next();
					if (moved.contains(w))
						continue;
					if (w.getTeam() == Team.red) {
						w.beforeMove();
						++w.location;
						moves.add(new MoveMessage(w));
						it.remove();
						cities[i + 1].warriorInCity.addFirst(w);
						moved.add(w);
					} else {
						w.beforeMove();
						--w.location;
						moves.add(new MoveMessage(w));
						it.remove();
						cities[i - 1].warriorInCity.addLast(w);
						moved.add(w);
					}
				}
			}

			it = cities[cities.length - 1].warriorInCity.iterator();// handle
																	// the last
																	// city
			while (it.hasNext()) {
				w = it.next();
				if (moved.contains(w))
					continue;
				if (w.getTeam() == Team.blue) {
					w.beforeMove();
					--w.location;
					moves.add(new MoveMessage(w));
					it.remove();
					cities[cities.length - 2].warriorInCity.addLast(w);
					moved.add(w);
				} else {
					w.beforeMove();
					blueHQOccupierCount++;
					++w.location;
					moves.add(new MoveMessage(w));
					it.remove();
					moved.add(w);
				}
			}
		} else {// if only one city
			it = cities[0].warriorInCity.iterator();
			while (it.hasNext()) {
				w = it.next();
				if (moved.contains(w))
					continue;
				if (w.getTeam() == Team.red) {
					w.beforeMove();
					blueHQOccupierCount++;
					++w.location;
					moves.add(new MoveMessage(w));
					it.remove();
					moved.add(w);
				} else {
					w.beforeMove();
					redHQOccupierCount++;
					--w.location;
					moves.add(new MoveMessage(w));
					it.remove();
					moved.add(w);
				}
			}

		}
		it = hq[1].warriorInHQ.iterator();// handle moves from blue HQ
		while (it.hasNext()) {
			w = it.next();
			w.beforeMove();
			--w.location;
			moves.add(new MoveMessage(w));
			it.remove();
			cities[cities.length - 1].warriorInCity.addLast(w);
		}
		checkVictory();

	}

	class MoveMessage implements Comparable<MoveMessage> {
		public Warrior w;

		MoveMessage(Warrior w) {
			this.w = w;
		}

		@Override
		public int compareTo(MoveMessage other) {
			if (this.w.location != other.w.location)
				return this.w.location - other.w.location;
			else if (w.getTeam() == Team.red)
				return -1;
			else
				return 1;
		}

		@Override
		public String toString() {
			if (w.location != 0 && w.location != cities.length + 1) {
				return clock + " " + w + " marched to city " + w.location + " " + w.getDetails();
			} else {
				if (w.getTeam() == Team.red)
					return (clock + " " + w + " reached blue headquarter " + w.getDetails());
				else
					return (clock + " " + w + " reached red headquarter " + w.getDetails());
			}
		}
	}

	protected void announceHQTaken(Team team) {
		System.out.println(clock + " " + team + " headquarter was taken");
	}

	protected void checkVictory() {
//		moves.sort(new Comparator<MoveMessage>() {// anonymous inner class
//			@Override
//			public int compare(MoveMessage o1, MoveMessage o2) {
//				return o1.compareTo(o2);
//			}
//		});
		moves.sort((o1,o2)->{
			return o1.compareTo(o2);//lambda expression
		});
		moves.forEach(System.out::println);
		moves.clear();

		if (redHQOccupierCount == 2) {
			end.set(true);
			announceHQTaken(Team.red);
		}
		if (blueHQOccupierCount == 2) {
			end.set(true);
			announceHQTaken(Team.blue);
		}
	}

	protected void produceLE() {
		for (int i = 0; i < cities.length; ++i) {
			cities[i].produceLifeElements();
		}
	}

	protected void takeLE(boolean shouldAnnounce) {
		for (int i = 0; i < cities.length; ++i) {
			if (cities[i].warriorInCity.size() == 1) {
				int LE = cities[i].takeLifeElements();
				if (cities[i].warriorInCity.get(0).getTeam() == Team.red) {
					hq[0].addLE(LE);
					if (shouldAnnounce)
						announceLE(cities[i].warriorInCity.get(0), LE);
				} else {
					hq[1].addLE(LE);
					if (shouldAnnounce)
						announceLE(cities[i].warriorInCity.get(0), LE);
				}
			}
		}
	}

	protected void announceLE(Warrior w, int LE) {
		if (LE != 0)
			System.out.println(clock + " " + w + " earned " + LE + " elements for his headquarter");
	}

	protected void combat() {
		redAwardee.clear();
		blueAwardee.clear();
		for (int i = 0; i < cities.length; ++i) {
			if (cities[i].warriorInCity.size() == 2) {// if there are two
														// warriors in a city
				if (cities[i].flag == Team.none) {
					if (i % 2 == 0)// "city index" odd, red attacks
						attack(cities[i].warriorInCity.getFirst(), cities[i].warriorInCity.getLast(), i);
					else
						attack(cities[i].warriorInCity.getLast(), cities[i].warriorInCity.getFirst(), i);
				}

				else if (cities[i].flag == Team.red) {
					attack(cities[i].warriorInCity.getFirst(), cities[i].warriorInCity.getLast(), i);
				}

				else {
					attack(cities[i].warriorInCity.getLast(), cities[i].warriorInCity.getFirst(), i);
				}
			}
		}
		rewardWarrior();
		takeLE(false);// take LE without announcing
	}

	/**
	 * A single attack.
	 * 
	 * @param wa
	 *            the attacker
	 * @param wb
	 *            the victim
	 * @param cityIndex
	 *            city index
	 */
	protected void attack(Warrior wa, Warrior wb, int cityIndex) {
		try {
			System.out.println(clock + " " + wa + " attacked " + wb + " in city " + (cityIndex + 1) + " with "
					+ wa.getHP() + " elements and force " + wa.getAttackV());
			wa.attack(wb);
			try {
				if (!(wb instanceof Ninja))
					System.out.println(clock + " " + wb + " fought back against " + wa + " in city " + (cityIndex + 1));
				wb.counter(wa);
			} catch (Cheer c) {// cheer after not being killed by enemy counter
				System.out.println(clock + " " + c + " in city " + (cityIndex + 1));
			}
		} catch (Death d) {
			System.out.println(clock + " " + d + " in city " + (cityIndex + 1));// announce
																				// death
			if (d.getKiller() instanceof Dragon) // cheer after not even
													// suffering counter or
													// killed enemy
				System.out.println(clock + " " + new Cheer((Dragon) d.getKiller()) + " in city " + (cityIndex + 1));
			if (d.getKiller().getTeam() == Team.red)
				redAwardee.addFirst(d.getKiller());
			else
				blueAwardee.add(d.getKiller());
			cities[cityIndex].warriorInCity.remove(d.getVictim());
			announceLE(d.getKiller(), cities[cityIndex].getLE());// fake
																	// announcement,
																	// not
																	// really
																	// taken
			changeFlag(cityIndex, d.getKiller().getTeam());
			return;
		}
		changeFlag(cityIndex, Team.none);
	}

	protected void rewardWarrior() {
		Iterator<Warrior> it;
		Warrior w;

		// reward red warriors
		it = redAwardee.iterator();
		while (it.hasNext()) {
			w = it.next();
			w.addHP(hq[0].rewardLE());
		}
		redAwardee.clear();

		// reward blue warriors
		it = blueAwardee.iterator();
		while (it.hasNext()) {
			w = it.next();
			w.addHP(hq[1].rewardLE());
		}
		blueAwardee.clear();
	}

	protected void changeFlag(int cityI, Team flag) {
		Team newFlag = Team.none;
		if (cities[cityI].lastKillerTeam == flag) {
			newFlag = flag;// consecutively twice
		} else
			cities[cityI].lastKillerTeam = flag;// record the first kill

		if (cities[cityI].flag != newFlag && newFlag != Team.none) {
			cities[cityI].flag = newFlag;
			announceFlag(cityI, newFlag);
		}

	}

	protected void announceFlag(int cityI, Team flag) {
		System.out.println(clock + " " + flag + " flag raised in city " + (cityI + 1));
	}

	protected void report() {
		System.out.println(clock + " " + hq[0]);
		System.out.println(clock + " " + hq[1]);
	}

	/**
	 * The main function of the game logic.
	 */
	public void run() {
		while (clock.getTime() <= T) {
			switch (clock.getMinute()) {
			case 0:
				spawn();
				break;
			case 10:
				move();
				if (end.get())
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
		}
		return;// end game (time out)
	}

}
