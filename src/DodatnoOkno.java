import java.awt.HeadlessException;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class DodatnoOkno extends JFrame {
	
	protected Okno okno;
	protected MiniPlatno platno;
	protected double real, imag;

	public DodatnoOkno(double real, double imag, Okno okno) throws HeadlessException {
		super();
		platno = new MiniPlatno(this, okno, 250, 250);
		add(platno);
		platno.narisiMiniJulia(real, imag);
	}
		
}
