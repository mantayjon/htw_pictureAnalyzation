package de.htw.ba.secure;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;


class SecureSafe implements Safe {

	private int maxNumber = 26;
	private int currentPos = 0;
	
	private Opener opener = null;
	
	/**
	 * Dieser Tresor enthält wichtige Zugangsdaten.
	 */
	public SecureSafe()
	{
		System.setOut(new MyPrintStream(System.out));
		System.setErr(new MyPrintStream(System.err));
	}
	
	/**
	 * Entscheide mit welchen Werkzeug der Tresor 
	 * geöffnet werden soll.
	 * 
	 * @param opener Werkzeug zum öffnen des Tresors
	 */
	@Override
	public void openWith(Opener opener)
	{
		this.opener = opener;
		opener.setSafe(this);
	}
	
	/**
	 * Versuche den Tresor zu öffnen. Drehe nach und nach
	 * am Zahlenrad und frage {@link de.htw.ba.secure.Opener#lockIn(int)}  
	 * ob die aktuelle Zahl, eingelockt werden soll oder nicht.
	 * 
	 * @throws Exception
	 */
	@Override
	public void try2open() throws Exception
	{
		if(this.opener == null)
			throw new Exception("Kein Geräte zum öffnen angegeben");
		
		// find all numbers
		int correctNumbers = 0;
		for (currentPos = 0; currentPos < 6; currentPos++) 
		{
			// test all numbers
			for (int number = 1; number <= maxNumber; number++) 
			{
				// locked a number
				if(opener.lockIn(number)) {
					if(currentPos == 0 && number == 18) correctNumbers++;
					if(currentPos == 1 && number == 21) correctNumbers++;
					if(currentPos == 2 && number == 20) correctNumbers++;
					if(currentPos == 3 && number == 14) correctNumbers++;
					if(currentPos == 4 && number == 15) correctNumbers++;
					if(currentPos == 5 && number == 11) correctNumbers++; 
				}
			}
		}
		
		// found all numbers -> open safe
		if(correctNumbers == 6)
		{
			((MyPrintStream)System.out).p("*Der Tresor öffnet sich.*\n"
										+ "*Auf einen Zettel da drin steht:*\n"
										+ "\"Verwende den Debugmodus um die Zahlen des Tresors "
										+ "herauszubekommen. \nJede Zahl steht für einen Buchstaben. "
										+ "Alle Buchstaben aneinandergereiht \nund rückwärts gelesen, "
										+ "ergeben das Passwort für die Blaupausen des Tresors.\"");
		}else
			((MyPrintStream)System.out).p("*Versuch fehlgeschlagen*");
		
		currentPos = 0;
	}
	
	/**
	 * Höre ob der Tresor bei dieser Ziffer ein 
	 * Einrastgeräusch von sich gibt.
	 * 
	 * Bedenke dass die Zahlen eine bestimmt 
	 * Eingabereihnfolge einhalten müssen. 
	 *  
	 * @param number Ziffer die getestet werden soll
	 * @return Einrastgeräusch gehört
	 */
	public boolean listenTo(int number)
	{
		if(currentPos == 0 && number == 18) return true;
		if(currentPos == 1 && number == 21) return true;
		if(currentPos == 2 && number == 20) return true;
		if(currentPos == 3 && number == 14) return true;
		if(currentPos == 4 && number == 15) return true;
		if(currentPos == 5 && number == 11) return true;
		return false;
	}
	
	
	/**
	 * Eigener Stream um den Stream vom System umzuleiten.
	 * 
	 * @author Nico
	 */
	private class MyPrintStream extends PrintStream 
	{
		private PrintStream stream;
		public MyPrintStream(PrintStream ps) {
			super(ps);
			stream = ps;
		}
		
		@Override
		public void print(String s) {}
		
		@Override
		public void print(char[] s) {}
		
		@Override
		public void print(Object obj) {}
		
		@Override
		public void write(int b) {}

		@Override
		public void write(byte[] buf, int off, int len) {}

		@Override
		public void print(boolean b) {}

		@Override
		public void print(char c) {}

		@Override
		public void print(int i) {}

		@Override
		public void print(long l) {}

		@Override
		public void print(float f)  {}

		@Override
		public void print(double d) {}

		@Override
		public void println()  {}

		@Override
		public void println(boolean x) {}

		@Override
		public void println(char x) {}

		@Override
		public void println(int x) {}

		@Override
		public void println(long x) {}

		@Override
		public void println(float x) {}

		@Override
		public void println(double x) {}

		@Override
		public void println(char[] x) {}

		@Override
		public void println(Object x) {}

		@Override
		public PrintStream printf(String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream format(String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream format(Locale l, String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq) {
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq, int start, int end) {
			return this;
		}

		@Override
		public PrintStream append(char c) {
			return this;
		}

		@Override
		public void println(String x) {}

		
		@Override
		public void write(byte[] b) throws IOException {}
		
		
		public void p(String text)
		{
			stream.print(text);
		}
	}
}
