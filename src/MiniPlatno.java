import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MiniPlatno extends JPanel {
	
	private int sirina;
	private int visina;
	private BufferedImage slika;
	private int maxIteration;
	private Okno okno;
	private Thread vlakno;
	private boolean ustavi;
	private double spremembaX, spremembaY;
	
	
	public MiniPlatno(Okno o, int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
		okno = o;
		spremembaX = (double)4/sirina;
		spremembaY = (double)4/visina;
	}
	
	
	public Dimension getPreferredSize(){
		return new Dimension(sirina, visina+35);
	}
	
	
	/**
	 * sprozi risanje v vzporednem vlaknu,
	 * ce risanje ze poteka, se trenutno risanje ustavi in zacne novo
	 * @param real - realna komponenta konstante c
	 * @param imag - imaginarna komponenta konstante c
	 * @throws InterruptedException
	 */
	public void narisi(double real, double imag) throws InterruptedException {
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
					narisiMiniJulia(real, imag);
					vlakno = null;
				} catch (Exception e) {
				}
			}
		});
		vlakno.start();
	}
	
	
	/**
	 * funkcija, ki izracuna sliko in pri tem uposteva simetrije:
	 * ce je imaginarni del konstante c nic, je slika simetricna glede na obe koordinatni osi,
	 * sicer pa je simetricna preko koordinatnega izhodisca
	 * @param real - realna komponenta konstante c
	 * @param imag - imaginarna komponenta konstante c
	 * @throws InterruptedException
	 */
	public void narisiMiniJulia(double real, double imag) throws InterruptedException {
		slika = new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_RGB);
		Vector<Double> koordinati = kompleksneKoordinate(0, 0);
		double a = koordinati.get(0);
		if (imag==0){
			// imaginarna komponenta stevila c je nic, zato je dovolj izracunati le cetrtino slike
			for (int x=0; x <= sirina/2; x++){
				if (ustavi) { return; }
				double b = koordinati.get(1);
				for (int y=0; y <= visina/2; y++){
					Color color = barvaJulia(a, b, real, imag);
					// nastavi pikslom barvo
					slika.setRGB(x, y, color.getRGB());
					slika.setRGB(sirina-1-x, y, color.getRGB());
					slika.setRGB(x, visina-1-y, color.getRGB());
					slika.setRGB(sirina-1-x, visina-1-y, color.getRGB());
					b = b-spremembaY;
				}
				a = a+spremembaX;
			}
		}
		else {
			// imaginarna komponenta stevila c ni nic, dovolj je izracunati polovico slike
			for (int x=0; x <= sirina/2; x++){
				double b = koordinati.get(1);
				for (int y=0; y < visina; y++){
					Color color = barvaJulia(a, b, real, imag);
					// nastavi pikslu barvo
					slika.setRGB(x, y, color.getRGB());
					slika.setRGB(sirina-1-x, visina-1-y, color.getRGB());
					b = b-spremembaY;
				}
				a = a+spremembaX;
			}
		}
		repaint();
	}
	
	
	/**
	 * funkcija, ki vsaki tocki doloci barvo
	 * @param x - prva koordinata tocke
	 * @param y - druga koordinata tocke
	 * @param real - realna komponenta konstante c
	 * @param imag - imaginarna komponenta konstante c
	 * @return barva tocke
	 */
	public Color barvaJulia(double a, double b, double real, double imag) {
		Color color = null;
		int iteracije=0;
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		// izracuna barvo
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo1()) {
			iteracije = steviloIteracijJulia(a, b, real, imag);
			if (iteracije >= maxIteration) {
				color = new Color(255, 255, 255);
			}
			else {
				color = new Color(0, 0, 0);
			}
		}
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getSivo()) {
			double nsmooth = smoothIteracijeJulia(a, b, real, imag);
			color = Color.getHSBColor(0, 0, (float) Math.sqrt(nsmooth/maxIteration));
		}

		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo2()) {
			int barva = dolociBarvoJuliaCrnoBelo(a, b, real, imag);
			color = new Color(barva, barva, barva);
		}
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva1()) {
			double nsmooth = smoothIteracijeJulia(a, b, real, imag);
			int colorIndex = (int) (nsmooth/maxIteration*768);
			if (colorIndex >= 768 || colorIndex < 0) {
				colorIndex = 0;
			}
			color = okno.platno.getColors().get(colorIndex);
		}
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva2()) {
			double nsmooth = smoothIteracijeJulia(a, b, real, imag);
			if (nsmooth == maxIteration) {
				color = Color.getHSBColor(0, 1, 0);
			}
			else {
				color = Color.getHSBColor((float) (2.0*nsmooth/maxIteration), 1.0f, 1.0f);
			}
		}
		
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva3()) {
			iteracije = steviloIteracijJulia(a, b, real, imag);
			color = Color.getHSBColor(iteracije % 256, 255, 255 * (iteracije ));
		}

		if (okno.getIzbiraBarv().getSelectedItem()==okno.getBarva4()) {
			double nsmooth = smoothIteracijeJulia(a, b, real, imag);
			color = Color.getHSBColor((float) ((nsmooth/maxIteration) % 256), 0.9f,(float) (255 * nsmooth/maxIteration));
		}
		
		return color;
	}
	
		
	/**
	 * pikslu doloci stevilo iteracij, ki so potrebne, da je |z| > 10 (bo slo neskoncnost),
	 * oz. vrne maxIteration, ce tocka ne divergira
	 * @param a prva kompleksna koordinata tocke
	 * @param b druga kompleksna koordinata tocke
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return stevilo iteracij
	 */
	public int steviloIteracijJulia(double a, double b, double real, double imag){
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
				zI += imag;
				zR = zrsqr - zisqr + real;
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
	public double smoothIteracijeJulia(double a, double b, double real, double imag) {
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
					zI += imag;
					zR = zrsqr - zisqr + real;
					zrsqr = zR*zR;
					zisqr = zI*zI;
					j++;
				}
				return j+1-Math.log(Math.log(Math.sqrt(zrsqr+zisqr)))/Math.log(10);
			}
			else {
				zI = zR*zI;
				zI += zI;
				zI += imag;
				zR = zrsqr - zisqr + real;
				zrsqr = zR*zR;
				zisqr = zI*zI;
			}
		}
		return maxIteration;
	}
	
	
	/**
	 * fukcija, ki doloci barvo tocke na podlagi predznaka imaginarne komponente
	 * stevila z na koncu iteriranja
	 * @param a prva kompleksna koordinata tocke
	 * @param b druga kompleksna koordinata tocke
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return barva tocke
	 */
	public int dolociBarvoJuliaCrnoBelo(double a, double b, double real, double imag){
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
				zI += imag;
				zR = zrsqr - zisqr + real;
				zrsqr = zR*zR;
				zisqr = zI*zI;
			}
		}
		color = 0;
		return color;
	}
	
		
	/**
	 * obmocje platna sirina*visina spremeni v obmocje [-2,2]x[-2i, 2i]
	 * @param x prva koordinata tocke v platnu
	 * @param y druga koordinata tocke v platnu
	 * @return vrne kompleksni koordinati tocke
	 */
	public Vector<Double> kompleksneKoordinate(int x, int y){
		double a = 4*(double)x/(sirina-1) - 2;
		double b = 2 - 4*(double)y/(visina-1);
		Vector<Double> koordinati = new Vector<Double>(2);
		koordinati.add(a);
		koordinati.add(b);
		return koordinati;
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (slika != null) {
			g.drawImage(slika, 0, 0, getBackground(), null);
		}
	}

}