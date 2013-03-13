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
The implementation of `forward()` and `scavenge()` is as follows and annotated appropriately:
<pre>
   private int forward(int obj) {
        // If obj is not a pointer (i.e., if it is greater than
        // or equal to zero), then you should just return obj
        // directly
        if (obj >= 0) return obj;
        // If obj is a pointer, but the length field of the object
        // that it points to is negative, then (a) we can conclude
        // that the object has already been forwarded; and (b) we
        // can just return the (negative) length value, which is a
        // pointer to the object's new location in toSpace.
        int addy = size + obj;
        if (heap[addy] < 0) return heap[addy];
        // If obj is a pointer and the length field is non negative,
        // then it points to an object that needs to be forwarded to
        // toSpace.  This requires us to copy the length word and
        // the associated fields from the heap into the toSpace.
        // After that, we overwrite the length field with a pointer
        // to the location of the object in toSpace (because it is
        // a pointer, this will be a negative number).
        int retval = hp - size;  //this will return the proper pointer to this object
        if (heap[addy] >= 0) {

            System.out.println("FORWARD:  object at " + obj);
            // now we need to copy not only the objects len field
            // but also every field in that object from heap into
            // toSpace
            for (int i = 0; i <= heap[addy]; i++) {
                toSpace[hp++] = heap[addy + i];
            }
            heap[addy] = retval;
        }
        return retval;
    }

    private int scavenge(int obj) {
        System.out.println("\nBEGIN SCAVENGE: object at address " + (obj-size));
        int len = toSpace[obj];
        // Scan the fields in this object, using forward on
        // any pointer fields that we find to make sure the
        // objects that they refer to are copied into toSpace.

        for (int i = obj; i <= (obj + len); i++) {
            toSpace[i] = forward(toSpace[i]);
        }
        System.out.println("END SCAVENGE: object at address " + (obj-size));
        return len + 1;
    }
</pre>
this implementation is tested thouroughly in the following two questions, but for completeness the output of TestHeap2 - 4 is included here.  these tests were run with `Heap.make()` set to return an instance of `TwoSpace.java` so that the forward and scavenge methods would be used where appropriate.  Also, in order to more thoroghly illustrate their behavior print statements have been included within the body of the functions to denote scavaging and forwarding as shown in the code above: 
###Output

---
####TestHeap2
Output with `N = 15` && `S = 100`
<pre>
Allocating object 0 at address -100
Allocating object 1 at address -91
Allocating object 2 at address -82
Allocating object 3 at address -73
Allocating object 4 at address -64
Allocating object 5 at address -55
Allocating object 6 at address -46
Allocating object 7 at address -37
Allocating object 8 at address -28
Allocating object 9 at address -19
Allocating object 10 at address -10
Allocating object 11 at address -100
Allocating object 12 at address -91
Allocating object 13 at address -82
Allocating object 14 at address -73
Object at address -100, length=8, data=[0, 0, 0, 0, 0, 0, 0, 0]
Object at address -91, length=8, data=[0, 0, 0, 0, 0, 0, 0, 0]
Object at address -82, length=8, data=[0, 0, 0, 0, 0, 0, 0, 0]
Object at address -73, length=8, data=[0, 0, 0, 0, 0, 0, 0, 0]
Heap allocation pointer: 36
Free space remaining = 64
</pre>
###TestHeap3
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
FORWARD:  object at -96

BEGIN SCAVENGE: object at address -100
FORWARD:  object at -82
FORWARD:  object at -70
END SCAVENGE: object at address -100

BEGIN SCAVENGE: object at address -90
END SCAVENGE: object at address -90

BEGIN SCAVENGE: object at address -84
END SCAVENGE: object at address -84
AFTER COLLECTION**********************************
Object at address -100, length=9, data=[-90, -84, 0, 0, 0, 0, 0, 0, 0]
Object at address -90, length=5, data=[0, 0, 0, 0, 0]
Object at address -84, length=4, data=[0, 0, 0, 0]
Heap allocation pointer: 21
Free space remaining = 79
</pre>
###TestHeap4
<pre>
Allocating object 2 at address -65
Allocating object 3 at address -61
Allocating object 4 at address -56
Allocating object 5 at address -50
Allocating object 6 at address -43
Allocating object 7 at address -35
Allocating object 8 at address -26
Allocating object 9 at address -16
Before garbage collection;
Object at address -65, length=3, data=[-66, -63, 0]
Object at address -61, length=4, data=[-62, -58, 0, 0]
Object at address -56, length=5, data=[-57, -52, 0, 0, 0]
Object at address -50, length=6, data=[-51, -45, 0, 0, 0, 0]
Object at address -43, length=7, data=[-44, -37, 0, 0, 0, 0, 0]
Object at address -35, length=8, data=[-36, -28, 0, 0, 0, 0, 0, 0]
Object at address -26, length=9, data=[-27, -18, 0, 0, 0, 0, 0, 0, 0]
Object at address -16, length=10, data=[-17, -7, 0, 0, 0, 0, 0, 0, 0, 0]
Heap allocation pointer: 60
Free space remaining = 5
FORWARD:  object at -16

BEGIN SCAVENGE: object at address -65
FORWARD:  object at -17
FORWARD:  object at -7
END SCAVENGE: object at address -65

BEGIN SCAVENGE: object at address -54
END SCAVENGE: object at address -54

BEGIN SCAVENGE: object at address -53
END SCAVENGE: object at address -53
After garbage collection;
Object at address -65, length=10, data=[-54, -53, 0, 0, 0, 0, 0, 0, 0, 0]
Object at address -54, length=0, data=[]
Object at address -53, length=0, data=[]
Heap allocation pointer: 13
Free space remaining = 52
FORWARD:  object at -65

BEGIN SCAVENGE: object at address -65
FORWARD:  object at -54
FORWARD:  object at -53
END SCAVENGE: object at address -65

BEGIN SCAVENGE: object at address -54
END SCAVENGE: object at address -54

BEGIN SCAVENGE: object at address -53
END SCAVENGE: object at address -53
After garbage collection;
Object at address -65, length=10, data=[-54, -53, 0, 0, 0, 0, 0, 0, 0, 0]
Object at address -54, length=0, data=[]
Object at address -53, length=0, data=[]
Heap allocation pointer: 13
Free space remaining = 52
FORWARD:  object at -65

BEGIN SCAVENGE: object at address -65
FORWARD:  object at -54
FORWARD:  object at -53
END SCAVENGE: object at address -65

BEGIN SCAVENGE: object at address -54
END SCAVENGE: object at address -54

BEGIN SCAVENGE: object at address -53
END SCAVENGE: object at address -53
After garbage collection;
Object at address -65, length=10, data=[-54, -53, 0, 0, 0, 0, 0, 0, 0, 0]
Object at address -54, length=0, data=[]
Object at address -53, length=0, data=[]
Heap allocation pointer: 13
Free space remaining = 52
FORWARD:  object at -65

BEGIN SCAVENGE: object at address -65
FORWARD:  object at -54
FORWARD:  object at -53
END SCAVENGE: object at address -65

BEGIN SCAVENGE: object at address -54
END SCAVENGE: object at address -54

BEGIN SCAVENGE: object at address -53
END SCAVENGE: object at address -53
After garbage collection;
Object at address -65, length=10, data=[-54, -53, 0, 0, 0, 0, 0, 0, 0, 0]
Object at address -54, length=0, data=[]
Object at address -53, length=0, data=[]
Heap allocation pointer: 13
Free space remaining = 52
</pre>

***Please Note:*** The specific tests which produced the output above have been included in their entirety in the appendix for reference.


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
Object at address -100, length=9, data=[-90, -84, 0, 0, 0, 0, 0, 0, 0]
Object at address -90, length=5, data=[0, 0, 0, 0, 0]
Object at address -84, length=4, data=[0, 0, 0, 0]
Heap allocation pointer: 21
Free space remaining = 79
</pre></br>
this output shows that after gargbage collection occurs the object pointed to within the the object of length 9 as well as that object itself were retained while everything else was thrown away.  Furthermore, the pointers stored in the first object of length 9 have been amended to reference the new addresses for the objects they reference.  this is very good news indeed.</br></br>
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

##Question 4
1.  In order for this program to function without resulting in `Fatal error: Out of memory!` the following relationship between `S` and `N` must hold<pre><img src="http://latex.codecogs.com/gif.latex?S>\sum_{i=2}^{N}i" border="0"/></pre>
2.  Calling `garbageCollect()` on the heap produced by this program will preserve all objects in reverse order on the new, garbage collected, heap. 
3.  cyclic structures can be created and preserved by the garbage collecter which is illustrated in the following annotated program and its accompanying output.<pre>
    class TestHeap5 {
        static final int S = 42;
            public static void main(String[] args) {
            Heap h = Heap.make(S);
      	    // allocate space for two objects on the heap
            h.a = h.alloc(1);
	        h.b = h.alloc(1);	
            // store a pointer to the second object in the first object
	        h.store(h.a, 1, h.b);            
            //store a pointer to the first object in the second
	        h.store(h.b, 1, h.a);	
	        System.out.println("Before garbage collection;");
	        h.dump();
	        System.out.println("Free space remaining = " + h.freeSpace());	
	        h.garbageCollect();
	        System.out.println("After garbage collection;");
	        h.dump();
	        System.out.println("Free space remaining = " + h.freeSpace());
        }
}
</pre><pre>Before garbage collection;
Object at address -42, length=1, data=[-40]
Object at address -40, length=1, data=[-42]
Heap allocation pointer: 4
Free space remaining = 38
After garbage collection;
Object at address -42, length=1, data=[-40]
Object at address -40, length=1, data=[-42]
Heap allocation pointer: 4
Free space remaining = 38
</pre>
Its apparent from the output generated above that the two objects point to eachother and that they are indeed being preserved on the heap after garbage collection.  However, for good measure and for science I repeated the experiment twice more for good measure by appending two more garbage collection cycles to the previous program and the results confirm that the values appear to be preserved.<pre>Before garbage collection;
Object at address -42, length=1, data=[-40]
Object at address -40, length=1, data=[-42]
Heap allocation pointer: 4
Free space remaining = 38
After garbage collection;
Object at address -42, length=1, data=[-40]
Object at address -40, length=1, data=[-42]
Heap allocation pointer: 4
Free space remaining = 38
After garbage collection;
Object at address -42, length=1, data=[-40]
Object at address -40, length=1, data=[-42]
Heap allocation pointer: 4
Free space remaining = 38
After garbage collection;
Object at address -42, length=1, data=[-40]
Object at address -40, length=1, data=[-42]
Heap allocation pointer: 4
Free space remaining = 38</pre>

##Appendix
<pre>
class TestHeap2 {
  static final int S = 100;
  static final int N = 15;

  public static void main(String[] args) {
    Heap h = Heap.make(S);
    for (int i=0; i &lt; N; i++) {
      System.out.println("Allocating object " + i
                       + " at address " + h.alloc(8));
    }
    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());
  }
}
</pre>
</br>
<pre>
class TestHeap3 {
  static final int S = 100;
    // declare local variable
    static int t;
  public static void main(String[] args) {
    Heap h = Heap.make(S);
    h.alloc(3);
    //assign position of heap pointer to local var.
    h.a = h.alloc(9);
    h.alloc(3);
    h.store(h.a, 1, h.alloc(5));
    h.alloc(5);
    h.store(h.a, 2, h.alloc(4));
    h.alloc(2);

    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());

    h.garbageCollect();

      System.out.println("AFTER COLLECTION**********************************");
    h.dump();
    System.out.println("Free space remaining = " + h.freeSpace());
  }
}
</pre>
<pre>
class TestHeap4 {
    static final int S = 65;
    static final int N = 10;

    public static void main(String[] args) {
        Heap h = Heap.make(S);
        for (int i = 2; i &lt; N; i++) {
            int t = h.alloc(1 + i);
            System.out.println("Allocating object " + i + " at address " + t);
            h.store(t, 1, t - 1);
            h.store(t, 2, t + i);
            h.a = t;
        }
        System.out.println("Before garbage collection;");
        h.dump();
        System.out.println("Free space remaining = " + h.freeSpace());

        h.garbageCollect();

        System.out.println("After garbage collection;");
        h.dump();
        System.out.println("Free space remaining = " + h.freeSpace());

        h.garbageCollect();


        System.out.println("After garbage collection;");
        h.dump();
        System.out.println("Free space remaining = " + h.freeSpace());

        h.garbageCollect();


        System.out.println("After garbage collection;");
        h.dump();
        System.out.println("Free space remaining = " + h.freeSpace());

        h.garbageCollect();


        System.out.println("After garbage collection;");
        h.dump();
        System.out.println("Free space remaining = " + h.freeSpace());

    }
}
</pre>