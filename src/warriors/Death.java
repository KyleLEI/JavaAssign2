package warriors;
/**
 * This class models a death. It records the killer and the victim. 
 * It should be thrown if a warrior is killed. It keeps the following information:
 * <ol>
 * <li>reference to the killer, a {@link Warrior} object</li>
 * <li>reference to the victim, a {@link Warrior} object</li>
 * </ol>
 *
 * @author  Kyle Lei
 * @version  1.0.0
 */
@SuppressWarnings("serial")
public class Death extends Throwable {
	private Warrior killer;
	private Warrior victim;
	
	Death(Warrior inKiller,Warrior inVictim){
		super();
		killer=inKiller;
		victim=inVictim;
	}

	public Warrior getKiller() {
		return killer;
	}

	public Warrior getVictim() {
		return victim;
	}

	@Override
	public String toString() {
		return victim.toString()+" was killed";
	}
	
}
