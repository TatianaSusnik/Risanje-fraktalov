
import java.awt.Dimension;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Platno extends JPanel {
	
	private int sirina;
	private int visina;
	
	public Platno(int sirina, int visina) {
		super();
		this.sirina = sirina;
		this.visina = visina;
	}
	
	
	public Dimension getPreferredSize(){
		return new Dimension(sirina, visina);
	}
	

}
