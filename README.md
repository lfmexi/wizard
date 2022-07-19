WIZARD
======
A Card game, which brings people into Rage!

A small sample project that applies some Domain Driven Design principles.

## Overall Architecture

I've tried to identify some bounded contexts (sorry if I've done that poorly), therefore, I am trying to have a sort of 
modular monolith that follows internally the hexagonal architecture. So far, the main bounded contexts that have been
identified are:

- Games: contains the whole logic of playing a wizard game.
- Players: everything that is going to be related to the players of a Wizard game.

If I identify more contexts, I will keep doing the refactoring.

## Build & Test

This project uses gradle. To build & test just do:

```
./gradlew build
```
