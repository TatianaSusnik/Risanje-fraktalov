
public class Aplikacija {
	
	public static void main(String[] args) throws InterruptedException {
		// odpre okno in izrise Juliajevo mnozico s konstanto c = 0
		Okno okno = new Okno();
		okno.pack();
		okno.setVisible(true);
		okno.platno.narisi();
	}

}
