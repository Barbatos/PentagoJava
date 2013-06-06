import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ChoixTypeJeu implements ActionListener {

	private JPanel panel;
	private JFrame window;
	private Jeu jeu;

	private JButton boutonJoueurvsJoueur;
	private JButton boutonJoueurvsIA;

	/**
	 * Initialisation de la fenêtre de choix du type de jeu.
	 * 
	 * Il peut y avoir deux types de jeu :
	 *  - joueur contre joueur
	 *  - joueur contre IA
	 */
	public ChoixTypeJeu(Jeu j, JFrame f, JPanel p) {
		this.window = f;
		this.panel = p;
		this.jeu = j;

		// Affichage de l'interface graphique
		if(this.jeu.getTypeInterface() == 1){
			drawGUI();
		}

		// Affichage des menus en console
		else {
			drawCUI();
		}
	}

	/**
	 * Affichage du menu en mode console.
	 */
	public void drawCUI(){
		Utils utils = new Utils();

		System.out.println("\n\nVeuillez selectionner le type de jeu:");
		System.out.println("	1) joueur vs joueur");
		System.out.println("	2) joueur vs IA");

		int type = utils.demandeNombre(1, 2);

		this.jeu.setTypeJeu(type);

		new ChoixPseudoJoueurs(this.jeu, this.window, this.panel);
	}

	/**
	 * Affichage du menu en GUI.
	 */
	public void drawGUI(){
		panel.removeAll();
		//panel.updateUI();

		JLabel label = new JLabel("Choix du type de jeu");
		label.setFont(new Font("Serif", Font.BOLD, 22));
		panel.add(label);

		boutonJoueurvsJoueur = new JButton("Joueur contre Joueur");
		boutonJoueurvsJoueur.addActionListener(this);

		boutonJoueurvsIA = new JButton("Joueur contre IA");
		boutonJoueurvsIA.addActionListener(this);

		panel.add(boutonJoueurvsJoueur);
		panel.add(boutonJoueurvsIA);

		window.setContentPane(panel);
		window.pack();
		window.setVisible(true);
	}

	/**
	 * Traitement de l'appui sur un bouton.
	 */
	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == boutonJoueurvsJoueur) {
			this.jeu.setTypeJeu(Jeu.TYPE_JOUEUR_VS_JOUEUR);
			new ChoixPseudoJoueurs(this.jeu, this.window, this.panel);
		} 

		else if(action.getSource() == boutonJoueurvsIA) {
			this.jeu.setTypeJeu(Jeu.TYPE_JOUEUR_VS_IA);
			new ChoixPseudoJoueurs(this.jeu, this.window, this.panel);
		} 
	}

}
