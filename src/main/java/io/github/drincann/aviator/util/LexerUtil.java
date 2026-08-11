package io.github.drincann.aviator.util;

public final class LexerUtil {
    private LexerUtil() {
    }

    public static boolean isIdentifierStart(int codePoint) {
        return Character.isJavaIdentifierStart(codePoint);
    }

    public static boolean isIdentifierRest(int codePoint) {
        return Character.isJavaIdentifierPart(codePoint);
    }

    public static boolean isStringLiteralStart(char ch) {
        return ch == '\'' || ch == '"';
    }

    public static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\f' || ch == '\b' || ch == '\n' || ch == '\r';
    }

    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
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
            case 0:
                return "EOF";
            default:
                return String.valueOf(ch);
        }
    }

    public static boolean isHexDigit(char ch) {
        return isDigit(ch) || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
    }
}
