
public class Aplikacija {
	
	public static void main(String[] args) throws InterruptedException {
		Okno okno = new Okno();
		okno.pack();
		okno.setVisible(true);
		okno.platno.narisi();
	}

}
