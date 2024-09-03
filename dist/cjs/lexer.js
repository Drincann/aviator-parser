"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Lexer = void 0;
var util_js_1 = require("./util.js");
var Lexer = /** @class */ (function () {
    function Lexer(code) {
        this.cursor = 0;
        this.code = code;
    }
    Lexer.fromCode = function (code) {
        return new Lexer(code);
    };
    Lexer.prototype.next = function () {
        var current;
        while (current = this.code[this.cursor]) {
            this.cursor++;
            if ((0, util_js_1.isEOF)(current))
                break;
            if (current === '\n')
                continue;
            if (current === '#') {
                this.skipComment();
                continue;
            }
            if ((0, util_js_1.isIdentifierStart)(current))
                return this.parseNextIdentifier();
            if ((0, util_js_1.isNumberLiteralStart)(current))
                return this.parseNextNumberLiteral();
            if ((0, util_js_1.isStringLiteralStart)(current))
                return this.parseNextStringLiteral();
            if (current === '+')
                return { type: 'Add', value: undefined, };
            if (current === '-')
                return { type: 'Subtract', value: undefined, };
            if (current === '*')
                return { type: 'Multiply', value: undefined, };
            if (current === '(')
                return { type: 'LeftParen', value: undefined, };
            if (current === ')')
                return { type: 'RightParen', value: undefined, };
            if (current === '?')
                return { type: 'Conditional', value: undefined, };
            if (current === ':')
                return { type: 'Colon', value: undefined, };
            if (current === ',')
                return { type: 'Comma', value: undefined, };
            if (current === '=') {
                if (this.code[this.cursor] === '=') {
                    this.cursor++;
                    return { type: 'Equal', value: undefined, };
                }
            }
            if (current === '!') {
                if (this.code[this.cursor] === '=') {
                    this.cursor++;
                    return { type: 'NotEqual', value: undefined, };
                }
                else
                    return { type: 'LogicNot', value: undefined, };
            }
            if (current === '<') {
                if (this.code[this.cursor] === '=') {
                    this.cursor++;
                    return { type: 'LessThanEqual', value: undefined, };
                }
                else
                    return { type: 'LessThan', value: undefined, };
            }
            if (current === '>') {
                if (this.code[this.cursor] === '=') {
                    this.cursor++;
                    return { type: 'GreaterThanEqual', value: undefined, };
                }
                else
                    return { type: 'GreaterThan', value: undefined, };
            }
            if (current === '&') {
                if (this.code[this.cursor] === '&') {
                    this.cursor++;
                    return { type: 'LogicAnd', value: undefined, };
                }
            }
            if (current === '|') {
                if (this.code[this.cursor] === '|') {
                    this.cursor++;
                    return { type: 'LogicOr', value: undefined, };
                }
            }
        } // end while
        return undefined;
    };
    Lexer.prototype.skipComment = function () {
        this.until(new Set(['\n', '\0']));
    };
    Lexer.prototype.parseNextIdentifier = function () {
        var start = this.cursor - 1;
        var end = this.cursor;
        var current = this.code[end];
        while ((0, util_js_1.isIdentifier)(current)) {
            end++;
            current = this.code[end];
        }
        this.cursor = end;
        var name = this.code.substring(start, end);
        if (name === 'true')
            return { type: 'True', value: 'true' };
        else if (name === 'false')
            return { type: 'False', value: 'false' };
        return { type: 'Identifier', value: name, };
    };
    Lexer.prototype.parseNextNumberLiteral = function () {
        var start = this.cursor - 1;
        if /* float */ ('.' === this.code[start] && (0, util_js_1.isDigit)(this.code[start + 1])) {
            var end = start + 1;
            var current = this.code[end];
            while ((0, util_js_1.isDigitWithUnderscore)(current)) {
                end++;
                current = this.code[end];
            }
            this.cursor = end;
            var value = this.code.substring(start, end).replace(/_/g, '');
            return { type: 'Number', value: (0, util_js_1.parseDec)('0' + value), };
        }
        else if ('0' === this.code[start]) {
            if /* hex */ ('0x' === this.code.substring(start, start + 2) && (0, util_js_1.isHexDigit)(this.code[start + 2])) {
                var end = start + 2;
                var current = this.code[end];
                while ((0, util_js_1.isHexDigitWithUnderscore)(current)) {
                    end++;
                    current = this.code[end];
                }
                this.cursor = end;
                var value = this.code.substring(start, end).replace(/_/g, '');
                return { type: 'Number', value: (0, util_js_1.parseHex)(value), };
            }
            else if /* oct */ ('0' === this.code[start] && (0, util_js_1.isOctDigit)(this.code[start + 1])) {
                var end = start + 1;
                var current = this.code[end];
                while ((0, util_js_1.isOctDigitWithUnderscore)(current)) {
                    end++;
                    current = this.code[end];
                }
                this.cursor = end;
                var value = this.code.substring(start, end).replace(/_/g, '');
                return { type: 'Number', value: (0, util_js_1.parseOct)(value), };
            }
            else if /* float */ ('.' === this.code[start + 1] && (0, util_js_1.isDigit)(this.code[start + 2])) {
                var end = start + 2;
                var current = this.code[end];
                while ((0, util_js_1.isDigitWithUnderscore)(current)) {
                    end++;
                    current = this.code[end];
                }
                this.cursor = end;
                var value = this.code.substring(start, end).replace(/_/g, '');
                return { type: 'Number', value: (0, util_js_1.parseDec)(value), };
            }
            return { type: 'Number', value: 0, };
        }
        else /* dec */ {
            var end = start + 1;
            var current = this.code[end];
            while ((0, util_js_1.isDigitWithUnderscore)(current)) {
                end++;
                current = this.code[end];
            }
            if (current === '.' && (0, util_js_1.isDigit)(this.code[end + 1])) {
                end++;
                current = this.code[end];
                while ((0, util_js_1.isDigitWithUnderscore)(current)) {
                    end++;
                    current = this.code[end];
                }
            }
            this.cursor = end;
            var value = this.code.substring(start, end).replace(/_/g, '');
            return { type: 'Number', value: (0, util_js_1.parseDec)(value), };
        }
    };
    Lexer.prototype.parseNextStringLiteral = function () {
        var start = this.cursor - 1;
        if ((0, util_js_1.isDoubleQuote)(this.code[start]) || (0, util_js_1.isSingleQuote)(this.code[start])) {
            return this.parseStringLiteral(start);
        }
        throw new Error("lexer error, invalid string literal");
    };
    /**
     * @param start the position of the first double quote
     */
    Lexer.prototype.parseStringLiteral = function (start) {
        var quote = this.code[start];
        var cursor = start + 1;
        var current = this.code[cursor];
        var literal = '';
        while (current !== quote && (0, util_js_1.isNotEOL)(current)) {
            if (current === '\\' && this.code[cursor + 1] !== undefined) {
                var escape_1 = (0, util_js_1.getEscape)(this.code[++cursor]);
                if (escape_1 === undefined) {
                    throw new Error("lexer error: invalid escape character");
                }
                literal += escape_1;
                current = this.code[++cursor];
                continue;
            }
            literal += current;
            current = this.code[++cursor];
        }
        if (current !== quote) {
            throw new Error("lexer error, unterminated string literal");
        }
        this.cursor = cursor + 1;
        return { type: 'String', value: literal, };
    };
    Lexer.prototype.until = function (charSet) {
        var current = this.code[this.cursor];
        while (!charSet.has(current) && (0, util_js_1.isNotEOF)(current)) {
            this.cursor++;
            current = this.code[this.cursor];
        }
    };
    return Lexer;
}());
exports.Lexer = Lexer;
