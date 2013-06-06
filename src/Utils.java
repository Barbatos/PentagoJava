import java.util.Random;
import java.util.Scanner;

class Utils {

	public Utils(){}
	
	/**
	 * Retourne un nombre aléatoire compris entre un min et un max.
	 * 
	 * @param min 	le nombre minimum à retourner.
	 * @param max 	le nombre maximum à retourner.
	 * @return 		le nombre généré aléatoirement.
	 */
	public int getNombreAleatoire(int min, int max){
		Random rand = new Random();

		int result = rand.nextInt(max - min + 1) + min;

		return result;
	}
	
	/**
	 * Demande de rentrer un nombre en console.
	 * 
	 * Cette fonction ne plante pas si on entre autre chose qu'un nombre
	 * ou si le nombre est trop grand / trop petit.
	 * 
	 * @param min 	le plus petit nombre qu'il soit possible de choisir.
	 * @param max 	le plus grand nombre qu'il soit possible de choisir.
	 * @return 		le nombre choisi après vérifications.
	 */
	public int demandeNombre(int min, int max){
		Scanner s = new Scanner(System.in);
		int numero = -1;

		System.out.println("Choisissez un nombre entre "+min+" et "+max+":");

		while(true){
			try {
				numero = Integer.parseInt(s.nextLine());
				if((numero >= min) && (numero <= max))
					break;
				else {
					System.out.println("Le nombre doit etre compris entre "+min+" et "+max+"");
				}
			}

			catch(NumberFormatException nfe){
				System.out.println("Merci d'entrer un nombre !");
			}
		}

		return numero;
	}
}