
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;



@SuppressWarnings("serial")
public class Platno extends JPanel implements MouseListener{
	
	private int sirina, visina, maxIteration;
	private BufferedImage slika;
	private Okno okno;
	private List<DodatnoOkno> dodatnaOkna;
	private Boolean jeMandelbrot;
	private Thread vlakno;
	private boolean ustavi;
	protected double sirinaKR = 4, visinaKR = 4;
	protected int izhodisceX = 250, izhodisceY = 250;
	protected double popravekX = 0, popravekY = 0;
	
	
	public Platno(Okno o, int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
		okno = o;
		this.addMouseListener(this);
		dodatnaOkna = new ArrayList<DodatnoOkno>();
	}
	

	public Dimension getPreferredSize(){
		return new Dimension(sirina*3/2, visina);
	}	
	

	/**
	 * zapre vsa mini okna in
	 * sprozi risanje v vzporednem vlaknu,
	 * ce risanje ze poteka, se trenutno risanje ustavi in zacne novo
	 * @throws InterruptedException
	 */
	public void narisi() throws InterruptedException {
		if (dodatnaOkna!=null){
			// zapre vsa mini okna
			for (DodatnoOkno o: dodatnaOkna){
				o.dispose();
		}
		dodatnaOkna.clear();
		}
		if (vlakno != null) {
			// ce je vlakno aktivno, ga ustavi
			ustavi = true;
			vlakno.join();
		}
		vlakno = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ustavi = false;
					setJeMandelbrot(false);
					if (okno.izbiraFraktala.getSelectedItem()==okno.getJulia()) {
						narisiJulia();
					}
					if (okno.izbiraFraktala.getSelectedItem()==okno.getMandelbrot()) {
						setJeMandelbrot(true);
						narisiMandelbrot();
					}
					vlakno = null;
				} catch (Exception e) {
				}
			}
		});
		vlakno.start();
	}
	
	
	public void narisiJulia() throws InterruptedException {
		setSlika(new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_RGB));
		if ((double) Double.parseDouble(okno.imagC.getText())==0){
			// imaginarni del konstante je nic
			if (izhodisceX >= 0 && izhodisceX < sirina){ 
				// izhodisceX je na platnu
				if (izhodisceX > sirina/2) {
					// izhodisceX je na desni polovici platna
					for (int x=0; x <= izhodisceX; x++){
						if (ustavi) {return;}
						if (izhodisceY >= 0 && izhodisceY < visina){
							// izhodisceY je na platnu
							if (izhodisceY > visina/2) {
								// izhodisceY je na desni polovici platna
								for (int y=0; y <= izhodisceY; y++){
									risiJulia0(x, y);
								}
							}
							else {
								// izhodisceY je na levi polovici platna
								for (int y=izhodisceY; y < visina; y++) {
									risiJulia0(x, y);
								}
							}
						}
						else {
							// izhodisceY je zunaj platna
							for (int y=0; y < visina; y++) {
								risiJulia0(x, y);
							}
						}
					}
				}
				else {
					// izhodisceX je na levi polovici platna
					for (int x=izhodisceX; x < sirina; x++) {
						if (ustavi) {return;}
						if (izhodisceY >= 0 && izhodisceY < visina){
							// izhodisceY je na platnu
							if (izhodisceY > visina/2) {
								// izhodisceY je na desni polovici platna
								for (int y=0; y <= izhodisceY; y++){
									risiJulia0(x, y);
								}
							}
							else {
								// izhodisceY je na levi polovici platna
								for (int y=izhodisceY; y < visina; y++) {
									risiJulia0(x, y);
								}
							}
						}
						else {
							// izhodisceY je zunaj platna
							for (int y=0; y < visina; y++) {
								risiJulia0(x, y);
							}
						}
					}
				}
			}
			else {
				// izhodisceX zunaj platna
				for (int x=0; x < sirina; x++) {
					if (ustavi) {return;}
					if (izhodisceY >= 0 && izhodisceY < visina){
						// izhodisceY je na platnu
						if (izhodisceY > visina/2) {
							// izhodisceY je na desni polovici platna
							for (int y=0; y <= izhodisceY; y++){
								risiJulia0(x, y);
							}
						}
						else {
							// izhodisceY je na levi polovici platna
							for (int y=izhodisceY; y < visina; y++) {
								risiJulia0(x, y);
							}
						}
					}
					else {
						// izhodisceY je zunaj platna
						for (int y=0; y < visina; y++) {
							risiJulia0(x, y);
						}
					}
				}
			}
			}
		// imaginarni del konstante je neniceln
		else {
			if (izhodisceX > 0 && izhodisceX < sirina) { 
				if (izhodisceX > sirina/2) {
					for (int x=0; x <= izhodisceX; x++){
						if (ustavi) {return;}
						for (int y=0; y < visina; y++){
							risiJuliaI(x, y);
						}
					}
				}
				else {
					for (int x=izhodisceX; x < sirina; x++) {
						if (ustavi) {return;}
						for (int y=0; y < visina; y++){
							risiJuliaI(x, y);
						}
					}
				}
			}
			else {
				for (int x=0; x < sirina; x++){
					if (ustavi) {return;}
					for (int y=0; y < visina; y++){
						risiJuliaI(x, y);
					}
				}
			}
		}
		repaint();
	}
	
	
	public void risiJulia0(int x, int y) {
		Color color = barvaJulia(x, y);
		// nastavi pikslu barvo
		getSlika().setRGB(x, y, color.getRGB());
		int z = x-2*(x-izhodisceX);
		int w = y - 2*(y - izhodisceY);
		if (z>=0 && z < sirina) {
			getSlika().setRGB(z, y, color.getRGB());
			if (w>=0 && w < visina) {
				getSlika().setRGB(z, w, color.getRGB());
			}
		}
		if (w>=0 && w < visina) {
			getSlika().setRGB(x, w, color.getRGB());
		}
	}
	
	
	public void risiJuliaI(int x, int y) {
		Color color = barvaJulia(x, y);
		int z = x-2*(x-izhodisceX);
		int w = y - 2*(y - izhodisceY);
		// nastavi pikslu barvo
		getSlika().setRGB(x, y, color.getRGB());
		if (z>=0 && z < sirina && w>=0 && w < visina) {
			getSlika().setRGB(z, w, color.getRGB());
		}
	}
	
	
	/**
	 * funkcija, ki vsaki tocki doloci barvo
	 * @param x - prva koordinata tocke
	 * @param y - druga koordinata tocke
	 * @return barva tocke
	 */
	public Color barvaJulia(int x, int y){
		Color color = null;
		int iteracije=0;
		// izracuna kompleksni koordinati tocke
		Vector<Double> koordinati = kompleksneKoordinate(x, y);
		double a = koordinati.get(0);
		double b = koordinati.get(1);
		// prebere konstanto c
		double real = (double) Double.parseDouble(okno.realC.getText());
		double imag = (double) Double.parseDouble(okno.imagC.getText());
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		// izracuna barvo
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo1()) {
			iteracije = steviloIteracijJulia(a, b, new Complex(real, imag));
			if (iteracije >= maxIteration) {
				color = new Color(255, 255, 255);
			}
			else {
				color = new Color(0, 0, 0);
			}
		}
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getSivo()) {
			iteracije = steviloIteracijJulia(a, b, new Complex(real, imag));
			int barva = (int)(255-(Math.sqrt((double)iteracije/maxIteration)*255));
			color = new Color(barva, barva, barva);
		}
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva1()) {
			iteracije = steviloIteracijJulia(a, b,new Complex(real, imag));
			int colorR = (int)(255-(Math.sqrt((double)iteracije/maxIteration))*255);
			int colorG = (int)(255-((double)iteracije/maxIteration)*255);
			int colorB = (int)(255-(((double)iteracije/maxIteration)*((double)iteracije/maxIteration))*255-20);
			if (colorB<0){
				colorB = 0;
			}
			color = new Color(colorR, colorG, colorB);
			if (iteracije >= maxIteration){
				color = new Color(50, 100, 100);
			}
		}
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva2()) {
			iteracije = steviloIteracijJulia(a, b,new Complex(real, imag));
			color = Color.getHSBColor(iteracije % 256, 255, 255 * (iteracije ));
		}
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo2()) {
			int barva = dolociBarvoJuliaCrnoBelo(a, b,new Complex(real, imag));
			color = new Color(barva, barva, barva);
		}
		return color;
	}
	
	
	public void narisiMandelbrot() throws InterruptedException {
		setSlika(new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_RGB));
		for (int x=0; x < sirina; x++){
			if (ustavi) {return;}
			if (izhodisceY >= 0 && izhodisceY < sirina) {
				if (izhodisceY > visina/2) {
					for (int y=0; y <= izhodisceY; y++){
						risiMandelbrot(x, y);
					}
				}
				else {
					for (int y=izhodisceY; y < sirina; y++){
						risiMandelbrot(x, y);
					}
				}
			}
			else {
				for (int y=0; y < sirina; y++){
					risiMandelbrot(x, y);
				}
			}
		}
		repaint();
	}
	
	
	public void risiMandelbrot(int x, int y) {
		Color color = null;
		int iteracije;
		// izracuna kompleksni koordinati tocke
		Vector<Double> koordinati = kompleksneKoordinate(x, y);
		double a = koordinati.get(0);
		double b = koordinati.get(1);
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		// izracuna barvo
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo1()) {
			iteracije = steviloIteracijMandelbrot(new Complex(a, b));
			if (iteracije >= maxIteration) {
				color = new Color(255, 255, 255);
			}
			else {
				color = new Color(0, 0, 0);
			}
		}
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getSivo()) {
			iteracije = steviloIteracijMandelbrot(new Complex(a, b));
			int barva = (int)(255-(Math.sqrt((double)iteracije/maxIteration)*255));
			color = new Color(barva, barva, barva);
		}
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva1()) {
			iteracije = steviloIteracijMandelbrot(new Complex(a, b));
			int colorR = (int)(255-(Math.sqrt((double)iteracije/maxIteration))*255);
			int colorG = (int)(255-((double)iteracije/maxIteration)*255);
			int colorB = (int)(255-(((double)iteracije/maxIteration)*((double)iteracije/maxIteration))*255-20);
			if (colorB<0){
				colorB = 0;
			}
			color = new Color(colorR, colorG, colorB);
			if (iteracije >= maxIteration){
				color = new Color(50, 100, 100);
			}
		}
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva2()) {
			iteracije = steviloIteracijMandelbrot(new Complex(a, b));
			color = Color.getHSBColor(iteracije % 256, 255, 255 * (iteracije));
		}	
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo2()) {
			int barva = dolociBarvoMandelbrotCrnoBelo(new Complex(a, b));
			color = new Color(barva, barva, barva);
		}
		int w = y - 2*(y - izhodisceY);
		// nastavi pikslom barvo
		getSlika().setRGB(x, y, color.getRGB());
		if (w>=0 && w < visina) {
			getSlika().setRGB(x, w, color.getRGB());
		}
	}
		
	
	/**
	 * pikslu doloci stevilo iteracij, ki so potrebne, da je |z| > 10 (bo slo neskoncnost),
	 * oz. vrne maxIteration, ce tocka ne divergira
	 * @param a prva kompleksna koordinata tocke
	 * @param b druga kompleksna koordinata tocke
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return stevilo iteracij
	 */
	public int steviloIteracijJulia(double a, double b, Complex c){
		Complex z = new Complex(a, b);
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		for (int j=0; j <= maxIteration; j++){
			if (z.mod() > 10) {
				return j;
			}
			else {
				z = (z.times(z)).plus(c);
			}
		}
		return maxIteration;
	}
	
	
	/**
	 * fukcija, ki doloci barvo tocke v Juliajevi mnozici na podlagi predznaka imaginarne komponente
	 * stevila z na koncu iteriranja
	 * @param a prva kompleksna koordinata tocke
	 * @param b druga kompleksna koordinata tocke
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return barva tocke
	 */
	public int dolociBarvoJuliaCrnoBelo(double a, double b, Complex c){
		Complex z = new Complex(a, b);
		int color;
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		for (int j=0; j <= maxIteration; j++){
			if (z.mod() > 10) {
				if (z.imag()>0){
					color = 0;
				}
				else {
					color = 255;
				}
				return color;
			}
			else {
				z = (z.times(z)).plus(c);
			}
		}
		color = 0;
		return color;
	}
	

	/**
	 * pikslu doloci stevilo iteracij, ki so potrebne, 
	 * da je |z| > 10 (bo slo neskoncnost) pri zacetni tocki (0,0) in konstanti c,
	 * oz. vrne maxIteration, ce tocka ne divergira
	 * @param c tocka v kompleksni ravnini = konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return stevilo iteracij
	 */
	public int steviloIteracijMandelbrot(Complex c){
		Complex z = new Complex(0, 0);
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		for (int j=0; j <= maxIteration; j++){
			if (z.mod() > 2) {
				return j;
			}
			else {
				z = (z.times(z)).plus(c);
			}
		}
		return maxIteration;
	}
	
	
	/**
	 * fukcija, ki doloci barvo tocke v Mandelbrotovi mnozici na podlagi predznaka imaginarne komponente
	 * stevila z na koncu iteriranja pri zacetni tocki (0, 0)
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return barva tocke
	 */
	public int dolociBarvoMandelbrotCrnoBelo(Complex c){
		Complex z = new Complex(0, 0);
		int color;
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		for (int j=0; j <= maxIteration; j++){
			if (z.mod() > 10) {
					if (z.imag()>0){
						color = 0;
					}
					else {
						color = 255;
					}
				return color;
			}
			else {
				z = (z.times(z)).plus(c);
			}
		}
		return 0;
	}
	
		
	/**
	 * obmocje platna sirina*visina spremeni v izbrano obmocje v kompleksni ravnini
	 * @param x prva koordinata tocke v platnu
	 * @param y druga koordinata tocke v platnu
	 * @return vrne kompleksni koordinati tocke
	 */
	public Vector<Double> kompleksneKoordinate(int x, int y){
		double a = (double)sirinaKR/sirina*(x + popravekX - izhodisceX);
		double b = (double)visinaKR/visina*(izhodisceY-y-popravekY);
		Vector<Double> koordinati = new Vector<Double>(2);
		koordinati.add(a);
		koordinati.add(b);
		return koordinati;
	}
	
	
	public Vector<Double> izracunajIzhodisce(double x, double y) {
		double a = 250 - (double)sirina/sirinaKR*x;
		double b = 250 + (double)visina/visinaKR*y;
		Vector<Double> izhodisce = new Vector<Double>(2);
		izhodisce.add(a);
		izhodisce.add(b);
		return izhodisce;
	}
	
	
	/**
	 * funkcija, ki sprozi risanje fraktala na obmocju polovicne sirine in visine ter
	 * s srediscem v tocki, kamor je uporabnik kliknil
	 * @param x - prva koordinata tocke, kamor je uporabnik kliknil
	 * @param y - druga koordinata tocke, kamor je uporabnik kliknil
	 */
	protected void povecaj(int x, int y) {
		Vector<Double> koordinati = kompleksneKoordinate(x, y);
		double kx = koordinati.get(0);
		double ky = koordinati.get(1);
		sirinaKR = sirinaKR/2;
		visinaKR = visinaKR/2;
		Vector<Double> izhodisce = izracunajIzhodisce(kx, ky);
		double a = izhodisce.get(0);
		double b = izhodisce.get(1);
		izhodisceX = (int) Math.floor(a);
		izhodisceY = (int) Math.floor(b);
		popravekX = a - izhodisceX;
		popravekY = b - izhodisceY;
		try {
			narisi();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	
	/**
	 * funkcija, ki sprozi risanje fraktala na obmocju dvojne sirine in visine ter
	 * s srediscem v tocki, kamor je uporabnik kliknil
	 * @param x - prva koordinata tocke, kamor je uporabnik kliknil
	 * @param y - druga koordinata tocke, kamor je uporabnik kliknil
	 */
	protected void pomanjsaj(int x, int y) {
		Vector<Double> koordinati = kompleksneKoordinate(x, y);
		double kx = koordinati.get(0);
		double ky = koordinati.get(1);
		sirinaKR = 2*sirinaKR;
		visinaKR = 2*visinaKR;
		Vector<Double> izhodisce = izracunajIzhodisce(kx, ky);
		double a = izhodisce.get(0);
		double b = izhodisce.get(1);
		izhodisceX = (int) Math.floor(a);
		izhodisceY = (int) Math.floor(b);
		popravekX = a - izhodisceX;
		popravekY = b - izhodisceY;
		try {
			narisi();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (getSlika() != null) {
			g.drawImage(getSlika(), 0, 0, getBackground(), null);
		}
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * ob kliku se bodisi slika pribliza (levi klik) ali oddalji (desni klik) bodisi se izrise
	 * miniJulia na mini oknu
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// koordinati tocke klika
		int x = e.getX();
		int y = e.getY();
		if (x < sirina) {
			if (getJeMandelbrot()) {
				// ce je narisan Mandelbrot imamo dve moznosti
				if (okno.getRdbtnObKliku2().isSelected()) {
					// na mini okno se izrisi miniJulia
					// izracuna kompleksni koordinati klikane tocke
					Vector<Double> koordinati = kompleksneKoordinate(x, y);
					double a = koordinati.get(0);
					double b = koordinati.get(1);
					a = ((double) Math.round(1000*a)/1000);
					b = ((double) Math.round(1000*b)/1000);
					DodatnoOkno novoOkno = null;
					try {
						// odpre mini okno
						novoOkno = new DodatnoOkno(a, b, this.okno);
					} catch (HeadlessException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					dodatnaOkna.add(novoOkno);
					novoOkno.pack();
					novoOkno.setVisible(true);
				}
				else if (okno.getRdbtnObKliku1().isSelected()) {
					// slika se pribliza/oddalji
					if(SwingUtilities.isLeftMouseButton(e)){
						povecaj(x, y);
					}
					if(SwingUtilities.isRightMouseButton(e)){
						pomanjsaj(x, y);
					}
				}
			}
			else {
				// ce je narisan Julia se slika ob kliku pribliza/oddalji
				if(SwingUtilities.isLeftMouseButton(e)){
					povecaj(x, y);
				}
				if(SwingUtilities.isRightMouseButton(e)){
					pomanjsaj(x, y);
				}
			}
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public BufferedImage getSlika() {
		return slika;
	}


	public void setSlika(BufferedImage slika) {
		this.slika = slika;
	}


	public Boolean getJeMandelbrot() {
		return jeMandelbrot;
	}


	public void setJeMandelbrot(Boolean jeMandelbrot) {
		this.jeMandelbrot = jeMandelbrot;
	}

}
