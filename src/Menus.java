import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class Menus extends JFrame {

	private static final long serialVersionUID = 42L;
	JPanel panel;
	JFrame window;

	/**
	 * Initialisation de l'interface graphique.
	 */
	public Menus(Jeu j) {

		// Si on est en console, pas besoin d'interface graphique
		if(j.getTypeInterface() == 0)
			return;

		window = new JFrame();

		window.setTitle("Pentago");
		window.pack();
		window.setSize(800, 600);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setLayout(null);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.white);

		panel.setBorder(new EmptyBorder(new Insets(40, 260, 120, 120)));

		window.setContentPane(panel);
		window.setVisible(true);
	}

	public JPanel getPanel(){
		return this.panel;
	}

	public JFrame getWindow(){
		return this.window;
	}

	public static void main(String args[]){

	}
}