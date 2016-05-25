import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class DodatnoOkno extends JFrame {
	
	protected Okno okno;
	protected MiniPlatno platno;
	protected double real, imag;

	public DodatnoOkno(double real, double imag, Okno okno) throws HeadlessException, InterruptedException {
		super();
		
		this.real = real;
		this.imag = imag;
		platno = new MiniPlatno(this, okno, 251, 251);
		platno.setBounds(0, 0, 251, 251);
		getContentPane().add(platno);
		platno.narisiMiniJulia(real, imag);
		platno.setLayout(null);

		
		JButton btnPovecaj = new JButton("Povecaj");
		btnPovecaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okno.izbiraFraktala.setSelectedItem(okno.getJulia());
				okno.realC.setText(Double.toString(real));
				okno.imagC.setText(Double.toString(imag));
				try {
					okno.platno.sirinaKR = 4;
					okno.platno.visinaKR = 4;
					okno.platno.izhodisceX = 250;
					okno.platno.izhodisceY = 250;
					okno.platno.popravekX = 0;
					okno.platno.popravekY = 0;
					okno.platno.narisi();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		btnPovecaj.setBounds(160, 255, 80, 25);;
		platno.add(btnPovecaj);
		
		String konstanta = String.format("c: %.3f + %.3fi", real, imag);
		JLabel lblC = new JLabel(konstanta);
		lblC.setBounds(5, 255, 155, 25);
		platno.add(lblC);

	}
}
