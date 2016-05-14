
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Platno extends JPanel{
	
	private int sirina;
	private int visina;
	private BufferedImage slika;
	private int maxIteration;
	protected Okno okno;
	
	public Platno(Okno o, int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
		okno = o;
	}
	
	
	
	public Dimension getPreferredSize(){
		return new Dimension(sirina*3/2, visina);
	}
	
	/**
	 * metoda, ki izracuna sliko
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 */
	public void narisi(){

		if (okno.izbiraFraktala.getSelectedItem()=="Juliajeva mnozica") {
			narisiJulia();
		}
		if (okno.izbiraFraktala.getSelectedItem()=="Mandelbrotova mnozica") {
			narisiMandelbrot();
		}
	}
	
	public void narisiJulia(){
		slika = new BufferedImage(sirina, visina, BufferedImage.TYPE_BYTE_BINARY);
		for (int x=0; x < sirina; x++){
			for (int y=0; y < visina; y++){
				// izracuna kompleksni koordinati tocke
				Vector<Double> koordinati = kompleksneKoordinate(x, y);
				double a = koordinati.get(0);
				double b = koordinati.get(1);
				// izracuna barvo
				double real = (double) okno.realC.getValue();
				double imag = (double) okno.imagC.getValue();
				int barva = dolociBarvoJulia(a, b, new Complex(real, imag));
				// nastavi pikslu barvo
				slika.setRGB(x, y, barva);
			}
		}
		repaint();
	}
	
	public void narisiMandelbrot(){
		slika = new BufferedImage(sirina, visina, BufferedImage.TYPE_BYTE_BINARY);
		for (int x=0; x < sirina; x++){
			for (int y=0; y < visina; y++){
				// izracuna kompleksni koordinati tocke
				Vector<Double> koordinati = kompleksneKoordinate(x, y);
				double a = koordinati.get(0);
				double b = koordinati.get(1);
				// izracuna barvo
				int barva = dolociBarvoMandelbrot(new Complex(a, b));
				// nastavi pikslu barvo
				slika.setRGB(x, y, barva);
			}
		}
		repaint();
	}
	
		
	/**
	 * pikslu doloci barvo - crna, ce gre tocka v neskoncnost 
	 * oz. bela, ce tocka med iteriranjem ne gre v neskoncnost
	 * @param a prva kompleksna koordinata tocke
	 * @param b druga kompleksna koordinata tocke
	 * @param c konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return barva piksla
	 */
	public int dolociBarvoJulia(double a, double b, Complex c){
		Complex z = new Complex(a, b);
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		for (int j=0; j <= maxIteration; j++){
			if (z.mod() > 10) {
				return Color.BLACK.getRGB();
			}
			else {
				z = (z.times(z)).plus(c);
			}
		}
		return Color.WHITE.getRGB();
	}
	
	/**
	 * pikslu doloci barvo - crna, ce gre tocka (0,0) pri konstanti c v neskoncnost 
	 * oz. bela, ce tocka (0,0) med iteriranjem ne gre v neskoncnost
	 * @param c tocka v kompleksni ravnini = konstanta v iteraciji z_{n+1} = z_{n}^2 + c
	 * @return barva piksla
	 */
	public int dolociBarvoMandelbrot(Complex c){
		Complex z = new Complex(0, 0);
		maxIteration = Integer.parseInt(okno.maxIteracij.getText());
		for (int j=0; j <= maxIteration; j++){
			if (z.mod() > 10) {
				return Color.BLACK.getRGB();
			}
			else {
				z = (z.times(z)).plus(c);
			}
		}
		return Color.WHITE.getRGB();
	}
	
		
	/**
	 * obmocje platna sirina*visina spremeni v obmocje [-2,2]x[-2i, 2i]
	 * @param x prva koordinata tocke v platnu
	 * @param y druga koordinata tocke v platnu
	 * @return vrne kompleksni koordinati tocke
	 */
	public Vector<Double> kompleksneKoordinate(int x, int y){
		double a = 4*(double)x/sirina - 2;
		double b = 2 - 4*(double)y/visina;
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
