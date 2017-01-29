# Bremersee Failed Access Counter
This project contains a small protection against brute-force-attacks.

A failed access counter stores failed access entries of a resource for a remote host, that wants to access the resource. 
The resource may be, for example, the login page. When the login has failed, this may be stored in the failed access counter.
When the counter of the failed access entry has reached a threshold the login can be blocked for a while.

The failed access counter needs a DAO (interface FailedAccessDao) to persist the failed access entries. Currently there are four DAO implementations available:

- FailedAccessInMemoryDao: Persists the entries in memory.
- FailedAccessJpaDao: Persists the entries in a SQL database.
- FailedAccessLdapDao: Persists the entries in a LDAP server.
- FailedAccessMongoDao: Persists the entries in a MongoDB.

The generated maven site is committed to the [gh-pages branch](https://github.com/bremersee/fac/tree/gh-pages) and visible [here](http://bremersee.github.io/fac/). There you can find more information.

## Release 1.1.1
Release 1.1.1 is available at Maven Central.

It consists of four modules:

##### API module
```xml
<dependency>
    <groupId>org.bremersee</groupId>
    <artifactId>bremersee-fac-api</artifactId>
    <version>1.1.1</version>
</dependency>
```

##### JPA module
```xml
<dependency>
    <groupId>org.bremersee</groupId>
    <artifactId>bremersee-fac-data-jpa</artifactId>
    <version>1.1.1</version>
</dependency>
```

##### LDAP module
```xml
<dependency>
    <groupId>org.bremersee</groupId>
    <artifactId>bremersee-fac-data-jpa</artifactId>
    <version>1.1.1</version>
</dependency>
```

##### MongoDB module
```xml
<dependency>
    <groupId>org.bremersee</groupId>
    <artifactId>bremersee-fac-data-jpa</artifactId>
    <version>1.1.1</version>
</dependency>
```

# Bremersee Failed Access Counter Example
This project contains for each DAO a small Spring Boot Application that demonstrates the 
usage and configuration of the Failed Access Counter library.

It's not available at Maven Central. You may check it out and build the application with
```
$ cd fac/bremersee-fac-example
$ mvn clean install
```
Than go to one of the modules and run the application (all examples use an in-memory storage):
```
$ cd bremersee-fac-example-ldap
$ mvn spring-boot:run
```
After the application is started you can open [http://localhost:8080/entries.html](http://localhost:8080/entries.html) in your favorite browser and have a look at the demonstration.
