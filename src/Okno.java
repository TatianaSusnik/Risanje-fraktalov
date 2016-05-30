import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import java.awt.FileDialog;


@SuppressWarnings("serial")
public class Okno extends JFrame {
	
	protected Platno platno;
	protected JTextField realC, imagC, maxIteracij;
	protected JComboBox<String> izbiraFraktala; 
	private JComboBox<String> izbiraBarv;
	private String julia, mandelbrot, crnoBelo1, crnoBelo2, sivo, barva1, barva2, barva3, barva4;
	private JRadioButton rdbtnObKliku1, rdbtnObKliku2;
	private static JFrame frame;
	static private List<String> koncnice = Arrays.asList("png", "jpg", "jpeg", "gif", "PNG", "JPG", "JPEG", "GIF");
	
	
	public Okno(){
		super();
		setTitle("Fraktali");
		
		// platno na katerega se izrise fraktal
		platno = new Platno(this, 500, 500);
		platno.setBounds(10, 23, 500, 500);
		getContentPane().add(platno);
		platno.setLayout(null);
		
		JLabel lblKonstanta = new JLabel("izberi kompleksno stevilo:");
		lblKonstanta.setBounds(553, 130, 172, 20);
		platno.add(lblKonstanta);
		
		JLabel lblC = new JLabel("c: ");
		lblC.setBounds(553, 155, 30, 20);
		platno.add(lblC);
		
		// izbira realne komponente konstante c v iteraciji z_{n+1} = z_{n}^2 + c
		realC = new JTextField("0");
		realC.setBounds(583, 155, 50, 20);
		platno.add(realC);
		
		JLabel lblPlus = new JLabel(" +   i*");
		lblPlus.setBounds(645, 155, 30, 20);
		platno.add(lblPlus);
		
		
		// izbira imaginarne komponente konstante c v iteraciji z_{n+1} = z_{n}^2 + c
		imagC = new JTextField("0");
		imagC.setBounds(675, 155, 50, 20);
		platno.add(imagC);
		
		
		// gumb za risanje fraktala z novimi parametri
		JButton btnNarisi = new JButton("Narisi");
		btnNarisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					// parametre nastavi na zacetno stanje:
					// gledamo obmocje [-2, 2] x [-2i, 2i]
					platno.sirinaKR = 4;
					platno.visinaKR = 4;
					platno.sredisceX = 0;
					platno.sredisceY = 0;
					platno.narisi();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		btnNarisi.setBounds(553, 425, 80, 25);
		platno.add(btnNarisi);
		getRootPane().setDefaultButton(btnNarisi);
		
		JLabel lblMaxIteracij = new JLabel("maksimalno stevilo iteracij:");
		lblMaxIteracij.setBounds(553, 205, 172, 20);
		platno.add(lblMaxIteracij);
		
		
		//izbira maksimalnega stevila iteracij
		maxIteracij = new JTextField("150");
		maxIteracij.setBounds(553, 230, 90, 20);
		platno.add(maxIteracij);
		
		
		JLabel lblFraktal = new JLabel("fraktal:");
		lblFraktal.setBounds(553, 55, 172, 20);
		platno.add(lblFraktal);
		
		
		// izbira fraktala
		setJulia("Juliajeva mnozica");
		setMandelbrot("Mandelbrotova mnozica");
		String[] fraktali = new String[] {getJulia(), getMandelbrot()};
		izbiraFraktala = new JComboBox<String>(fraktali);
		izbiraFraktala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (izbiraFraktala.getSelectedItem()==mandelbrot) {
					// konstanta ni potrebna
					realC.setEnabled(false);
					imagC.setEnabled(false);
					// ok kliku sta dve moznosti
					getRdbtnObKliku1().setEnabled(true);
					getRdbtnObKliku2().setEnabled(true);
				}
				else if (izbiraFraktala.getSelectedItem()==julia) {
					// potrebuje konstanto
					realC.setEnabled(true);
					imagC.setEnabled(true);
					// ok kliku ni izbire (se lahko samo poveca)
					getRdbtnObKliku1().setSelected(true);
					getRdbtnObKliku1().setEnabled(false);
					getRdbtnObKliku2().setEnabled(false);
				}
			}
		});
		izbiraFraktala.setBounds(553, 80, 172, 20);
		platno.add(izbiraFraktala);
		
		JLabel lblBarve = new JLabel("barvna izbira:");
		lblBarve.setBounds(553, 280, 172, 20);
		platno.add(lblBarve);
		
		// izbira barve opcije
		setCrnoBelo1("navadno crno-belo");
		setSivo("sivo");
		setCrnoBelo2("crno-belo 2");
		setBarva1("barva 1");
		setBarva2("barva 2");
		setBarva3("barva 3");
		setBarva4("barva 4");
		String[] barvneOpcije = new String[] {getCrnoBelo1(), getSivo(),getBarva1(), getBarva2(), getBarva3(), getBarva4(), getCrnoBelo2()};
		setIzbiraBarv(new JComboBox<String>(barvneOpcije));
		getIzbiraBarv().setBounds(553, 305, 172, 20);
		platno.add(getIzbiraBarv());
		
		
		// gumb za shranjevanje slike
		JButton btnShrani = new JButton("Shrani");
		btnShrani.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// zapomni si sliko
				BufferedImage image = platno.getSlika();				
				try{
					// okno za izbiro lokacije, kamor bomo shranili sliko, in imena slike
				    FileDialog fDialog = new FileDialog(frame,"Shrani", FileDialog.SAVE);
				    // nastavi privzeto ime slike
				    if (!platno.getJeMandelbrot()) {
				    	// privzeto ime Juliajeve mnozice vsebuje konstanto c
				    	String juliaIme = "";
				        if (Double.parseDouble(imagC.getText())<0) {
				        	juliaIme = String.format("Julia%.3f%.3fi.png", Double.parseDouble(realC.getText()), Double.parseDouble(imagC.getText()));
				        }
				        else {
				            juliaIme = String.format("Julia%.3f+%.3fi.png", Double.parseDouble(realC.getText()), Double.parseDouble(imagC.getText()));
				        }
					    fDialog.setFile(juliaIme);
				    }
				    if (platno.getJeMandelbrot()) {
				    	fDialog.setFile("Mandelbrot.png");
				    }
				    fDialog.setVisible(true);
				    // preveri, ce ima ime koncnico, ki ustreza formatu slike
				    // ce je nima, doda koncnico .png
				    if (fDialog.getFile() != null) {
					    String[] koncnica = fDialog.getFile().split("\\.");
					    String path;
					    if (!koncnice.contains(koncnica[koncnica.length-1])){
					    	path = fDialog.getDirectory()+fDialog.getFile()+".png";
				        }
				        else {
				            path = fDialog.getDirectory()+fDialog.getFile();
				        }
					File f = new File(path);
					// shrani sliko
				    ImageIO.write(image,"png", f);
				    }
				}
				                
				catch(Exception ex){
				    ex.printStackTrace();
				}
			}
		});
		btnShrani.setBounds(645, 425, 80, 25);
		platno.add(btnShrani);
		
		JLabel lblKlik = new JLabel("Ob kliku:");
		lblKlik.setBounds(553, 345, 172, 20);
		platno.add(lblKlik);
				
		// izbira ali se ob kliku na izrisano Mandelbrotovo mnozico 
		// pribliza fraktal ali se odpre okno s pripadajoco Juliajevo mnozico
		
		// moznost za priblizevanje/oddaljevanje
		setRdbtnObKliku1(new JRadioButton("priblizevanje / oddaljevanje"));
		getRdbtnObKliku1().setBounds(553, 365, 192, 20);
		platno.add(getRdbtnObKliku1());
		getRdbtnObKliku1().setSelected(true);
		
		// moznost za prikaz miniJulia
		setRdbtnObKliku2(new JRadioButton("narisi miniJulia"));
		getRdbtnObKliku2().setBounds(553, 385, 172, 20);
		platno.add(getRdbtnObKliku2());
		
		// ker je na zacetku izrisana Juliajeva mnozica, je izbiranje onemogoceno
		getRdbtnObKliku1().setEnabled(false);
		getRdbtnObKliku2().setEnabled(false);
		
		// zdruzimo obe moznosti
		ButtonGroup group = new ButtonGroup();
        group.add(getRdbtnObKliku1());
        group.add(getRdbtnObKliku2());
		
        // ce zapremo glavno okno, se morajo zapreti tudi vsa majhna okna
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		            System.exit(0);
		}
		});
		
	}
	

	public String getJulia() {
		return julia;
	}


	public void setJulia(String julia) {
		this.julia = julia;
	}


	public String getMandelbrot() {
		return mandelbrot;
	}


	public void setMandelbrot(String mandelbrot) {
		this.mandelbrot = mandelbrot;
	}



	public JComboBox<String> getIzbiraBarv() {
		return izbiraBarv;
	}


	public void setIzbiraBarv(JComboBox<String> izbiraBarv) {
		this.izbiraBarv = izbiraBarv;
	}


	public JRadioButton getRdbtnObKliku2() {
		return rdbtnObKliku2;
	}


	public void setRdbtnObKliku2(JRadioButton rdbtnObKliku2) {
		this.rdbtnObKliku2 = rdbtnObKliku2;
	}


	public JRadioButton getRdbtnObKliku1() {
		return rdbtnObKliku1;
	}


	public void setRdbtnObKliku1(JRadioButton rdbtnObKliku1) {
		this.rdbtnObKliku1 = rdbtnObKliku1;
	}


	public String getCrnoBelo1() {
		return crnoBelo1;
	}


	public void setCrnoBelo1(String crnoBelo1) {
		this.crnoBelo1 = crnoBelo1;
	}


	public String getSivo() {
		return sivo;
	}


	public void setSivo(String sivo) {
		this.sivo = sivo;
	}


	public String getCrnoBelo2() {
		return crnoBelo2;
	}


	public void setCrnoBelo2(String crnoBelo2) {
		this.crnoBelo2 = crnoBelo2;
	}
	

	public String getBarva1() {
		return barva1;
	}


	public void setBarva1(String barva1) {
		this.barva1 = barva1;
	}


	public String getBarva2() {
		return barva2;
	}


	public void setBarva2(String barva2) {
		this.barva2 = barva2;
	}


	public String getBarva3() {
		return barva3;
	}


	public void setBarva3(String barva3) {
		this.barva3 = barva3;
	}


	public String getBarva4() {
		return barva4;
	}


	public void setBarva4(String barva4) {
		this.barva4 = barva4;
	}



}
