# dictomaton

## Introduction

This Java library implements dictionaries that are stored in finite state
automata. *Dictomaton* has the following features:

 * Finite state dictionaries that implement the Java Set<String> interface.
 * Perfect hash dictionaries, that provide a unique hash for each character
   sequence that is in the dictionary. Perfect hash dictionaries can be used
   in two directions: (1) obtaining the hash code for a character sequence
   and (2) obtaining the character sequence for a hash code.
 * Levenshtein automata, that allow you to efficiently find all the sequences
   in the dictionary that are within the given edit distance of a sequence.
 * String to primitive type mappings, where the keys are stored in a perfect
   hashing automaton and the values in an (unboxed) array.

## Using Dictomaton

Dictomaton is in the Maven Central Repository:

~~~
<dependency>
    <groupId>eu.danieldk.dictomaton</groupId>
    <artifactId>dictomaton</artifactId>
    <version>1.1.1</version>
</dependency>
~~~

SBT:

~~~
libraryDependencies += "eu.danieldk.dictomaton" % "dictomaton" % "1.1.1"
~~~

Grails:

~~~
compile 'eu.danieldk.dictomaton:dictomaton:1.1.1'
~~~

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

    mvn test -Djunit.groups=eu.danieldk.dictomaton.categories.Benchmarks

## Changelog

### 1.2.0

* Exposing state through StateInfo object, which allows user of PerfectHashDictionary to resume transitions, which makes it e.g. far more efficient to look up a string and its prefixes. (contributed by René Kriegler).
* DictionaryBuilder now accepts adding more general CharSequence instead of String and uses CharSequence internally (contributed by René Kriegler).

### 1.1.0

* Added immutable mapping from String to a generic type.
* Added a key-ordered builder for immutable mappings. This builder is more
  efficient since it construct the key automaton on the fly.

### 1.0.0

* Added Levenshtein automata for looking up sequences in a <tt>Dictionary</tt> that
  are within a certain edit distance of a sequence.
* Provide a variant of perfect hash automata that puts right language
  cardinalities in transitions rather than states. This provides faster
  hashing and hashcode lookups at the cost of some memory.
* Added String to String mapping (<tt>ImmutableStringStringMap</tt>).
* Generic object values.

### 0.0.3

* Fix an off-by-one error in integer width of the state table.

### 0.0.2

* Rename the project from *fsadict-java* to *dictomaton*.
* Store the state and transition tables as packed int arrays, resulting in drastically smaller automata.


## Release plan

Plans for 1.3.0: Perhaps an explicit, fast, and compact data storage format
as an alternative to Java serialization. C or C++ version.

## Contributors

* Daniël de Kok (maintainer)
* René Kriegler
