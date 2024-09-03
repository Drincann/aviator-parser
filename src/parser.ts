import { Lexer, Token, TokenType } from "./lexer.js"

export type ExpressionNode = {
  type: 'binary-expression',
  left: ExpressionNode,
  right: ExpressionNode,
  operator: BinaryOperator
} | {
  type: 'unary-expression',
  argument: ExpressionNode,
  operator: UnaryOperator

} | RegexLiteral | StringLiteral | NumberLiteral | BooleanLiteral | Identifier | FunctionCall

export type BinaryOperator =
  | 'Add' | 'Subtract' | 'Multiply' | 'Divide' | 'Mod'
  | 'Like' | 'Equal' | 'NotEqual' | 'LessThan' | 'LessThanEqual' | 'GreaterThan' | 'GreaterThanEqual' | 'LogicOr' | 'LogicAnd' | 'LogicNot'

export type UnaryOperator = 'LogicNot'

export type RegexLiteral = {
  type: 'regex-literal',
  value: string
}

export type StringLiteral = {
  type: 'string-literal',
  value: string
}

export type NumberLiteral = {
  type: 'number-literal',
  value: number
}

export type BooleanLiteral = {
  type: 'boolean-literal',
  value: boolean
}

export type Identifier = {
  type: 'identifier',
  name: string
}

export type FunctionCall = {
  type: 'function-call',
  name: string,
  arguments: ExpressionNode[]
}

export class AviatorExpressionParser {
  private lexer: Lexer
  private currentToken?: Token
  constructor(code: string) { this.lexer = new Lexer(code) }
  public parse(): ExpressionNode {
    this.next()
    return this.parseExpression()
  }

  private parseExpression(priority: number = 0): ExpressionNode {
    let left: ExpressionNode | undefined = this.tryParseUnary()
    if (left === undefined) {
      throw new Error('Unexpected token: ' + this.currentToken?.type + ' ' + this.currentToken?.value)
    }

    while (isBinaryOperator(this.currentToken) && getPriority(this.currentToken!.type) > priority) {
      const operator = this.currentToken
      this.next()
      left = {
        type: 'binary-expression',
        left: left!,
        operator: operator!.type as BinaryOperator,
        right: this.parseExpression(getPriority(operator!.type))
      }
    }

    return left as ExpressionNode
  }

  private tryParseUnary(): ExpressionNode | undefined {
    if (this.currentToken?.type === 'Regex') {
      const value = this.currentToken.value as string
      this.match('Regex')
      return { type: 'regex-literal', value }
    } else if (this.currentToken?.type === 'String') {
      const value = this.currentToken.value as string
      this.match('String')
      return { type: 'string-literal', value }
    } else if (this.currentToken?.type === 'Number') {
      const value = this.currentToken.value as number
      this.match('Number')
      return { type: 'number-literal', value }
    } else if (this.currentToken?.type === 'True') {
      this.match('True')
      return { type: 'boolean-literal', value: true }
    } else if (this.currentToken?.type === 'False') {
      this.match('False')
      return { type: 'boolean-literal', value: false }
    } else if (this.currentToken?.type === 'Identifier') {
      const name = this.currentToken.value as string
      this.match('Identifier')
      // @ts-ignore
      if (this.currentToken.type === 'LeftParen') {
        return this.parseFunctionCall(name)
      }
      return { type: 'identifier', name }
    } else if (this.currentToken?.type === 'LeftParen') {
      this.match('LeftParen')
      const expression = this.parseExpression()
      this.match('RightParen')
      return expression
    } else if (this.currentToken?.type === 'LogicNot') {
      this.match('LogicNot')
      const argument = this.parseExpression(getPriority('LogicNot'))
      return {
        type: 'unary-expression',
        operator: 'LogicNot',
        argument
      }
    }
  }

  private parseFunctionCall(name: string): FunctionCall {
    this.match('LeftParen')
    const args: ExpressionNode[] = []
    while (this.currentToken?.type !== 'RightParen') {
      args.push(this.parseExpression())
      if (this.currentToken?.type === 'Comma') {
        this.match('Comma')
      }
    }
    this.match('RightParen')
    return { type: 'function-call', name, arguments: args }
  }

  private next(): void {
    this.currentToken = this.lexer.next()
  }

  private match(type: TokenType): void {
    if (this.currentToken?.type !== type) {
      throw new Error('Unexpected token: ' + this.currentToken?.type + ' ' + this.currentToken?.value)
    }
    this.next()
  }

  private tryMatch(type: TokenType): boolean {
    if (this.currentToken?.type === type) {
      this.next()
      return true
    }

    return false
  }
}


function isBinaryOperator(currentToken: Token | undefined) {
  if (currentToken === undefined) return false
  return currentToken.type in {
    'Add': true, 'Subtract': true, 'Multiply': true, 'Divide': true, 'Mod': true,
    'Like': true, 'Equal': true, 'NotEqual': true, 'LessThan': true, 'LessThanEqual': true, 'GreaterThan': true, 'GreaterThanEqual': true, 'LogicOr': true, 'LogicAnd': true, 'LogicNot': true
  }
}

function getPriority(type: TokenType): number {
  switch (type) {
    case 'LeftParen': case 'RightParen': return 15
    case 'LogicNot': return 14
    case 'Multiply': case 'Divide': case 'Mod': return 13
    case 'Add': case 'Subtract': return 12
    case 'LessThan': case 'LessThanEqual': case 'GreaterThan': case 'GreaterThanEqual': return 11
    case 'Equal': case 'NotEqual': case 'Like': return 10
    case 'LogicAnd': return 9
    case 'LogicOr': return 8
    case 'Conditional': return 7
    default: throw new Error('Unknown operator: ' + type)
  }
}
