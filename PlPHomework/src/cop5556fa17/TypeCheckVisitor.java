package cop5556fa17;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import java.net.*;



public class TypeCheckVisitor implements ASTVisitor {
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
		SymbolTable symtab = new SymbolTable();

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		Declaration d = symtab.lookup(declaration_Variable.name);
		
		if(d == null) {
			throw new SemanticException(declaration_Variable.firstToken,"ident " + declaration_Variable.name + " not declared or is not visible in current scope");
		}
		
		if(d.Type != null) {
			throw new SemanticException(declaration_Variable.firstToken,"ident " + declaration_Variable.name + " not declared or is not visible in current scope");
		}
		
		
		boolean decflag = symtab.insert(declaration_Variable.name, declaration_Variable);
		
		if(decflag == false)
			throw new SemanticException(declaration_Variable.firstToken,"variable " + declaration_Variable.name + " already declared in current scope");
		
	
		declaration_Variable.Type = TypeUtils.getType(declaration_Variable.type);
		
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_Binary.e0.visit(this, null);
		expression_Binary.e1.visit(this, null);
		
		if((expression_Binary.e0.Type != expression_Binary.e1.Type) || expression_Binary.Type == null) {
			throw new SemanticException(expression_Binary.firstToken," Invalid Type ");
		}
		
		if(expression_Binary.op.equals(Kind.OP_EQ) || expression_Binary.op.equals(Kind.OP_NEQ)) {
			expression_Binary.Type = Type.BOOLEAN;
		}
		else if((expression_Binary.op.equals(Kind.OP_GT) || expression_Binary.op.equals(Kind.OP_LT) || expression_Binary.op.equals(Kind.OP_GE) ||
				expression_Binary.op.equals(Kind.OP_LE)) && expression_Binary.e0.Type == Type.INTEGER ) {
			expression_Binary.Type = Type.BOOLEAN;
		}
		else if((expression_Binary.op.equals(Kind.OP_AND) || expression_Binary.op.equals(Kind.OP_OR)) && (expression_Binary.e0.Type == Type.INTEGER ||
				expression_Binary.e0.Type == Type.BOOLEAN)) {
			expression_Binary.Type = expression_Binary.e0.Type;
		}
		else if((expression_Binary.op.equals(Kind.OP_DIV) || expression_Binary.op.equals(Kind.OP_PLUS) || expression_Binary.op.equals(Kind.OP_MINUS)
				|| expression_Binary.op.equals(Kind.OP_MOD) || expression_Binary.op.equals(Kind.OP_POWER)
				|| expression_Binary.op.equals(Kind.OP_TIMES)) && expression_Binary.e0.Type == Type.INTEGER) {
			expression_Binary.Type = Type.INTEGER;
		}
		else {
			expression_Binary.Type = null;
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_Unary.e.visit(this, null);
		
		if(expression_Unary.op.equals(Kind.OP_EXCL) && (expression_Unary.e.Type == Type.BOOLEAN || expression_Unary.e.Type == Type.INTEGER )) {
			expression_Unary.Type = expression_Unary.e.Type;
		}
		else if((expression_Unary.op.equals(Kind.OP_PLUS) || expression_Unary.op.equals(Kind.OP_MINUS)) &&  expression_Unary.e.Type == Type.INTEGER) {
			expression_Unary.Type = Type.INTEGER;
		}
		else {
			expression_Unary.Type = null;
		}
		
		if(expression_Unary.Type == null) {
			throw new SemanticException(expression_Unary.firstToken," Invalid Type ");
		}
		
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		index.e0.visit(this, null);
		index.e1.visit(this, null);
			
			
		if(index.e0.Type != Type.INTEGER || index.e1.Type != Type.INTEGER) {
			throw new SemanticException(index.firstToken," Invalid Type ! Not a Boolean ");
		}
		
		index.isCartesian = !(index.e0.firstToken.kind == Kind.KW_r  	&& index.e1.firstToken.kind == Kind.KW_r);
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		Declaration d = symtab.lookup(expression_PixelSelector.name);
		
		if(d == null) {
			throw new SemanticException(expression_PixelSelector.firstToken,"ident " + expression_PixelSelector.name + " not declared or is not visible in current scope");
		}
		
		//source_Ident.type = d.Type;
		
		//expression_PixelSelector.name.Type =  
		
		
		
		if(expression_PixelSelector.Type == null) {
			throw new SemanticException(expression_PixelSelector.firstToken," Invalid Type ");
		}
		
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		expression_Conditional.condition.visit(this, null);
		expression_Conditional.falseExpression.visit(this, null);
		expression_Conditional.trueExpression.visit(this, null);
		
		
		if(expression_Conditional.condition.Type != Type.BOOLEAN) {
			throw new SemanticException(expression_Conditional.firstToken," Invalid Type ! Not a Boolean ");
		}
		
		if(expression_Conditional.trueExpression.Type != expression_Conditional.falseExpression.Type ) {
			throw new SemanticException(expression_Conditional.firstToken,"Invalid Type ! Not a Boolean ");
		}
		
		expression_Conditional.Type = expression_Conditional.trueExpression.Type;
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		if(symtab.lookup(declaration_Image.name) != null) {
			throw new SemanticException(declaration_Image.firstToken," Invalid entry in lookup table");
		}
		
		boolean decflag = symtab.insert(declaration_Image.name, declaration_Image);
		
		if(decflag == false)
			throw new SemanticException(declaration_Image.firstToken,"variable " + declaration_Image.name + " already declared in current scope");
		
		
		declaration_Image.Type = Type.IMAGE;
		return null;
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		
		boolean success = true;
		try {
			URL s = new URL(source_StringLiteral.fileOrUrl);
		}
	
		catch(Exception e) {
			success = false;
		}
		
		if(success) {
			source_StringLiteral.type = Type.URL;
		}
		else {
			source_StringLiteral.type = Type.FILE;
		}
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		source_CommandLineParam.paramNum.visit(this, null);
		
		source_CommandLineParam.type = source_CommandLineParam.paramNum.Type; 
		
		if(source_CommandLineParam.type != Type.INTEGER) {
			throw new SemanticException(source_CommandLineParam.firstToken, " Invalid ParamNum type ! Not an Integer");
		}
		return null;
		//throw new UnsupportedOperationException();
	}
	
	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		
		Declaration d = symtab.lookup(source_Ident.name);
		
		if(d == null) {
			throw new SemanticException(source_Ident.firstToken,"ident " + source_Ident.name + " not declared or is not visible in current scope");
		}
		
		source_Ident.type = d.Type;
		
		if(source_Ident.type != Type.FILE || source_Ident.type != Type.URL) {
			throw new SemanticException(source_Ident.firstToken, " Invalid source_Ident type ! Not a File or Url");
		}
		

		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		if(symtab.lookup(declaration_SourceSink.name) != null) {
			throw new SemanticException(declaration_SourceSink.firstToken," Invalid entry in lookup table");
		}
		
		boolean decflag = symtab.insert(declaration_SourceSink.name, declaration_SourceSink);
		
		if(decflag == false)
			throw new SemanticException(declaration_SourceSink.firstToken,"variable " + declaration_SourceSink.name + " already declared in current scope");
		
		//throw new UnsupportedOperationException();
		
		//declaration_SourceSink.type = TypeUtils.getType(declaration_SourceSink.firstToken); 
		
		return null;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_IntLit.Type = Type.INTEGER;
		return null;
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		expression_FunctionAppWithExprArg.arg.visit(this, null);
		
		if(expression_FunctionAppWithExprArg.arg.Type != Type.INTEGER) {
			throw new SemanticException(expression_FunctionAppWithExprArg.arg.firstToken, " Invalid expression_FunctionAppWithExprArg type ! Not an Integer");
		}
		
		expression_FunctionAppWithExprArg.Type = Type.INTEGER;
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		//expression_FunctionAppWithIndexArg.arg.visit(this, null);
		
		// CHECKK ?? DOUBTTT ??
		
		expression_FunctionAppWithIndexArg.Type = Type.INTEGER;
		return null;
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		expression_PredefinedName.Type = Type.INTEGER;
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		statement_Out.sink.visit(this, null);
		
		//statement_Out.declaration = statement_Out.getDec();
		
		if(statement_Out.getDec() == null) {
			
		}
		
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		statement_Assign.lhs.visit(this, null);
		statement_Assign.e.visit(this, null);
		
		if(statement_Assign.lhs.Type != statement_Assign.e.Type) {
			throw new SemanticException(statement_Assign.firstToken, " Invalid statement_Assign type");
		}
		
		statement_Assign.setCartesian(statement_Assign.lhs.isCartesian);
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		// SHOULD I VISIT INDEX ?
		
		lhs.index.visit(this, null);
		
		Declaration d = symtab.lookup(lhs.name);
		lhs.declaration = d;
		lhs.Type = lhs.declaration.Type;
		lhs.isCartesian = lhs.index.isCartesian;
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		sink_SCREEN.Type = Type.SCREEN;
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		
		Declaration d = symtab.lookup(sink_Ident.name);
		
		if(d == null) {
			throw new SemanticException(sink_Ident.firstToken, "ident " + sink_Ident.name + " not declared or is not visible in current scope");
		}
		
		sink_Ident.Type = d.Type;
		
		if(sink_Ident.Type != Type.FILE) {
			throw new SemanticException(sink_Ident.firstToken, " Invalid sink_IDENT type ! Not a File");
		}
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		expression_BooleanLit.Type = Type.BOOLEAN;
		return null;
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Declaration d = symtab.lookup(expression_Ident.name);
		
		if(d == null) {
			throw new SemanticException(expression_Ident.firstToken, "ident " + expression_Ident.name + " not declared or is not visible in current scope");
		}
		
		expression_Ident.Type = d.Type;
		return null;
		//throw new UnsupportedOperationException();
	}

}
