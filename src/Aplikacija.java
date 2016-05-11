
public class Aplikacija {
	
	public static void main(String[] args) {
		Okno okno = new Okno();
		okno.pack();
		okno.setVisible(true);
		okno.platno.narisi(new Complex(-0.7589, 0));
	}

}
