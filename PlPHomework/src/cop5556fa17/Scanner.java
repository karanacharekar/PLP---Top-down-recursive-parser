/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;


//import Scanner.Kind;
//import Scanner.State;
//import Scanner.Token;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	
	
	
	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}
	
		
	public enum State{
		START, AFTR_EQUALS , DIGITS , IDENTIFIER , AFTR_LT , 
		AFTR_GT , AFTR_EXCL  , AFTR_DIV , 
		AFTR_MINUS , AFTR_TIMES, /*BOOLEAN_LITERAL,*/ STRING_LITERAL,INTERIM
		}
	
		
	Map<String,Kind> map = null;

	
	
	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
			
			
		}

		public boolean isKind(Kind k)
		{
			return this.kind.equals(k);
		}
		
		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public boolean boolVal() {
			assert kind == Kind.BOOLEAN_LITERAL;
			String val = this.getText();
			if(val.equals("true")) {
			return true;
			}
			else {
			return false;
			}
		}
		
		
		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}
		
		

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  

	boolean isJavaIdentifierPart(char c){
		if(c == '$' || c == '_' || (c>='a' && c<='z') || (c>='A' && c<='Z') || (c>='0' && c<='9')){
			return true;
		}
		return false;
	 }
	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
		
		this.map = new HashMap<String,Kind>();
		
		
		map.put("true",Kind.BOOLEAN_LITERAL);
		map.put("false",Kind.BOOLEAN_LITERAL);
		map.put("x",Kind.KW_x);
		map.put("y",Kind.KW_y);
		map.put("X",Kind.KW_X);
		map.put("Y",Kind.KW_Y);
		map.put("r",Kind.KW_r);
		map.put("R",Kind.KW_R);
		map.put("a",Kind.KW_a);
		map.put("A",Kind.KW_A);
		map.put("Z",Kind.KW_Z);
		map.put("DEF_X",Kind.KW_DEF_X);
		map.put("DEF_Y",Kind.KW_DEF_Y);
		map.put("SCREEN",Kind.KW_SCREEN);
		map.put("cart_x",Kind.KW_cart_x);
		map.put("cart_y",Kind.KW_cart_y);
		map.put("polar_a",Kind.KW_polar_a);
		map.put("polar_r",Kind.KW_polar_r);
		map.put("abs",Kind.KW_abs);
		map.put("sin",Kind.KW_sin);
		map.put("cos",Kind.KW_cos);
		map.put("atan",Kind.KW_atan);
		map.put("log",Kind.KW_log);
		map.put("image",Kind.KW_image);
		map.put("int",Kind.KW_int);
		map.put("boolean",Kind.KW_boolean);
		map.put("url",Kind.KW_url);
		map.put("file",Kind.KW_file);
	}
	

	
	
	public Token earlierToken() {
		int x = nextTokenPos-2;
		Token t = tokens.get(x);
		nextTokenPos = nextTokenPos -1;
		return t;
	}
	

	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		State state = State.START;
		int startPos = 0 ;
		int ch = 0;
		
		//System.out.println(chars.length);
		
		while(pos < chars.length) {
		//ch = chars[pos];
		ch = pos < chars.length ? chars[pos] : EOFchar;
		switch(state) 
		{
		
		case START:

		    ch = pos < chars.length ? chars[pos] : EOFchar;
		    startPos = pos;
			
			switch(ch) {
			
			/* operators */
			
			case '+':{
				Token t = new Token(Kind.OP_PLUS,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '-':{
				state = State.AFTR_MINUS;
				pos++;
				posInLine++;
			} break;
				
			case '<':{
				state = State.AFTR_LT;
				pos++;
				posInLine++;
			} break;
			
			case '>':{
				state = State.AFTR_GT;
				pos++;
				posInLine++;
			} break;
				
			case ':':{
				Token t = new Token(Kind.OP_COLON,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '!':{		
				state = State.AFTR_EXCL;
				pos++;
				posInLine++;
			} break;
				
			case '?':{
				Token t = new Token(Kind.OP_Q,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '%':{
				Token t = new Token(Kind.OP_MOD,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '*':{
				state = State.AFTR_TIMES;
				pos++;
				posInLine++;
			} break;
				
			case '/':{
				state = State.AFTR_DIV;
				pos++;
				posInLine++;
				} break;
				
			case '&':{
				Token t = new Token(Kind.OP_AND,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '@':{
				Token t = new Token(Kind.OP_AT,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '|':{
				Token t = new Token(Kind.OP_OR,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '=':{		
				state = State.AFTR_EQUALS;
				pos++;
				posInLine++;
			} break;
			
			/* separators */
				
			case '[':{
				Token t = new Token(Kind.LSQUARE,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				}break;
				
			case ']':{
				Token t = new Token(Kind.RSQUARE,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				}break;
				
			case '(':{
				Token t = new Token(Kind.LPAREN,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				}break;
				
			case ')':{
				Token t = new Token(Kind.RPAREN,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				}break;
				
			case ';':{
				Token t = new Token(Kind.SEMI,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				}break;
				
			case ',':{
				Token t = new Token(Kind.COMMA,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;	
			
				
			case '0': {
				Token t = new Token(Kind.INTEGER_LITERAL,startPos,1,line,posInLine);
				tokens.add(t);
				pos++;
				posInLine++;
				state = State.START;
				} break;
				
			case '"': {
				state = State.STRING_LITERAL;
				pos++;
				posInLine++;
			} break;
				
	        case EOFchar : { 
	        	Token t = new Token(Kind.EOF,pos,0,line,posInLine);
				tokens.add(t);
	        	pos++;
	        	posInLine++;// next iteration should terminate loop
	        	} break;
			
	     
	       /* white space */
	        	
	        case ' ': {
	        	posInLine++;
				pos++;
				
				state = State.START;
				} break;
				
				
	        case '\t': {
	        	posInLine++;
				pos++;
				
				state = State.START;
				} break;
				
	        case '\f': {
				posInLine++;
				pos++;
				
				state = State.START;
				} break;
				
			/* new line */	
				
	        case '\n': {
				line++;
				posInLine = 1;
				pos++;
				
				state = State.START;
				} break;
	        	
	        case '\r': {
				pos++;
				posInLine++;
				state = State.INTERIM;
				} break;
	        	
			default:{
			if (Character.isDigit(chars[pos])){
				startPos = pos;
				pos++;
				posInLine++;
				state = State.DIGITS;
				
			}
			else if (isJavaIdentifierPart(chars[pos])) {
				pos++;
				posInLine++;
				state = State.IDENTIFIER;	
			}
			else {
				throw new LexicalException("No match!",pos);
			}
			}
			}break;
			
			
		case AFTR_EQUALS:{
			if (chars[pos]=='=') {
				pos++;
				Token t = new Token(Kind.OP_EQ,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
			}
			else {
				Token t = new Token(Kind.OP_ASSIGN,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
			}
			state = State.START;	
		} break;
			
		case AFTR_TIMES:
		{
			if (chars[pos]=='*') {
				pos++;
				Token t = new Token(Kind.OP_POWER,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
			}
			else {
				Token t = new Token(Kind.OP_TIMES,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
			}
			state = State.START;	
		} break;
			
		case DIGITS:
		{
			if (Character.isDigit(chars[pos])) {
				while(Character.isDigit(chars[pos])) {
					pos++;
					posInLine++;
				}
				
				try
				{
					String charss = new String(chars);
					Integer.parseInt(charss.substring(startPos, pos));
				}
				catch(Exception e)
				{
					throw new LexicalException("Int literal overflow!",startPos);
				}
				
				String charss = new String(chars);
				Integer.parseInt(charss.substring(startPos, pos));
				System.out.println(charss.substring(startPos, pos));
				Token t = new Token(Kind.INTEGER_LITERAL,startPos,pos-startPos,line,posInLine-(pos-startPos));
				tokens.add(t);
				//posInLine=pos+1;
				state = State.START;
			}
			else {
				
				try
				{
					String charss = new String(chars);
					Integer.parseInt(charss.substring(startPos, pos));
				}
				catch(Exception e)
				{
					throw new LexicalException("Int literal overflow!",pos);
				}
				
				Token t = new Token(Kind.INTEGER_LITERAL,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
				state = State.START;
				
			}
		} break;
			
		case IDENTIFIER:
		{
			
			while(pos < chars.length-1 && isJavaIdentifierPart(chars[pos])) {
				pos++;
				posInLine++;
			}
			
			StringBuilder sb = new StringBuilder();
			for (int i = startPos; i < pos ; i++) {
				sb.append(chars[i]);
			}
			//System.out.println(sb.toString());
			String str = sb.toString();
			if (map.containsKey(str)) {
				Token t = new Token(map.get(str),startPos,pos-startPos,line,posInLine-(pos-startPos));
				tokens.add(t);
				//posInLine=pos+1;
			}
			else {
				Token t = new Token(Kind.IDENTIFIER,startPos,pos-startPos,line,posInLine-(pos-startPos));
				tokens.add(t);
				//posInLine=pos+1;
			}
			state = State.START;
			
		} break;
		
		case INTERIM:
		{
			if(chars[pos]=='\n') {
				line++;
				posInLine = 1;
				pos++;
				
				state = State.START;
			}
			else {
				line++;
				posInLine = 1;
				//pos++;
				
				state = State.START;
			}
				
		} break;
		
		case STRING_LITERAL:
		{
			while(chars[pos]!='"') {
				if (chars[pos]==EOFchar || chars[pos]== '\n' || chars[pos]== '\r' ) {
					throw new LexicalException("String literal invalid",pos);
				}
				else {
					
				
				if(chars[pos]=='\\'){
					if(chars[pos+1]=='b' || chars[pos+1]=='t' || chars[pos+1]=='n' || chars[pos+1]=='f' || chars[pos+1]=='r' || chars[pos+1]=='"' || chars[pos+1]=='\'' || chars[pos+1]=='\\') {
						pos++;	
						pos++;	
						posInLine++;
						posInLine++;
						continue;
					}
					else {
						throw new LexicalException("String literal invalid",pos+1);
						
					}
						}
				pos++;	
				posInLine++;
			}}
			
			pos++;
			posInLine++;
			Token t = new Token(Kind.STRING_LITERAL,startPos,pos-startPos,line,posInLine-(pos-startPos));
			tokens.add(t);
			state = State.START;
		} break;
		
		case AFTR_LT:
		{
			if(chars[pos]=='=') {
				pos++;
				Token t = new Token(Kind.OP_LE,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
				state = State.START;
			}
			else if (chars[pos]=='-') {
				pos++;
				Token t = new Token(Kind.OP_LARROW,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
				state = State.START;
			}
			else {
				Token t = new Token(Kind.OP_LT,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
				state = State.START;
			}
		} break;
		
		case AFTR_GT:
		{
			if(chars[pos]=='=') {
				pos++;
				Token t = new Token(Kind.OP_GE,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
				state = State.START;
			}
			else {
				Token t = new Token(Kind.OP_GT,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
				state = State.START;	
			}
		} break;
			
		case AFTR_EXCL:
		{
			
			if(chars[pos]=='=') {
				pos++;
				Token t = new Token(Kind.OP_NEQ,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
				state = State.START;
			}
			else {
				Token t = new Token(Kind.OP_EXCL,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
				state = State.START;
				
			}
		} break;
		
		case AFTR_DIV:	
		{
			if(chars[pos]=='/') {
				while(pos < chars.length-2 && chars[pos]!='\n' && chars[pos]!='\r') {
					pos++;
					posInLine++;
				}
				if(chars[pos]=='\n' || chars[pos]=='\r') {
					state = State.START;
				}
				else {
				pos++;
				posInLine++;
				state = State.START;
			}
			}
			else
			{
				Token t = new Token(Kind.OP_DIV,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				//posInLine++;
				state = State.START;
			}
			
		} break;
			
		case AFTR_MINUS:
		{
			if(chars[pos]=='>') {
				pos++;
				Token t = new Token(Kind.OP_RARROW,startPos,pos-startPos,line,posInLine-(pos-startPos)+1);
				tokens.add(t);
				posInLine++;
				//posInLine=pos;
				state = State.START;
			}
			else {
				Token t = new Token(Kind.OP_MINUS,startPos,1,line,posInLine-(pos-startPos));
				tokens.add(t);
				//pos++;
				state = State.START;
			}
		} break;
		
		
		default:
			assert false;
		
		}
		
		}
		
		//Token t = new Token(Kind.EOF, pos, 0,line,posInLine);
		//tokens.add(t);
		return this;
	
	}

	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
