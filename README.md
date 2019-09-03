# KSUID

**A KSUID implementation for Java**

## Getting Started

For Maven-based projects, add the following to your `pom.xml` file. This dependency is available from the Maven Central repository.

```xml
<dependency>
    <groupId>com.bisna.util</groupId>
    <artifactId>ksuid</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

```java
KSUID ksuid = KSUID.randomKSUID();

System.out.println(ksuid.toString()); // Prints: OcNnDGsRbpVsTlPzpme0jkufhcN 
```

You can also generate new KSUIDs from a timestamp:

```java
int timestamp = (int) ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;
KSUID ksuid = KSUID.randomKSUIDFromTimestamp(timestamp);

System.out.println(ksuid.toString()); // Prints: OcNnDGsRbpVsTlPzpme0jkufhcN
```

API also allows you to do some introspection:

````java
KSUID ksuid = KSUID.randomKSUID();

System.out.println(ksuid.timestamp()); // Prints: -180445
System.out.println(ksuid.payload()); // Prints: [B@134593bf
System.out.println(ksuid.toString()); // Prints: OcNo0sKWXqjaUHcSgWKFlXJgyeo

KSUID nextKsuid = ksuid.nextKSUID();

System.out.println(nextKsuid.timestamp()); // Prints: -180445
System.out.println(nextKsuid.payload()); // Prints: [B@4bb4de6a
System.out.println(nextKsuid.toString()); // Prints: OcNo0lnW75xjehfD67LInMjISOL
```` 

## Licensing

This project is licensed under the [MIT License](LICENSE).