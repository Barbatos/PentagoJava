import java.io.*;

public class Coup implements Serializable {
	
	private static final long serialVersionUID = 42L;
	
	private int joueur;
	private int plateau;
	private int numCase;
	private int sens;

	/**
	 * Ajout d'un coup (un tour jou� par un joueur).
	 * 
	 * @param joueur	le num�ro du joueur qui a jou� ce coup.
	 * @param plateau 	le num�ro du plateau jou�.
	 * @param numCase	le num�ro de la case jou�e sur le plateau.
	 * @param sens		le sens de rotation choisi par le joueur.
	 */
	public Coup(int joueur, int plateau, int numCase, int sens){
		this.joueur = joueur;
		this.plateau = plateau;
		this.numCase = numCase;
		this.sens = sens;
	}

	public int getJoueur(){
		return this.joueur;
	}
	
	public int getPlateau(){
		return this.plateau;
	}

	public int getNumCase(){
		return this.numCase;
	}

	public int getSens(){
		return this.sens;
	}

}	