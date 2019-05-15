# Java CQRS CommandBus
[![Build Status](https://travis-ci.com/dathoangse/java-cqrs-commandbus.svg?branch=develop)](https://travis-ci.com/dathoangse/java-cqrs)
[![codecov](https://codecov.io/gh/dathoangse/java-cqrs/branch/develop/graph/badge.svg)](https://codecov.io/gh/dathoangse/java-cqrs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.dathoang.cqrs.commandbus/core/badge.svg)](https://mvnrepository.com/artifact/net.dathoang.cqrs.commandbus/core)

A lightweight & highly extensible CQRS framework for implementing application layer and CQRS architectural pattern in Java.

## Who uses Java CQRS CommandBus
* [YouthDev](https://youthdev.net/en/)

## Code style
The project follows [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for code style & convention.

## Git workflow
The project uses [Git flow](https://nvie.com/posts/a-successful-git-branching-model/) for git workflow.

## Versioning
The project uses [Semantic Versioning 2.0.0 | Semantic Versioning](https://semver.org/) for versioning releases.

## Libs/frameworks used
* [JUnit 5](https://junit.org/junit5/) for unit testing.
* [AssertJ](http://joel-costigliola.github.io/assertj/) for assertions in unit test.
* [Mockito](https://github.com/mockito/mockito) for mocking in unit test.
* [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/)  as logging interface.

Built with:
* [Gradle Build Tool](https://gradle.org/).

# Features
`Java CQRS CommandBus` is a very lightweight and highly extensible CQRS CommandBus library that help you implement your application layer and CQRS architectural pattern:
1. `Lightweight`: The library comes with 2 distinct core modules: the `spec` module and the `core` module. The `spec` module (will be available in the future) contains all the public interfaces of the framework, these interfaces are minimized to reduce the dependency of your project on the library, and your project only need to depends on the `spec` module. The `core` module contains the library's implementation of the `spec` module, your code will not need to depends on this module at all, dependency injection framework will automatically bind the implementation of the `core` module to the interfaces in the `spec` module. With this design, it's possible and easy to swap out or reimplement the whole library with minimal efforts and without affecting your codebase.
2. `Highly extensible`: We consider extensibility as the core value of the library, so we design it to make it highly extensible via: middleware pipeline. You can inject any custom middleware to intercept the handling of the commands dispatched into the bus.

## Integrate into your project

### For maven project

Add the dependency:

```
<dependency>
    <groupId>net.dathoang.cqrs.commandbus</groupId>
    <artifactId>core</artifactId>
    <version>0.1.0</version>
    <type>pom</type>
</dependency>
```

### For gradle project

Add the dependency:

```
compile group: 'net.dathoang.cqrs.commandbus', name: 'core', version: '0.1.0', ext: 'pom'
```

## Contribute
We welcome all contributions.
Please fork the repository and base your work on `develop` branch.
Before creating pull request, please make sure:
* All tests passed.
* There is 100% code coverage on all new codes.

## License
Java CQRS CommandBus is an Open Source Software released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)
