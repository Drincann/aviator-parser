# aviator-parser

## Install

```bash
npm install aviator-parser
```

## Usage

```typescript
import { AviatorParser } from "aviator-parser";

const parser = new AviatorParser("a =~ /xx.*/ || !b == true");
console.log(JSON.stringify(parser.parse(), null, 2));
// output:
// [
//   {
//     "type": "statement",
//     "expression": {
//       "type": "binary-expression",
//       "left": { // a =~ /xx.*/
//         "type": "binary-expression",
//         "left": { "type": "identifier", "name": "a" },
//         "operator": "Like",
//         "right": { "type": "regex-literal", "value": "xx.*" }
//       },
//       "operator": "LogicOr",
//       "right": { // !b == true
//         "type": "binary-expression",
//         "left": {
//           "type": "unary-expression",
//           "operator": "LogicNot",
//           "argument": { "type": "identifier", "name": "b" }
//         },
//         "operator": "Equal",
//         "right": { "type": "boolean-literal", "value": true }
//       }
//     }
//   }
// ]
```
