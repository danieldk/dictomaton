# fsadict-java

## Introduction

This Java package implements dictionaries that are stored in finite state
automata. Two types of dictionaries are supported:

 * Finite state dictionaries that can be checked for membership and
   iterated.
 * Perfect hash dictionaries, that also provide a unique hash for each
   character sequence that is in the dictionary.

## Benchmarks

Benchmarks are in a different test group than normal unit tests. You can run
benchmarks via Maven, adding the Benchmarks group:

  mvn test -Dgroups=eu.danieldk.fsadict.categories.Benchmarks
