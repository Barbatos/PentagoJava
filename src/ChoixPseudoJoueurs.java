import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;


public class ChoixPseudoJoueurs implements ActionListener {

	private JPanel panel;
	private JFrame window;
	private Jeu jeu;

	private JTextField pseudoJ1;
	private JTextField pseudoJ2;
	private JButton boutonOK;

	/**
	 * Initialisation de la fenêtre de choix du pseudo des joueurs.
	 */
	public ChoixPseudoJoueurs(Jeu j, JFrame f, JPanel p) {
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
		Scanner s = new Scanner(System.in);
		int limite = Jeu.NOMBRE_JOUEURS;

		// Si on fait du joueur contre IA, pas besoin de demander le pseudo de l'IA..
		if(this.jeu.getTypeJeu() == Jeu.TYPE_JOUEUR_VS_IA){
			limite = Jeu.NOMBRE_JOUEURS - 1;

			// On crée le joueur pour l'IA nous-même
			this.jeu.setJoueur(1, new Joueur(1, "Bot", 2));
		}

		// Ensuite on demande au ou aux joueur(s) de rentrer son/leur pseudo
		for(int i = 0; i < limite; i++){
			System.out.println("Joueur "+i+", veuillez choisir un pseudonyme :");
			String result = s.nextLine();
			this.jeu.setJoueur(i, new Joueur(i, result, i+1));
		}

		new Partie(this.jeu, this.window, this.panel, true);
	}

	/**
	 * Affichage en mode GUI.
	 */
	public void drawGUI(){
		panel.removeAll();
		//panel.updateUI();

		JLabel label = new JLabel("Choix pseudo joueurs");
		label.setFont(new Font("Serif", Font.BOLD, 22));
		panel.add(label);

		JLabel j1 = new JLabel("Joueur 1");
		j1.setFont(new Font("Serif", Font.PLAIN, 16));
		panel.add(j1);

		pseudoJ1 = new JTextField();
		pseudoJ1.setFont(new Font("Serif", Font.PLAIN, 16));
		pseudoJ1.setPreferredSize(new Dimension(150, 30));
		pseudoJ1.setForeground(Color.BLACK);

		panel.add(pseudoJ1);

		// Si on ne joue pas contre une IA, on demande au joueur 2 son pseudo aussi
		if(this.jeu.getTypeJeu() != Jeu.TYPE_JOUEUR_VS_IA){
			JLabel j2 = new JLabel("Joueur 2");
			j2.setFont(new Font("Serif", Font.PLAIN, 16));
			panel.add(j2);

			pseudoJ2 = new JTextField();
			pseudoJ2.setFont(new Font("Serif", Font.PLAIN, 16));
			pseudoJ2.setPreferredSize(new Dimension(150, 30));
			pseudoJ2.setForeground(Color.BLACK);

			panel.add(pseudoJ2);
		}

		boutonOK = new JButton("Ok");
		boutonOK.addActionListener(this);
		panel.add(boutonOK);

		window.setContentPane(panel);
		window.pack();
		window.setVisible(true);
	}

	/**
	 * Traitement de l'appui sur les boutons 
	 * et récupération des pseudonymes entrés.
	 */
	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == boutonOK){
			if(this.jeu.getTypeJeu() == Jeu.TYPE_JOUEUR_VS_IA){
				if(!pseudoJ1.getText().equals("")){
					this.jeu.setJoueur(0, new Joueur(0, pseudoJ1.getText(), 1));
					this.jeu.setJoueur(1, new Joueur(1, "Bot", 2));
					new Partie(this.jeu, this.window, this.panel, true);
				} 
				else {
					System.out.println("Il faut remplir les champs !");
				}
			}
			else {
				if(!pseudoJ1.getText().equals("") && !pseudoJ2.getText().equals("")){
					this.jeu.setJoueur(0, new Joueur(0, pseudoJ1.getText(), 1));
					this.jeu.setJoueur(1, new Joueur(1, pseudoJ2.getText(), 2));
					new Partie(this.jeu, this.window, this.panel, true);
				}
				else {
					System.out.println("Il faut remplir les champs !");
				}
			}
		}
	}

}
