package umpire.local;

import core.Metaposition;

public class IncompatibleMetapositionException extends Exception {
   Metaposition m1;
   Metaposition m2;

   public IncompatibleMetapositionException(Metaposition mp1, Metaposition mp2) {
      this.m1 = mp1;
      this.m2 = mp2;
   }
}
