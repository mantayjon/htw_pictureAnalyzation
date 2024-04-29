package de.htw.ba.secure;

/**
 * Dieser Tresor enthält wichtige Zugangsdaten.
 * 
 * @author Nico Hezel
 */
public interface Safe {

	/**
	 * Entscheide mit welchen Werkzeug der Tresor 
	 * geöffnet werden soll.
	 * 
	 * @param opener Werkzeug zum öffnen des Tresors
	 */
	public void openWith(Opener opener);
	
	
	/**
	 * Versuche den Tresor zu öffnen. Drehe nach und nach
	 * am Zahlenrad und frage {@link de.htw.ba.secure.Opener#lockIn(int)}  
	 * ob die aktuelle Zahl, eingelockt werden soll oder nicht.
	 * 
	 * @throws Exception für den Fall der Tresor kann nicht geöffnet werden
	 */
	public void try2open() throws Exception;

	
	/**
	 * Der gefüllte und gut abgesicherte Tresor.
	 * 
	 * @return gesicherter Tresor
	 */
	public static Safe getSecureSafe() {
		return new SecureSafe();
	}
}
