import javax.swing.JFrame;


@SuppressWarnings("serial")
public class Okno extends JFrame {
	
	protected Platno platno;
	
	public Okno(){
		super();
		setTitle("Fraktali");
		platno = new Platno(500,500);
		add(platno);
	}

}
