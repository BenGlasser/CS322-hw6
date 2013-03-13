package src.main.java;

class TestHeap2 {
  static final int S = 100;
  static final int N = 11;

  public static void main(String[] args) {
    Heap h = Heap.make(S);
    for (int i=0; i<N; i++) {
      System.out.println("Allocating object " + i
                       + " at address " + h.alloc(8));
    }
    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());
  }
}
