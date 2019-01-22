
// for comparing values
interface IComparator {

  boolean apply(Edge t1, Edge t2);

  int compareTo(Edge edge, Edge pivot);
}

// compares edges by weight
class CompareEdges implements IComparator {

  public boolean apply(Edge e1, Edge e2) {
    return e1.weight < e2.weight;
  }

  public int compareTo(Edge e1, Edge e2) {
    if (e1.weight < e2.weight) {
      return -1;
    } else if (e1.weight > e2.weight) {
      return 1;
    } else {
      return 0;
    }
  }
}