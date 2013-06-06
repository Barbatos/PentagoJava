import java.io.*;

public class Coup implements Serializable {
	
	private static final long serialVersionUID = 42L;
	
	private int joueur;
	private int plateau;
	private int numCase;
	private int sens;

	/**
	 * Ajout d'un coup (un tour joué par un joueur).
	 * 
	 * @param joueur	le numéro du joueur qui a joué ce coup.
	 * @param plateau 	le numéro du plateau joué.
	 * @param numCase	le numéro de la case jouée sur le plateau.
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