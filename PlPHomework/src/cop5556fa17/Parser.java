package cop5556fa17;

//import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
//import cop5556fa17.SimpleParser.SyntaxException;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;

import java.util.ArrayList;

//import Parser.SyntaxException;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input. Checks for EOF
	 * 
	 * @throws SyntaxException
	 */

	public Program parse() throws SyntaxException {

		Program p = null;

		try {
			p = program();
			matchEOF();
			return p;
		} catch (SyntaxException se) {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

	}

	/*
	 * private Token match(Kind kind) throws SyntaxException { if (t.isKind(kind)) {
	 * return consume(); } throw new SyntaxException(t,"saw " + t.kind +
	 * " expected " + kind); }
	 */

	/**
	 * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */

	Program program() throws SyntaxException {
		// TODO implement this

		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		if (t.isKind(IDENTIFIER)) {
			Token id = t;
			Token name = t;
			consume();
			Program p = null;

			while (t.isKind(KW_int) || t.isKind(KW_boolean) || t.isKind(KW_url) || t.isKind(KW_file)
					|| t.isKind(KW_image) || t.isKind(IDENTIFIER)) {
				if (t.isKind(KW_int) || t.isKind(KW_boolean) || t.isKind(KW_url) || t.isKind(KW_file)
						|| t.isKind(KW_image)) {
					Declaration d = Declaration();
					decsAndStatements.add(d);
					if (t.isKind(SEMI)) {
						consume();
					} else {
						throw new SyntaxException(t,
								"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
					}
				} else if (t.isKind(IDENTIFIER)) {
					Statement s = Statement();
					decsAndStatements.add(s);
					if (t.isKind(SEMI)) {
						consume();
					} else {
						throw new SyntaxException(t,
								"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
					}
				}
			}
			p = new Program(id, name, decsAndStatements);
			return p;

		}

		else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		// throw new UnsupportedOperationException();
	}

	Declaration Declaration() throws SyntaxException {
		Declaration dec = null;

		if (t.isKind(KW_int) || t.isKind(KW_boolean)) {
			dec = VariableDeclaration();
		} else if (t.isKind(KW_image)) {
			dec = ImageDeclaration();
		} else if (t.isKind(KW_url) || (t.isKind(KW_file))) {
			dec = SourceSinkDeclaration();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		return dec;
	}

	Declaration_Variable VariableDeclaration() throws SyntaxException {

		Token ft = t;
		Token name = null;
		Expression e = null;
		Declaration_Variable dv = null;

		Token type = VarType();
		if (t.isKind(IDENTIFIER)) {
			name = consume();
			if (t.isKind(OP_ASSIGN)) {
				consume();
				e = expression();
			}
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		dv = new Declaration_Variable(ft, type, name, e);
		return dv;
	}

	Token VarType() throws SyntaxException {
		Kind kind = t.kind;
		Token type = null;
		switch (kind) {
		case KW_int:
		case KW_boolean: {
			type = consume();

		}
			break;

		default:
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
		return type;
	}

	Declaration_Image ImageDeclaration() throws SyntaxException {

		Token ft = t;
		Token name = null;
		Expression xSize = null;
		Expression ySize = null;
		Source source = null;
		Declaration_Image di = null;

		if (t.isKind(KW_image)) {
			consume();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		if (t.isKind(LSQUARE)) {
			consume();
			xSize = expression();
			if (t.isKind(COMMA)) {
				consume();
				ySize = expression();
				if (t.isKind(RSQUARE)) {
					consume();
					if (t.isKind(IDENTIFIER)) {
						name = consume();
						if (t.isKind(OP_LARROW)) {
							consume();
							source = Source();
						}
					} else {

					}
				} else {
					throw new SyntaxException(t,
							"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
				}
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}

			di = new Declaration_Image(ft, xSize, ySize, name, source);
			return di;

		}

		else if (t.isKind(IDENTIFIER)) {
			name = consume();
			if (t.isKind(OP_LARROW)) {
				consume();
				source = Source();
			}
		}
		di = new Declaration_Image(ft, xSize, ySize, name, source);
		return di;

	}

	Declaration_SourceSink SourceSinkDeclaration() throws SyntaxException {

		Token ft = t;
		Token name = null;
		Token type = null;
		Source source = null;
		Declaration_SourceSink ds = null;

		type = SourceSinkType();
		if (t.isKind(IDENTIFIER)) {
			name = consume();
			if (t.isKind(OP_ASSIGN)) {
				consume();
				source = Source();
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
		ds = new Declaration_SourceSink(ft, type, name, source);
		return ds;
	}

	Token SourceSinkType() throws SyntaxException {

		Token type = null;
		Kind kind = t.kind;
		switch (kind) {
		case KW_url:
		case KW_file: {
			type = consume();
		}
			break;

		default:
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
		return type;
	}

	Source Source() throws SyntaxException {
		Token ft = t;
		Token name = null;
		Source_StringLiteral ssl = null;
		Source_Ident si = null;
		Source_CommandLineParam scmdp = null;
		Expression paramNum = null;

		if (t.isKind(STRING_LITERAL)) {
			String fileorurl = t.getText();
			consume();
			ssl = new Source_StringLiteral(ft, fileorurl);
			return ssl;
		} else if (t.isKind(OP_AT)) {
			consume();
			paramNum = expression();
			scmdp = new Source_CommandLineParam(ft, paramNum);
			return scmdp;
		} else if (t.isKind(IDENTIFIER)) {
			name = consume();
			si = new Source_Ident(ft, name);
			return si;
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
	}

	Index RaSelector() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		Index index = null;

		if (t.isKind(KW_r)) {
			e0 = new Expression_PredefinedName(ft, t.kind);
			consume();
			if (t.isKind(COMMA)) {
				consume();
				if (t.isKind(KW_A)) {
					e1 = new Expression_PredefinedName(ft, t.kind);
					consume();
				} else {
					throw new SyntaxException(t,
							"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
				}
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		index = new Index(ft, e0, e1);
		return index;
	}

	Index XySelector() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		Index index = null;

		if (t.isKind(KW_x)) {
			e0 = new Expression_PredefinedName(ft, t.kind);
			consume();
			if (t.isKind(COMMA)) {
				consume();
				if (t.isKind(KW_y)) {
					e1 = new Expression_PredefinedName(ft, t.kind);
					consume();
				} else {
					throw new SyntaxException(t,
							"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
				}
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		index = new Index(ft, e0, e1);
		return index;
	}

	Index Selector() throws SyntaxException {
		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		Index index = null;

		e0 = expression();
		if (t.isKind(COMMA)) {
			consume();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		e1 = expression();
		index = new Index(ft, e0, e1);
		return index;
	}

	Expression IdentOrPixelSelectorExpression() throws SyntaxException {
		Token ft = t;
		Token name = null;
		Index index = null;
		Expression_Ident ei = null;
		Expression_PixelSelector eps = null;

		if (t.isKind(IDENTIFIER)) {
			ei = new Expression_Ident(ft, ft);
			name = consume();
			if (t.isKind(LSQUARE)) {
				consume();
				index = Selector();
				// return null;
				if (t.isKind(RSQUARE)) {
					consume();
				} else {
					throw new SyntaxException(t,
							"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
				}

				eps = new Expression_PixelSelector(ft, name, index);
				return eps;
			}

		}
		return ei;
	}

	Expression Primary() throws SyntaxException {

		Token ft = t;
		// Expression_IntLit ilit = null;
		// Expression_BooleanLit blit = null;
		Expression lit = null;
		boolean bvalue;
		int ivalue;

		if (t.isKind(INTEGER_LITERAL)) {
			ivalue = t.intVal();
			consume();
			lit = new Expression_IntLit(ft, ivalue);
			// return lit;
		} else if (t.isKind(LPAREN)) {
			consume();
			lit = expression();
			if (t.isKind(RPAREN)) {
				consume();
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}
		} else if (t.isKind(KW_sin) || t.isKind(KW_cos) || t.isKind(KW_atan) || t.isKind(KW_abs) || t.isKind(KW_cart_x)
				|| t.isKind(KW_cart_y) || t.isKind(KW_polar_a) || t.isKind(KW_polar_r)) {
			lit = FunctionApplication();
		} else if (t.isKind(BOOLEAN_LITERAL)) {
			bvalue = t.boolVal();
			consume();
			lit = new Expression_BooleanLit(ft, bvalue);
			// return blit;
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
		return lit;
	}

	Expression UnaryExpression() throws SyntaxException {

		Token ft = t;
		Token op = null;
		Expression e = null;
		Expression e1 = null;
		// Expression eu = null;

		if (t.isKind(OP_PLUS)) {
			op = consume();
			e1 = UnaryExpression();
			e = new Expression_Unary(ft, op, e1);
		} else if (t.isKind(OP_MINUS)) {
			op = consume();
			e1 = UnaryExpression();
			e = new Expression_Unary(ft, op, e1);
		} else {
			e = UnaryExpressionNotPlusMinus();
		}

		return e;
	}

	Expression UnaryExpressionNotPlusMinus() throws SyntaxException {

		Token ft = t;
		Kind kind = null;
		// Expression_PredefinedName epn = null;
		Expression e = null;
		Expression eu = null;

		if (t.isKind(OP_EXCL)) {
			Token op = consume();
			eu = UnaryExpression();
			e = new Expression_Unary(ft, op, eu);
			// return eu;
		} else if (t.isKind(IDENTIFIER)) {
			e = IdentOrPixelSelectorExpression();
		} else if (t.isKind(INTEGER_LITERAL) || t.isKind(BOOLEAN_LITERAL) || t.isKind(LPAREN) || t.isKind(KW_sin)
				|| t.isKind(KW_cos) || t.isKind(KW_atan) || t.isKind(KW_abs) || t.isKind(KW_cart_x)
				|| t.isKind(KW_cart_y) || t.isKind(KW_polar_a) || t.isKind(KW_polar_r)) {
			e = Primary();
		} else if (t.isKind(KW_x) || t.isKind(KW_y) || t.isKind(KW_r) || t.isKind(KW_a) || t.isKind(KW_X)
				|| t.isKind(KW_Y) || t.isKind(KW_Z) || t.isKind(KW_A) || t.isKind(KW_R) || t.isKind(KW_DEF_X)
				|| t.isKind(KW_DEF_Y)) {
			kind = t.kind;
			consume();
			e = new Expression_PredefinedName(ft, kind);
			// return epn;
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		return e;
	}

	Expression_FunctionApp FunctionApplication() throws SyntaxException {

		Token ft = t;
		Kind function = null;
		Expression earg = null;
		Index iarg = null;
		Expression_FunctionAppWithExprArg efa = null;
		Expression_FunctionAppWithIndexArg efi = null;

		function = FunctionName();
		if (t.isKind(LPAREN)) {
			consume();
			earg = expression();
			if (t.isKind(RPAREN)) {
				consume();
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}

			efa = new Expression_FunctionAppWithExprArg(ft, function, earg);
			return efa;
		} else if (t.isKind(LSQUARE)) {
			consume();
			iarg = Selector();
			if (t.isKind(RSQUARE)) {
				consume();
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}

			efi = new Expression_FunctionAppWithIndexArg(ft, function, iarg);
			return efi;
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

	}

	Kind FunctionName() throws SyntaxException {
		Kind kind = t.kind;
		Kind function = null;
		switch (kind) {

		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_abs:
		case KW_cart_x:
		case KW_cart_y:
		case KW_polar_a:
		case KW_polar_r: {
			function = kind;
			consume();
		}
			break;

		default:
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		return function;
	}

	Statement Statement() throws SyntaxException {

		Statement st = null;

		if (t.isKind(IDENTIFIER)) {
			consume();
			if (t.isKind(OP_LARROW)) {
				st = ImageInStatement();
			} else if (t.isKind(OP_RARROW)) {
				st = ImageOutStatement();
			} else {
				//t = scanner.earlierToken();
				st = AssignmentStatement();
			}
		} else {
			throw new SyntaxException(t,
					"Line" + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		return st;
	}

	Statement_Out ImageOutStatement() throws SyntaxException {

		t = scanner.earlierToken();
		Token ft = t;
		Token name = t;
		Sink sink = null;
		Statement_Out so = null;
		consume();

		if (t.isKind(OP_RARROW)) {
			consume();
			sink = Sink();
			so = new Statement_Out(ft, name, sink);
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
		return so;
	}

	Statement_In ImageInStatement() throws SyntaxException {

		t = scanner.earlierToken();
		Token ft = t;
		Token name = t;
		Source source = null;
		Statement_In sin = null;
		consume();

		if (t.isKind(OP_LARROW)) {
			consume();
			source = Source();
			sin = new Statement_In(ft, name, source);
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}
		return sin;
	}

	Statement_Assign AssignmentStatement() throws SyntaxException {

		t = scanner.earlierToken();
		Token ft = t;
		LHS lhs = null;
		Expression e = null;
		Statement_Assign sa = null;
		//consume();

		lhs = Lhs();
		if (t.isKind(OP_ASSIGN)) {
			consume();
			e = expression();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		sa = new Statement_Assign(ft, lhs, e);
		return sa;
	}

	Sink Sink() throws SyntaxException {

		Token ft = t;
		Token name = null;
		Sink_Ident sinkid = null;
		Sink_SCREEN sinksc = null;

		if (t.isKind(IDENTIFIER)) {
			name = consume();
			sinkid = new Sink_Ident(ft, name);
			return sinkid;
		} else if (t.isKind(KW_SCREEN)) {
			consume();
			sinksc = new Sink_SCREEN(ft);
			return sinksc;
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

	}

	LHS Lhs() throws SyntaxException {
		
		Token ft = t;
		LHS lhs = null;
		Token name = null;
		Index index = null;

		if (t.isKind(IDENTIFIER)) {
			name = consume();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		if (t.isKind(LSQUARE)) {
			consume();
			index = LhsSelector();
			if (t.isKind(RSQUARE)) {
				consume();
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}
		}

		lhs = new LHS(ft, name, index);
		return lhs;

	}

	Index LhsSelector() throws SyntaxException {

		Index index = null;

		if (t.isKind(LSQUARE)) {
			consume();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		if (t.isKind(KW_x)) {
			index = XySelector();
		}
		if (t.isKind(KW_r)) {
			index = RaSelector();
		}

		if (t.isKind(RSQUARE)) {
			consume();
		} else {
			throw new SyntaxException(t,
					"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
		}

		return index;
	}

	/**
	 * Expression ::= OrExpression OP_Q Expression OP_COLON Expression |
	 * OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental
	 * development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		// TODO implement this.

		Token ft = t;
		Expression cond = null;
		Expression trueExpression = null;
		Expression falseExpression = null;
		Expression ec = null;

		ec = OrExpression();
		if (t.isKind(OP_Q)) {
			consume();
			trueExpression = expression();
			if (t.isKind(OP_COLON)) {
				consume();
				falseExpression = expression();
			} else {
				throw new SyntaxException(t,
						"Line " + t.line + ": Unexpected token " + t.getText() + " at position " + t.pos);
			}

			// throw new UnsupportedOperationException();
			// ec = new Expression_Conditional(ft,cond,trueExpression,falseExpression);
			ec = new Expression_Conditional(ft, ec, trueExpression, falseExpression);
		}
		
		return ec;
	}

	Expression OrExpression() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		// Expression_Binary e2 = null;

		e0 = AndExpression();
		while (t.isKind(OP_OR)) {
			Token op = consume();
			e1 = AndExpression();
			e0 = new Expression_Binary(ft, e0, op, e1);
		}

		return e0;
	}

	Expression AndExpression() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;

		e0 = EqExpression();
		while (t.isKind(OP_AND)) {
			Token op = consume();
			e1 = EqExpression();
			e0 = new Expression_Binary(ft, e0, op, e1);
		}
		return e0;
	}

	Expression EqExpression() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;

		e0 = RelExpression();
		while (t.isKind(OP_EQ) || t.isKind(OP_NEQ)) {
			Token op = consume();
			e1 = RelExpression();
			e0 = new Expression_Binary(ft, e0, op, e1);
		}
		return e0;
	}

	Expression RelExpression() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;

		e0 = AddExpression();
		while (t.isKind(OP_LT) || t.isKind(OP_GT) || t.isKind(OP_LE) || t.isKind(OP_GE)) {
			Token op = consume();
			e1 = AddExpression();
			e0 = new Expression_Binary(ft, e0, op, e1);
		}
		return e0;
	}

	Expression AddExpression() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;

		e0 = MultExpression();
		while (t.isKind(OP_PLUS) || t.isKind(OP_MINUS)) {
			Token op = consume();
			e1 = MultExpression();
			e0 = new Expression_Binary(ft, e0, op, e1);
		}
		return e0;
	}

	Expression MultExpression() throws SyntaxException {

		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;

		e0 = UnaryExpression();
		while (t.isKind(OP_TIMES) || t.isKind(OP_DIV) || t.isKind(OP_MOD)) {
			Token op = consume();
			e1 = UnaryExpression();
			e0 = new Expression_Binary(ft, e0, op, e1);
		}
		return e0;
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */

	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message = "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}

	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException(t, "saw " + t.kind + " expected " + kind);
	}

	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
