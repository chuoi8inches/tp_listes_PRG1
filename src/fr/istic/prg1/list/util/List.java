package fr.istic.prg1.list.util;
import fr.istic.prg1.list_util.SuperT;
import fr.istic.prg1.list_util.Iterator;
public class List<T extends SuperT> {
	// liste en double chainage par references

	private class Element {
		// element de List<Item> : (Item, Element, Element)
		public T value;
		public Element left, right;

		public Element() {
			value = null;
			left = null;
			right = null;
		}
	} // class Element

	public class ListIterator implements Iterator<T> {
		private Element current;

		private ListIterator() {
			Element elem = new Element();
			current = elem;
		}

		@Override
		public void goForward() {
			if(!(current.right == null)){
				current = current.right;
			}
		}

		@Override
		public void goBackward() {
			if(!(current.left == null)){
				current = current.left;
			}
		}

		@Override
		public void restart() {
			while(!(current.left == null) && !this.isOnFlag()){
				current = flag.left;
			}
		}

		@Override
	        public boolean isOnFlag() { return this.current == flag; }

		@Override
		public void remove() {
			try {
				assert current != flag : "\n\n\nimpossible de retirer le drapeau\n\n\n";
			} catch (AssertionError e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		@Override		 
		public T getValue() {
			try {
				assert this.current.value != null : "\n\n\nl'element courant n'a pas de valeur\n\n\n";
			} catch (AssertionError var2) {
				var2.printStackTrace();
				System.exit(0);
			}

			return this.current.value;
		}


		@Override
		public T nextValue() {
			try {
				assert this.current.left != null : "\n\n\nimpossible d'avancer : le voisin droit n'existe pas\n\n\n";
			} catch (AssertionError var2) {
				var2.printStackTrace();
				System.exit(0);
			}

			this.goForward();
			return this.getValue();
		}

		@Override
		public void addLeft(T v) {
			Element leftNeighbor = current.right;
			Element rightNeighbor= current;
			Element newElement = new Element();
			newElement.value=v;
			current.right=leftNeighbor;
			current.left=rightNeighbor;
			leftNeighbor.left = current;
			rightNeighbor.right=current;
		}

		@Override
		public void addRight(T v) {}

		@Override
		public void setValue(T v) { }

		@Override
		public void selfDestroy() { }

		@Override
		public String toString() {
			return "parcours de liste : pas d'affichage possible \n";
		}

	} // class IterateurListe

	private Element flag;

	public List() { }

	public ListIterator iterator() { return null; }

	public boolean isEmpty() { return false; }

	public void clear() { }

	public void setFlag(T v) { }

	public void addHead(T v) { }

	public void addTail(T v) { }

	@SuppressWarnings("unchecked")
	public List<T> clone() {
		List<T> nouvListe = new List<T>();
		ListIterator p = iterator();
		while (!p.isOnFlag()) {
			nouvListe.addTail((T) p.getValue().clone());
			// UNE COPIE EST NECESSAIRE !!!
			p.goForward();
		}
		return nouvListe;
	}

	@Override
	public String toString() {
		String s = "contenu de la liste : \n";
		ListIterator p = iterator();
		while (!p.isOnFlag()) {
			s = s + p.getValue().toString() + " ";
			p.goForward();
		}
		return s;
	}
}
