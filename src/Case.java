import java.io.*;

public class Case implements Serializable {
	
	private int numero;
	private int couleur;
	private int posX;
	private int posY;

	public final static int COULEUR_VIDE = 0;
	public final static int COULEUR_BLANC = 1;
	public final static int COULEUR_NOIR = 2;
	
	private static final long serialVersionUID = 42L;
	
	/**
	 * Initialisation d'une case.
	 * 
	 * @param numero 	le numéro de la case dans le tablier (de 0 à 8).
	 * @param couleur 	la couleur de la case (noir - blanc - vide).
	 * @param posX		la position sur l'axe des abscisses de la case sur le tablier.
	 * @param posY		la position sur l'axe des ordonnées de la case sur le tablier.
	 */
	public Case(int numero, int couleur, int posX, int posY){
		this.numero = numero;
		this.couleur = couleur;
		this.posX = posX;
		this.posY = posY;
	}

	public int getNumero(){
		return this.numero;
	}
	
	public void setNumero(int n){
		this.numero = n;
	}
	
	public int getCouleur(){
		return this.couleur;
	}
	
	public void setCouleur(int c){
		this.couleur = c;
	}
	
	public int getPosX(){
		return this.posX;
	}
	
	public void setPosX(int p){
		this.posX = p;
	}
	
	public int getPosY(){
		return this.posY;
	}
	
	public void setPosY(int p){
		this.posY = p;
	}

	public String toString(){
		return ""+this.getCouleur()+"";
	}

	public String getType(){
		if(this.couleur == COULEUR_NOIR)
			return "N";

		else if(this.couleur == COULEUR_BLANC)
			return "B";

		else 
			return "0";
	}
}
