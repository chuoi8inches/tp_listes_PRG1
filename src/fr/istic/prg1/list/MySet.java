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
      Scanner sc = new Scanner(is);
      while (sc.hasNextInt()) {
        int val = sc.nextInt();
        //Condition d'arrêt : -1
        if (val != -1) {
          addNumber(val);
        }
      }
      sc.close();
    }

  /**
   * Ajouter value à this.
   *
   * @param value un entier.
   * valeur à ajouter.
   */
  public void addNumber(int value) {
    int rank = value / 256;
    int mod = value % 256;
    //ajouter le rank ainsi que le modulo a notre SubSet
    internalAdd(rank, mod);
  }


  /**
   * @param rank un entier.
   * @param mod un entier.
   * valeurs à ajouter.
   */
  private void internalAdd(int rank, int mod) {
    boolean added = false;

    if (rank >=MAX_RANG  || rank < 0) {
      System.out.println("Rank out of bounds");
    }
    Iterator<SubSet> it = iterator();

    while (!it.isOnFlag()) {	//	on vérifie que l'itérateur 'it' a un élément après.
      SubSet val = it.getValue();
      //si le rank n'est pas existé avant, l'itérateur a passé le rank qu'on veut ajouter, alors on crée un noveau rank avec un nouveau SmallSet
      if (val.rank > rank && size() <= MAX_RANG) {
        added = true;
        SmallSet set = new SmallSet();
        set.add(mod);
        SubSet temp = new SubSet(rank, set);
        //addLeft pour maintenir l'ordre croissant
        it.addLeft(temp);
        break;
      }
      //	si le rank existe déjà, on ajoute notre variable mod au SmallSet dans ce rank.
      else if (val.rank == rank) {
        val.set.add(mod);
        added = true;
        break;
      }
      it.goForward();
      //if(it.getValue()==null) it.goBackward();
    }
    //Dernier cas, si booléen 'added' = false, notre MySet peut être vide ou notre rank est plus grand que les ranks dans notre MySet.
    if (added == false && size() <= MAX_RANG) {
      SmallSet set = new SmallSet();
      set.add(mod);
      SubSet temp = new SubSet(rank, set);
      it.addLeft(temp);
    }
  }

  /**
   * Supprimer de this toutes les valeurs saisies par l'utilisateur et
   * afficher le nouveau contenu (arrêt par lecture de -1).
   */
  public void remove() {
    System.out.println("  valeurs à supprimer (-1 pour finir) : ");
    this.remove(System.in);
    System.out.println(" nouveau contenu :");
    this.printNewState();
  }

  /**
   * Supprimer de this toutes les valeurs prises dans is.
   *
   * @param is une entrée utilisateur.
   * flux d'entrée
   */
  public void remove(InputStream is) {
    Scanner sc = new Scanner(is);
    while (sc.hasNextInt()) {
      int val = sc.nextInt();
      if (val != -1) {
        removeNumber(val);
      }
    }
    sc.close();
  }

  /**
   * Supprimer value de this.
   *
   * @param value
   * valeur à supprimer
   */
  public void removeNumber(int value) {
    int rank = value/256;
    int mod = value % 256;

    Iterator<SubSet> it = iterator();

    while (!it.isOnFlag()) {
      SubSet val = it.getValue();
      if (val.rank == rank) {
        val.set.remove(mod);
        //si le SmallSet de ce rank est maintenant vide, on supprime le rank entièrement
        if (val.set.isEmpty()) {
          it.remove();
        }
      }
      it.goForward();
    }
  }

  /**
   * @return taille de l'ensemble this.
   */
  public int size() {
    int size = 0;

    Iterator<SubSet> it = iterator();

    while (!it.isOnFlag()) {
      //on va dans chaque SmallSet et compter toutes les valeurs dedans
      size += it.getValue().set.size();
      it.goForward();
    }

    return size;
  }


  /**
   * @return true si le nombre saisi par l'utilisateur appartient à this,
   *         false sinon
   */
  public boolean contains() {
    System.out.println(" valeur cherchee : ");
    int value = readValue(standardInput, 0);
    return this.contains(value);
  }

  /**
   * @param value un entier.
   * valeur à tester
   * @return true si valeur appartient à l'ensemble, false sinon.
   */

  public boolean contains(int value) {
    int rank = value / 256;
    int mod = value % 256;

    Iterator<SubSet> it = iterator();

    while (!it.isOnFlag()) {
      SubSet val = it.getValue();
      //Rien à dire, si on a trouvé un rank équivalent à notre rank cherché corespondant à la value, et que
      //le rank contains notre mod, renvoie true
      if (val.rank == rank && val.set.contains(mod))
        return true;
      it.goForward();
    }
    return false;
  }

  // /////////////////////////////////////////////////////////////////////////////
  // /////// 		Difference, DifferenceSymetrique, Intersection, Union 	 ///////
  // /////////////////////////////////////////////////////////////////////////////

  /**
   * This devient la difference de this et set2.
   *
   * @param set2 un ensemble MySet.
   * deuxième ensemble
   */
  public void difference(MySet set2) {
    Iterator<SubSet> it1 = this.iterator();
    while (!it1.isOnFlag()) {
      Iterator<SubSet> it2 = set2.iterator();
      while (!it2.isOnFlag()){
        if (it2.getValue().rank == it1.getValue().rank) {
          //On utilise la fonction intersection dans SmallSet
          it1.getValue().set.difference(it2.getValue().set);
        }
        it2.goForward();
      }
      // si l'ensemble est vide à cause de la fonction difference dans SmallSet,
      //on supprime les ranks avec SmallSet vide
      if (it1.getValue().set.isEmpty()) {
        it1.remove();
        it1.goBackward();
      }
      it1.goForward();
    }
  }


  /**
   * This devient la différence symétrique de this et set2.
   *
   * @param set2 un ensemble MySet.
   * deuxième ensemble
   */
  //Union this
  public void symmetricDifference(MySet set2) {
    //set3 devient this; this est l'union de this et set2; set3 est l'intersection de lui même avec set2;
    //this est la différence entre this et set3.
    //
    //Une autre explication
    //Supposons deux entité A et B, il se croise en C. On a créé un entité A+B, un entité C, puis on fait
    //(A + B) - C
    MySet set3 = new MySet();
    set3.union(this);
    this.union(set2);
    set3.intersection(set2);
    this.difference(set3);
  }

  /**
   * This devient l'intersection de this et set2.
   *
   * @param set2 un ensemble MySet.
   * deuxième ensemble
   */
  public void intersection(MySet set2) {
    Iterator<SubSet> it1 = iterator();
    while (!it1.isOnFlag()) {
      Iterator<SubSet> it = set2.iterator();
      boolean same = false;
      while (!it.isOnFlag()){
        //Si on a trouvé le même rank, il suffit d'appeler la fonction intersection du SmallSet
        if (it.getValue().rank == it1.getValue().rank) {
          it1.getValue().set.intersection(it.getValue().set);
          same = true;
          //Si le rank est maintenant vide on le supprime
          if (it1.getValue().set.isEmpty()) {
            it1.remove();
            it1.goBackward();
          }
        }
        it.goForward();
      }
      //Si on n'a pas trouvé les même ranks on doit supprimer ce rank là de la liste
      if (!same) {
        it1.remove();
        it1.goBackward();
      }
      it1.goForward();
    }
  }

  /**
   * This devient l'union de this et set2.
   *
   * @param set2 un ensemble MySet.
   * deuxième ensemble
   */
  public void union(MySet set2) {
    Iterator<SubSet> it = set2.iterator();
    while (!it.isOnFlag()) {
      SubSet val = it.getValue();
      for (int mod = 0; mod < 256; mod++)
        if (val.set.contains(mod))
          internalAdd(val.rank, mod);
      it.goForward();
    }
  }

  // /////////////////////////////////////////////////////////////////////////////
  // ///////////////////	 		Egalite, Inclusion 			////////////////////
  // /////////////////////////////////////////////////////////////////////////////

  /**
   * @param obj un Objet de collection Java.
   * deuxième ensemble.
   *
   * @return true si les ensembles this et obj sont égaux, false sinon.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) 		// si this et obj sont égaux, on renvoie true.
      return true;

    if (obj == null)		// si obj est nul, on renvoie false.
      return false;

    if (!(obj instanceof MySet))		// si obj n'est pas d'instance MySet, on renvoie false.
      return false;

    MySet set2 = (MySet)obj;			// on créer un ensemble 'set2' de type MySet, égale au cast de obj en MySet.

    if (size() != set2.size())			// si la taille du nouvel ensemble 'set2' n'est pas la bonne (exemple : vide).
      return false;

    Iterator<SubSet> it1 = iterator();
    while (!it1.isOnFlag()) {
      Boolean found = false;
      Iterator<SubSet> it2 = set2.iterator();
      while (!it2.isOnFlag()){
        //compare rank
        if (it2.getValue().rank == it1.getValue().rank){
          found = true;
          //compare valeur si rank est égale
          if (!it1.getValue().set.equals(it2.getValue().set))
            return false;
        }
        it2.goForward();
      }
      //si on trouve pas le rank
      if (!found) { return false; }
      it1.goForward();
    }
    return true;
  }

  /**
   * @param set2 un ensemble MySet.
   * deuxième ensemble
   * @return true si this est inclus dans set2, false sinon
   */
  public boolean isIncludedIn(MySet set2) {
    MySet set3 = new MySet();
    set3.union(this);
    //set3 est maintenant l'intersection de this et set2
    set3.intersection(set2);
    //si c'est true alors this est l'intersection de this et set 2, ça veut dire il est inclus dans set2
    return this.equals(set3);
  }

  // /////////////////////////////////////////////////////////////////////////////
  // ////////		 Rangs, Restauration, Sauvegarde, Affichage			////////////
  // /////////////////////////////////////////////////////////////////////////////

  /**
   * Afficher les rangs présents dans this.
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
