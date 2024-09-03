"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.AviatorExpressionParser = void 0;
var lexer_js_1 = require("./lexer.js");
var AviatorExpressionParser = /** @class */ (function () {
    function AviatorExpressionParser(code) {
        this.lexer = new lexer_js_1.Lexer(code);
    }
    AviatorExpressionParser.prototype.parse = function () {
        this.next();
        return this.parseExpression();
    };
    AviatorExpressionParser.prototype.parseExpression = function (priority) {
        var _a, _b;
        if (priority === void 0) { priority = 0; }
        var left = this.tryParseUnary();
        if (left === undefined) {
            throw new Error('Unexpected token: ' + ((_a = this.currentToken) === null || _a === void 0 ? void 0 : _a.type) + ' ' + ((_b = this.currentToken) === null || _b === void 0 ? void 0 : _b.value));
        }
        while (isBinaryOperator(this.currentToken) && getPriority(this.currentToken.type) > priority) {
            var operator = this.currentToken;
            this.next();
            left = {
                type: 'binary-expression',
                left: left,
                operator: operator.type,
                right: this.parseExpression(getPriority(operator.type))
            };
        }
        return left;
    };
    AviatorExpressionParser.prototype.tryParseUnary = function () {
        var _a, _b, _c, _d, _e, _f, _g;
        if (((_a = this.currentToken) === null || _a === void 0 ? void 0 : _a.type) === 'String') {
            var value = this.currentToken.value;
            this.match('String');
            return { type: 'string-literal', value: value };
        }
        else if (((_b = this.currentToken) === null || _b === void 0 ? void 0 : _b.type) === 'Number') {
            var value = this.currentToken.value;
            this.match('Number');
            return { type: 'number-literal', value: value };
        }
        else if (((_c = this.currentToken) === null || _c === void 0 ? void 0 : _c.type) === 'True') {
            this.match('True');
            return { type: 'boolean-literal', value: true };
        }
        else if (((_d = this.currentToken) === null || _d === void 0 ? void 0 : _d.type) === 'False') {
            this.match('False');
            return { type: 'boolean-literal', value: false };
        }
        else if (((_e = this.currentToken) === null || _e === void 0 ? void 0 : _e.type) === 'Identifier') {
            var name_1 = this.currentToken.value;
            this.match('Identifier');
            return { type: 'identifier', name: name_1 };
        }
        else if (((_f = this.currentToken) === null || _f === void 0 ? void 0 : _f.type) === 'LeftParen') {
            this.match('LeftParen');
            var expression = this.parseExpression();
            this.match('RightParen');
            return expression;
        }
        else if (((_g = this.currentToken) === null || _g === void 0 ? void 0 : _g.type) === 'LogicNot') {
            this.match('LogicNot');
            var argument = this.parseExpression(getPriority('LogicNot'));
            return {
                type: 'unary-expression',
                operator: 'LogicNot',
                argument: argument
            };
        }
    };
    AviatorExpressionParser.prototype.next = function () {
        this.currentToken = this.lexer.next();
    };
    AviatorExpressionParser.prototype.match = function (type) {
        var _a, _b, _c;
        if (((_a = this.currentToken) === null || _a === void 0 ? void 0 : _a.type) !== type) {
            throw new Error('Unexpected token: ' + ((_b = this.currentToken) === null || _b === void 0 ? void 0 : _b.type) + ' ' + ((_c = this.currentToken) === null || _c === void 0 ? void 0 : _c.value));
        }
        this.next();
    };
    return AviatorExpressionParser;
}());
exports.AviatorExpressionParser = AviatorExpressionParser;
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
