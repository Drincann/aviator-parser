# aviator-parser

## Install

```bash
npm install aviator-parser
```

## Usage

```typescript
import { AviatorExpressionParser } from "aviator-parser";

const parser = new AviatorParser("a == 1 || !b == true");
console.log(JSON.stringify(parser.parse(), null, 2));
// output:
// { // a == 1 || !b == true
//   "type": "binary-expression",
//   "left": { // a == 1
//     "type": "binary-expression",
//     "left": { "type": "identifier", "name": "a" },
//     "operator": "Equal",
//     "right": { "type": "number-literal", "value": 1 }
//   },
//   "operator": "LogicOr",
//   "right": { // !b == true
//     "type": "binary-expression",
//     "left": { // !b
//       "type": "unary-expression",
//       "operator": "LogicNot",
//       "argument": { "type": "identifier", "name": "b" }
//     },
//     "operator": "Equal",
//     "right": { "type": "boolean-literal", "value": true }
//   }
// }
```
