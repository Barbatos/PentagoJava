import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuChargerPartie extends JFrame implements ActionListener {
	private static final long serialVersionUID = 42L;
	private JPanel panel;
	private JFrame window;
	private JButton boutonCharger;
	private JButton boutonRetour;
	private JList<String> listeFichiers;
	private Jeu jeu;

	/**
	 * Initialisation du menu de chargement d'une partie.
	 */
	public MenuChargerPartie(Jeu j, JFrame f, JPanel p) {
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
	 * Affichage en console de la liste des parties disponibles.
	 */
	public void drawCUI(){
		String[] listeSauv;
		int i;
		Utils utils = new Utils();

		System.out.println("\n=== Chargement d'une partie ===");
		System.out.println("Choisissez la partie a charger :");
		listeSauv = recupererSauvegardes();

		for (i = 0; i < listeSauv.length; i++)
			if(listeSauv[i].endsWith(".ser"))
				System.out.println(" " + i + ") " + listeSauv[i]);

		int choix = utils.demandeNombre(0, (i-1));
		
		if(chargerPartie(listeSauv[choix])){
			new Partie(this.jeu, this.window, this.panel, false);
		}
		else {
			System.out.println("Impossible de charger cette partie. Retour au menu principal");
			new MenuPrincipal(this.jeu, this.window, this.panel);
		}
	}

	/**
	 * Affichage en GUI de la liste des sauvegardes.
	 */
	public void drawGUI(){
		panel.removeAll();
		JLabel label = new JLabel("Choisissez la partie a charger :");
		label.setFont(new Font("Serif", Font.BOLD, 22));
		panel.add(label);

		// On affiche la liste des sauvegardes disponibles
		afficherListeSauvegardes(recupererSauvegardes());
	}

	/**
	 * Récupère tous les fichiers de sauvegardes.
	 * @return 	la liste des fichiers de sauvegarde disponibles.
	 */
	public String[] recupererSauvegardes(){

		// On commence par récupérer la liste de fichiers du répertoire de sauvegarde
		String[] dir = new java.io.File(Jeu.REPERTOIRE_SAUVEGARDE).list(new FilenameFilter() {
			// On ne garde que les fichiers ayant l'extension de sauvegarde (.ser)
			public boolean accept(File dir, String name) {
		    	return name.toLowerCase().endsWith(Jeu.EXTENSION_SAUVEGARDE);
		    }
		});
 
        // On trie les résultats
		java.util.Arrays.sort(dir);

		return dir;
	}
	
	/**
	 * Affichage en GUI de la liste des sauvegardes.
	 * 
	 * @param dir 	la liste des fichiers de sauvegarde.
	 */
	@SuppressWarnings("unchecked")
	public void afficherListeSauvegardes(String[] dir){
		listeFichiers = new JList<String>(dir);
		listeFichiers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listeFichiers.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listeFichiers.setVisibleRowCount(-1);

		// On crée une scrollbar 
		JScrollPane listScroller = new JScrollPane(listeFichiers);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        // On crée un panel afin de pouvoir mettre un titre à notre liste
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Selection de sauvegarde");
        label.setLabelFor(listeFichiers);

        // On ajoute le titre, la liste, et la scrollbar au panel
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Bouton permettant de charger la partie sélectionnée dans la liste
        boutonCharger = new JButton("Charger");
		boutonCharger.addActionListener(this);

		// Bouton permettant un retour au menu principal
		boutonRetour = new JButton("Retour");
		boutonRetour.addActionListener(this);

        // On ajoute le panel au panel général
        panel.add(listPane, BorderLayout.CENTER);
        panel.add(boutonCharger);
        panel.add(boutonRetour);

        // On affiche le tout
        window.setContentPane(panel);
        window.pack();
		window.setVisible(true);
 
	}

	/**
	 * Chargement d'une partie. On utilise la sérialisation.
	 * 
	 * @param file 	le nom du fichier à charger.
	 * @return 		true si la partie a été chargée correctement.
	 */
	@SuppressWarnings("unchecked")
	public boolean chargerPartie(String file){
		List liste = new ArrayList();

		try {
			// On ouvre le fichier qui se trouve dans le répertoire de sauvegarde
			File f = new File(Jeu.REPERTOIRE_SAUVEGARDE + file);

			// Le fichier n'a pas été trouvé
			if(!f.exists()){
				System.out.println("Ce fichier de sauvegarde n'existe pas.");
				return false;
			}

			FileInputStream fichier = new FileInputStream(f);
			ObjectInputStream infos = new ObjectInputStream(fichier);

			// On récupère le contenu du fichier et on le place dans une liste
			liste = (List) infos.readObject();

			// On ferme le fichier
			infos.close();
			fichier.close();
		}
		catch(IOException e){
			System.out.println("Impossible de charger la partie !");
			return false;
		}
		catch(ClassNotFoundException c){
			System.out.println("Erreur lors du chargement de la partie. Classe introuvable !");
			c.printStackTrace();
			return false;
		}

		ListIterator li = liste.listIterator();

		// On récupère les objets qui nous intéressent, à savoir la liste des joueurs 
		// et la liste des cases
		jeu.setCases( (Case[][]) li.next() );
		jeu.setJoueurs( (Joueur[]) li.next() );
		jeu.setNbTours( Integer.parseInt(li.next().toString()) );
		jeu.setListeTours( (List<Case[][]>) li.next() );
		jeu.setTypeJeu( Integer.parseInt( li.next().toString()) );
		jeu.setListeCoups( (List<Coup>) li.next() );

		return true;
	}

	/**
	 * Traitement de l'appui sur l'un des boutons.
	 */
	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == boutonCharger){
			if( chargerPartie( (String)listeFichiers.getSelectedValue() ) ){
				new Partie(this.jeu, this.window, this.panel, false);
			}
		} 
		else if(action.getSource() == boutonRetour){
			new MenuPrincipal(this.jeu, this.window, this.panel);
		}
	}
}
