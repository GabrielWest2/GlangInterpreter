# Glang Interpreter

Glang is a custom programming language designed for fun. This repository contains an interpreter for Glang and a few example programs to demonstrate its capabilities.

## Getting Started

### Prerequisites

To run the Glang interpreter, you need:
- Java Development Kit (JDK) installed
- [Maven](https://maven.apache.org/) installed

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/GabrielWest2/GlangInterpreter.git
    cd GlangInterpreter
    ```

2. Compile the interpreter using Maven:
```bash
mvn clean install
```

### Usage

```bash
java -jar glang.jar <program>
```

## Glang Language Features

### Variables and Data Types

Glang supports variable declarations with simple data types such as integers and strings, as well as arrays.

```js
var x = 5;
var y = "Hello, world!";
var arr = [1, 2, 3, 4, 5];
```

### Ternary Operator

Glang supports the ternary operator for concise conditional expressions.

```js
var result = (x > 10) ? "Greater than 10" : "Less than or equal to 10";
```

### Functions

Define reusable functions using the `def` keyword.

```js
def add(a, b) {
    return a + b;
}
```

### Control Structures

#### Conditionals

Use `if`, `else if`, and `else` for conditional execution.

```js
if (x > 10) {
    println("x is greater than 10");
} else if (x == 10) {
    println("x is equal to 10");
} else {
    println("x is less than 10");
}
```

#### Loops

Glang supports `for`, `while`, and `foreach` loops.

##### For Loop

```js
for (var i = 0; i < 10; i++) {
    println(i);
}
```

##### While Loop

```js
var i = 0;
while (i < 10) {
    println(i);
    i++;
}
```

##### Foreach Loop

```js
var arr = [1, 2, 3, 4];
foreach(var a : arr) {
    println(a);
}
```

### Classes and Inheritance

Glang supports object-oriented programming, including class definitions and inheritance. Instance variables are defined in the constructor.

```js
class Animal {
    constructor(name) {
        this.name = name;
    }
    
    speak() {
        println("The animal makes a sound.");
    }
    
    sleep() {
        println(this.name + " is sleeping.");
    }
}

class Dog extends Animal {
    
    constructor(name) {
        this.name = name;
    }
    
    speak() {
        println(this.name + " says: Woof!");
    }
}

var myDog = Dog("Buddy");
myDog.speak();
myDog.sleep();
```

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
