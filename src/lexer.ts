import {
  parseDec, parseHex, parseOct, isDigitWithUnderscore, isDigit,
  isOctDigitWithUnderscore, isOctDigit, isHexDigitWithUnderscore,
  isHexDigit, isIdentifierStart, isNumberLiteralStart, isIdentifierChar,
  isEOF, isStringLiteralStart, getEscape, isDoubleQuote, isSingleQuote,
  isNotEOL, isNotEOF,
  isRegexLiteralStart
} from "./util.js"

export type Token<TokenTypeGeneric extends TokenType = TokenType> = {
  type: TokenTypeGeneric
  value: TokenValueType<TokenTypeGeneric>
}

type TokenValueType<TokenTypeGeneric extends TokenType> =
  /*                             TokenType => ValueType */
  TokenTypeGeneric extends 'Identifier' ? string :
  TokenTypeGeneric extends 'Number' ? number :
  TokenTypeGeneric extends 'String' ? string :
  TokenTypeGeneric extends 'Regex' ? string :
  TokenTypeGeneric extends 'Comment' ? string :
  undefined

export type TokenType =
  | 'Identifier' | 'Number' | 'String' | 'True' | 'False' | 'Regex'
  | 'Add' | 'Subtract' | 'Multiply' | 'Divide' | 'Mod'
  | 'Like' | 'Equal' | 'NotEqual' | 'LessThan' | 'LessThanEqual' | 'GreaterThan' | 'GreaterThanEqual' | 'LogicOr' | 'LogicAnd' | 'LogicNot'
  | 'LeftParen' | 'RightParen'
  | 'Conditional'
  | 'Colon'
  | 'Comma'

export class Lexer {
  private code: string
  private cursor: number = 0

  public constructor(code: string) {
    this.code = code
  }

  public static fromCode(code: string): Lexer {
    return new Lexer(code)
  }

  public next(): Token<any> | undefined {
    let current: string | undefined
    while (current = this.code[this.cursor]) {
      this.cursor++
      if (isEOF(current)) break
      if (current === '\n') continue

      if (current === '#') {
        this.skipComment()
        continue
      }

      if (isIdentifierStart(current)) return this.parseNextIdentifier()
      if (isNumberLiteralStart(current)) return this.parseNextNumberLiteral()
      if (isStringLiteralStart(current)) return this.parseNextStringLiteral()
      if (isRegexLiteralStart(current)) return this.parseNextRegexLiteral()

      if (current === '+') return { type: 'Add', value: undefined, }
      if (current === '-') return { type: 'Subtract', value: undefined, }
      if (current === '*') return { type: 'Multiply', value: undefined, }
      if (current === '(') return { type: 'LeftParen', value: undefined, }
      if (current === ')') return { type: 'RightParen', value: undefined, }
      if (current === '?') return { type: 'Conditional', value: undefined, }
      if (current === ':') return { type: 'Colon', value: undefined, }
      if (current === ',') return { type: 'Comma', value: undefined, }

      if (current === '=') {
        if (this.code[this.cursor] === '=') {
          this.cursor++
          return { type: 'Equal', value: undefined, }
        } else if (this.code[this.cursor] === '~') {
          this.cursor++
          return { type: 'Like', value: undefined, }
        }
      }

      if (current === '!') {
        if (this.code[this.cursor] === '=') {
          this.cursor++
          return { type: 'NotEqual', value: undefined, }
        } else return { type: 'LogicNot', value: undefined, }
      }

      if (current === '<') {
        if (this.code[this.cursor] === '=') {
          this.cursor++
          return { type: 'LessThanEqual', value: undefined, }
        } else return { type: 'LessThan', value: undefined, }
      }

      if (current === '>') {
        if (this.code[this.cursor] === '=') {
          this.cursor++
          return { type: 'GreaterThanEqual', value: undefined, }
        } else return { type: 'GreaterThan', value: undefined, }
      }

      if (current === '&') {
        if (this.code[this.cursor] === '&') {
          this.cursor++
          return { type: 'LogicAnd', value: undefined, }
        }
      }

      if (current === '|') {
        if (this.code[this.cursor] === '|') {
          this.cursor++
          return { type: 'LogicOr', value: undefined, }
        }
      }

    } // end while
    return undefined
  }

  private skipComment() {
    this.until(new Set(['\n', '\0']))
  }

  private parseNextIdentifier(): Token<'Identifier' | 'True' | 'False'> | undefined {
    const start = this.cursor - 1

    let end = this.cursor
    let current = this.code[end]
    while (isIdentifierChar(current)) {
      end++
      if (this.code[end] === '.' && this.code[end + 1] === '.') {
        throw new Error("lexer error, invalid object access syntax")
      }
      current = this.code[end]
    }

    this.cursor = end

    const name = this.code.substring(start, end)
    if (name === 'true') return { type: 'True', value: 'true' }
    else if (name === 'false') return { type: 'False', value: 'false' }
    return { type: 'Identifier', value: name, }
  }

  private parseNextNumberLiteral(): Token<'Number'> | undefined {
    const start = this.cursor - 1

    if /* float */('.' === this.code[start] && isDigit(this.code[start + 1])) {
      let end = start + 1
      let current = this.code[end]
      while (isDigitWithUnderscore(current)) {
        end++
        current = this.code[end]
      }
      this.cursor = end

      const value = this.code.substring(start, end).replace(/_/g, '')
      return { type: 'Number', value: parseDec('0' + value), }
    } else if ('0' === this.code[start]) {
      if /* hex */('0x' === this.code.substring(start, start + 2) && isHexDigit(this.code[start + 2])) {
        let end = start + 2
        let current = this.code[end]
        while (isHexDigitWithUnderscore(current)) {
          end++
          current = this.code[end]
        }
        this.cursor = end

        const value = this.code.substring(start, end).replace(/_/g, '')
        return { type: 'Number', value: parseHex(value), }
      } else if /* oct */ ('0' === this.code[start] && isOctDigit(this.code[start + 1])) {
        let end = start + 1
        let current = this.code[end]
        while (isOctDigitWithUnderscore(current)) {
          end++
          current = this.code[end]
        }
        this.cursor = end

        const value = this.code.substring(start, end).replace(/_/g, '')
        return { type: 'Number', value: parseOct(value), }
      } else if /* float */ ('.' === this.code[start + 1] && isDigit(this.code[start + 2])) {
        let end = start + 2
        let current = this.code[end]
        while (isDigitWithUnderscore(current)) {
          end++
          current = this.code[end]
        }
        this.cursor = end

        const value = this.code.substring(start, end).replace(/_/g, '')
        return { type: 'Number', value: parseDec(value), }
      }

      return { type: 'Number', value: 0, }
    } else /* dec */ {
      let end = start + 1
      let current = this.code[end]
      while (isDigitWithUnderscore(current)) {
        end++
        current = this.code[end]
      }
      if (current === '.' && isDigit(this.code[end + 1])) {
        end++
        current = this.code[end]
        while (isDigitWithUnderscore(current)) {
          end++
          current = this.code[end]
        }
      }
      this.cursor = end

      const value = this.code.substring(start, end).replace(/_/g, '')
      return { type: 'Number', value: parseDec(value), }
    }
  }

  private parseNextStringLiteral(): Token<'String'> | undefined {
    const start = this.cursor - 1
    if (isDoubleQuote(this.code[start]) || isSingleQuote(this.code[start])) {
      return this.parseStringLiteral(start)
    }
    throw new Error("lexer error, invalid string literal")
  }

  private parseNextRegexLiteral(): Token<'Regex'> | undefined {
    const start = this.cursor - 1
    let cursor = start + 1
    let current = this.code[cursor]
    let literal = ''

    while (current !== '/' && isNotEOL(current)) {
      literal += current
      current = this.code[++cursor]
    }
    if (current !== '/') {
      throw new Error("lexer error, unterminated regex literal")
    }

    this.cursor = cursor + 1
    return { type: 'Regex', value: literal }
  }

  /**
   * @param start the position of the first double quote
   */
  private parseStringLiteral(start: number): Token<'String'> {
    let quote = this.code[start]
    let cursor = start + 1
    let current = this.code[cursor]
    let literal = ''

    while (current !== quote && isNotEOL(current)) {
      if (current === '\\' && this.code[cursor + 1] !== undefined) {
        const escape = getEscape(this.code[++cursor])
        if (escape === undefined) {
          throw new Error("lexer error: invalid escape character")
        }

        literal += escape
        current = this.code[++cursor]
        continue
      }

      literal += current
      current = this.code[++cursor]
    }
    if (current !== quote) {
      throw new Error("lexer error, unterminated string literal")
    }

    this.cursor = cursor + 1
    return { type: 'String', value: literal, }
  }

  private until(charSet: Set<string>) {
    let current = this.code[this.cursor]
    while (!charSet.has(current) && isNotEOF(current)) {
      this.cursor++
      current = this.code[this.cursor]
    }
  }
}
