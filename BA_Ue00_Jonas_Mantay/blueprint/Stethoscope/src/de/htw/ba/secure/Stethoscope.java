package de.htw.ba.secure;

import de.htw.ba.secure.Opener;
import de.htw.ba.secure.Safe;
import de.htw.ba.secure.SecureSafe;

/**
 * Dieses Stethopscope ist leider dumm.
 * Es kann keine Tresore öffnen.
 * 
 * @author Nico Hezel
 */
public class Stethoscope implements Opener {

	private SecureSafe currentSafe = null;
	
	/**
	 * Der Tresor ruft diese Methode innerhalb seiner {@link Safe#try2open} Routine auf
	 * und übermittelt die aktuell am Zahlenschloss eingestellte Zahl hierher.
	 * <br><br>
	 * Entscheide ob diese Zahl eingeloggt (true) werden soll  oder nicht (false).
	 * <br><br>
	 * Siehe "mechanische Zahlenschlösser" bei Google Images oder im 
	 * <a href="http://www.duden.de/rechtschreibung/Zahlenschloss">Duden</a>.
	 * <br><br>
	 * Da eine intelligente Implementierung fehlt, gibt sie immer "false" zurück.
	 * 
	 * @param number aktuelle Zahl die am Tresor eingestellt ist
	 * @return soll diese Zahl eingerastet werden oder nicht
	 */
	@Override
	public boolean lockIn(int number) {
		return false;
	}

	/**
	 * Höre ob beim einstellen der Zahl der Tresor 
	 * ein Einrastgeräusch von sich gibt.
	 * 
	 * @param number aktuelle Zahl
	 * @return gibt es ein Einrastgeräusch (ja/nein)
	 */
	protected boolean listen(int number) {
		return currentSafe.listenTo(number);
	}


	/**
	 * Welcher Safe mit soll mit dem Werkgezeug geöffnet werden.
	 * 
	 * @param safe der geöffnet werden soll
	 */
	@Override
	public void setSafe(Safe safe) {
		this.currentSafe = (SecureSafe) safe;
	}
}
