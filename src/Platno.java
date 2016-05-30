
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
	protected double sredisceX = 0, sredisceY = 0;
	private double spremembaX, spremembaY;
	private ArrayList<Color> colors;
	
	
	public Platno(Okno o, int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
		okno = o;
		this.addMouseListener(this);
		dodatnaOkna = new ArrayList<DodatnoOkno>();
		
		setColors(new ArrayList<Color>());
		for (int i=0; i<768; i++) {
			int colorValueR = 0;
			int colorValueG = 0;
			int colorValueB = 0;
			if (i >= 512) {
				colorValueR = i-512;
				colorValueG = 255 - colorValueR;
			}
			else if (i >= 256) {
				colorValueG = i-256;
				colorValueB = 255 - colorValueG;
			}
			else {
				colorValueB = i;
			}
			getColors().add(new Color(colorValueR, colorValueG, colorValueB));
		}
		
	}
	

	public Dimension getPreferredSize(){
		return new Dimension(sirina*3/2, visina-10);
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
				if (okno.klikNaGumb){
					if (okno.izbiraFraktala.getSelectedItem()==okno.getMandelbrot()) {
						setJeMandelbrot(true);
						// ok kliku sta dve moznosti
						okno.getRdbtnObKliku1().setEnabled(true);
						okno.getRdbtnObKliku2().setEnabled(true);
					}
					else {
						setJeMandelbrot(false);
						// ok kliku ni izbire (se lahko samo poveca)
						okno.getRdbtnObKliku1().setSelected(true);
						okno.getRdbtnObKliku1().setEnabled(false);
						okno.getRdbtnObKliku2().setEnabled(false);
					}
					okno.klikNaGumb = false;
				}
				try {
					ustavi = false;
					if (!jeMandelbrot) {
						narisiJulia();
					}
					if (jeMandelbrot) {
						narisiMandelbrot();
					}
					vlakno = null;
				} catch (Exception e) {
				}
			}
		});
		vlakno.start();
	}
	
	
	/**
	 * funkcija, ki izracuna Juliajevo mnozico
	 * @throws InterruptedException
	 */
	public void narisiJulia() throws InterruptedException {
		setSlika(new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_RGB));
		Vector<Double> koordinati = kompleksneKoordinate(0, 0);
		double a = koordinati.get(0);		
		spremembaX = (double)sirinaKR/sirina;
		spremembaY = (double)visinaKR/visina;
		for (int x=0; x<sirina; x++) {
			if (ustavi) {return;}
			double b = koordinati.get(1);
			for (int y=0; y<visina; y++) {
				Color color = null;
				int iteracije=0;
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
					double nsmooth = smoothIteracijeJulia(a, b,new Complex(real, imag));
					color = Color.getHSBColor(0, 0, (float) Math.sqrt(nsmooth/maxIteration));
				}
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo2()) {
					int barva = dolociBarvoJuliaCrnoBelo(a, b,new Complex(real, imag));
					color = new Color(barva, barva, barva);
				}

				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva1()) {
					double nsmooth = smoothIteracijeJulia(a, b,new Complex(real, imag));
					int colorIndex = (int) (nsmooth/maxIteration*768);
					if (colorIndex >= 768 || colorIndex < 0) {
						colorIndex = 0;
					}
					color = getColors().get(colorIndex);
				}
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva2()) {
					double nsmooth = smoothIteracijeJulia(a, b,new Complex(real, imag));
					if (nsmooth == maxIteration) {
						color = Color.getHSBColor(0, 1, 0);
					}
					else {
						color = Color.getHSBColor((float) (2.0*nsmooth/maxIteration), 1.0f, 1.0f);
					}
				}
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva3()) {
					iteracije = steviloIteracijJulia(a, b,new Complex(real, imag));
					color = Color.getHSBColor(iteracije % 256, 255, 255 * (iteracije ));
				}

				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva4()) {
					double nsmooth = smoothIteracijeJulia(a, b,new Complex(real, imag));
					color = Color.getHSBColor((float) ((nsmooth/maxIteration) % 256), 0.9f,(float) (255 * nsmooth/maxIteration));
				}

				// nastavi pikslu barvo
				getSlika().setRGB(x, y, color.getRGB());
				b = b-spremembaY;
			}
			a = a+spremembaX;
		}
		repaint();
	}
	
	
	/**
	 * funkcija, ki izracuna Mandelbrotovo mnozico
	 * @throws InterruptedException
	 */
	public void narisiMandelbrot() throws InterruptedException {
		setSlika(new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_RGB));
		Vector<Double> koordinati = kompleksneKoordinate(0, 0);
		double a = koordinati.get(0);		
		spremembaX = (double)sirinaKR/sirina;
		spremembaY = (double)visinaKR/visina;
		for (int x=0; x < sirina; x++) {
			double b = koordinati.get(1);
			for (int y=0; y < visina; y++) {
				Color color = null;
				int iteracije;
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
					double nsmooth = smoothIteracijeMandelbrot(new Complex(a, b));
					color = Color.getHSBColor(0, 0, (float) Math.sqrt(nsmooth/maxIteration));
				}
								
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo2()) {
					int barva = dolociBarvoMandelbrotCrnoBelo(new Complex(a, b));
					color = new Color(barva, barva, barva);
				}
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva1()) {
					double nsmooth = smoothIteracijeMandelbrot(new Complex(a, b));
					int colorIndex = (int) (nsmooth/maxIteration*768);
					if (colorIndex >= 768 || colorIndex < 0) {
						colorIndex = 0;
					}
					color = getColors().get(colorIndex);
				}
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva2()) {
					double nsmooth = smoothIteracijeMandelbrot(new Complex(a, b));
					if (nsmooth == maxIteration) {
						color = Color.getHSBColor(0, 1, 0);
					}
					else {
						color = Color.getHSBColor((float) (2.0*nsmooth/maxIteration), 1.0f, 1.0f);
					}
				}
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva3()) {
					iteracije = steviloIteracijMandelbrot(new Complex(a, b));
					color = Color.getHSBColor(iteracije % 256, 255, 255 * (iteracije));
				}	
				
				if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva4()) {
					double nsmooth = smoothIteracijeMandelbrot(new Complex(a, b));
					color = Color.getHSBColor((float) ((nsmooth/maxIteration) % 256), 0.9f,(float) (255 * nsmooth/maxIteration));
				}
				
				// nastavi pikslu barvo
				getSlika().setRGB(x, y, color.getRGB());
				b = b-spremembaY;
			}
			a = a+spremembaX;
		}
		repaint();
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
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		double zR = a;
		double zI = b;
		double zrsqr = a*a;
		double zisqr = b*b;
		for (int j=0; j <= maxIteration; j++){
			if ((zrsqr+zisqr) > 100) {
				return j;
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += c.imag();
				zR = zrsqr - zisqr + c.real();
				zrsqr = zR*zR;
				zisqr = zI*zI;
			}
		}
		return maxIteration;
	}
	
	
	/**
	 * pikslu doloci stevilo odvisno od iteracij, ki so potrebne, da je |z| > 10 (bo slo neskoncnost),
	 * oz. vrne maxIteration, ce tocka ne divergira
	 * @param a prva kompleksna koordinata tocke
	 * @param b druga kompleksna koordinata tocke
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return stevilo odvisno od iteracij (lepse prelivanje barv)
	 */
	public double smoothIteracijeJulia(double a, double b, Complex c) {
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		double zR = a;
		double zI = b;
		double zrsqr = a*a;
		double zisqr = b*b;
		for (int j=0; j <= maxIteration; j++){
			if ((zrsqr+zisqr) > 100) {
				for (int i=0; i<3; i++) {
					zI = zR*zI;
					zI += zI;
					zI += c.imag();
					zR = zrsqr - zisqr + c.real();
					zrsqr = zR*zR;
					zisqr = zI*zI;
					j++;
				}
				return j+1-Math.log(Math.log(Math.sqrt(zrsqr+zisqr)))/Math.log(10);
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += c.imag();
				zR = zrsqr - zisqr + c.real();
				zrsqr = zR*zR;
				zisqr = zI*zI;
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
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		int color;
		double zR = a;
		double zI = b;
		double zrsqr = a*a;
		double zisqr = b*b;
		for (int j=0; j <= maxIteration; j++){
			if ((zrsqr+zisqr) > 100) {
				if (zI > 0) {
					color = 0;
				}
				else {
					color = 255;
				}
				return color;
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += c.imag();
				zR = zrsqr - zisqr + c.real();
				zrsqr = zR*zR;
				zisqr = zI*zI;
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
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		double zR = 0;
		double zI = 0;
		double zrsqr = 0;
		double zisqr = 0;
		for (int j=0; j <= maxIteration; j++){
			if ((zrsqr+zisqr) > 9) {
				return j;
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += c.imag();
				zR = zrsqr - zisqr + c.real();
				zrsqr = zR*zR;
				zisqr = zI*zI;
			}
		}
		return maxIteration;
	}
	
	
	/**
	 * pikslu doloci stevilo odvisno od iteracij, ki so potrebne, 
	 * da je |z| > 10 (bo slo neskoncnost) pri zacetni tocki (0,0) in konstanti c,
	 * oz. vrne maxIteration, ce tocka ne divergira
	 * @param c tocka v kompleksni ravnini = konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return stevilo odvisno od iteracij (lepse prelivanje barv)
	 */
	public double smoothIteracijeMandelbrot(Complex c){
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		double zR = 0;
		double zI = 0;
		double zrsqr = 0;
		double zisqr = 0;
		for (int j=0; j <= maxIteration; j++){
			if ((zrsqr+zisqr) > 9) {
				for (int i=0; i<3; i++) {
					zI = zR*zI;
					zI += zI;
					zI += c.imag();
					zR = zrsqr - zisqr + c.real();
					zrsqr = zR*zR;
					zisqr = zI*zI;
					j++;
				}
				return j+1-Math.log(Math.log(Math.sqrt(zrsqr+zisqr)))/Math.log(3);
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += c.imag();
				zR = zrsqr - zisqr + c.real();
				zrsqr = zR*zR;
				zisqr = zI*zI;
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
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		int color;
		double zR = 0;
		double zI = 0;
		double zrsqr = 0;
		double zisqr = 0;
		for (int j=0; j <= maxIteration; j++){
			if ((zrsqr+zisqr) > 100) {
				if (zI > 0) {
					color = 0;
				}
				else {
					color = 255;
				}
				return color;
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += c.imag();
				zR = zrsqr - zisqr + c.real();
				zrsqr = zR*zR;
				zisqr = zI*zI;
			}
		}
		color = 0;
		return color;
	}
	
		
	/**
	 * obmocje platna sirina*visina spremeni v izbrano obmocje v kompleksni ravnini
	 * @param x prva koordinata tocke v platnu
	 * @param y druga koordinata tocke v platnu
	 * @return vrne kompleksni koordinati tocke
	 */
	public Vector<Double> kompleksneKoordinate(int x, int y){
		double a = sredisceX + (double)sirinaKR/sirina*(x - sirina/2);
		double b = sredisceY + (double)visinaKR/visina*(visina/2 - y);
		Vector<Double> koordinati = new Vector<Double>(2);
		koordinati.add(a);
		koordinati.add(b);
		return koordinati;
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
		sredisceX = kx;
		sredisceY = ky;
		sirinaKR = sirinaKR/2;
		visinaKR = visinaKR/2;
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
		sredisceX = kx;
		sredisceY = ky;
		sirinaKR = 2*sirinaKR;
		visinaKR = 2*visinaKR;
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


	public ArrayList<Color> getColors() {
		return colors;
	}


	public void setColors(ArrayList<Color> colors) {
		this.colors = colors;
	}

}
