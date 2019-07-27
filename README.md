# Java CQRS CommandBus
[![Build Status](https://travis-ci.com/dathoangse/java-cqrs-commandbus.svg?branch=develop)](https://travis-ci.com/dathoangse/java-cqrs-commandbus)
[![codecov](https://codecov.io/gh/dathoangse/java-cqrs-commandbus/branch/develop/graph/badge.svg)](https://codecov.io/gh/dathoangse/java-cqrs-commandbus)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.dathoang.cqrs.commandbus/core/badge.svg)](https://mvnrepository.com/artifact/net.dathoang.cqrs.commandbus/commandbus-spec)

A lightweight & highly extensible CQRS framework for implementing application layer and CQRS architectural pattern in Java.

## Who uses Java CQRS CommandBus
* [YouthDev](https://youthdev.net/en/)

## Benefits of using Command Bus
Command Bus pattern (a.k.a **Command Dispatcher Pattern**) help to:
* **Decouple architecture from framework**: Good architecture does not depend on framework, it also does not depends on how the system is used (Our architecture should be the same whether we’re building a Restful API, WebSocket, TCP-socket or CLI applications.). By modeling all the interactions (business use-case) via Commands, application layer (or service layer) no longer need to depend on framework, and it also doesn’t depend on the protocol which was used to communicate with the system.
* **Make the architecture scream**: Good architecture is the screaming architecture, which means we can easily know the system use-cases just by looking at the architecture. By presenting all the business user-case/intent and user interaction (with the system) via Command, we can quickly understand what the system can do just by looking at all the Commands available in the system, thus all the Commands have already screamed about the system use-cases.
* **Unified communication interface**: With Command Bus pattern, the communication interface of application layer (service layer) is unified into only one method: the `dispatch()` method of Command Bus. This simplified interface will help to increase maintainability.
* **Handle application layer (service layer) cross-cutting concerns in one place**: Because all the interactions to the system go through the `dispatch()` method of Command Bus, we can easily handle all the application layer (service layer) cross-cutting concerns like: logging, audit-logging, auto-restarting transaction (ex: restart on database deadlock exception), rate-limiting. Because of this, the `middleware pipeline` feature of `Java CQRS CommandBus` will help you to easily add middleware to handle cross-cutting concerns easily, as well as to extend functionalities.
* **Built-in natural audit-log**: Because all the interactions to the system are modeled as Command (which represent business use-case/intent), the Command naturally represent system audit-log. By adding a middleware to log succeeded command, it naturally becomes the audit-log of the system.

## Features
**Java CQRS CommandBus** is a very lightweight and highly extensible CQRS CommandBus library that help you implement your application layer and CQRS architectural pattern:
* **Lightweight**: The library comes with 2 distinct core modules: the `commandbus-spec` module and the `commandbus-core` module. The `commandbus-spec` module contains all the public interfaces of the framework, these interfaces are minimized to reduce the dependency of your project on the library, and your project only need to depends on the `commandbus-spec` module. The `commandbus-core` module contains the library’s implementation of the `commandbus-spec` module, your code will not need to depends on this module at all, dependency injection framework will automatically bind the implementation of the `commandbus-core` module to the interfaces in the `commandbus-spec` module. With this design, it’s possible and easy to swap out or reimplement the whole library with minimal efforts without affecting your codebase.
* **Highly extensible**: We consider extensibility as the core value of the library, so we design it to make it highly extensible via: middleware pipeline. You can inject any custom middleware to intercept the handling of the commands dispatched into the bus.


## Code style
The project follows [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Git workflow
Before v0.3.0, the project uses [Gitflow](https://nvie.com/posts/a-successful-git-branching-model/).

From v0.3.0, the project uses [Trunk-based development](https://trunkbaseddevelopment.com/).

## Versioning
The project uses [SemVer v2.0.0](https://semver.org/).

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
Please fork the repository and base your work on **develop** branch.
Before creating pull request, please make sure:
* All tests passed.
* There is 100% code coverage on all new codes.

## License
Java CQRS CommandBus is an Open Source Software released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

