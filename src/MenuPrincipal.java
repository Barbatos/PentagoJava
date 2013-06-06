import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;


public class MenuPrincipal implements ActionListener {

	private JPanel panel;
	private JFrame window;
	private Jeu jeu;

	private JButton boutonNouvellePartie;
	private JButton boutonChargerPartie;
	private JButton boutonRegles;
	private JButton boutonQuitter;

	/**
	 * Initialisation du menu principal.
	 */
	public MenuPrincipal(Jeu j, JFrame f, JPanel p) {
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
	 * Affichage du menu principal en console.
	 */
	public void drawCUI(){
		Utils utils = new Utils();

		System.out.println("======================================");
		System.out.println("=== Bienvenue sur le jeu Pentago ! ===");
		System.out.println("======================================");
		System.out.println("Menu Principal :");
		System.out.println("	1) Nouvelle partie");
		System.out.println("	2) Charger une partie");
		System.out.println("	3) Regles du jeu");
		System.out.println("	4) Quitter");

		int choix = utils.demandeNombre(1, 4);

		switch(choix){

			// Nouvelle partie
			case 1:
				new ChoixTypeJeu(this.jeu, this.window, this.panel);
				break;

			// Charger une partie
			case 2:
				new MenuChargerPartie(this.jeu, this.window, this.panel);
				break;

			// Règles du jeu
			case 3:
				System.out.println("Les regles du jeu sont simples !\n");
				System.out.println("Le jeu se joue a deux joueurs jouant l'un contre l'autre.");
				System.out.println("Il vous est possible de jouer contre une intelligence artificielle.\n");
				System.out.println("Le but du jeu est de reussir a aligner 5 billes de votre couleur.");
				System.out.println("Vous pouvez faire des alignements en lignes, en colonnes ou encore en diagonales !");
				System.out.println("Votre couleur vous est attribuee automatiquement lors de la selection de votre pseudonyme.");
				System.out.println("Le premier joueur reussissant a faire un alignement a gagne.\n");
				System.out.println("Il vous est possible d'enregistrer une partie en cours puis d'y rejouer plus tard.");
				System.out.println("Vous pouvez aussi a tout moment annuler un ou plusieurs tours de jeu !\n");
				System.out.println("Bon courage et que le meilleur gagne !");
				System.out.println("\nAppuyez sur une touche pour revenir au menu principal...");
				
				Scanner s = new Scanner(System.in);
				if(!s.nextLine().isEmpty())	
					drawCUI();
				
				break;

			// Quitter
			case 4:
			default:
				System.out.println("=== Merci d'avoir joue et a bientot ! ===");
				System.exit(0);
				break;
		}
	}

	/**
	 * Affichage de l'interface graphique du menu principal.
	 */
	public void drawGUI(){
		panel.removeAll();
		panel.updateUI();

		JLabel label = new JLabel("Bienvenue sur le jeu Pentago");
		label.setFont(new Font("Serif", Font.BOLD, 22));
		panel.add(label);

		boutonNouvellePartie = new JButton("Nouvelle partie");
		boutonNouvellePartie.addActionListener(this);

		boutonChargerPartie = new JButton("Charger une partie");
		boutonChargerPartie.addActionListener(this);

		boutonRegles = new JButton("Regles du jeu");
		boutonRegles.addActionListener(this);

		boutonQuitter = new JButton("Quitter");
		boutonQuitter.addActionListener(this);

		panel.add(boutonNouvellePartie);
		panel.add(boutonChargerPartie);
		panel.add(boutonRegles);
		panel.add(boutonQuitter);

		window.setContentPane(panel);
		window.pack();
		window.setVisible(true);
	}

	/**
	 * Traitement de l'appui sur l'un des boutons du menu.
	 */
	public void actionPerformed(ActionEvent action) {

		if(action.getSource() == boutonNouvellePartie) {
			new ChoixTypeJeu(this.jeu, this.window, this.panel);
		} 

		else if(action.getSource() == boutonChargerPartie) {
			new MenuChargerPartie(this.jeu, this.window, this.panel);
		} 

		else if(action.getSource() == boutonRegles) {
			
			JOptionPane.showMessageDialog(
                this.window,
                "Les regles du jeu sont simples !\n" +
                "Le jeu se joue a deux joueurs jouant l'un contre l'autre.\n" +
                "Il vous est possible de jouer contre une intelligence artificielle.\n" +
                "Le but du jeu est de reussir a aligner 5 billes de votre couleur.\n" +
                "Vous pouvez faire des alignements en lignes, en colonnes ou encore en diagonales !\n" +
                "Votre couleur vous est attribuee automatiquement lors de la selection de votre pseudonyme.\n" +
                "Le premier joueur reussissant a faire un alignement a gagne.\n" +
                "Il vous est possible d'enregistrer une partie en cours puis d'y rejouer plus tard.\n" +
                "Vous pouvez aussi a tout moment annuler un ou plusieurs tours de jeu !\n" +
                "Bon courage et que le meilleur gagne !\n",
                "Regles du jeu",
                JOptionPane.PLAIN_MESSAGE);
		} 

		else if(action.getSource() == boutonQuitter) {
			System.exit(0);
		}
	}

}