package io.github.drincann.aviator.util;

public class LexerUtil {
    public static boolean isIdentifierStart(char ch) {
        return isAlpha(ch) || ch == '_';
    }

    public static boolean isIdentifierRest(char ch) {
        return isAlpha(ch) || isDigit(ch) || ch == '_';
    }

    public static boolean isNotIdentifierStart(char ch) {
        return !isIdentifierStart(ch);
    }

    public static boolean isNumberLiteralStart(char ch) {
        return isDigit(ch) || ch == '.';
    }

    public static boolean isStringLiteralStart(char ch) {
        return ch == '\'' || ch == '"';
    }

    public static boolean isNotEOL(char ch) {
        return ch != '\n' && ch != '\r';
    }

    public static boolean isWhiteSpace(char ch) {
        return ch ==' ' || ch == '\t' || ch == '\f' || ch == '\b' || ch == '\n' || ch == '\r';
    }

    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static String toPrintable(char ch) {
        switch (ch) {
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            case '\f':
                return "\\f";
            case '\b':
                return "\\b";
            default:
                return String.valueOf(ch);
        }
    }

    public static boolean isHexDigit(char ch) {
        return isDigit(ch) || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
    }

    private static boolean isAlpha(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }
}
