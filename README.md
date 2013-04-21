# fsadict-java

## Introduction

This Java library implements dictionaries that are stored in finite state
automata. Two types of dictionaries are supported:

 * Finite state dictionaries that can be checked for membership and
   iterated.
 * Perfect hash dictionaries, that also provide a unique hash for each
   character sequence that is in the dictionary.

*fsadict-java* also contains maps from Strings to primitive types, where
keys are stored in a perfect hash dictionary and the values in a flat array
for compact and fast storage.

## Comparisons

The following table compares the sizes of the object graphs of the
<tt>Dictionary</tt> type of this library to that of <tt>TreeSet</tt> and
<tt>HashSet</tt>. The comparisons were obtained by storing all the words
in the *web2* and *web2a* dictionaries and were measured using
[memory-measurer](https://code.google.com/p/memory-measurer/)

<table>
   <tr><th>Data type</th><th>Objects</th><th>References</th><th>char</th><th>int</th><th>boolean</th><th>float</th></tr>
   <tr><td>TreeSet<String></td><td align="right">936277</td><td align="right">1872555</td><td align="right">3193749</td><td align="right">624184</td><td align="right">312091</td><td>0</td></tr>
   <tr><td>HashSet<String></td><td align="right">936277</td><td align="right">1772657</td><td align="right">3193749</td><td align="right">936277</td><td align="right">1</td><td>1</td></tr>
   <tr><td>Dictionary<String></td><td align="right">41188</td><td align="right">94546</td><td align="right">424169</td><td align="right">397033</td><td align="right">1</td><td>1</td></tr>
</table>

## Benchmarks

Benchmarks are in a different test group than normal unit tests. You can run
benchmarks via Maven, adding the Benchmarks group:

    mvn test -Djunit.groups=eu.danieldk.fsadict.categories.Benchmarks

## Release plan

 * **0.0.2**: project and package name finalization. fsadict-java is not
   a good name, come up with something better and name project and packages
   accordingly.
 * **0.0.3**: add ImmutableStringStringMap, wherein values are also stored
   in an automaton.
 * **0.0.4**: generic object values.
 * **1.0.0**: first stable release.

Plans for 1.2.0: use short-based arrays for small automata. Perhaps an
explicit, fast, and compact data storage format as an alternative to Java
serialization. C or C++ version.
