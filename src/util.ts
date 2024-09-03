const _0_ASCII = '0'.charCodeAt(0)

export function parseDec(value: string): number {
  const [integerPart, decimalPart] = value.split(".")
  return parseInteger(integerPart) + (decimalPart ? parseInteger(decimalPart) / Math.pow(10, decimalPart.length) : 0)
}

function parseInteger(value: string): number {
  const digits = value.split("").map(digit => digit.charCodeAt(0) - _0_ASCII).reverse()
  let result = 0
  for (let i = 0; i < digits.length; ++i) {
    result += Math.pow(10, i) * digits[i]
  }
  return result
}

export function parseOct(value: string): number {
  const digits = value.split("").slice(1).map(digit => digit.charCodeAt(0) - _0_ASCII).reverse()

  let result = 0
  for (let i = 0; i < digits.length; ++i) {
    result += Math.pow(8, i) * digits[i]
  }
  return result
}

export function parseHex(value: string): number {
  const digits = value.split("").slice(2).map(digit => parseSingleHex(digit)).reverse()

  let result = 0
  for (let i = 0; i < digits.length; ++i) {
    result += Math.pow(16, i) * digits[i]
  }
  return result
}

function parseSingleHex(value: string): any {
  if (isDigit(value)) {
    return value.charCodeAt(0) - _0_ASCII
  }

  return value.toLowerCase().charCodeAt(0) - 'a'.charCodeAt(0) + 10
}

export function isEOF(char?: string): boolean {
  return char === '\0' || char === undefined
}

export function isNotEOF(char?: string): boolean {
  return !isEOF(char)
}

export function isIdentifierStart(char: string) {
  return isAlpha(char) || char === '_'
}

export function isIdentifier(char: string) {
  return isAlpha(char) || isDigit(char) || char === '_'
}

export function isAlpha(char: string) {
  return (char >= 'a' && char <= 'z') || (char >= 'A' && char <= 'Z')
}

export function isDigit(char: string): boolean {
  return char >= '0' && char <= '9'
}

export function decNumber(char: string): boolean {
  return char >= '0' && char <= '9' || char === '.'
}

export function isDigitWithUnderscore(char: string): boolean {
  return isDigit(char) || char === '_'
}

export function isHexDigit(char: string): boolean {
  return (char >= '0' && char <= '9') || (char >= 'a' && char <= 'f') || (char >= 'A' && char <= 'F')
}

export function isHexDigitWithUnderscore(char: string): boolean {
  return isHexDigit(char) || char === '_'
}

export function isOctDigit(char: string): boolean {
  return char >= '0' && char <= '7'
}

export function isOctDigitWithUnderscore(char: string): boolean {
  return isOctDigit(char) || char === '_'
}

export function isNumberLiteralStart(char: string): boolean {
  return isDigit(char) || char === '.'
}

export function isDecLiteralStart(char: string): boolean {
  return char >= '1' && char <= '9'
}

export function isStringLiteralStart(current: string): boolean {
  return current === '"' || current === "'"
}

export function getCurrentLine(code: string, cursor?: number): string | undefined {
  if (cursor === undefined) {
    cursor = code.length - 1
  }

  let current = code[cursor]
  if (current === undefined) {
    return undefined
  }

  let end = cursor
  while (code[end] !== '\n' && code[end] !== '\r' && code[end] !== undefined) {
    end++
  }
  if (end >= code.length) {
    end = code.length
  }


  let start = cursor
  while (code[start] !== '\n' && code[start] !== '\r' && code[start] !== undefined) {
    start--
  }

  return code.substring(start + 1, end)
}

export function getEscape(char: string): string {
  if (char === 'n') {
    return '\n'
  } else if (char === 't') {
    return '\t'
  } else if (char === 'r') {
    return '\r'
  } else if (char === '0') {
    return '\0'
  }

  return char
}

export function isDoubleQuote(char: string): char is '"' {
  return char === '"'
}

export function isSingleQuote(char: string): char is "'" {
  return char === "'"
}

export function isNotEOL(char: string): boolean {
  return !isEOF(char) && char !== '\n' && char !== '\r'
}
