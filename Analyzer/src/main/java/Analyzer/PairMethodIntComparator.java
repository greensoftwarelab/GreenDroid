package Analyzer;

import java.util.Comparator;

/**
 * Created by rrua on 22/06/17.
 */
public class PairMethodIntComparator implements Comparator<PairMetodoInt> {
    @Override
    public int compare(PairMetodoInt pairMetodoInt, PairMetodoInt t1) {
      return pairMetodoInt.metodo.compareTo(t1.metodo);
    }
}
