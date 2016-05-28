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
	
	
	public MiniPlatno(Okno o, int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
		okno = o;
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
		if (imag==0){
			// imaginarna komponenta stevila c je nic, zato je dovolj izracunati le cetrtino slike
			for (int x=0; x <= sirina/2; x++){
				if (ustavi) { return; }
				for (int y=0; y <= visina/2; y++){
					Color color = barvaJulia(x, y, real, imag);
					// nastavi pikslom barvo
					slika.setRGB(x, y, color.getRGB());
					slika.setRGB(sirina-1-x, y, color.getRGB());
					slika.setRGB(x, visina-1-y, color.getRGB());
					slika.setRGB(sirina-1-x, visina-1-y, color.getRGB());
				}
			}
		}
		else {
			// imaginarna komponenta stevila c ni nic, dovolj je izracunati polovico slike
			for (int x=0; x <= sirina/2; x++){
				for (int y=0; y < visina; y++){
					Color color = barvaJulia(x, y, real, imag);
					// nastavi pikslu barvo
					slika.setRGB(x, y, color.getRGB());
					slika.setRGB(sirina-1-x, visina-1-y, color.getRGB());
				}
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
	public Color barvaJulia(int x, int y, double real, double imag) {
		Color color = null;
		int iteracije=0;
		// izracuna kompleksni koordinati tocke
		Vector<Double> koordinati = kompleksneKoordinate(x, y);
		double a = koordinati.get(0);
		double b = koordinati.get(1);
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
			color = Color.getHSBColor(iteracije % 256, 255, 255 * (iteracije));
		}
		if (okno.getIzbiraBarv().getSelectedItem()==okno.getCrnoBelo2()) {
			int barva = dolociBarvoJuliaCrnoBelo(a, b,new Complex(real, imag));
			color = new Color(barva, barva, barva);
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
	 * fukcija, ki doloci barvo tocke na podlagi predznaka imaginarne komponente
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