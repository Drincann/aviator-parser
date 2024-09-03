import { Lexer } from "./lexer.js";
export class AviatorExpressionParser {
    lexer;
    currentToken;
    constructor(code) { this.lexer = new Lexer(code); }
    parse() {
        this.next();
        return this.parseExpression();
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
        }
        return left;
    }
    tryParseUnary() {
        if (this.currentToken?.type === 'String') {
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
        else if (this.currentToken?.type === 'Identifier') {
            const name = this.currentToken.value;
            this.match('Identifier');
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
    next() {
        this.currentToken = this.lexer.next();
    }
    match(type) {
        if (this.currentToken?.type !== type) {
            throw new Error('Unexpected token: ' + this.currentToken?.type + ' ' + this.currentToken?.value);
        }
        this.next();
    }
}
function isBinaryOperator(currentToken) {
    if (currentToken === undefined)
        return false;
    return currentToken.type in {
        'Add': true, 'Subtract': true, 'Multiply': true, 'Divide': true, 'Mod': true,
        'Equal': true, 'NotEqual': true, 'LessThan': true, 'LessThanEqual': true, 'GreaterThan': true, 'GreaterThanEqual': true, 'LogicOr': true, 'LogicAnd': true, 'LogicNot': true
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
        case 'NotEqual': return 10;
        case 'LogicAnd': return 9;
        case 'LogicOr': return 8;
        case 'Conditional': return 7;
        default: throw new Error('Unknown operator: ' + type);
    }
}
