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

##Question 3
1.  The referenced program produces the following output:
<?prettify?>
<pre>
	Object at address -100, length=3, data=[0, 0, 0]
	Object at address -96, length=9, data=[-82, -70, 0, 0, 0, 0, 0, 0, 0]
	Object at address -86, length=3, data=[0, 0, 0]
	Object at address -82, length=5, data=[0, 0, 0, 0, 0]
	Object at address -76, length=5, data=[0, 0, 0, 0, 0]
	Object at address -70, length=4, data=[0, 0, 0, 0]
	Object at address -65, length=2, data=[0, 0]
	Heap allocation pointer: 38
	Free space remaining = 62
	AFTER COLLECTION**********************************
	Object at address -100, length=9, data=[10, 16, 0, 0, 0, 0, 0, 0, 0]
	Object at address -90, length=5, data=[0, 0, 0, 0, 0]
	Object at address -84, length=4, data=[0, 0, 0, 0]
	Heap allocation pointer: 21
	Free space remaining = 79
</pre></br>
this output shows that after gargbage collection occurs the object pointed to within the the object of length 9 as well as that object its self were retained while everything else was thrown away.  Furthermore, the pointers are no longer preprsented as negative numbers.  Thus, if the Garbage collector is run again these three objects will be thrown away.  This gives the garbage collector a sort of "generational" property.</br></br>
2.  The following code snippet illustrates how the program testHeap3.java can be modified so that `h.a` is replaced with a local integer variable.  We do this by first declaring a static local integer variable and then assigning the heap pointer returned by alloc to that variable.
<?prettify?>
<pre>
    ...

    // declare local variable
    static int t;

  public static void main(String[] args) {
    Heap h = Heap.make(S);
    h.alloc(3);

    //assign position of heap pointer to local var.
    t  = h.alloc(9);

    h.alloc(3);
    h.store(h.a, 1, h.alloc(5));      
   
    ...
</pre></br>
running this program produces the following output:</br>
`Fatal error: Invalid object reference`</br>
The reason this error is received is because we have lost the our reference to where we are in the heap and therefore we con't allocate subsequent objects appropriately.
3.  If the referenced code snippet is ommited and the data in each new object is not initialized to zero during object creation then it will contain "junk" data which may have a negative value.  If that happens, such data will be interpreted by our `garbage collector` as a pointer to a valid object.  In the best case it points to a valid address on the heap and is saved inadvertently when it should have been discarded.  In the worst case it points to an address outside the heap and our program either explodes or becomes self aware and tries to eradicate the human infestation of earth.