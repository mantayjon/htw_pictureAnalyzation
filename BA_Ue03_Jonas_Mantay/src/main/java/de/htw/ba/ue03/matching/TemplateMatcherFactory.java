package de.htw.ba.ue03.matching;


/**
 * Erzeugt TemplateMatcher je nachdem welche Methode gefordert ist
 * 
 * @author Nico Hezel
 *
 */
public class TemplateMatcherFactory {

	public static enum Methods {
		Copy,
		Absolute, 
		Maximum, 
		Square, 
		CorreCoef
	};
	
	protected int[] templatePixel; 
	protected int templateWidth;
	protected int templateHeight;
	
	public TemplateMatcherFactory(int[] templatePixel, int templateWidth, int templateHeight) {
		this.templatePixel = templatePixel;
		this.templateWidth = templateWidth;
		this.templateHeight = templateHeight;
	}

	/**
	 * Gibt den passenden Template Matcher zurück.
	 * TODO: füge die eigenen Matcher hinzu
	 * 
	 * @param method
	 * @return
	 */


	// bei vereinfachten Korrelationskoeffizient (vereinfacht) kann man die phiR einmalig for
	// allen berechnugnen in einer For-schleife berechnen
	// am besten Probieren,



	public TemplateMatcher getTemplateMatcher(Methods method) {
		switch (method) {
			case Absolute:
				return new TemplateMatcherAbsolute(templatePixel, templateWidth, templateHeight);
			case CorreCoef:
				return new TemplateMatcherCorreCoef(templatePixel, templateWidth, templateHeight);
			case Maximum:
				return new TemplateMatcherMaximum(templatePixel, templateWidth, templateHeight);
			case Square:
				return new TemplateMatcherSquare(templatePixel, templateWidth, templateHeight);
			case Copy:
			default:
				return new TemplateMatcherCopy(templatePixel, templateWidth, templateHeight);

		}
	}
}
