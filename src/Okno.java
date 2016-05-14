import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class Okno extends JFrame {
	
	protected Platno platno;
	protected JSpinner realC;
	protected JSpinner imagC;
	protected JTextField maxIteracij;
	
	
	public Okno(){
		super();
		setTitle("Fraktali");
		
		// platno na katerega se izrise fraktal
		platno = new Platno(this, 500, 500);
		platno.setBounds(10, 23, 500, 500);
		getContentPane().add(platno);
		platno.setLayout(null);
		
		JLabel lblC = new JLabel("c: ");
		lblC.setBounds(553, 130, 30, 20);
		platno.add(lblC);
		
		// izbira realne komponente konstante c v iteraciji z_{n+1} = z_{n}^2 + c
		realC = new JSpinner();
		realC.setModel(new SpinnerNumberModel(new Double(0.00), null, null, new Double(0.01)));
		realC.setBounds(583, 130, 50, 20);
		platno.add(realC);
		
		JLabel lblPlus = new JLabel(" +   i*");
		lblPlus.setBounds(645, 130, 30, 20);
		platno.add(lblPlus);
		
		
		// izbira imaginarne komponente konstante c v iteraciji z_{n+1} = z_{n}^2 + c
		imagC = new JSpinner();
		imagC.setModel(new SpinnerNumberModel(new Double(0.00), null, null, new Double(0.01)));
		imagC.setBounds(675, 130, 50, 20);
		platno.add(imagC);
		
		
		JButton btnNarisi = new JButton("Narisi");
		btnNarisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				platno.narisi();
			}
		});
		btnNarisi.setBounds(625, 400, 100, 25);
		platno.add(btnNarisi);
		
		JLabel lblMaxIteracij = new JLabel("maxIteracij:");
		lblMaxIteracij.setBounds(553, 180, 80, 20);
		platno.add(lblMaxIteracij);
		
		maxIteracij = new JTextField("150");
		maxIteracij.setBounds(635, 180, 90, 20);
		platno.add(maxIteracij);

	}
}
