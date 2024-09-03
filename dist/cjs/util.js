"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.parseDec = parseDec;
exports.parseOct = parseOct;
exports.parseHex = parseHex;
exports.isEOF = isEOF;
exports.isNotEOF = isNotEOF;
exports.isIdentifierStart = isIdentifierStart;
exports.isIdentifierChar = isIdentifierChar;
exports.isAlpha = isAlpha;
exports.isDigit = isDigit;
exports.decNumber = decNumber;
exports.isDigitWithUnderscore = isDigitWithUnderscore;
exports.isHexDigit = isHexDigit;
exports.isHexDigitWithUnderscore = isHexDigitWithUnderscore;
exports.isOctDigit = isOctDigit;
exports.isOctDigitWithUnderscore = isOctDigitWithUnderscore;
exports.isNumberLiteralStart = isNumberLiteralStart;
exports.isDecLiteralStart = isDecLiteralStart;
exports.isStringLiteralStart = isStringLiteralStart;
exports.isRegexLiteralStart = isRegexLiteralStart;
exports.getCurrentLine = getCurrentLine;
exports.getEscape = getEscape;
exports.isDoubleQuote = isDoubleQuote;
exports.isSingleQuote = isSingleQuote;
exports.isNotEOL = isNotEOL;
var _0_ASCII = '0'.charCodeAt(0);
function parseDec(value) {
    var _a = value.split("."), integerPart = _a[0], decimalPart = _a[1];
    return parseInteger(integerPart) + (decimalPart ? parseInteger(decimalPart) / Math.pow(10, decimalPart.length) : 0);
}
function parseInteger(value) {
    var digits = value.split("").map(function (digit) { return digit.charCodeAt(0) - _0_ASCII; }).reverse();
    var result = 0;
    for (var i = 0; i < digits.length; ++i) {
        result += Math.pow(10, i) * digits[i];
    }
    return result;
}
function parseOct(value) {
    var digits = value.split("").slice(1).map(function (digit) { return digit.charCodeAt(0) - _0_ASCII; }).reverse();
    var result = 0;
    for (var i = 0; i < digits.length; ++i) {
        result += Math.pow(8, i) * digits[i];
    }
    return result;
}
function parseHex(value) {
    var digits = value.split("").slice(2).map(function (digit) { return parseSingleHex(digit); }).reverse();
    var result = 0;
    for (var i = 0; i < digits.length; ++i) {
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
function isEOF(char) {
    return char === '\0' || char === undefined;
}
function isNotEOF(char) {
    return !isEOF(char);
}
function isIdentifierStart(char) {
    return isAlpha(char) || char === '_';
}
function isIdentifierChar(char) {
    return isAlpha(char) || isDigit(char) || char === '_' || char === '.';
}
function isAlpha(char) {
    return (char >= 'a' && char <= 'z') || (char >= 'A' && char <= 'Z');
}
function isDigit(char) {
    return char >= '0' && char <= '9';
}
function decNumber(char) {
    return char >= '0' && char <= '9' || char === '.';
}
function isDigitWithUnderscore(char) {
    return isDigit(char) || char === '_';
}
function isHexDigit(char) {
    return (char >= '0' && char <= '9') || (char >= 'a' && char <= 'f') || (char >= 'A' && char <= 'F');
}
function isHexDigitWithUnderscore(char) {
    return isHexDigit(char) || char === '_';
}
function isOctDigit(char) {
    return char >= '0' && char <= '7';
}
function isOctDigitWithUnderscore(char) {
    return isOctDigit(char) || char === '_';
}
function isNumberLiteralStart(char) {
    return isDigit(char) || char === '.';
}
function isDecLiteralStart(char) {
    return char >= '1' && char <= '9';
}
function isStringLiteralStart(current) {
    return current === '"' || current === "'";
}
function isRegexLiteralStart(current) {
    return current === '/';
}
function getCurrentLine(code, cursor) {
    if (cursor === undefined) {
        cursor = code.length - 1;
    }
    var current = code[cursor];
    if (current === undefined) {
        return undefined;
    }
    var end = cursor;
    while (code[end] !== '\n' && code[end] !== '\r' && code[end] !== undefined) {
        end++;
    }
    if (end >= code.length) {
        end = code.length;
    }
    var start = cursor;
    while (code[start] !== '\n' && code[start] !== '\r' && code[start] !== undefined) {
        start--;
    }
    return code.substring(start + 1, end);
}
function getEscape(char) {
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
function isDoubleQuote(char) {
    return char === '"';
}
function isSingleQuote(char) {
    return char === "'";
}
function isNotEOL(char) {
    return !isEOF(char) && char !== '\n' && char !== '\r';
}
