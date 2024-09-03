const _0_ASCII = '0'.charCodeAt(0);
export function parseDec(value) {
    const [integerPart, decimalPart] = value.split(".");
    return parseInteger(integerPart) + (decimalPart ? parseInteger(decimalPart) / Math.pow(10, decimalPart.length) : 0);
}
function parseInteger(value) {
    const digits = value.split("").map(digit => digit.charCodeAt(0) - _0_ASCII).reverse();
    let result = 0;
    for (let i = 0; i < digits.length; ++i) {
        result += Math.pow(10, i) * digits[i];
    }
    return result;
}
export function parseOct(value) {
    const digits = value.split("").slice(1).map(digit => digit.charCodeAt(0) - _0_ASCII).reverse();
    let result = 0;
    for (let i = 0; i < digits.length; ++i) {
        result += Math.pow(8, i) * digits[i];
    }
    return result;
}
export function parseHex(value) {
    const digits = value.split("").slice(2).map(digit => parseSingleHex(digit)).reverse();
    let result = 0;
    for (let i = 0; i < digits.length; ++i) {
        result += Math.pow(16, i) * digits[i];
    }
    return result;
}
function parseSingleHex(value) {
    if (isDigit(value)) {
        return value.charCodeAt(0) - _0_ASCII;
    }
    return value.toLowerCase().charCodeAt(0) - 'a'.charCodeAt(0) + 10;
}
export function isEOF(char) {
    return char === '\0' || char === undefined;
}
export function isNotEOF(char) {
    return !isEOF(char);
}
export function isIdentifierStart(char) {
    return isAlpha(char) || char === '_';
}
export function isIdentifier(char) {
    return isAlpha(char) || isDigit(char) || char === '_';
}
export function isAlpha(char) {
    return (char >= 'a' && char <= 'z') || (char >= 'A' && char <= 'Z');
}
export function isDigit(char) {
    return char >= '0' && char <= '9';
}
export function decNumber(char) {
    return char >= '0' && char <= '9' || char === '.';
}
export function isDigitWithUnderscore(char) {
    return isDigit(char) || char === '_';
}
export function isHexDigit(char) {
    return (char >= '0' && char <= '9') || (char >= 'a' && char <= 'f') || (char >= 'A' && char <= 'F');
}
export function isHexDigitWithUnderscore(char) {
    return isHexDigit(char) || char === '_';
}
export function isOctDigit(char) {
    return char >= '0' && char <= '7';
}
export function isOctDigitWithUnderscore(char) {
    return isOctDigit(char) || char === '_';
}
export function isNumberLiteralStart(char) {
    return isDigit(char) || char === '.';
}
export function isDecLiteralStart(char) {
    return char >= '1' && char <= '9';
}
export function isStringLiteralStart(current) {
    return current === '"' || current === "'";
}
export function getCurrentLine(code, cursor) {
    if (cursor === undefined) {
        cursor = code.length - 1;
    }
    let current = code[cursor];
    if (current === undefined) {
        return undefined;
    }
    let end = cursor;
    while (code[end] !== '\n' && code[end] !== '\r' && code[end] !== undefined) {
        end++;
    }
    if (end >= code.length) {
        end = code.length;
    }
    let start = cursor;
    while (code[start] !== '\n' && code[start] !== '\r' && code[start] !== undefined) {
        start--;
    }
    return code.substring(start + 1, end);
}
export function getEscape(char) {
    if (char === 'n') {
        return '\n';
    }
    else if (char === 't') {
        return '\t';
    }
    else if (char === 'r') {
        return '\r';
    }
    else if (char === '0') {
        return '\0';
    }
    return char;
}
export function isDoubleQuote(char) {
    return char === '"';
}
export function isSingleQuote(char) {
    return char === "'";
}
export function isNotEOL(char) {
    return !isEOF(char) && char !== '\n' && char !== '\r';
}
