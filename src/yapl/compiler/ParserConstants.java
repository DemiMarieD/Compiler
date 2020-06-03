/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
package yapl.compiler;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int comment = 5;
  /** RegularExpression Id. */
  int SEMICOLON = 6;
  /** RegularExpression Id. */
  int COMMA = 7;
  /** RegularExpression Id. */
  int DOT = 8;
  /** RegularExpression Id. */
  int ASSIGN = 9;
  /** RegularExpression Id. */
  int LESS = 10;
  /** RegularExpression Id. */
  int GREATER = 11;
  /** RegularExpression Id. */
  int LESS_EQUAL = 12;
  /** RegularExpression Id. */
  int GREATER_EQAUL = 13;
  /** RegularExpression Id. */
  int IS = 14;
  /** RegularExpression Id. */
  int EQUAL = 15;
  /** RegularExpression Id. */
  int NOT_EQUAL = 16;
  /** RegularExpression Id. */
  int PLUS = 17;
  /** RegularExpression Id. */
  int MINUS = 18;
  /** RegularExpression Id. */
  int MULT = 19;
  /** RegularExpression Id. */
  int DIV = 20;
  /** RegularExpression Id. */
  int MOD = 21;
  /** RegularExpression Id. */
  int SHARP = 22;
  /** RegularExpression Id. */
  int PAR_LEFT = 23;
  /** RegularExpression Id. */
  int PAR_RIGHT = 24;
  /** RegularExpression Id. */
  int BRACKET_LEFT = 25;
  /** RegularExpression Id. */
  int BRACKET_RIGHT = 26;
  /** RegularExpression Id. */
  int CURLY_LEFT = 27;
  /** RegularExpression Id. */
  int CURLY_RIGHT = 28;
  /** RegularExpression Id. */
  int NEW = 29;
  /** RegularExpression Id. */
  int TRUE = 30;
  /** RegularExpression Id. */
  int FALSE = 31;
  /** RegularExpression Id. */
  int AND = 32;
  /** RegularExpression Id. */
  int OR = 33;
  /** RegularExpression Id. */
  int IF = 34;
  /** RegularExpression Id. */
  int THEN = 35;
  /** RegularExpression Id. */
  int ELSE = 36;
  /** RegularExpression Id. */
  int ENDIF = 37;
  /** RegularExpression Id. */
  int WHILE = 38;
  /** RegularExpression Id. */
  int DO = 39;
  /** RegularExpression Id. */
  int ENDWHILE = 40;
  /** RegularExpression Id. */
  int WRITE = 41;
  /** RegularExpression Id. */
  int INT = 42;
  /** RegularExpression Id. */
  int BOOL = 43;
  /** RegularExpression Id. */
  int DECL = 44;
  /** RegularExpression Id. */
  int BEGIN = 45;
  /** RegularExpression Id. */
  int END = 46;
  /** RegularExpression Id. */
  int PROGRAM = 47;
  /** RegularExpression Id. */
  int PROCEDURE = 48;
  /** RegularExpression Id. */
  int VOID = 49;
  /** RegularExpression Id. */
  int RETURN = 50;
  /** RegularExpression Id. */
  int CONST = 51;
  /** RegularExpression Id. */
  int RECORD = 52;
  /** RegularExpression Id. */
  int ENDRECORD = 53;
  /** RegularExpression Id. */
  int string = 54;
  /** RegularExpression Id. */
  int otherchar = 55;
  /** RegularExpression Id. */
  int ident = 56;
  /** RegularExpression Id. */
  int number = 57;
  /** RegularExpression Id. */
  int letter = 58;
  /** RegularExpression Id. */
  int digit = 59;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<comment>",
    "\";\"",
    "\",\"",
    "\".\"",
    "\":=\"",
    "\"<\"",
    "\">\"",
    "\"<=\"",
    "\">=\"",
    "\"=\"",
    "\"==\"",
    "\"!=\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"%\"",
    "\"#\"",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "\"new\"",
    "\"True\"",
    "\"False\"",
    "\"And\"",
    "\"Or\"",
    "\"If\"",
    "\"Then\"",
    "\"Else\"",
    "\"EndIf\"",
    "\"While\"",
    "\"Do\"",
    "\"EndWhile\"",
    "\"Write\"",
    "\"int\"",
    "\"bool\"",
    "\"Declare\"",
    "\"Begin\"",
    "\"End\"",
    "\"Program\"",
    "\"Procedure\"",
    "\"void\"",
    "\"Return\"",
    "\"Const\"",
    "\"Record\"",
    "\"EndRecord\"",
    "<string>",
    "<otherchar>",
    "<ident>",
    "<number>",
    "<letter>",
    "<digit>",
  };

}