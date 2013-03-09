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

    mvn test -Dgroups=eu.danieldk.fsadict.categories.Benchmarks
