# jlibra Spring Boot Starter

Integrate [jlibra](https://github.com/ketola/jlibra) into your Spring Boot applications via Spring's dependency injection and Spring Boots external configuration and Actuator functionality.  
_This is basically a copy with search/replace of [web3j-spring-boot-starter](https://github.com/web3j/web3j-spring-boot-starter)_

## Usage

The `jlibra-spring-boot-starter` project enables other (Spring Boot) applications, like web applications, shell applications, and more, to use the jlibra library.  

As a sample for this usage the `java-libra-cli` application was created.

To use, create a new [Spring Boot Application](https://spring.io/guides/gs/spring-boot/), and 
include the following dependencies:

Maven:

```xml
<dependency>
    <groupId>dev.jlibra</groupId>
    <artifactId>jlibra-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
compile ('dev.jlibra:jlibra-spring-boot-starter:1.0.0')
```

Now Spring can inject jlibra instances for you where ever you need them:

```java
@Autowired
private JLibra jlibra;
```

The `JLibra` class is initialized with the values configured according to [externalized configuration in Spring](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config).  
For example, add the configuration values to a file `application.properties` next to the runnable jar.

```properties
jlibra.service-url=ad.testnet.libra.org
jlibra.service-port=80
jlibra.faucet-url=faucet.testnet.libra.org
jlibra.faucet-port=80
jlibra.gas-unit-price=0
jlibra.max-gas-amount=10000
```

## Predefined Actions

There are currently two predefined actions in the `action` package.

* AccountStateQuery
* PeerToPeerTransfer

These actions can just be `@Autowired` and are preconfigured as well, so you don't have to deal with `jlibra` directly.  
This is WIP and other actions will be added soon. 

```
@Autowired
private PeerToPeerTransfer peerToPeerTransfer

public void transfer(...) {
	peerToPeerTransfer.transfer(...);
}
``` 
## Further information

For further information on jlibra, please refer to the [jlibra GitHub](https://github.com/ketola/jlibra).
