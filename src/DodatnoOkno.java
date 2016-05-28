import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class DodatnoOkno extends JFrame {
	
	private MiniPlatno platno;
	
	
	public DodatnoOkno(double real, double imag, Okno okno) throws HeadlessException, InterruptedException {
		super();
		
		// platno na katerega se izrise Juliajeva mnozica z izbrano konstanto c
		platno = new MiniPlatno(okno, 251, 251);
		platno.setBounds(0, 0, 251, 251);
		getContentPane().add(platno);
		platno.narisiMiniJulia(real, imag);
		platno.setLayout(null);

		// gumb, ki povzroci, da se narisani fraktal izrise na platno v glavnem oknu
		JButton btnPovecaj = new JButton("Povecaj");
		btnPovecaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// nastavi parametre, da ustrezajo narisanemu fraktalu
				okno.izbiraFraktala.setSelectedItem(okno.getJulia());
				okno.realC.setText(Double.toString(real));
				okno.imagC.setText(Double.toString(imag));
				try {
					// nastavi na obmocje [-2, 2] x [-2i, 2i]
					// in narise fraktal
					okno.platno.sirinaKR = 4;
					okno.platno.visinaKR = 4;
					okno.platno.sredisceX = 0;
					okno.platno.sredisceY = 0;
					okno.platno.narisi();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		btnPovecaj.setBounds(160, 255, 80, 25);;
		platno.add(btnPovecaj);
		
		// oknu doda napis konstante, ki priprada fraktalu
		String konstanta = String.format("c: %.3f + %.3fi", real, imag);
		JLabel lblC = new JLabel(konstanta);
		lblC.setBounds(5, 255, 155, 25);
		platno.add(lblC);

	}
}
