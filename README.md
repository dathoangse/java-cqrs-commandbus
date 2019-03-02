# Java CQRS
A lightweight & highly extensible CQRS framework for implementing CQRS architectural pattern in java

## Who uses Java CQRS
* [YouthDev](https://youthdev.net/en/)

## Build status
[![Build Status](https://travis-ci.com/dathoangse/java-cqrs.svg?branch=develop)](https://travis-ci.com/dathoangse/java-cqrs)

## Code style
The project follows [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for code style & convention.

## Git workflow
The project uses [Git flow](https://nvie.com/posts/a-successful-git-branching-model/) for git workflow.

## Versioning
The project uses [Semantic Versioning 2.0.0 | Semantic Versioning](https://semver.org/) for versioning releases.

Special note: When the major version of the project is still 0 (ex: v0.1.3), the framework is not ready for production use, and the framework interface is still not stable (which means there might be some breaking changes between minor versions when the major version is still 0).

## Libs/frameworks used
* [JUnit 5](https://junit.org/junit5/) for unit testing.
* [AssertJ](http://joel-costigliola.github.io/assertj/) for assertions in unit test.
* [Mockito](https://github.com/mockito/mockito) for mocking in unit test.
* [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/)  as logging interface.

Built with:
* [Gradle Build Tool](https://gradle.org/).

# Features
`Java CQRS` is a very lightweight and highly extensible CQRS framework that help you implement the CQRS architecture into your project.
Characteristics:
1. `Lightweight`: The core interfaces of the framework is minimized, making it easy to swap out the implementation of the whole framework, and you can easily integrate into your project with little efforts.
2. `Highly extensible`: The lib provide a very high extensibility via a *middleware pipeline* and well-structured modules.
3. `Architecturally unoptionated`: The framework doesn't make any assumption about the architecture you're building, thus it doesn't force you to design your architecture in a certain architectural style. CQRS can be implemented at different levels & scales, depending on your project requirement.
So:
* You can simply take the command bus and use it to implement your application layer to act as a bridge between your UI/presentation layer & domain layer, and simply have the read side (read models/operations) and write side (write models/operations) separated.
* Or you can take it to the next level and have a separated read database & write database that keep in sync using event projection mechanism, which can help to make the read side massively scalable & extremely high performance.
* Or you can even design using full-blown CQRS & Event Sourcing architectural pattern that use event store as the single source of truth.

## Build
To build, run the command in terminal at the root of the project:
```
./gradlew clean build
```

## Tests
To run unit test, run the command in terminal at the root of the project:
```
./gradlew clean test
```

## How to use?
```
Coming soon...
```

## Contribute
We welcome all contributions.
Please fork the repository and base your work on `develop` branch.
Before creating pull request, please make sure:
* All tests passed.
* There is 100% code coverage on all new codes.

## License
```
Coming soon
```
