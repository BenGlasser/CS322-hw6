package src.main.java;

class TestHeap4 {
  static final int S = 100;
  static final int N = 10;

  public static void main(String[] args) {
    Heap h = Heap.make(S);
    for (int i=0; i<N; i++) {
      int t = h.alloc(1+i);
      System.out.println("Allocating object " + i + " at address " + t);
      h.store(t, 1, h.a);
      h.a = t;
    }
    System.out.println("Before garbage collection;");
    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());

    h.garbageCollect();

    System.out.println("After garbage collection;");
    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());
  }
}
