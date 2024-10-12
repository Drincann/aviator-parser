package io.github.drincann.aviator.lexer.token;

public enum AviatorTokenType {
    // 数学运算符
    ADD /* + */, SUBTRACT /* - */, MULTIPLY /* * */, DIVIDE /* / */, MOD /* % */, POW /* ** */,
    BIT_AND /* & */, BIT_OR /* | */, BIT_XOR /* ^ */, BIT_NOT /* ~ */, SIGNED_BIT_SHIFT_LEFT /* << */, SIGNED_BIT_SHIFT_RIGHT /* >> */,
    UNSIGNED_BIT_SHIFT_RIGHT /* >>> */,

    // 逻辑运算
    LIKE /* =~ */, EQUAL /* == */, NOT_EQUAL /* != */,
    GREATER_THAN /* > */, GREATER_THAN_EQUAL /* >= */,
    LESS_THAN /* < */, LESS_THAN_EQUAL /* <= */,
    LOGIC_AND /* && */, LOGIC_OR /* || */, LOGIC_NOT /* ! */,
    CONDITIONAL /* ? */, COLON /* : */,
    COMMA /* , */, SEMICOLON /* ; */,

    // 括号
    LEFT_PAREN /* ( */, RIGHT_PAREN /* ) */,
    LEFT_BRACKET /* [ */, RIGHT_BRACKET /* ] */,
    LEFT_BRACE /* { */, RIGHT_BRACE /* } */,

    // 对象访问
    DOT /* . */,

    // 流程控制关键字
    IF /* if */, ELSE /* else */, ELSE_IF /* elsif */,
    FOR /* for */, IN /* in */, WHILE /* while */, BREAK /* break */, CONTINUE /* continue */, RETURN /* return */,

    // 异常处理关键字
    TRY /* try */, CATCH /* catch */, FINALLY /* finally */, THROW /* throw */,

    // 函数和闭包关键字
    FN /* fn */, LAMBDA /* lambda */, ARROW /* -> */, END /* end */,

    // 注释
    COMMENT, // ::= ##<comment_content>
    // <comment_content> ::= <comment_char>*

    // 赋值、变量声明、new 创建对象、use 导入
    LET /* let */, NEW /* new */, USE /* use */,

    // 字面量关键字
    TRUE /* true */, FALSE /* false */, NIL /* nil */,

    // 赋值
    ASSIGN /* = */,

    // 字面量
    IDENTIFIER, // ::= <identifier_start>+ <identifier_rest>*
    // <identifier_start> ::= _ | [a-z] | [A-Z]
    // <identifier_rest> ::= <identifier_start> | [0-9]

    NUMBER, // ::= <hex_number> | <decimal_number>
    // <hex_number> ::= 0x<hex_digit>+
    // <decimal_number> ::= <digit>+
    // <digit> ::= [0-9]
    // <hex_digit> ::= <digit> | [a-f] | [A-F]

    STRING, // ::= (<single_quoted_string> | <double_quoted_string>)
    // <single_quoted_string> ::= '<string_content>'
    // <double_quoted_string> ::= "<string_content>"
    // <string_content> ::= <string_char>*
    // <string_char> ::= \r | \n | \t | \\ | \" | \' | <unicode_char>

    REGEX, // ::= /<regex_content>/
    // <regex_content> ::= <regex_char>*

    EOF,
}
