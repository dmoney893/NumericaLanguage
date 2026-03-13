# Numerica Language Implementation (CSC321)

## Overview
Numerica is a programming language designed for mathematical and scientific computation.  
It focuses on making numeric expressions easy to read and write using syntax similar to standard mathematical notation.

This project implements **Phase 1 of the language**, which includes the lexer and parser for a minimal core subset of Numerica.

## Features Implemented
The Phase 1 implementation supports:

- Integer literals
- Identifiers
- Binary expressions using +, -, *, /
- Assignment statements
- Print statements
- Parentheses for precedence

Example program:

x = 5;
y = x + 2;
print y;

## Architecture

The program follows this pipeline:

Source Code → Lexer → Tokens → Parser → AST → Printed Tree

### Components

Lexer  
Converts source code into tokens.

Parser  
Reads tokens and constructs an Abstract Syntax Tree (AST).

AST Nodes  
Represent the structure of the program.

CLI (Main.java)  
Handles command-line commands for lexing and parsing.

## Running the Program

Compile the project and run using:

Lexer:

java -cp out/production/NumericaLanguage Main lex tests/valid1.ml

Parser:

java -cp out/production/NumericaLanguage Main parse tests/valid1.ml

## Grammar (EBNF)

program       → statement* EOF  
statement     → assignment | print_stmt  
assignment    → IDENTIFIER '=' expression ';'  
print_stmt    → 'print' expression ';'  
expression    → term (('+' | '-') term)*  
term          → factor (('*' | '/') factor)*  
factor        → INTEGER | IDENTIFIER | '(' expression ')'

## Project Structure

src/  
Contains lexer, parser, AST classes, and CLI entry point.

tests/  
Contains valid and invalid test programs.

README.md  
Documentation for the project.

## Testing

The project includes:

- 10 valid programs
- 5 invalid programs

Valid programs should produce a correct AST.  
Invalid programs should produce an error message.