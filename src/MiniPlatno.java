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
	protected Okno okno;
	protected DodatnoOkno dodatnoOkno;
	
	
	public MiniPlatno(DodatnoOkno o, Okno oo, int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
		dodatnoOkno = o;
		okno = oo;
	}
	
	
	public Dimension getPreferredSize(){
		return new Dimension(sirina, visina);
	}
	
	
	public void narisiMiniJulia(double real, double imag){
		slika = new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_RGB);
		Color color = null;
		int iteracije=0;
		for (int x=0; x < sirina; x++){
			for (int y=0; y < visina; y++){
				// izracuna kompleksni koordinati tocke
				Vector<Double> koordinati = kompleksneKoordinate(x, y);
				double a = koordinati.get(0);
				double b = koordinati.get(1);
				// izracuna barvo
				maxIteration = Integer.parseInt(okno.maxIteracij.getText());
				if (okno.izbiraBarv.getSelectedItem()==okno.getCrnoBelo1()) {
					iteracije = steviloIteracijJulia(a, b, new Complex(real, imag));
					if (iteracije >= maxIteration) {
						color = new Color(255, 255, 255);
					}
					else {
						color = new Color(0, 0, 0);
					}
				}
				if (okno.izbiraBarv.getSelectedItem()==okno.getSivo()) {
					iteracije = steviloIteracijJulia(a, b, new Complex(real, imag));
					int barva = (int)(255-(Math.sqrt((double)iteracije/maxIteration)*255));
					color = new Color(barva, barva, barva);
				}
				if (okno.izbiraBarv.getSelectedItem()==okno.getBarva1()) {
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
				
				if (okno.izbiraBarv.getSelectedItem()==okno.getCrnoBelo2()) {
					int barva = dolociBarvoJuliaCrnoBelo(a, b,new Complex(real, imag));
					color = new Color(barva, barva, barva);
				}
				// nastavi pikslu barvo
				slika.setRGB(x, y, color.getRGB());
			}
		}
		repaint();
	}
	
		
	/**
	 * pikslu doloci stevilo iteracij, ki so potrebne, da gre tocka c v neskoncnost
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