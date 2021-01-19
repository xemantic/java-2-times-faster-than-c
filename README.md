# java-2-times-faster-than-c
_An experiment showing double performance of the code running on JVM comparing to equivalent native C code_


## What??? How?

It might be 2 times, it might be 10 times when C code is not optimized.
Hard to say exactly because I still haven't figured out why it is fast.
Please help me explain this phenomenon. I have a hypothesis but
I might be completely mistaken and the claim might be false.
Maybe the only thing that this project is showing are
my poor C skills, because the last time I was coding C was maybe 2 decades ago.
I believe there is plenty of space for improvement, so:

**Please show that I'm wrong!**

The code is almost similar in both languages, still
using typical conventions of both of them:

 * [java_2_times_faster_than_c.c](src/main/c/java_2_times_faster_than_c.c)
 * [Java2TimesFasterThanC.java](src/main/java/com/xemantic/test/howfast/Java2TimesFasterThanC.java)

It also shows how close and influenced Java actually is by C, and how it is designed to
run surprisingly close to the hardware (primitive data types).
The code is establishing a ring of nodes first, and then traversing it continuously while
deleting nodes in one direction and inserting them in another.


## Why?

I tried writing this isolated example because of lack of information on this topic 
on the Internet and because of my intuition which emerged after my experience of
building backend systems for various organizations. During these years I could observe
the evolution of JVM, and how the performance improved, especially in a highly
concurrent environment and with new paradigms like
[reactive programming](https://en.wikipedia.org/wiki/Reactive_programming).


## How typical such a performance boost is?

Hard to say. You can find many microbenchmarks usually showing JVM to be around 10%-20% percent
slower than the equivalent optimized native code. But these benchmarks usually avoid memory
allocation which seems to be an issue here. Many isolated algorithms can be written to avoid
memory management, but it's counterintuitive in the software development process heavily
relaying on standardized data structures varying in size. Therefore I would say that it might be
quite typical.
If your code represents typical dynamic web stack, with data retrieved from the database, possibly
streamed, and converted to JSON on the fly, each request typically involves myriads of allocations
in the pipeline, which are immediately discarded at the end of the request. The goal is to
minimize the response time and JVM seems to greatly contribute to that.


## Myths

There are several modern myths associated with virtual machines and automatic memory management:

 * code executing on VM is always slower than the native one
 * garbage collection is always harming the performance
 * garbage collection is causing "stop the world"

None of these seem to be true these days:

 * it seems that the code executing on VM can be actually much faster than the native one thanks to
   technologies like [HotSpot](https://en.wikipedia.org/wiki/HotSpot_(virtual_machine))
 * garbage collection can actually greatly improve the performance of certain algorithms
 * on JVM GC is mostly happening as a parallel operation these days


## Should I rewrite all my code in Java now?

Absolutely not!!! Performance is not the only reason why we are choosing given language.
When I started coding in JDK 1.0.2 (the first stable release), it was 20 times slower than
the native code, but the Java
code I compiled back then in 1997 still just runs on the newest JVM of
Java 15. I cannot say the same about the code from this time written in Pascal,
Assembler, C, C++. The promise
"[Write once, run anywhere](https://en.wikipedia.org/wiki/Write_once,_run_anywhere)", given me buy
legendary [Sun Microsystems](https://en.wikipedia.org/wiki/Sun_Microsystems)
was kept while the whole runtime and toolchain became open source. This is the actual
superpower of Java I want to pay tribute to - it has been helping me in building
complex software systems for years, with the speed of incremental compilers inherent to the
design of the language from equally legendary
[James Gosling](https://en.wikipedia.org/wiki/James_Gosling).

I rarely code Java myself these days, rather GLSL, JavaScript, HTML, CSS and Kotlin, the last one
still usually running on JVM though, although with JavaScript and native as possible compilation
targets. My IDE runs on JVM as well. Sometimes I transpile Java to JavaScript. Sometimes
I transpile JavaScript to JavaScript. There are plenty of other possible reasons why you
shouldn't use Java:

 * You are proficient in another language.
 * You prefer pure functional programming.
 * JVM-based solutions tend to have higher memory footprint which is disqualifying it in many
   embedded systems.
 * For the code relying mostly on GPU performance gains on CPU might be neglectable.
 * etc.

**But if your solution requires a cluster of 100 servers behind load balancer, then you can maybe
improve average response time from 100ms to 50ms on the same virtual hardware while safely shutting
down half of these machines? It might cut enough Amazon data center costs to hire
2 or 3 more developers :)**

I'm not a big fan of microbenchmarks and language comparisons which are often biased and
misleading without the context, therefore fueling "holy crusades" and
"genital measurement contests".
But I'm a natural born iconoclast, always eager to compare the myth with the reality. And 
in reality you will often hear "arguments from performance" which are equally often
irrelevant to the context they are expressed in. Language is just a tool. Spoken is often
cherished on the altar of national ideology and computer ones are often becoming a fetish
of our idiosyncrasy which we impose on the others. We can do better.

From my experience of leading brilliant software teams I've learned that the actual quality
change in performance does not come from particular technology, but rather from the paradigm
shift in the architecture of the whole system. JVM might be a tool of improvement, but it can
also be misused terribly.


## Build

In the project dir:

```console
$ ./build-c.sh
$ ./gradlew build
```

## Running

```console
$ time ./build/c/java_2_times_faster_than_c 
checksum: 5000000494530

real	0m56,769s
user	0m56,766s
sys	0m0,001s
$ time java -cp build/classes/java/main com.xemantic.test.howfast.Java2TimesFasterThanC 
checksum: 5000000494530

real	0m34,139s
user	0m34,831s
sys	0m0,386s
```


## Future research

I would like to test equivalent code with some other languages:

 * Go
 * Rust
 * C#
 * Kotlin on JVM  
 * JavaScript on node and in the browser
 * Kotlin transpiled to JS also on node and in the browser

Any contributions are welcome.
