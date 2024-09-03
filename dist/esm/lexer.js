import { parseDec, parseHex, parseOct, isDigitWithUnderscore, isDigit, isOctDigitWithUnderscore, isOctDigit, isHexDigitWithUnderscore, isHexDigit, isIdentifierStart, isNumberLiteralStart, isIdentifier, isEOF, isStringLiteralStart, getEscape, isDoubleQuote, isSingleQuote, isNotEOL, isNotEOF } from "./util.js";
export class Lexer {
    code;
    cursor = 0;
    constructor(code) {
        this.code = code;
    }
    static fromCode(code) {
        return new Lexer(code);
    }
    next() {
        let current;
        while (current = this.code[this.cursor]) {
            this.cursor++;
            if (isEOF(current))
                break;
            if (current === '\n')
                continue;
            if (current === '#') {
                this.skipComment();
                continue;
            }
            if (isIdentifierStart(current))
                return this.parseNextIdentifier();
            if (isNumberLiteralStart(current))
                return this.parseNextNumberLiteral();
            if (isStringLiteralStart(current))
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
    }
    skipComment() {
        this.until(new Set(['\n', '\0']));
    }
    parseNextIdentifier() {
        const start = this.cursor - 1;
        let end = this.cursor;
        let current = this.code[end];
        while (isIdentifier(current)) {
            end++;
            current = this.code[end];
        }
        this.cursor = end;
        const name = this.code.substring(start, end);
        if (name === 'true')
            return { type: 'True', value: 'true' };
        else if (name === 'false')
            return { type: 'False', value: 'false' };
        return { type: 'Identifier', value: name, };
    }
    parseNextNumberLiteral() {
        const start = this.cursor - 1;
        if /* float */ ('.' === this.code[start] && isDigit(this.code[start + 1])) {
            let end = start + 1;
            let current = this.code[end];
            while (isDigitWithUnderscore(current)) {
                end++;
                current = this.code[end];
            }
            this.cursor = end;
            const value = this.code.substring(start, end).replace(/_/g, '');
            return { type: 'Number', value: parseDec('0' + value), };
        }
        else if ('0' === this.code[start]) {
            if /* hex */ ('0x' === this.code.substring(start, start + 2) && isHexDigit(this.code[start + 2])) {
                let end = start + 2;
                let current = this.code[end];
                while (isHexDigitWithUnderscore(current)) {
                    end++;
                    current = this.code[end];
                }
                this.cursor = end;
                const value = this.code.substring(start, end).replace(/_/g, '');
                return { type: 'Number', value: parseHex(value), };
            }
            else if /* oct */ ('0' === this.code[start] && isOctDigit(this.code[start + 1])) {
                let end = start + 1;
                let current = this.code[end];
                while (isOctDigitWithUnderscore(current)) {
                    end++;
                    current = this.code[end];
                }
                this.cursor = end;
                const value = this.code.substring(start, end).replace(/_/g, '');
                return { type: 'Number', value: parseOct(value), };
            }
            else if /* float */ ('.' === this.code[start + 1] && isDigit(this.code[start + 2])) {
                let end = start + 2;
                let current = this.code[end];
                while (isDigitWithUnderscore(current)) {
                    end++;
                    current = this.code[end];
                }
                this.cursor = end;
                const value = this.code.substring(start, end).replace(/_/g, '');
                return { type: 'Number', value: parseDec(value), };
            }
            return { type: 'Number', value: 0, };
        }
        else /* dec */ {
            let end = start + 1;
            let current = this.code[end];
            while (isDigitWithUnderscore(current)) {
                end++;
                current = this.code[end];
            }
            if (current === '.' && isDigit(this.code[end + 1])) {
                end++;
                current = this.code[end];
                while (isDigitWithUnderscore(current)) {
                    end++;
                    current = this.code[end];
                }
            }
            this.cursor = end;
            const value = this.code.substring(start, end).replace(/_/g, '');
            return { type: 'Number', value: parseDec(value), };
        }
    }
    parseNextStringLiteral() {
        const start = this.cursor - 1;
        if (isDoubleQuote(this.code[start]) || isSingleQuote(this.code[start])) {
            return this.parseStringLiteral(start);
        }
        throw new Error("lexer error, invalid string literal");
    }
    /**
     * @param start the position of the first double quote
     */
    parseStringLiteral(start) {
        let quote = this.code[start];
        let cursor = start + 1;
        let current = this.code[cursor];
        let literal = '';
        while (current !== quote && isNotEOL(current)) {
            if (current === '\\' && this.code[cursor + 1] !== undefined) {
                const escape = getEscape(this.code[++cursor]);
                if (escape === undefined) {
                    throw new Error("lexer error: invalid escape character");
                }
                literal += escape;
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
    }
    until(charSet) {
        let current = this.code[this.cursor];
        while (!charSet.has(current) && isNotEOF(current)) {
            this.cursor++;
            current = this.code[this.cursor];
        }
    }
}
