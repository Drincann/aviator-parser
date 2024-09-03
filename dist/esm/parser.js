import { Lexer } from "./lexer.js";
export class AviatorParser {
    lexer;
    currentToken;
    constructor(code) { this.lexer = new Lexer(code); }
    parse() {
        this.next();
        return this.parseStatements();
    }
    parseStatements() {
        const statements = [];
        while (this.currentToken !== undefined) {
            if (this.currentToken?.type === 'Semicolon') {
                this.next();
                continue;
            }
            statements.push({ type: 'statement', expression: this.parseExpression() });
        }
        return statements;
    }
    parseExpression(priority = 0) {
        let left = this.tryParseUnary();
        if (left === undefined) {
            throw new Error('Unexpected token: ' + this.currentToken?.type + ' ' + this.currentToken?.value);
        }
        while (isBinaryOperator(this.currentToken) && getPriority(this.currentToken.type) > priority) {
            const operator = this.currentToken;
            this.next();
            left = {
                type: 'binary-expression',
                left: left,
                operator: operator.type,
                right: this.parseExpression(getPriority(operator.type))
            };
            if (this.tryMatch('Conditional')) {
                const test = left;
                const consequent = this.parseExpression(getPriority('Conditional'));
                this.match('Colon');
                const alternate = this.parseExpression(getPriority('Colon'));
                return { type: 'ternary-expression', test, consequent, alternate };
            }
        }
        return left;
    }
    tryParseUnary() {
        if (this.currentToken?.type === 'Regex') {
            const value = this.currentToken.value;
            this.match('Regex');
            return { type: 'regex-literal', value };
        }
        else if (this.currentToken?.type === 'String') {
            const value = this.currentToken.value;
            this.match('String');
            return { type: 'string-literal', value };
        }
        else if (this.currentToken?.type === 'Number') {
            const value = this.currentToken.value;
            this.match('Number');
            return { type: 'number-literal', value };
        }
        else if (this.currentToken?.type === 'True') {
            this.match('True');
            return { type: 'boolean-literal', value: true };
        }
        else if (this.currentToken?.type === 'False') {
            this.match('False');
            return { type: 'boolean-literal', value: false };
        }
        else if (this.currentToken?.type === 'Nil') {
            this.match('Nil');
            return { type: 'nil-literal' };
        }
        else if (this.currentToken?.type === 'Identifier') {
            const name = this.currentToken.value;
            this.match('Identifier');
            // @ts-ignore
            if (this.currentToken.type === 'LeftParen') {
                return this.parseFunctionCall(name);
            }
            return { type: 'identifier', name };
        }
        else if (this.currentToken?.type === 'LeftParen') {
            this.match('LeftParen');
            const expression = this.parseExpression();
            this.match('RightParen');
            return expression;
        }
        else if (this.currentToken?.type === 'LogicNot') {
            this.match('LogicNot');
            const argument = this.parseExpression(getPriority('LogicNot'));
            return {
                type: 'unary-expression',
                operator: 'LogicNot',
                argument
            };
        }
    }
    parseFunctionCall(name) {
        this.match('LeftParen');
        const args = [];
        while (this.currentToken?.type !== 'RightParen') {
            args.push(this.parseExpression());
            if (this.currentToken?.type === 'Comma') {
                this.match('Comma');
            }
        }
        this.match('RightParen');
        return { type: 'function-call', name, arguments: args };
    }
    next() {
        this.currentToken = this.lexer.next();
    }
    match(type) {
        if (this.currentToken?.type !== type) {
            throw new Error('Unexpected token: ' + this.currentToken?.type + ' ' + this.currentToken?.value);
        }
        this.next();
    }
    tryMatch(type) {
        if (this.currentToken?.type === type) {
            this.next();
            return true;
        }
        return false;
    }
}
function isBinaryOperator(currentToken) {
    if (currentToken === undefined)
        return false;
    return currentToken.type in {
        'Add': true, 'Subtract': true, 'Multiply': true, 'Divide': true, 'Mod': true,
        'Like': true, 'Equal': true, 'NotEqual': true, 'LessThan': true, 'LessThanEqual': true, 'GreaterThan': true, 'GreaterThanEqual': true, 'LogicOr': true, 'LogicAnd': true, 'LogicNot': true
    };
}
function getPriority(type) {
    switch (type) {
        case 'LeftParen':
        case 'RightParen': return 15;
        case 'LogicNot': return 14;
        case 'Multiply':
        case 'Divide':
        case 'Mod': return 13;
        case 'Add':
        case 'Subtract': return 12;
        case 'LessThan':
        case 'LessThanEqual':
        case 'GreaterThan':
        case 'GreaterThanEqual': return 11;
        case 'Equal':
        case 'NotEqual':
        case 'Like': return 10;
        case 'LogicAnd': return 9;
        case 'LogicOr': return 8;
        case 'Conditional':
        case 'Colon': return 7;
        default: throw new Error('Unknown operator: ' + type);
    }
}
