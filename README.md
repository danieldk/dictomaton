# fsadict-java

## Introduction

This Java package implements dictionaries that are stored in finite state
automata. Two types of dictionaries are supported:

 * Finite state dictionaries that can be checked for membership and
   iterated.
 * Perfect hash dictionaries, that also provide a unique hash for each
   character sequence that is in the dictionary.

*fsadict-java* also contains maps from Strings to primitive types, where
keys are stored in a perfect hash dictionary and the values in a flat array
for compact and fast storage.

## Benchmarks

Benchmarks are in a different test group than normal unit tests. You can run
benchmarks via Maven, adding the Benchmarks group:

    mvn test -Djunit.groups=eu.danieldk.fsadict.categories.Benchmarks

## Release plan

 * **0.0.2**: project and package name finalization. fsadict-java is not
   a good name, come up with something better and name project and packages
   accordingly.
 * **0.0.3**: add ImmutableStringStringMap, wherein keys are also stored
   in an automaton.
 * **0.0.4**: generic object keys.
 * **1.0.0**: first stable release.

Plans for 1.2.0: use short-based arrays for small automata. Perhaps an
explicit, fast, and compact data storage format as an alternative to Java
serialization. C or C++ version.
