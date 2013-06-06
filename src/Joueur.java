import java.io.*;

public class Joueur implements Serializable {
	private int numero;
	private String pseudo;
	private int couleur;

	private static final long serialVersionUID = 42L;
	
	/**
	 * Initialisation d'un joueur.
	 * 
	 * @param numero	le numéro du joueur.
	 * @param pseudo	le pseudonyme en jeu du joueur.
	 * @param couleur 	la couleur de bille du joueur.
	 */
	public Joueur(int numero, String pseudo, int couleur){
		this.numero = numero;
		this.pseudo = pseudo;
		this.couleur = couleur;
	}

	public int getNumero(){
		return this.numero;
	}

	public void setNumero(int n){
		this.numero = n;
	}

	public String getPseudo(){
		return this.pseudo;
	}

	public void setPseudo(String p){
		this.pseudo = p;
	}

	public int getCouleur(){
		return this.couleur;
	}

	public void setCouleur(int c){
		this.couleur = c;
	}
}
