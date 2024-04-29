package de.htw.ba.secure;

/**
 * Allgemeines Werkzeug zum öffnen von gesicherten Gegenständen.
 * 
 * @author Nico Hezel
 */
interface Opener {
	
	/**
	 * Der Tresor ruft diese Methode innerhalb seiner {@link Safe#try2open} Routine auf
	 * und übermittelt die aktuell am Zahlenschloss eingestellte Zahl hierher.
	 * <br><br>
	 * Entscheide ob diese Zahl eingeloggt (true) werden soll  oder nicht (false).
	 * <br><br>
	 * Siehe "mechanische Zahlenschlösser" bei Google Images oder im 
	 * <a href="http://www.duden.de/rechtschreibung/Zahlenschloss">Duden</a>.
	 * 
	 * @param number aktuelle Zahl die am Tresor eingestellt ist
	 * @return soll diese Zahl eingerastet werden oder nicht
	 */
	public boolean lockIn(int number);
	
	/**
	 * Welcher Safe mit soll mit dem Werkgezeug geöffnet werden.
	 * 
	 * @param safe der geöffnet werden soll
	 */
	public void setSafe(Safe safe);
}
