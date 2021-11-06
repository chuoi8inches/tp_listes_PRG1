package fr.istic.prg1.list;

import fr.istic.prg1.list_util.Comparison;
import fr.istic.prg1.list_util.Iterator;
import fr.istic.prg1.list_util.List;
import fr.istic.prg1.list_util.SmallSet;

import java.io.*;
import java.util.Scanner;


/**
 * @author Mickael Foursov <foursov@univ-rennes1.fr>
 * @version 5.0
 * @since 2018-10-02
 */

public class MySet extends List<SubSet> {

	/**
	 * Borne superieure pour les rangs des sous-ensembles.
	 */
	private static final int MAX_RANG = 128;
	/**
	 * Sous-ensemble de rang maximal a mettre dans le drapeau de la liste.
	 */
	private static final SubSet FLAG_VALUE = new SubSet(MAX_RANG,
			new SmallSet());
	/**
	 * Entree standard.
	 */
	private static final Scanner standardInput = new Scanner(System.in);

	public MySet() {
		super();
		setFlag(FLAG_VALUE);
	}

	/**
	 * Fermer tout (actuellement juste l'entree standard).
	 */
	public static void closeAll() {
		standardInput.close();
	}

	private static Comparison compare(int a, int b) {
		if (a < b) {
			return Comparison.INF;
		} else if (a == b) {
			return Comparison.EGAL;
		} else {
			return Comparison.SUP;
		}
	}

	/**
	 * Afficher a l'ecran les entiers appartenant a this, dix entiers par ligne
	 * d'ecran.
	 */
	public void print() {
		System.out.println(" [version corrigee de contenu]");
		this.print(System.out);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// //////////// Appartenance, Ajout, Suppression, Cardinal
	// ////////////////////
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Ajouter a this toutes les valeurs saisies par l'utilisateur et afficher
	 * le nouveau contenu (arret par lecture de -1).
	 */
	public void add() {
		System.out.println(" valeurs a ajouter (-1 pour finir) : ");
		this.add(System.in);
		System.out.println(" nouveau contenu :");
		this.printNewState();
	}

	/**
	 * Ajouter a this toutes les valeurs prises dans is.
	 * C'est une fonction auxiliaire pour add() et restore().
	 * 
	 * @param is
	 *            flux d'entree.
	 */
	public void add(InputStream is) {
		System.out.println("-------------------------------------------------");
		Scanner sc = new Scanner(is);
		while (sc.hasNextInt()){
			int val = sc.nextInt();
			if(val != -1) this.addNumber(val);
			else break;
		}
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Ajouter value a this.
	 * 
	 * @param value
	 *            valuer a ajouter.
	 */
	public void addNumber(int value) {
		System.out.println("-------------------------------------------------");
		if(value>32767 || value<0) return;
		int rank = value/256;
		int valueInSet = value%256;
		Iterator<SubSet> it = this.iterator();
		it.restart();
		while(!it.isOnFlag()){
			SubSet ss = it.getValue();
			if(ss.rank==rank){ss.set.add(valueInSet);return;}
			else it.goForward();
		}
		//A ce point, c'est sur que on doit ajouter un nouveau SubSet
		it.restart();
		while(!it.isOnFlag()) {
			SubSet ss = it.getValue();
			if(ss.rank>rank){
				SmallSet smallset=new SmallSet();
				smallset.add(valueInSet);
				it.addRight(new SubSet(rank,smallset));
				return;
			}
			else it.goForward();
		}
		//A ce point, c'est sur que rang du nouveau Subset est le plus grand dans this
		SmallSet smallset=new SmallSet();
		smallset.add(valueInSet);
		it.addRight(new SubSet(rank,smallset));
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Supprimer de this toutes les valeurs saisies par l'utilisateur et
	 * afficher le nouveau contenu (arret par lecture de -1).
	 */
	public void remove() {
		System.out.println("  valeurs a supprimer (-1 pour finir) : ");
		this.remove(System.in);
		System.out.println(" nouveau contenu :");
		this.printNewState();
	}

	/**
	 * Supprimer de this toutes les valeurs prises dans is.
	 * 
	 * @param is
	 *            flux d'entree
	 */
	public void remove(InputStream is) {
		System.out.println("-------------------------------------------------");
		Scanner sc = new Scanner(is);
		while (sc.hasNextInt()){
			int val = sc.nextInt();
			if(val != -1) this.removeNumber(val);
			else return;
		}
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Supprimer value de this.
	 * 
	 * @param value
	 *            valeur a supprimer
	 */
	public void removeNumber(int value) {
		System.out.println("-------------------------------------------------");
		if(value>32767 || value<0) return;
		int rank = value/256;
		int valueInSet = value%256;
		Iterator<SubSet> it = this.iterator();
		it.restart();
		while(!it.isOnFlag()){
			SubSet ss = it.getValue();
			if(ss.rank==rank && ss.set.contains(valueInSet)){
				ss.set.remove(valueInSet);
				if (ss.set.size()==0) it.remove();
			}
			else it.goForward();
		}
		System.out.println("-------------------------------------------------");
	}

	/**
	 * @return taille de l'ensemble this
	 */
	public int size() {
		System.out.println("-------------------------------------------------");
		int size = 0;
		Iterator<SubSet> it = this.iterator();
		it.restart();
		while(!it.isOnFlag()){
			size+=it.getValue().set.size();
			it.goForward();
		}
		System.out.println("-------------------------------------------------");
		return size;
	}


	/**
	 * @return true si le nombre saisi par l'utilisateur appartient a this,
	 *         false sinon
	 */
	public boolean contains() {
		System.out.println(" valeur cherchee : ");
		int value = readValue(standardInput, 0);
		return this.contains(value);
	}

	/**
	 * @param value
	 *            valeur a tester
	 * @return true si valeur appartient a l'ensemble, false sinon
	 */

	public boolean contains(int value) {
		int rank = value/256;
		int valueInSet = value%256;
		System.out.println("-------------------------------------------------");
		Iterator<SubSet> it = this.iterator();
		it.restart();
		while(!it.isOnFlag()){
			if(it.getValue().rank==rank && it.getValue().set.contains(valueInSet)) return true;
			it.goForward();
		}
		System.out.println("-------------------------------------------------");
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////// Difference, DifferenceSymetrique, Intersection, Union ///////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * This devient la difference de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void difference(MySet set2) {
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println("Fonction a ecrire");
		System.out.println("-------------------------------------------------");
		System.out.println();
	}

	/**
	 * This devient la difference symetrique de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void symmetricDifference(MySet set2) {
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println("Fonction a ecrire");
		System.out.println("-------------------------------------------------");
		System.out.println();
	}

	/**
	 * This devient l'intersection de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void intersection(MySet set2) {
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println("Fonction a ecrire");
		System.out.println("-------------------------------------------------");
		System.out.println();
	}

	/**
	 * This devient l'union de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void union(MySet set2) {
		System.out.println("-------------------------------------------------");
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();
		it1.restart();
		it2.restart();
		while (!it2.isOnFlag()){
			while ((!it1.isOnFlag())){
				int rank2=it2.getValue().rank;
				int rank1=it1.getValue().rank;
				SmallSet smallset2 = it2.getValue().set;
				SmallSet smallset1 = it1.getValue().set;
				if(rank2<rank1){
					it1.addLeft(new SubSet(rank2,smallset2));
					break;
				}
				else if (rank2==rank1){
					smallset1.union(smallset2);
					it1.setValue(new SubSet(rank2,smallset1));
					break;
				}
				else it1.goForward();
				if (it1.getValue().rank==128) {
					it1.goBackward();
					it1.addRight(new SubSet(rank2,smallset2));
					break;
				}
			}
			it2.goForward();
		}
		System.out.println("-------------------------------------------------");
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////////////////// Egalite, Inclusion ////////////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * @param o
	 *            deuxieme ensemble
	 * 
	 * @return true si les ensembles this et o sont egaux, false sinon
	 */
	@Override
	public boolean equals(Object o) {
		boolean b = true;
		if (this == o) {
			b = true;
		} else if (o == null) {
			b = false;
		} else if (!(o instanceof MySet)) {
			b = false;
		} else {
			System.out.println();
			System.out
					.println("-------------------------------------------------");
			System.out.println("Dernier cas a ecrire");
			System.out
					.println("-------------------------------------------------");
			System.out.println();

		}
		return b;
	}

	/**
	 * @param set2
	 *            deuxieme ensemble
	 * @return true si this est inclus dans set2, false sinon
	 */
	public boolean isIncludedIn(MySet set2) {
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println("Fonction a ecrire");
		System.out.println("-------------------------------------------------");
		System.out.println();
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// //////// Rangs, Restauration, Sauvegarde, Affichage //////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Afficher les rangs presents dans this.
	 */
	public void printRanks() {
		System.out.println(" [version corrigee de rangs]");
		this.printRanksAux();
	}

	private void printRanksAux() {
		int count = 0;
		System.out.println(" Rangs presents :");
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			System.out.print("" + it.getValue().rank + "  ");
			count = count + 1;
			if (count == 10) {
				System.out.println();
				count = 0;
			}
			it.goForward();
		}
		if (count > 0) {
			System.out.println();
		}
	}

	/**
	 * Creer this a partir d'un fichier choisi par l'utilisateur contenant une
	 * sequence d'entiers positifs terminee par -1 (cf f0.ens, f1.ens, f2.ens,
	 * f3.ens et f4.ens).
	 */
	public void restore() {
		String fileName = readFileName();
		InputStream inFile;
		try {
			inFile = new FileInputStream(fileName);
			System.out.println(" [version corrigee de restauration]");
			this.clear();
			this.add(inFile);
			inFile.close();
			System.out.println(" nouveau contenu :");
			this.printNewState();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("fichier " + fileName + " inexistant");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier " + fileName);
		}
	}

	/**
	 * Sauvegarder this dans un fichier d'entiers positifs termine par -1.
	 */
	public void save() {
		System.out.println(" [version corrigee de sauvegarde]");
		OutputStream outFile;
		try {
			outFile = new FileOutputStream(readFileName());
			this.print(outFile);
			outFile.write("-1\n".getBytes());
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("pb ouverture fichier lors de la sauvegarde");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier");
		}
	}

	/**
	 * @return l'ensemble this sous forme de chaine de caracteres.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int count = 0;
		SubSet subSet;
		int startValue;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			subSet = it.getValue();
			startValue = subSet.rank * 256;
			for (int i = 0; i < 256; ++i) {
				if (subSet.set.contains(i)) {
					String number = String.valueOf(startValue + i);
					int numberLength = number.length();
					for (int j = 6; j > numberLength; --j) {
						number += " ";
					}
					result.append(number);
					++count;
					if (count == 10) {
						result.append("\n");
						count = 0;
					}
				}
			}
			it.goForward();
		}
		if (count > 0) {
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Imprimer this dans outFile.
	 * 
	 * @param outFile
	 *            flux de sortie
	 */
	private void print(OutputStream outFile) {
		try {
			String string = this.toString();
			outFile.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Afficher l'ensemble avec sa taille et les rangs presents.
	 */
	private void printNewState() {
		this.print(System.out);
		System.out.println(" Nombre d'elements : " + this.size());
		this.printRanksAux();
	}

	/**
	 * @param scanner
	 * @param min
	 *            valeur minimale possible
	 * @return l'entier lu au clavier (doit être entre min et 32767)
	 */
	private static int readValue(Scanner scanner, int min) {
		int value = scanner.nextInt();
		while (value < min || value > 32767) {
			System.out.println("valeur incorrecte");
			value = scanner.nextInt();
		}
		return value;
	}

	/**
	 * @return nom de fichier saisi psar l'utilisateur
	 */
	private static String readFileName() {
		System.out.print(" nom du fichier : ");
		String fileName = standardInput.next();
		return fileName;
	}
}
