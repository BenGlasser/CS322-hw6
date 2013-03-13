---
#CS322 Languages and Compiler Design II Homework 6

---
##Question 1
<?prettify?>
<pre class="prettyprint linenums">
class TestHeap2 {
  static final int S = 100;
  static final int N = 11;

  public static void main(String[] args) {
    Heap h = Heap.make(S);
    for (int i=0; i&lt;N; i++){
		System.out.println("Allocating object " + i
                       + " at address " + h.alloc(8));
    }
    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());
  }
}
</pre>
The above program can be broken down into the following steps

- make a heap with `100` words (`S = 100`)
- allocate space for `11` objects  
- for each object allocate space for `8` fields (`N = 8`)

Thus the program will only allow `N < 12` ceteris paribus.  

The reason for this is that each object contains `8` fields. which means it is neccessary to allocate `9` words on the heap for each object. `1` for each of the 8 fields and then `1` tag to specify the number of fields conatined within the object.  This means each object in this program neads to allocate space for a total of `9` words on the Heap.  Thus, when N equals `11`, there will be `99` words on the heap.

more succinctly, this program will be able to allocate enough space on the heap if the following relationship holds true:

    N * 9 < S

So, If 'N >= 12' Program will encounter a fatal error due to lack of memory for the heap.

We can manipulate N and S however we see fit, as long as this relationship holds, assuming the implementation of the body of the `For` is held constant.

If we had a working garbage collector, everytime the heap filled up the next object would blow it away and start writing values on a fresh heap since none of the objects have any data in them thats being used.
##Question 2
