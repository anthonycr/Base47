# Base₄₇
A Base₄₇ encoder/decoder that uses emojis as its character set

## What? 🤔
This is a silly base encoding algorithm written in Kotlin. It purposefully uses an inefficient base to encode to, base 47, and also uses a non-traditional character set. The character set used is a subset of unicode emojis, most of the animal emojis.

## Usage 🙄

Include it as a gradle submodule
```groovy
dependencies {
    compile project(':library')
}
```

Encode

#### Kotlin
```kotlin
val testString = "Hello World!!!"

val encodedTestString = Base47.encode(testString.getBytes())
...
```

#### Java
```java
String testString = "Hello World!!!";

String encodedTestString = Base47.encode(testString.getBytes());
...
```

Decode

#### Kotlin
```kotlin
...
val decodedTestStringBytes = Base47.decode(encodedTestString)

val decodedTestString = new String(decodedTestStringBytes)

assert(decodedTestString == testString)
```

#### Java
```java
...
byte[] decodedTestStringBytes = Base47.decode(encodedTestString);

String decodedTestString = new String(decodedTestStringBytes);

assert decodedTestString.equals(testString);
```

## License
`Base47` is available under the MIT license. See the [LICENSE](LICENSE) file for more information.
