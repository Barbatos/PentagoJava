import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Partie implements ActionListener {

	private JFrame window;
	private JPanel panel;
	private Jeu jeu;
	private Utils utils;
	
	private JButton[][] listeBoutonsCases;
	private JPanel grilleTabliers;
	private JButton[][] boutonsTournerTabliers;
	
	private JMenuBar barreMenus;
	private JMenu menuJeu;
	private JMenuItem menuRetourArriere, menuNouvellePartie, menuChargerPartie, menuEnregistrerPartie, menuQuitter;
	
	GridBagConstraints contrainteGridBag;

	/**
	 * Crée la fenêtre (GUI) et lance la partie.
	 *
	 * @param  nouvellePartie  mettre à true si c'est une nouvelle partie. Sinon, 
	 * 						   c'est que l'on a chargé une partie.
	 */
	public Partie(Jeu j, JFrame f, JPanel p, boolean nouvellePartie) {
		this.jeu = j;
		this.window = f;
		this.panel = p;
		this.utils = new Utils();
		
		if(nouvellePartie)
			this.jeu.nouvellePartie();
		
		// Affichage de l'interface graphique
		if(this.jeu.getTypeInterface() == 1){
			window.setSize(800, 600);
			window.setResizable(false);
			window.setVisible(true);
			drawGUI();
		}

		// Affichage en console
		else {
			drawCUI();
		}
	}
	
	/**
	 * Affichage et traitement du jeu en mode console.
	 */
	public void drawCUI(){
		boolean ok = false;
		int numTablier = -1, numCase = -1, sensRotation = -1;
		
		System.out.println("\nTour "+(this.jeu.getNbTours()+1));
		System.out.println(this.jeu.getJoueur(this.jeu.getJoueurActuel()).getPseudo()+", c'est votre tour.\n");
		
		// On propose au début d'un tour si on veut enregistrer la partie et quitter
		if((this.jeu.getNbTours() >= 1) && (this.jeu.getJoueurActuel() == 0)){
			System.out.println("\nVoulez-vous enregistrer la partie et quitter ?");
			System.out.println("0: non - 1: oui");
			int saveAndQuit = this.utils.demandeNombre(0, 1);

			if(saveAndQuit == 1){
				enregistrerPartie();
				this.jeu.quitterJeu();
			}
		}
		
		// S'il y a eu au moins un tour de fait, on demande au joueur s'il veut
		// effectuer un retour en arrière. On ne demande pas aux bots indeed
		if(!this.jeu.joueurActuelEstUnBot() && (this.jeu.getNbTours() >= 1)){
			System.out.println("\nVoulez-vous retourner en arriere ?");
			System.out.println("0: non - 1: oui");
			int retourArriere = this.utils.demandeNombre(0, 1);

			// Si le joueur veut retourner en arrière, on lui demande de combien de tours
			if(retourArriere == 1){
				System.out.println("\nDe combien de tours voulez-vous revenir en arriere ?");
				int nbToursRetour = this.utils.demandeNombre(1, this.jeu.getNbTours());
				this.jeu.retourArriere(nbToursRetour);
			}
		}
		
		// On demande au joueur de choisir un tablier et une case
		// Tant qu'il n'a pas effectué de requête valide, on boucle
		while(!ok){
			
			// On commence par afficher le plateau
			this.afficherPlateauCUI();
	
			// On demande le numéro du tablier au joueur
			System.out.println("Tapez le numero du tablier dans lequel vous voulez mettre votre bille (de 0 a 3):");
			numTablier = this.utils.demandeNombre(0, 3);
	
			// Maintenant on demande la case
			numCase = this.jeu.demandeNumeroCase(numTablier);
			
			// Si la case a bien été jouée, on peut passer à la suite
			// Sinon, on boucle..
			if(this.jeu.jouerCase(numTablier, numCase))
				ok = true;
		}
		
		// Une fois la case jouée, on réaffiche le plateau
		this.afficherPlateauCUI();
		
		// On demande dans quel sens le joueur veut faire tourner le tablier
		System.out.println("\nDans quel sens voulez-vous tourner le tablier ?");
		System.out.println("1: horaire - 2: anti-horaire");
		sensRotation = this.utils.demandeNombre(1, 2);

		// On tourne le tablier
		// et si on a trouvé un gagnant...
		if(this.jeu.tournerTablier(numTablier, sensRotation)){
			System.out.println("Le joueur "+this.jeu.getJoueur(this.jeu.getJoueurGagnant()).getPseudo()+" a gagne la partie !");
			new Jeu();
		} 
		
		// Sinon, pas de gagnant trouvé
		else {
			
			// C'est reparti pour un tour..
			drawCUI();
		}
		
	}
	
	/**
	 * Affichage de l'interface graphique.
	 */
	public void drawGUI(){
		
		panel.removeAll();
		
		afficherMenus();
		
		panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.LIGHT_GRAY);
		contrainteGridBag = new GridBagConstraints();
		
		JPanel panelHaut = new JPanel();
		panelHaut.setLayout(new BoxLayout(panelHaut, BoxLayout.Y_AXIS));
		panelHaut.setBackground(Color.LIGHT_GRAY);
		
		JLabel labelTour = new JLabel("Tour "+(this.jeu.getNbTours()+1));
		labelTour.setFont(new Font("Serif", Font.BOLD, 22));
		
		contrainteGridBag.fill = GridBagConstraints.HORIZONTAL;
		contrainteGridBag.gridx = 0;
		contrainteGridBag.gridy = 0;
		contrainteGridBag.gridwidth = 50;
		contrainteGridBag.ipadx = 1;
		panelHaut.add(labelTour, contrainteGridBag);
		
		JLabel labelTourDe = new JLabel("C'est a "+this.jeu.getJoueur(this.jeu.getJoueurActuel()).getPseudo()+" de jouer !");
		labelTourDe.setFont(new Font("Serif", Font.BOLD, 22));
		
		contrainteGridBag.fill = GridBagConstraints.HORIZONTAL;
		contrainteGridBag.gridx = 0;
		contrainteGridBag.gridy = 1;
		contrainteGridBag.gridwidth = 50;
		panelHaut.add(labelTourDe, contrainteGridBag);
		
		panel.add(panelHaut);
		
		afficherPlateau();
	}
	
	/**
	 * Affichage du plateau de jeu en mode console.
	 */
	public void afficherPlateauCUI(){
		System.out.println("Plateau :");
		System.out.println("- - -   - - -");

		// Affichage des 4 plateaux, le plateau 0 et le plateau 1 étant côte à côte.
		// Idem pour les plateaux 2 et 3.
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 3; j++){
				for(int k = 0; k < 3; k++){
					System.out.print(this.jeu.getCases()[(i*2)][(j*3)+k].getType()+" ");
				}
				System.out.print("  ");
				for(int k = 0; k < 3; k++){
					System.out.print(this.jeu.getCases()[(i*2)+1][(j*3)+k].getType()+" ");
				}

				System.out.println("");
			}
			System.out.println("- - -   - - -");
		}
	}

	/**
	 * Affichage de la liste des joueurs en mode console.
	 */
	public void afficherJoueursCUI(){
		System.out.println("\nListe des joueurs : ");

		for(int i = 0; i < 2; i++){
			System.out.println("Joueur "+this.jeu.getJoueurs()[i].getNumero()+": "+this.jeu.getJoueurs()[i].getCouleur()+" - "+this.jeu.getJoueurs()[i].getPseudo());
		}
	}
	
	/**
	 * Affichage du plateau en mode GUI.
	 */
	public void afficherPlateau(){
		listeBoutonsCases = new JButton[4][9];
		grilleTabliers = new JPanel();
		boutonsTournerTabliers = new JButton[4][2];
		
		grilleTabliers.setLayout(new GridLayout(2,2, 5, 5));
		
		// Ajout des boutons pour pouvoir tourner les tabliers
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++){
				if(b == 1)
					boutonsTournerTabliers[a][b] = new JButton("==>");
				else
					boutonsTournerTabliers[a][b] = new JButton("<==");
				
				boutonsTournerTabliers[a][b].setPreferredSize(new Dimension(100, 50));
				boutonsTournerTabliers[a][b].addActionListener(this);
				
				contrainteGridBag.fill = GridBagConstraints.HORIZONTAL;
				
				if(a == 1)
					contrainteGridBag.gridx = b+18;
				else
					contrainteGridBag.gridx = b;
				
				contrainteGridBag.gridy = 2;
				contrainteGridBag.gridwidth = 1;
				panel.add(boutonsTournerTabliers[a][b], contrainteGridBag);
			}
			
		}
		
		for(int i = 0; i < 4; i++){
			JPanel sousGrilleTablier = new JPanel();
			sousGrilleTablier.setLayout(new GridLayout(3,3));
			
			// Ajout des cases du tablier
			for(int j = 0; j < 9; j++){
				Case[][] cases = this.jeu.getCases();
				
				listeBoutonsCases[i][j] = new JButton("");
				listeBoutonsCases[i][j].setPreferredSize(new Dimension(50, 50));
				
				if(cases[i][j].getCouleur() == Case.COULEUR_VIDE){
					listeBoutonsCases[i][j].setBackground(Color.GRAY);
					listeBoutonsCases[i][j].setForeground(Color.BLACK);
				} 
				else if(cases[i][j].getCouleur() == Case.COULEUR_BLANC){
					listeBoutonsCases[i][j].setBackground(Color.WHITE);
					listeBoutonsCases[i][j].setForeground(Color.BLACK);
				} 
				else {
					listeBoutonsCases[i][j].setBackground(Color.BLACK);
					listeBoutonsCases[i][j].setForeground(Color.WHITE);
				}
				
				listeBoutonsCases[i][j].addActionListener(this);
				sousGrilleTablier.add(listeBoutonsCases[i][j]);
				grilleTabliers.add(sousGrilleTablier);
			}
		}
		
		contrainteGridBag.fill = GridBagConstraints.HORIZONTAL;
		contrainteGridBag.gridx = 0;
		contrainteGridBag.gridy = 3;
		contrainteGridBag.gridwidth = 20;
		contrainteGridBag.ipadx = 1;
		contrainteGridBag.ipady = 120;
		panel.add(grilleTabliers, contrainteGridBag);
		
		contrainteGridBag.ipadx = 90;
		contrainteGridBag.ipady = 1;
		
		// Ajout des boutons pour pouvoir tourner les tabliers
		for(int a = 2; a < 4; a++){
			for(int b = 0; b < 2; b++){
				if(b == 1)
					boutonsTournerTabliers[a][b] = new JButton("==>");
				else
					boutonsTournerTabliers[a][b] = new JButton("<==");
				
				boutonsTournerTabliers[a][b].setPreferredSize(new Dimension(100, 50));
				boutonsTournerTabliers[a][b].addActionListener(this);
				
				contrainteGridBag.fill = GridBagConstraints.HORIZONTAL;
				if(a == 2)
					contrainteGridBag.gridx = b;
				else
					contrainteGridBag.gridx = b+18;
				
				contrainteGridBag.gridy = 4;
				contrainteGridBag.gridwidth = 1;
				panel.add(boutonsTournerTabliers[a][b], contrainteGridBag);
			}
		}
		window.setContentPane(panel);
        window.pack();
	}
	
	/**
	 * Affichage des menus de la GUI.
	 */
	public void afficherMenus(){
		barreMenus = new JMenuBar();
		
		menuJeu = new JMenu("Jeu");
		barreMenus.add(menuJeu);
		
		menuNouvellePartie = new JMenuItem("Nouvelle partie");
		menuChargerPartie = new JMenuItem("Charger partie");
		menuEnregistrerPartie = new JMenuItem("Enregistrer la partie");
		menuRetourArriere = new JMenuItem("Retour en arrière");
		menuQuitter = new JMenuItem("Quitter :(");
		
		menuNouvellePartie.addActionListener(this);
		menuChargerPartie.addActionListener(this);
		menuRetourArriere.addActionListener(this);
		menuEnregistrerPartie.addActionListener(this);
		menuQuitter.addActionListener(this);
		
		menuJeu.add(menuNouvellePartie);
		menuJeu.add(menuChargerPartie);
		menuJeu.add(menuEnregistrerPartie);
		menuJeu.add(menuRetourArriere);
		menuJeu.add(menuQuitter);
		
		this.window.setJMenuBar(barreMenus);
	}

	/**
	 * Enregistrement d'une partie.
	 * 
	 * Cette fonction est utilisée à la fois par la GUI
	 * et par la console.
	 */
	public void enregistrerPartie(){
		if(this.jeu.getJoueurActuel() != 0){
			System.out.println("Veuillez finir un tour avant d'enregistrer la partie!");
			
			if(this.jeu.getTypeInterface() >= 1)
				JOptionPane.showMessageDialog(this.window, "Veuillez finir un tour avant d'enregistrer la partie !");
			return;
		}
		
		if(this.jeu.enregistrerPartie(this.jeu.getJoueurs(), this.jeu.getCases())){
			if(this.jeu.getTypeInterface() >= 1)
				JOptionPane.showMessageDialog(this.window, "Partie enregistree avec succes !");
		}
		else {
			if(this.jeu.getTypeInterface() >= 1)
				JOptionPane.showMessageDialog(this.window, "Impossible d'enregistrer la partie !");
		}
	}
	
	/**
	 * Traitement des évènements GUI (appui sur bouton, choix dans une liste...).
	 * 
	 * @param action  informations sur l'action effectuée.
	 */
	public void actionPerformed(ActionEvent action) {
		
		// Parcours de toutes les cases pour voir si une case a été cliquée
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 9; j++){
				// Si le joueur a cliqué sur une case
				if(action.getSource() == listeBoutonsCases[i][j]){
					
					// On joue la case
					this.jeu.jouerCase(i, j);
					
					// On met à jour la GUI
					drawGUI();
				}
			}
		}
		
		// Parcours de tous les boutons de rotation afin de voir si un a été cliqué
		for(int k = 0; k < 4; k++){
			for(int m = 0; m < 2; m++){
				// Si le joueur a cliqué sur un bouton afin de tourner un tablier
				if(action.getSource() == boutonsTournerTabliers[k][m]){
					
					if(this.jeu.tournerTablier(k, m+1)){
						JOptionPane.showMessageDialog(this.window, "Le joueur "+this.jeu.getJoueur(this.jeu.getJoueurGagnant()).getPseudo()+" a gagne la partie !");
						
						// On ferme cette fenêtre et on réouvre le menu principal
						this.window.dispose();
						new Jeu();
					}
					else {
						drawGUI();
					}
				}
			}
		}
		
		// On regarde si on clique sur un menu
		if(action.getSource() == menuNouvellePartie){
			this.window.dispose();
			Menus menu = new Menus(this.jeu);
			new ChoixTypeJeu(this.jeu, menu.getWindow(), menu.getPanel());
		}
		
		if(action.getSource() == menuChargerPartie){
			this.window.dispose();
			Menus menu = new Menus(this.jeu);
			new MenuChargerPartie(this.jeu, menu.getWindow(), menu.getPanel());
		}
		
		if(action.getSource() == menuEnregistrerPartie){
			enregistrerPartie();
		}
		
		if(action.getSource() == menuQuitter){
			System.exit(0);
		}
		
		if(action.getSource() == menuRetourArriere){
			Object[] listeTours = new Object[this.jeu.getNbTours()+1];
			
			listeTours[0] = "0";
			for(int i = 1; i <= this.jeu.getNbTours(); i++){
				listeTours[i] = i;
			}
			
			int s = -1;
			s = Integer.parseInt(JOptionPane.showInputDialog(
                    this.window,
                    "De combien de tours souhaitez-vous reculer ?",
                    "Retour en arriere",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    listeTours,
                    null).toString());
			
			// Si on a quelque chose
			if ((s != -1) && (s != 0)){
				this.jeu.retourArriere(s);
				drawGUI();
			    return;
			}
		}
	}
}