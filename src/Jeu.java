/*
TODO:
- faire une IA un peu plus intelligente
- proposer une liste des meilleurs scores
- corriger le retour en arrière multiple
- faire une fenêtre pour voir la liste des coups joués
*/

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

public class Jeu implements Serializable {

	// Nombre de joueur autorisés, devrait toujours être à 2
	public final static int NOMBRE_JOUEURS = 2;

	// Les sens de rotation des tabliers
	private final static int SENS_HORAIRE = 1;
	private final static int SENS_ANTIHORAIRE = 2;

	// Nom du répertoire où sont les fichiers de sauvegarde
	public final static String REPERTOIRE_SAUVEGARDE = "sauvegardes/";

	// Extension des fichiers de sauvegarde
	public final static String EXTENSION_SAUVEGARDE = ".ser";
	
	// Les différents types de jeux possibles. On a donc le joueur contre joueur 
	// et le joueur contre intelligence artificielle
	public final static int TYPE_JOUEUR_VS_JOUEUR = 1;
	public final static int TYPE_JOUEUR_VS_IA = 2;
	
	// On mémorise à quel stade de jeu est le joueur
	// Il peut y avoir deux stades :
	public final static int STADE_CHOIX_CASE = 0;
	public final static int STADE_CHOIX_SENS = 1;
	
	// Sert à la serialization...
	private static final long serialVersionUID = 42L;
	
	
	private Utils utils;

	// Tableau d'objets contenant les informations des joueurs
	public Joueur listeJoueurs[];

	// Tableau d'objets contenant la liste des tabliers ayant chacun leurs 9 cases
	public Case listeCases[][];

	// Liste contenant pour chaque tour de jeu l'objet listeCases[][] afin de 
	// pouvoir faire des retours en arrière
	List<Case[][]> listeTours;

	// Liste des coups joués
	List<Coup> listeCoups;

	// Mémorisation du joueur ayant actuellement la main
	private int joueurActuel;
	
	private int stadeJeu = STADE_CHOIX_CASE;
	
	// Mémorisation du nombre de tours effectués
	private int nbToursJeu;
	
	// Numéro du joueur gagnant
	private int joueurGagnant = -1;

	// Par défaut, interface console (0)
	private int typeInterface = 0;

	// On stocke le type de jeu, par défaut on fait du joueur vs joueur
	private int typeJeu = TYPE_JOUEUR_VS_JOUEUR;
	
	// Numéro de la dernière case jouée
	private int derniereCaseJouee;

	
	/**
	 * Initialisation du jeu.
	 * 
	 * On demande dans cette fonction de choisir entre le mode console
	 * et le mode GUI, puis on lance le menu principal.
	 */
	public Jeu() {
		listeTours = new ArrayList<Case[][]>();
		listeCoups = new ArrayList<Coup>();
		listeJoueurs = new Joueur[2];
		utils = new Utils();

		System.out.println("\nVoulez-vous lancer le jeu en mode console ou graphique ?");
		System.out.println("0: console - 1: graphique");
		typeInterface = utils.demandeNombre(0, 1);
		
		// On initialise les menus
		Menus menu = new Menus(this);
		
		// On affiche le menu principal
		new MenuPrincipal(this, menu.getWindow(), menu.getPanel());
	}
	
	/**
	 * Lancement d'une nouvelle partie.
	 */
	public void nouvellePartie(){
		// On réinitialise le nombre de tours du jeu
		this.setNbTours(0);
		
		// On réinitialise le joueur gagnant
		this.setJoueurGagnant(-1);
		
		// On initialise toutes les cases
		creerCases();
		
		// On stocke le plateau vide dans la liste de tours afin de pouvoir 
		// effectuer un retour en arrière plus tard sur ce plateau vide
		listeTours.add(copierPlateau());
	}
	
	/**
	 * Effectue l'action de jouer une case.
	 * 
	 * @param tablier  	le numéro du tablier dans lequel on veut jouer la case.
	 * @param numCase  	le numéro de la case à jouer.
	 * @return 			true si la case a bien été jouée, false dans le cas contraire.
	 */
	public boolean jouerCase(int tablier, int numCase){
		
		// Si ce n'est pas le moment de jouer une case...
		if(this.getStadeJeu() != Jeu.STADE_CHOIX_CASE){
			System.out.println("Vous devez choisir un sens de rotation !");
			return false;
		}
		
		// On vérifie que la case ne soit pas déjà utilisée
		if(this.getCases()[tablier][numCase].getCouleur() != Case.COULEUR_VIDE){
			System.out.println("Cette case est deja occupee !");
			return false;
		}
		
		// On attribue la case au joueur
		this.getCases()[tablier][numCase].setCouleur(this.getJoueur(this.getJoueurActuel()).getCouleur());
		this.setStadeJeu(Jeu.STADE_CHOIX_SENS);
		this.derniereCaseJouee = numCase;
		
		return true;
	}
	
	/**
	 * Fait jouer automatiquement l'IA.
	 */
	public void jouerIA(){
		boolean ok = false;
		
		while(!ok){
			// On génère aléatoirement un numéro de tablier et de case
			// TODO: système d'IA intelligente
			int numTablier = this.utils.getNombreAleatoire(0, 3);
			int numCase = this.utils.getNombreAleatoire(0, 8);

			// On vérifie que la case ne soit pas déjà attribuée
			if(this.getCases()[numTablier][numCase].getCouleur() == Case.COULEUR_VIDE){
				// La case est inoccupée, on l'attribue à l'IA
				this.getCases()[numTablier][numCase].setCouleur(this.getJoueur(this.getJoueurActuel()).getCouleur());
				
				// On définit un sens de rotation du tablier
				int sensRotation = this.utils.getNombreAleatoire(1, 2);
				
				// On prépare le terrain pour le vrai joueur
				this.setStadeJeu(Jeu.STADE_CHOIX_SENS);
				this.setDerniereCaseJouee(numCase);
				
				// Et enfin on tourne le tablier
				tournerTablier(numTablier, sensRotation);
				ok = true;
			}
		}
	}
	
	/**
	 * Tourne le tablier.
	 * 
	 * Si le tablier a bien été tourné, on cherche un gagnant et 
	 * on fait jouer l'IA si nécessaire.
	 * 
	 * @param tablier  	numéro du tablier à tourner.
	 * @param sens 		sens de rotation (horaire - trigo).
	 * @return 			true si on a un gagnant.
	 */
	public boolean tournerTablier(int tablier, int sens){
		String sensRotation = "trigonometrique";
		
		// Si ce n'est pas le moment de tourner un tablier...
		if(this.getStadeJeu() != Jeu.STADE_CHOIX_SENS){
			System.out.println("Vous devez d'abord choisir une case !");
			return false;
		}
		
		// On effectue la rotation du tablier
		this.rotationTablier(tablier, sens);
		
		// On ajoute le coup à l'historique des coups joués
		this.getListeCoups().add(new Coup(this.getJoueurActuel(), tablier, this.getDerniereCaseJouee(), sens));

		if(sens == Jeu.SENS_HORAIRE)
			sensRotation = "horaire";
		
		// On affiche le coup joué
		System.out.println("\nLe joueur "+this.getJoueurs()[this.getJoueurActuel()].getPseudo()+" a joue la case "+this.getDerniereCaseJouee()+" sur le tablier "+tablier+" et a tourne le tablier dans le sens "+sensRotation+"");
		
		// On cherche si on a un gagnant
		this.chercherGagnant();
		
		// Si pas de gagnant
		if(this.getJoueurGagnant() == -1){
			// On passe au joueur suivant
			this.joueurSuivant();
			
			// Si le prochain joueur est un bot, on le fait jouer automatiquement
			if( (this.getTypeJeu() == Jeu.TYPE_JOUEUR_VS_IA) && (this.getJoueurActuel() == (Jeu.NOMBRE_JOUEURS-1)) ){
				this.jouerIA();
				return false;
			}
			
			return false;
		}
		
		// On a un gagnant
		else 
			return true;
	}

	/**
	 * Création des cases du plateau.
	 */
	public void creerCases(){
		listeCases = new Case[4][9]; // 4 tabliers de 9 cases

		// On fait une boucle sur les 4 tabliers
		for(int i = 0; i < 4; i++){
			int nb = 0;

			// Chaque tablier a 3x3 cases qui ont toutes 
			// leur position x (k) et y (j)
			for(int j = 0; j < 3; j++){
				for(int k = 0; k < 3; k++, nb++){
					listeCases[i][nb] = new Case(nb, 0, k, j);
				}
			}
		}
	}

	/**
	 * Passage au joueur suivant.
	 */
	public void joueurSuivant(){
		// Le tour est terminé, on refait jouer le premier joueur
		if(this.joueurActuel >= Jeu.NOMBRE_JOUEURS - 1){
			this.setJoueurActuel(0);

			// On enregistre le tour dans la liste des tours
			// afin de pouvoir faire un retour en arrière plus tard
			listeTours.add(copierPlateau());
			this.setNbTours(this.nbToursJeu+1);
		}

		// c'est au 2e joueur de jouer
		else
			this.setJoueurActuel(this.getJoueurActuel()+1);
		
		// On réinitialise le stade de jeu
		this.setStadeJeu(Jeu.STADE_CHOIX_CASE);
	}

	/**
	 * Demande les coordonnées de la case à jouer.
	 * 
	 * @param tablier	numéro du tablier dans lequel se trouve la case.
	 * @return 			le numéro de la case jouée.
	 */
	public int demandeNumeroCase(int tablier){
		int i;

		System.out.println("Entrez maintenant les coordonnees de la case dans laquelle mettre une bille:");
		
		System.out.println("x: ");
		int x = utils.demandeNombre(0, 2);

		System.out.println("y: ");
		int y = utils.demandeNombre(0, 2);

		for(i = 0; i < 9; i++){
			if( (listeCases[tablier][i].getPosX() == x) && (listeCases[tablier][i].getPosY() == y) )
				break;
		}

		return i;
	}

	/**
	 * Rotation du tablier.
	 * 
	 * @param tablier 	numéro du tablier à tourner
	 * @param sens		le sens de rotation
	 */
	public void rotationTablier(int tablier, int sens){
		int i, j, k, l, posX, posY;
		Case temp[] = new Case[9];

		//System.arraycopy(listeCases[tablier], 0, temp, 0, temp.length);
		//temp = listeCases[tablier].clone();

		// on effectue une copie du tableau dans un tableau temporaire
		// on ne peut pas utiliser arraycopy() ou clone() car ces fonctions 
		// font une copie "partielle", les valeurs de l'objet clone font référence
		// aux valeurs de l'objet principal. Donc si on modifie l'objet principal,
		// le cloné l'est aussi... il faut donc copier chaque élément de l'objet
		// un par un.
		for(i = 0; i < 9; i++){
			temp[i] = new Case(listeCases[tablier][i].getNumero(), listeCases[tablier][i].getCouleur(), 
				listeCases[tablier][i].getPosX(), listeCases[tablier][i].getPosY());
		}

		// Si c'est le sens horaire
		if(sens == SENS_HORAIRE){

			// On fait un parcours de toutes les cases de 0 à 8 et on les 
			// place à leur nouvelle position
			for(i = 0, j = posX = 2; j >= 0 && i < 7; i += 3, j--, posX--){
				for(k = i, l = j, posY = 0; k < (i+3); k++, l += 3, posY++){
					listeCases[tablier][l].setCouleur(temp[k].getCouleur());
					listeCases[tablier][l].setNumero(temp[k].getNumero());
					listeCases[tablier][l].setPosX(posX);
					listeCases[tablier][l].setPosY(posY);
				}
			}
		} 

		// Si c'est le sens anti horaire
		else if(sens == SENS_ANTIHORAIRE){

			// On fait un parcours de toutes les cases de 0 à 8 et on les 
			// place à leur nouvelle position
			for(i = posX = 0, j = 6; j <= 8 && i < 7; i += 3, j++, posX++){
				for(k = i, l = j, posY = 2; k < (i+3); k++, l -= 3, posY--){
					listeCases[tablier][l].setCouleur(temp[k].getCouleur());
					listeCases[tablier][l].setNumero(temp[k].getNumero());
					listeCases[tablier][l].setPosX(posX);
					listeCases[tablier][l].setPosY(posY);
				}
			}
		}
		else {
			System.out.println("Sens invalide !");
		}
	}

	/**
	 * Vérification des lignes pour trouver un gagnant.
	 * On effectue une vérification case par case et ligne par ligne.
	 * 
	 * @param Case[][] tableau 	le tableau de 9x9.
	 * @param couleurJoueur 	la couleur du joueur.
	 * @return 					true si le joueur a un alignement gagnant.
	 */
	public boolean checkLignes(Case[][] tableau, int couleurJoueur){
		for(int i = 0; i < 6; i++)
		{
			// On met les compteurs à zéro
			int nbBilles = 0;
			int maxNbBilles = 0;

			for(int j = 0; j < 6; j++)
			{
				// Si une bille de la couleur du joueur se trouve sur la case
				// on incrémente les compteurs.
				if(tableau[i][j].getCouleur() == couleurJoueur){
					nbBilles++;
					maxNbBilles = nbBilles;
				}
				else {
					nbBilles = 0;
				}
			}
			
			// Si on a trouvé au moins 5 billes alignées, le joueur a gagné
			if(maxNbBilles >= 5)
				return true;

		}

		// Le joueur n'a pas aligné au moins 5 billes
		return false;
	}
	
	/**
	 * Vérification des colonnes pour trouver un gagnant.
	 * On effectue une vérification case par case et colonne par colonne.
	 * 
	 * @param Case[][] tableau 	le tableau de 9x9.
	 * @param couleurJoueur		la couleur du joueur.
	 * @return 					true si le joueur a un alignement gagnant.
	 */
	public boolean checkColonnes(Case[][] tableau, int couleurJoueur){
		for(int i = 0; i < 6; i++)
		{
			// On met les compteurs à zéro
			int nbBilles = 0;
			int maxNbBilles = 0;

			for(int j = 0; j < 6; j++)
			{
				// Si une bille de la couleur du joueur se trouve sur la case
				// on incrémente les compteurs.
				if(tableau[j][i].getCouleur() == couleurJoueur){
					nbBilles++;
					maxNbBilles = nbBilles;
				}
				else {
					nbBilles = 0;
				}
			}
			
			// Si on a trouvé au moins 5 billes alignées, le joueur a gagné
			if(maxNbBilles >= 5)
				return true;

		}

		// Le joueur n'a pas aligné au moins 5 billes
		return false;
	}
	
	/**
	 * Vérification des diagonales pour trouver un gagnant.
	 * 
	 * @param tableau[][]	le tableau de 9x9 cases.
	 * @return 				true si le joueur a un alignement gagnant.
	 */
	public boolean checkDiagonales(Case tableau[][], int couleurJoueur){
		
		for(int diagonale = -1; diagonale <= 1; diagonale++){
			int nbBilles = 0, maxNbBilles = 0;
			
			for(int colonne = 0; colonne < 6; colonne++){
				// On élimine la position -1 qui n'existe pas
				if( ((diagonale + colonne) >= 0) && ((diagonale + colonne) < 6) ){
					if(tableau[colonne][diagonale + colonne].getCouleur() == couleurJoueur){
						nbBilles++;
						maxNbBilles = nbBilles;
					}
					else {
						nbBilles = 0;
					}
				}
			}
			
			if(maxNbBilles >= 5){
				return true;
			}
		}
		
		for(int diagonale = -1; diagonale <= 1; diagonale++){
			int nbBilles = 0, maxNbBilles = 0;
			
			for(int colonne = 0; colonne < 6; colonne++){
				// On élimine la position -1 qui n'existe pas
				if( ((diagonale + 5 - colonne) >= 0) && ((diagonale + 5 - colonne) < 6) ){
					if(tableau[colonne][diagonale + 5 - colonne].getCouleur() == couleurJoueur){
						nbBilles++;
						maxNbBilles = nbBilles;
					}
					else {
						nbBilles = 0;
					}
				}
			}
			
			if(maxNbBilles >= 5){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Conversion des 4 tabliers de 3x3 en un tableau de 6x6.
	 * Cela permet de chercher plus facilement un gagnant.
	 * 
	 * @return Case[][]  le tableau de 6x6.
	 */
	public Case[][] convertirTabliersEnTableau(){
		Case tableau[][] = new Case[6][6];
		int ligne = 0, colonne = 0;
		int x = 0, y = 0, yDepart = 0, xDepart = 0;
		
		for(ligne = 0; ligne < 6; ligne++, yDepart += 3){
			if(ligne == 3){
				yDepart = 0;
				xDepart = 2;
			}
			
			for(colonne = 0, y = yDepart, x = xDepart; colonne < 6; colonne++, y++){
				if(colonne == 3){
					x = xDepart+1;
					y = yDepart;
				}
				tableau[ligne][colonne] = listeCases[x][y];
			}
		}
		
		return tableau;
	}	
	
	/**
	 * Cherche s'il y a un gagnant.
	 */
	public void chercherGagnant(){
		Case[][] tableau;
		
		// Conversion des tabliers en un gros tableau de 6x6
		tableau = convertirTabliersEnTableau();
		
		// On regarde si le joueur 1 a gagné
		if(checkLignes(tableau, this.getJoueur(0).getCouleur()) || 
				checkColonnes(tableau, this.getJoueur(0).getCouleur()) || 
				checkDiagonales(tableau, this.getJoueur(0).getCouleur())){
			this.setJoueurGagnant(0);
			System.out.println("Le joueur 1 a gagne !!");
			return;
		}
		
		// On regarde si le joueur 2 a gagné
		else if(checkLignes(tableau, this.getJoueur(1).getCouleur()) || 
				checkColonnes(tableau, this.getJoueur(1).getCouleur()) || 
				checkDiagonales(tableau, this.getJoueur(1).getCouleur())){
			this.setJoueurGagnant(1);
			System.out.println("Le joueur 2 a gagne !!");
			return;
		}
		
		else {
			this.setJoueurGagnant(-1);
			return;
		}
	}

	/**
	 * Enregistrement de la partie.
	 * 
	 * On sérialise toutes les données dont on a besoin
	 * et on met le tout dans un fichier de sauvegarde.
	 * 
	 * @return 				true si l'enregistrement s'est bien passé.
	 */
	@SuppressWarnings("unchecked")
	public boolean enregistrerPartie(Joueur[] joueurs, Case[][] cases){
		String nomFichier = "";
		// On crée une liste
		List liste = new ArrayList();

		// On ajoute nos objets à la liste
		liste.add(this.getCases());
		liste.add(this.getJoueurs());
		liste.add(this.getNbTours());
		liste.add(this.getListeTours());
		liste.add(this.getTypeJeu());
		liste.add(this.getListeCoups());

		// On essaye d'enregistrer le fichier de sauvegarde
		try {
			// Le nom du fichier de sauvegarde se base sur la date/heure de l'instant où on sauvegarde
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

			nomFichier = Jeu.REPERTOIRE_SAUVEGARDE + dateFormat.format(date) + Jeu.EXTENSION_SAUVEGARDE;

			// On crée le fichier
			FileOutputStream fichier = new FileOutputStream(nomFichier);
			ObjectOutputStream infos = new ObjectOutputStream(fichier);

			System.out.println("Enregistrement d'une partie dans le fichier "+nomFichier);

			// On écrit notre liste dans le fichier
			infos.writeObject(liste);
			infos.flush();
			infos.close();
			fichier.close();
			
			return true;
		}
		catch(IOException e){
			System.out.println("Impossible d'enregistrer la partie !");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Copie "réelle" du plateau.
	 * 
	 * On ne peut pas utiliser les fonctions .clone() ou .copy() car
	 * ces fonctions ne font que des copies partielles (par référence)
	 * du plateau. On doit donc recréer complètement le plateau.
	 * 
	 * @return Case[][] 	le plateau copié.
	 */
	public Case[][] copierPlateau(){
		Case[][] resultat = new Case[4][9];
		
		// Parcours des 4 tabliers
		for(int i = 0; i < 4; i++){
			
			// Parcours des 9 cases du tablier
			for(int j = 0; j < 9; j++){
				
				// On effectue la copie
				resultat[i][j] = new Case(listeCases[i][j].getNumero(), listeCases[i][j].getCouleur(), 
								listeCases[i][j].getPosX(), listeCases[i][j].getPosY());
			}
		}

		return resultat;
	}
	
	/**
	 * Retour en arrière.
	 * 
	 * @param nbTours	nombre de retours à reculer
	 */
	public void retourArriere(int nbTours){

		// On calcule à quel tour on doit retourner en fonction du nombre
		// de tours de recul
		int numTourRecul = this.nbToursJeu - nbTours;
		ListIterator<Case[][]> li = listeTours.listIterator(numTourRecul);
		
		if(li.hasNext()){
			Case[][] temp = (Case[][]) li.next();
			// On met a jour le plateau de jeu
			setCases(temp);

			// On met a jour le tour actuel de jeu
			this.nbToursJeu = numTourRecul;

			// On supprime les éléments suivant le tour numTourRecul dans la liste listeTours
			while(li.hasNext()){
				li.next();
				li.remove();
			}

			System.out.println("\nRetour en arriere effectue avec succes !\n");
		} 
		else {
			System.out.println("\nImpossible de retourner en arriere ! Operation annulee.\n");
		}
	}

	/**
	 * Vérifie si le joueur actuellement en train de jouer
	 * n'est autre que l'IA.
	 * 
	 * @return 	true si c'est l'IA.
	 */
	public boolean joueurActuelEstUnBot(){
		if( (this.getTypeJeu() == Jeu.TYPE_JOUEUR_VS_IA) && (this.joueurActuel == (Jeu.NOMBRE_JOUEURS-1)) )
			return true;
		else
			return false;
	}
	
	/**
	 * Quitte le jeu.
	 */
	public void quitterJeu(){
		System.out.println("Fin de la partie.");
		System.out.println("A bientot ! :)");
		System.exit(0);
	}
	
	
	// Getters
	
	public int getStadeJeu(){
		return this.stadeJeu;
	}
	
	public List<Coup> getListeCoups(){
		return this.listeCoups;
	}

	public int getTypeJeu(){
		return this.typeJeu;
	}

	public List<Case[][]> getListeTours(){
		return this.listeTours;
	}

	public int getNbTours(){
		return this.nbToursJeu;
	}

	public int getTypeInterface(){
		return this.typeInterface;
	}

	public Joueur[] getJoueurs(){
		return listeJoueurs;
	}

	public Case[][] getCases(){
		return listeCases;
	}
	
	public Joueur getJoueur(int i){
		return listeJoueurs[i];
	}
	
	public int getJoueurActuel(){
		return this.joueurActuel;
	}
	
	public int getDerniereCaseJouee(){
		return this.derniereCaseJouee;
	}
	
	public int getJoueurGagnant(){
		return this.joueurGagnant;
	}
	
	public Joueur joueurActuel(){
		return listeJoueurs[this.joueurActuel];
	}
	
	// Setters
	
	public void setStadeJeu(int stade){
		this.stadeJeu = stade;
	}
	
	public void setJoueurGagnant(int g){
		this.joueurGagnant = g;
	}
	
	public void setDerniereCaseJouee(int num){
		this.derniereCaseJouee = num;
	}
	
	public void setJoueurActuel(int j){
		this.joueurActuel = j;
	}
	
	public void setJoueur(int n, Joueur j){
		this.listeJoueurs[n] = j;
	}
	
	public void setCases(Case[][] c){
		this.listeCases = c;
	}

	public void setJoueurs(Joueur[] j){
		this.listeJoueurs = j;
	}
	
	public void setNbTours(int n){
		this.nbToursJeu = n;
	}
	
	public void setListeTours(List<Case[][]> t){
		this.listeTours = t;
	}
	
	public void setTypeJeu(int t){
		this.typeJeu = t;
	}
	
	public void setListeCoups(List<Coup> l){
		this.listeCoups = l;
	}
	
	
	public static void main(String args[]){
		new Jeu();
	}
}
