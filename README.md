# java-2-times-faster-than-c
_An experiment showing double performance of the code running on JVM comparing to equivalent native C code_

![artwork](https://repository-images.githubusercontent.com/330790563/c56fa080-5a76-11eb-81d2-515c4cc4b42d)

:warning: The title of this project is provocative, and it is meant to be, to bring
attention to certain ideas. Please read through this document before jumping to any conclusions.
For now, I will just say that the title applies just to the algorithm presented here, not to
Java and C in general. I am also the farthest away from convincing anyone to choose Java
over any other language, and I even see good reasons to discourage you from using Java in
plethora of cases. I rarely code Java myself these days.


## My typical dialog from the past days

> "Your code running on a virtual machine will be ALWAYS slower than equivalent native code."
> 
> "Why?"
> 
> "Because of automatic memory management."
> 
> "Why is it so?"
> 
> "Things like automatic memory management ALWAYS add additional overhead to execution."
> 
> "Hmm, let me try, here is a code in Java, and direct equivalent in C,
> the first one is almost 2 times faster."
> 
> "It's because you are doing things wrong. No one would write C code like this."
> 
> "Why?"
> 
> "Because you need to properly manage your memory for efficiency."
> 
> "How do you do it?"
> 
> "Depending on your problem, sometimes even by adding automatic memory management."
> 
> "Ok, so did you just make contradictory statements?"
> 
> "I don't think so, just add these few lines to your code."
> 
> "Do you think it's still the same algorithm afterwards?"
> 
> "Yeah."
> 
> "But is your memory management solution adjusted to this specific C code and
> therefore extending the algorithm?"
> 
> "Yeah."
> 
> "So it's no longer algorithmically equivalent code, isn't it?"
> 
> "Yeah."
> 
> "Did you just make contradictory statements again?"
> 
> "I don't think so."


## Show me the code

The code is almost the same in both languages, still using typical conventions of both of them:

* [java_2_times_faster_than_c.c](src/main/c/java_2_times_faster_than_c.c)
* [Java2TimesFasterThanC.java](src/main/java/com/xemantic/test/howfast/Java2TimesFasterThanC.java)

I am pretty convinced that algorithmically they are equal, except for obvious explicit memory
releasing in C version. Here is an
[old but comprehensive article](https://www.ibm.com/developerworks/java/library/j-jtp09275/index.html)
shedding some light on my results.

I haven't written any C code for 2 decades, and it was nice to write some now, to rediscover how
close and influenced Java actually is by C, and how it is designed to run surprisingly close
to the hardware (primitive data types).

The code is establishing a ring of nodes first, and then mutating it continuously while
deleting nodes in one direction and inserting them in another.
The number of inserted and deleted nodes is unpredictable. The same applies to the size
of a node. Still the pseudo random distribution will be exactly the same for Java and C.

To achieve this, I took deterministic, almost random distribution I often use in GLSL,
which I borrowed from [The Book of Shaders](https://thebookofshaders.com/). I also wrote
a benchmark for this one:

* [almost_pseudo_random.c](src/main/c/almost_pseudo_random.c)
* [AlmostPseudoRandom](src/main/java/com/xemantic/test/howfast/AlmostPseudoRandom.java)

I was expecting this time C code to be 2 times faster, but to my surprise Java version is faster
again (although not 2 times), which I cannot explain. I have many hypothesis:

* HotSpot is doing some aggressive inlining possible after the running code is analyzed for a while.
* C math functions are from the library, so maybe they actually cannot be inlined, while HotSpot
  has the freedom of inlining whatever it pleases.
* Unlike C, Java allows using the `%` operator also for floating point numbers. It might be mapped
  onto more effective machine code.

Please feel free to disassemble the code and create PR with proper explanation. It is also
possible to dump assembly running on JVM:

https://wiki.openjdk.java.net/display/HotSpot/PrintAssembly


## Speeding up C version

My example is pushing things to absurd, for a reason. Of course it is possible to outperform
Java version by managing memory better in C. But it would imply embedding additional algorithms
of memory management into my original code, therefore I wouldn't call it "equivalent" anymore
in algorithmic sense, because memory allocation, and releasing it implicitly or explicitly,
is a crucial part of this algorithm.

While saying that, I've received amazing feedback showing me how to achieve extremely efficient
memory management in C, for example in ticket [#1](../../issues/1), and I am grateful for this
contribution and opportunity to learn. Therefore I would like to include also extra version of this
algorithm in C, but with more efficient memory management, also taking variable size of data
structures into account. Unfortunately my limited C experience does not allow me at this point to
write it myself. :( If you feel up for this challenge, please contribute to this project.

And here is my hypothesis:

**Certain classes of algorithms can gain extra performance just thanks to the fact of being
expressed in the language assuming automatic memory management.**

My experience of writing complex distributed systems, and also my intuition, tells me that
these algorithms are pretty common, and in the same time I have a feeling that these cases are
rarely covered in microbenchmarks comparing speed of the code expressed and compiled in
different languages. If there is a minimal thing I want to achieve with this experiment, it is to
convince myself and others, to always question certain dogmas of modern software development and
validity of certain arguments in given context. Please check issue [#2](../../issues/2) as an
exemplum of what I am referring to.


## Does it have any practical implications?

It does not, except from methodological perspective it seems to falsify certain statements with 
generalized quantifiers. So it becomes rather something like:

"Your algorithm written in code designed to run on a virtual machine will be usually slower than
equivalent native code."

"always", becomes "usually", and "usually" implies that from now on we should rather revalidate for
each case than make categorical statements.

Common wisdom from microbenchmarks is usually showing JVM to be around 10%-20% percent
slower than the equivalent optimized native code, with big outliers in favor of the
native code. My simplest microbenchmark with `almost pseudo random` is showing something
opposite, but I wouldn't jump to any conclusion from it.

But how do these tests actually compare to real life code where most of the software is
using data structures already standardized for each language?
I don't know of any benchmarks which can objectively, in generic way, measure the effects which I
am describing here. I don't believe that my experiment can be really scaled. But I believe that the
power of certain algorithmic expressiveness improves the performance
instead of degrading it, like in case of [reactive programming](https://en.wikipedia.org/wiki/Reactive_programming)
paradigm. Of course by principle, at the end of the day, everything can be reduced to machine code,
which can be optimized to extreme in assembly code. But would it be really algorithmically
equivalent? I will leave this question open.

In distributed systems powering the dynamic internet services we are using, the CPU based
performance is usually impacted by IO throughput. Overall performance improvement will come not
from the fact that our code is faster, but from the fact that we are waiting "better",
with less contention, while collecting the garbage in parallel, and while preventing the whole
system from entering the [thrashing state](https://en.wikipedia.org/wiki/Thrashing_(computer_science))
with tools like [circuit breakers](https://en.wikipedia.org/wiki/Circuit_breaker_design_pattern).
If your code represents typical dynamic web stack, with data retrieved from the database, possibly
streamed, and converted to JSON on the fly, each request typically involves myriads of new data
instances of unpredictable size in the pipeline, which are immediately discarded at the end of the
request. The goal is to minimize the response time and virtual machines seem to greatly contribute
to that.


## Myths and Urban Legends of modern computing :)

Just to recapitulate the myths associated with virtual machines and automatic memory management:

 * code executing on VM is ALWAYS slower than the native one
 * garbage collection is ALWAYS harming the performance
 * garbage collection is causing "stop the world"

None of these seem to be true these days:

 * it seems that the code executing on VM can be actually quite optimal thanks to
   technologies like [HotSpot](https://en.wikipedia.org/wiki/HotSpot_(virtual_machine))
   which even my simplest benchmark shows.
 * garbage collection can actually greatly improve the performance of common algorithms
 * on JVM GC is mostly happening as a parallel operation these days


## Should I rewrite all my code in Java now?

Absolutely not!!! Performance is not the only reason why we are choosing given language.
When I started coding in JDK 1.0.2 (the first stable release), it was 20 times slower than
the native code, but the Java code I compiled back then in 1997 still just runs on the
newest JVM of Java 15. I cannot say the same about the code from this time written in Pascal,
Assembler, C, C++. The promise
"[Write once, run anywhere](https://en.wikipedia.org/wiki/Write_once,_run_anywhere)", given me by
legendary [Sun Microsystems](https://en.wikipedia.org/wiki/Sun_Microsystems),
was kept while the whole runtime and toolchain became open source. This is the actual
superpower of Java I want to pay tribute to - it has been helping me in building
complex software systems for years, with the speed of great toolchain of remote debuggers,
statistical profilers and incremental compilers inherent to the design of the language from equally
legendary [James Gosling](https://en.wikipedia.org/wiki/James_Gosling).

But I also want to pay a tribute to C, which is powering the Linux kernel - the operating system
we are using every day, even if we are not aware of it. It might be in our Android phone
or tablet, router, and all the servers on the way passing the signal from one human brain
to another. Even git itself, the tool managing source code of this project, is written in C.
And all of it thanks to the charisma of one person -
[Linus Torvalds](https://en.wikipedia.org/wiki/Linus_Torvalds).

I rarely code Java myself these days, rather GLSL, JavaScript, HTML, CSS and Kotlin, the last one
still usually running on JVM, although with JavaScript and native as possible compilation
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

I did this for several organizations in the past, while always improving the performance of the
stack by order of magnitude.

I'm not a big fan of microbenchmarks and language comparisons which are often biased and
misleading without the context, therefore fueling "holy crusades" and "genital measurement contests".
But I'm a natural born iconoclast, always eager to compare the myth with the reality. And 
in reality you will often hear "arguments from performance" which are equally often
irrelevant to the context they are expressed in. Language is just a tool. Spoken is often
cherished on the altar of national ideology and computer ones are often becoming a fetish
of our idiosyncrasy which we impose on the others. We can do better. I write WE, because
obviously I am also not free from these tendencies. :)

From my experience of leading brilliant software teams I've learned that the actual quality
change in performance does not come from particular technology, but rather from the paradigm
shift in the architecture of the whole system. Technologies like JVM might be a tool of
improvement, but they can also be misused terribly.


## Build

In the project dir:

```console
$ ./build-c.sh
$ ./gradlew build
```

## Running

Here are tests results from my machine:

```console
$ time ./build/c/java_2_times_faster_than_c 
node count: 1079
checksum: 410502150

real    1m17,218s
user    1m17,210s
sys     0m0,004s
$ time java -cp build/classes/java/main com.xemantic.test.howfast.Java2TimesFasterThanC
node count: 1079
checksum: 410502150

real    0m23,768s
user    0m24,515s
sys     0m0,731s
```




<details>
<summary>Click to see the specs of my machine</summary>
<p>

```console
$ cat /proc/cpuinfo 
processor	: 0
vendor_id	: GenuineIntel
cpu family	: 6
model		: 142
model name	: Intel(R) Core(TM) i7-8550U CPU @ 1.80GHz
stepping	: 10
microcode	: 0xe0
cpu MHz		: 700.046
cache size	: 8192 KB
physical id	: 0
siblings	: 8
core id		: 0
cpu cores	: 4
apicid		: 0
initial apicid	: 0
fpu		: yes
fpu_exception	: yes
cpuid level	: 22
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx pdpe1gb rdtscp lm constant_tsc art arch_perfmon pebs bts rep_good nopl xtopology nonstop_tsc cpuid aperfmperf pni pclmulqdq dtes64 monitor ds_cpl vmx est tm2 ssse3 sdbg fma cx16 xtpr pdcm pcid sse4_1 sse4_2 x2apic movbe popcnt tsc_deadline_timer aes xsave avx f16c rdrand lahf_lm abm 3dnowprefetch cpuid_fault epb invpcid_single pti ssbd ibrs ibpb stibp tpr_shadow vnmi flexpriority ept vpid ept_ad fsgsbase tsc_adjust bmi1 avx2 smep bmi2 erms invpcid mpx rdseed adx smap clflushopt intel_pt xsaveopt xsavec xgetbv1 xsaves dtherm ida arat pln pts hwp hwp_notify hwp_act_window hwp_epp md_clear flush_l1d
vmx flags	: vnmi preemption_timer invvpid ept_x_only ept_ad ept_1gb flexpriority tsc_offset vtpr mtf vapic ept vpid unrestricted_guest ple pml ept_mode_based_exec
bugs		: cpu_meltdown spectre_v1 spectre_v2 spec_store_bypass l1tf mds swapgs itlb_multihit srbds
bogomips	: 3999.93
clflush size	: 64
cache_alignment	: 64
address sizes	: 39 bits physical, 48 bits virtual
power management:

```
x8 cores

```console
$ cat /proc/meminfo 
MemTotal:       16102660 kB
MemFree:          710648 kB
MemAvailable:    4814532 kB
// ...
```

</p>
</details> 

## Contributions

### C# version

Contributed by [hercegyu](https://github.com/hercegyu) 

```console
$ wget https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
$ sudo dpkg -i packages-microsoft-prod.deb
$ sudo apt-get update
$ sudo apt-get install dotnet-sdk-5.0
$ ./build-csharp
$ time ./build/csharp/java-4-times-faster-than-c-sharp 
node count: 1079
checksum: 410502150

real    3m11,511s
user    3m10,530s
sys     0m8,327s
```


## Future research

I would like to test equivalent code with some other languages:

 * Go
 * Rust
 * Kotlin on JVM  
 * JavaScript on node and in the browser
 * Kotlin transpiled to JS also on node and in the browser

Any contributions are welcome.
