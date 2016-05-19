import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;


@SuppressWarnings("serial")
public class Okno extends JFrame {
	
	protected Platno platno;
	protected JTextField realC, imagC, maxIteracij;
	protected JComboBox<String> izbiraFraktala, izbiraBarv;
	private String julia, mandelbrot, crnoBelo1, crnoBelo2, sivo, barva1;
	
	
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
		
		
		// gumb za ponovno risanje fraktala z novimi parametri
		JButton btnNarisi = new JButton("Narisi");
		btnNarisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				platno.narisi();
			}
		});
		btnNarisi.setBounds(553, 425, 80, 25);
		platno.add(btnNarisi);
		
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
		izbiraFraktala = new JComboBox(fraktali);
		izbiraFraktala.setBounds(553, 80, 172, 20);
		platno.add(izbiraFraktala);
		
		JLabel lblBarve = new JLabel("barvna izbira:");
		lblBarve.setBounds(553, 280, 172, 20);
		platno.add(lblBarve);
		
		// izbira barve opcije
		setCrnoBelo1("navadno crno-belo");
		setSivo("sivo");
		setCrnoBelo2("crno-belo");
		setBarva1("barva1");
		String[] barvneOpcije = new String[] {getCrnoBelo1(), getSivo(), getCrnoBelo2(), getBarva1()};
		izbiraBarv = new JComboBox(barvneOpcije);
		izbiraBarv.setBounds(553, 305, 172, 20);
		platno.add(izbiraBarv);
		
		
		// gumb za shranjevanje slike
		JButton btnShrani = new JButton("Shrani");
		btnShrani.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 BufferedImage image = platno.getSlika();
		            try{
		            	JFrame shraniFrame = new JFrame();
		            	JFileChooser fileChooser = new JFileChooser();
		            	fileChooser.setDialogTitle("Shrani");
		            	if (izbiraFraktala.getSelectedItem()==julia) {
		            		String juliaIme = "";
		            		if (Double.parseDouble(imagC.getText())<0) {
		            			juliaIme = String.format("C:/Julia%.3f%.3fi", Double.parseDouble(realC.getText()), Double.parseDouble(imagC.getText()));
		            		}
		            		else {
		            			juliaIme = String.format("C:/Julia%.3f+%.3fi", Double.parseDouble(realC.getText()), Double.parseDouble(imagC.getText()));
		            		}
		            		fileChooser.setSelectedFile(new File(juliaIme));
			            	
		            	}
		            	if (izbiraFraktala.getSelectedItem()==mandelbrot) {
		            		fileChooser.setSelectedFile(new File("C:/Mandelbrot"));
		            	}
		            	int userSelection = fileChooser.showSaveDialog(shraniFrame);
		            	if (userSelection == JFileChooser.APPROVE_OPTION) {
		            		File fileToSave = fileChooser.getSelectedFile();
		            		String[] koncnica = fileToSave.getAbsolutePath().split("\\.");
		            		if (koncnica.length == 2) {
		            			ImageIO.write(image,"png", fileToSave);
		            		}
		            		else if (koncnica.length == 1) {
		            			ImageIO.write(image,"png", new File(fileToSave.getAbsoluteFile()+".png"));
		            		}
		            	}
		                }
		            catch(Exception ex){
		                 ex.printStackTrace();
		                }
			}
		});
		btnShrani.setBounds(645, 425, 80, 25);
		platno.add(btnShrani);
		

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


	public String getBarva1() {
		return barva1;
	}


	public void setBarva1(String barva1) {
		this.barva1 = barva1;
	}


	public String getCrnoBelo2() {
		return crnoBelo2;
	}


	public void setCrnoBelo2(String crnoBelo2) {
		this.crnoBelo2 = crnoBelo2;
	}
}
