# Numerica Programming Language

## CSC321 – Language Implementation Project

---

# Overview

Numerica is a custom programming language designed for mathematical and scientific computation. The language focuses on numeric clarity, simple syntax, strong typing, and expression-based programming.

The syntax is intentionally lightweight and resembles standard mathematical notation so that students, scientists, and engineers can write readable mathematical code without dealing with unnecessary complexity.

Numerica was implemented entirely in Java using:

* A handwritten lexer
* A recursive descent parser
* An Abstract Syntax Tree (AST)
* An interpreter/evaluator
* A custom Swing GUI IDE

---

# Language Goals

Numerica was designed with the following goals:

1. Make mathematical expressions easy to write and read
2. Enforce strong typing and predictable behavior
3. Keep the language lightweight and focused on numerical computation
4. Provide clear runtime and parser errors
5. Support useful programming constructs without unnecessary complexity

---

# Features

## Core Features

* Integers
* Floats
* Booleans
* None value (`none`)
* Numeric lists
* Arithmetic operations
* Comparison operations
* Boolean logic
* Immutable variables (`let`)
* Mutable variables (`var`)
* Functions
* Recursion
* Conditionals
* Loops
* Built-in math functions
* Runtime type checking
* Comments
* Error reporting with line numbers

---

# Data Types

## Integers

```txt
5
-2
100
```

## Floats

```txt
3.14
-0.5
2.0
```

## Booleans

```txt
true
false
```

## None

```txt
none
```

## Numeric Lists

```txt
[1, 2, 3]
[1.5, 2.0, 3.7]
```

Lists are restricted to numeric values only.

---

# Variables

## Immutable Variables

```txt
let x = 5;
```

Immutable variables cannot be reassigned.

## Mutable Variables

```txt
var x = 5;
x = 10;
```

---

# Arithmetic Operators

| Operator | Meaning        |
| -------- | -------------- |
| +        | Addition       |
| -        | Subtraction    |
| *        | Multiplication |
| /        | Division       |

## Example

```txt
let x = 5 + 2 * 3;
paste x;
```

---

# Comparison Operators

| Operator | Meaning               |
| -------- | --------------------- |
| ==       | Equal                 |
| !=       | Not equal             |
| >        | Greater than          |
| <        | Less than             |
| >=       | Greater than or equal |
| <=       | Less than or equal    |

---

# Boolean Logic

| Operator | Meaning     |
| -------- | ----------- |
| and      | Logical AND |
| or       | Logical OR  |
| not      | Logical NOT |

## Example

```txt
when x > 0 and not (x == 5) {
    paste x;
}
```

---

# Conditionals

Numerica uses `when` and `otherwise`.

## Example

```txt
let x = 5;

when x > 0 {
    paste x;
} otherwise {
    paste 0;
}
```

---

# Loops

## Repeat Loop

```txt
var i = 0;

repeat i < 5 {
    paste i;
    i = i + 1;
}
```

## Range Loop

```txt
range x from 1 to 5 by 1 {
    paste x;
}
```

## Stop Statement

```txt
repeat true {
    stop;
}
```

---

# Functions

Functions are declared using `fn`.

## Example

```txt
fn square(x) {
    give x * x;
}

paste square(5);
```

---

# Recursion

Numerica supports recursion.

## Example

```txt
fn factorial(n) {
    when n == 0 {
        give 1;
    } otherwise {
        give n * factorial(n - 1);
    }
}

paste factorial(5);
```

---

# Built-in Math Functions

| Function    | Description    |
| ----------- | -------------- |
| abs(x)      | Absolute value |
| sqrt(x)     | Square root    |
| power(x, y) | Exponentiation |
| max(a, b)   | Maximum        |
| min(a, b)   | Minimum        |

## Example

```txt
paste abs(-5);
paste sqrt(25);
paste power(2, 3);
paste max(10, 4);
paste min(10, 4);
```

---

# Lists

## List Creation

```txt
var nums = [1, 2, 3];
```

## Index Access

```txt
paste nums[1];
```

## List Mutation

```txt
nums[1] = 10;
```

---

# Comments

Numerica supports single-line comments using `#`.

## Example

```txt
# this is a comment
let x = 5;
```

---

# Error Handling

Numerica stops execution at the first error.

Supported errors include:

* Undefined variable errors
* Division by zero
* Type mismatch errors
* Invalid list access
* Invalid function arguments
* Parser syntax errors
* Line-number error reporting

## Example

```txt
let x = 5;
paste x / 0;
```

Output:

```txt
ArithmeticError: division by zero
```

---

# Lexer

The lexer converts source code into tokens.

Supported token categories include:

* Keywords
* Identifiers
* Integers
* Floats
* Operators
* Delimiters
* Comments

## Example

Input:

```txt
let x = 5;
```

Lexer Output:

```txt
LET(let)
IDENTIFIER(x)
EQUAL(=)
INTEGER(5)
SEMICOLON(;)
EOF
```

---

# Parser

Numerica uses a handwritten recursive descent parser.

The parser:

* Enforces precedence rules
* Builds an AST
* Handles functions
* Handles loops
* Handles expressions
* Detects syntax errors

---

# AST

The parser generates a structured Abstract Syntax Tree.

## Example Parse Output

```txt
Program
    VariableDeclaration(let)
        Identifier(x)
        IntegerLiteral(5)
    PasteStatement
        Identifier(x)
```

---

# Evaluator / Interpreter

The evaluator executes AST nodes.

The interpreter supports:

* Variables
* Arithmetic
* Booleans
* Lists
* Loops
* Functions
* Recursion
* Built-in math functions
* Runtime type checking

---

# GUI IDE

Numerica includes a Java Swing GUI.

The GUI supports:

* Writing Numerica code
* Running lexer output
* Viewing AST output
* Running programs
* Viewing interpreter results

The GUI integrates the full Numerica pipeline:

```txt
Source Code → Lexer → Parser → AST → Evaluator → Output
```

---

# Running Numerica

## Command Line

### Lexer

```powershell
java -cp out/production/NumericaLanguage Main lex tests/valid1.ml
```

### Parser

```powershell
java -cp out/production/NumericaLanguage Main parse tests/invalid1.ml
```

### Run Program

```powershell
java -cp out/production/NumericaLanguage Main run tests/run1.ml
```

---

# Running the GUI

Open:

```txt
NumericaGUI.java
```

Then run:

```txt
Run 'NumericaGUI.main()'
```

---

# Example Full Program

```txt
# Numerica demo

let start = 1;
let finish = 5;
var total = 0;

fn factorial(n) {
    when n == 0 {
        give 1;
    } otherwise {
        give n * factorial(n - 1);
    }
}

paste factorial(5);

range x from start to finish by 1 {
    total = total + x;
}

paste total;

paste power(2, 3);
paste sqrt(25);
```

---

# Example Output

```txt
120
15
8.0
5.0
```

---

# Challenges Encountered

Some of the main implementation challenges included:

* Correct operator precedence parsing
* Supporting recursion
* Managing variable scope
* Implementing mutable vs immutable variables
* Supporting built-in functions
* Implementing runtime type checking
* Integrating the GUI with interpreter output
* Maintaining consistent AST formatting