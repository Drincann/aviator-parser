# aviator-parser

A Pratt-style parser for [Aviator Script](https://github.com/killme2008/aviatorscript).

Since Aviator Script does not expose a public parser API, this project implements a standalone parser that builds an AST for Aviator expressions. With it, you can analyze, validate, transform, or interpret Aviator expressions without relying on the full Aviator runtime.

This library also includes a **short-circuit evaluator**, implemented as a demonstration and provided as part of the core module.

## Maven

```xml
<dependency>
  <groupId>io.github.drincann</groupId>
  <artifactId>aviator-parser</artifactId>
  <version>0.5.1</version>
</dependency>
```

## Build & Test

```bash
mvn clean verify
```

## Features

- Pratt Parsing for operator precedence and associativity

- Composable AST with distinct types: Node, Leaf, FunctionCall, LambdaFunction

- Reverse Polish Notation (RPN) and serialization utilities

- Short-circuit analysis engine with staged execution

## Usage

### Expression Parser

This parser is designed for expression-style Aviator syntax (i.e., non-Turing-complete scripts). It parses expressions and returns an AST that can be further inspected or transformed.

```java
Expr ast = Pratt.parse("A == 1 ? (lambda (x) -> x + 1 end)(1) : func_call(a, b)");

System.out.println("Serialized: " + ast.serialize()); 
// Output: (A == 1) ? lambda (x) -> (x + 1) end(1):func_call(a, b))

System.out.println("Reverse Polish Notation: " + ast.rp()); 
// Output: (? (== A 1) (lambda (x) -> (+ x 1) end 1) (func_call a b))

// AST inspection
assert ast instanceof Node;
assert ast.getChildren().size() == 3;

Node condition = (Node) ast.getChildren().get(0);
FunctionCall thenExpr = (FunctionCall) ast.getChildren().get(1);
assert thenExpr.getFunction() instanceof LambdaFunction;

FunctionCall elseExpr = (FunctionCall) ast.getChildren().get(2);
List<Expr> arguments = elseExpr.getArguments();
```

### Short-circuit Evaluator

You can use the short-circuit evaluator to determine whether an expression can be executed given a partially known variable set. This is useful in staged or lazy evaluation contexts, such as rule engines or event-driven flows.

```java
PendingExecution pending = AviatorPendingExecutionFactory.compile(
    new SimpleAviatorRuntime(), // A user-defined runtime interface
    "A && !(B && C)"
);
```

The first argument to `compile(...)` is an implementation of `AviatorRuntime`. This interface decouples the evaluator from Aviator's internal runtime, giving you full control over variable resolution. A minimal implementation `SimpleAviatorRuntime` is provided as a demonstration:


```java
public class SimpleAviatorRuntime implements ExpressionRuntime {
    @Override
    public Object run(String expression, Map<String, Object> context) {
        return AviatorEvaluator.compile(expression, true).execute(context);
    }
}
```

Sample usage:

```java
assert pending.canExecute() == false;

pending.provide("B", true);
assert pending.canExecute() == false;

pending.provide("C", true);
assert pending.canExecute() == true;

assert pending.execute() == false;
```

## Architecture

### AST Hierarchy

- Expr: Base interface

- Leaf: Atomic expressions (identifiers, literals)

- Node: Composite expressions (binary, ternary, grouped)

- FunctionCall: Function invocation node with arguments

- LambdaFunction: First-class anonymous function

### Pratt Parser

The parser is based on Pratt's top-down operator precedence parsing. It supports:

- Custom token precedence and binding power

- Arithmetic, logical, and comparison operations

- Function calls and lambdas

- Conditional (ternary) expressions

This design enables modular parsing without deep recursion or grammar ambiguity.

## Contributing

Pull requests and issues are welcome. Feel free to contribute features, tests, or improvements.

## License

This project is licensed under the WTFPL, see [LICENSE](LICENSE) for details.

You can do anything you want with this code, including using it in commercial applications, without any restrictions.
