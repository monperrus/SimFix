/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */
package cofix.common.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import cofix.common.config.Identifier;
import cofix.common.node.expr.ArrayAcc;
import cofix.common.node.expr.ArrayCreate;
import cofix.common.node.expr.ArrayInitial;
import cofix.common.node.expr.Assign;
import cofix.common.node.expr.BoolLiteral;
import cofix.common.node.expr.CastExpr;
import cofix.common.node.expr.CharLiteral;
import cofix.common.node.expr.ClassInstanceCreate;
import cofix.common.node.expr.Comment;
import cofix.common.node.expr.ConditionalExpr;
import cofix.common.node.expr.CreationRef;
import cofix.common.node.expr.DoubleLiteral;
import cofix.common.node.expr.Expr;
import cofix.common.node.expr.ExpressionMethodRef;
import cofix.common.node.expr.FieldAcc;
import cofix.common.node.expr.FloatLiteral;
import cofix.common.node.expr.InfixExpr;
import cofix.common.node.expr.InstanceofExpr;
import cofix.common.node.expr.IntLiteral;
import cofix.common.node.expr.Label;
import cofix.common.node.expr.LambdaExpr;
import cofix.common.node.expr.LongLiteral;
import cofix.common.node.expr.MethodInv;
import cofix.common.node.expr.MethodRef;
import cofix.common.node.expr.NillLiteral;
import cofix.common.node.expr.NumLiteral;
import cofix.common.node.expr.ParenthesiszedExpr;
import cofix.common.node.expr.PostfixExpr;
import cofix.common.node.expr.PrefixExpr;
import cofix.common.node.expr.QName;
import cofix.common.node.expr.SName;
import cofix.common.node.expr.StrLiteral;
import cofix.common.node.expr.SuperFieldAcc;
import cofix.common.node.expr.SuperMethodInv;
import cofix.common.node.expr.SuperMethodRef;
import cofix.common.node.expr.ThisExpr;
import cofix.common.node.expr.TyLiteral;
import cofix.common.node.expr.TypeMethodRef;
import cofix.common.node.expr.VarDeclarationExpr;
import cofix.common.node.expr.Vdf;
import cofix.common.node.metric.Literal;
import cofix.common.node.metric.MethodCall;
import cofix.common.node.metric.Operator;
import cofix.common.node.metric.Structure;
import cofix.common.node.metric.Variable;
import cofix.common.node.stmt.AnonymousClassDecl;
import cofix.common.node.stmt.AssertStmt;
import cofix.common.node.stmt.Blk;
import cofix.common.node.stmt.BreakStmt;
import cofix.common.node.stmt.ConstructorInv;
import cofix.common.node.stmt.ContinueStmt;
import cofix.common.node.stmt.DoStmt;
import cofix.common.node.stmt.EmptyStmt;
import cofix.common.node.stmt.EnhancedForStmt;
import cofix.common.node.stmt.ExpressionStmt;
import cofix.common.node.stmt.ForStmt;
import cofix.common.node.stmt.IfStmt;
import cofix.common.node.stmt.LabeledStmt;
import cofix.common.node.stmt.ReturnStmt;
import cofix.common.node.stmt.Stmt;
import cofix.common.node.stmt.SuperConstructorInv;
import cofix.common.node.stmt.SwCase;
import cofix.common.node.stmt.SwitchStmt;
import cofix.common.node.stmt.SynchronizedStmt;
import cofix.common.node.stmt.ThrowStmt;
import cofix.common.node.stmt.TryStmt;
import cofix.common.node.stmt.TypeDeclarationStmt;
import cofix.common.node.stmt.VarDeclarationStmt;
import cofix.common.node.stmt.WhileStmt;
import cofix.common.parser.NodeUtils;
import cofix.common.parser.ProjectInfo;
import cofix.common.util.LevelLogger;
import cofix.common.util.Pair;

/**
 * @author Jiajun
 * @datae Jun 24, 2017
 */
public class CodeBlock {

	private CompilationUnit _cunit = null;
	private List<ASTNode> _nodes = null;
	private List<Node> _parsedNodes = null;
	private int _maxLines = 10;
	private int _currlines = 0;
	private Integer _buggyMethod = null;
	
	// <name, <type, count>>
	private Map<Variable, Integer> _variables = null;
	// <literal, count>
	private Map<Literal, Integer> _constants = null;
	// <if, for, ...>
	private List<Structure> _structures = null;
	// {type.name(p0,p1),...}
	private Map<MethodCall, Integer> _methodCalls = null;
	// <+, -, ...>
	private List<Operator> _operators = null;
	
	public CodeBlock(CompilationUnit cunit, List<ASTNode> nodes) {
		this(cunit, nodes, 10);
	}
	
	public CodeBlock(CompilationUnit cunit, List<ASTNode> nodes, int maxLines) {
		_cunit = cunit;
		_nodes = nodes;
		_maxLines = maxLines;
		_currlines = 0;
		for(ASTNode s : nodes){
			_currlines += NodeUtils.getValidLineNumber(s);
		}
	}
	
	public List<ASTNode> getNodes(){
		return _nodes;
	}
	
	public int getCurrentLine(){
		return _currlines;
	}
	
	public int getMaxLines(){
		return _maxLines;
	}
	
	public Pair<Integer, Integer> getLineRangeInSource(){
		int start = 0;
		int end = 0;
		if(_nodes != null && _nodes.size() > 0){
			ASTNode snode = _nodes.get(0);
			start = _cunit.getLineNumber(snode.getStartPosition());
			ASTNode enode = _nodes.get(_nodes.size() - 1);
			end = _cunit.getLineNumber(enode.getStartPosition());
		}
		return new Pair<Integer, Integer>(start, end);
	}
	
	public Integer getWrapMethodID(){
		if(_buggyMethod == null){
			if(_nodes != null && _nodes.size() > 0){
				ASTNode node = _nodes.get(0);
				while(node != null){
					if(node instanceof MethodDeclaration){
						if(node.getParent() instanceof AnonymousClassDeclaration){
							continue;
						} else {
							String methodString = NodeUtils.buildMethodInfoString((MethodDeclaration) node);
							_buggyMethod = Identifier.getIdentifier(methodString);
							break;
						}
					}
					node = node.getParent();
				}
			}
		}
		return _buggyMethod;
	}
	
	public void accept(ASTVisitor visitor){
		for(ASTNode node : _nodes){
			node.accept(visitor);
		}
	}
	
	public Map<Variable, Integer> getVariables(){
		if(_variables == null){
			if(_parsedNodes == null){
				parseNode();
			}
			_variables = new HashMap<>();
			for(Node node : _parsedNodes){
				List<Variable> vars = node.getVariables();
				for(Variable variable : vars){
					Integer count = _variables.get(variable);
					if(count == null){
						count = 0;
					}
					_variables.put(variable, count + 1);
				}
			}
		}
		return _variables;
	}
	
	public Map<Literal, Integer> getConstants(){
		if(_constants == null){
			if(_parsedNodes == null){
				parseNode();
			}
			_constants = new HashMap<>();
			for(Node node : _parsedNodes){
				List<Literal> literals = node.getLiterals();
				for(Literal literal : literals){
					Integer count = _constants.get(literal);
					if(count == null){
						count = 0;
					}
					_constants.put(literal, count + 1);
				}
			}
		}
		return _constants;
	}
	
	public List<Structure> getStructures(){
		if(_structures == null){
			if(_parsedNodes == null){
				parseNode();
			}
			_structures = new ArrayList<>();
			for(Node node : _parsedNodes){
				_structures.addAll(node.getStructures());
			}
		}
		return _structures;
	}
	
	public Map<MethodCall, Integer> getMethodCalls(){
		if(_methodCalls == null){
			if(_parsedNodes == null){
				parseNode();
			}
			_methodCalls = new HashMap<>();
			for(Node node : _parsedNodes){
				List<MethodCall> methods = node.getMethodCalls();
				for(MethodCall method : methods){
					Integer count = _methodCalls.get(method);
					if(count == null){
						count = 0;
					}
					_methodCalls.put(method, count + 1);
				}
			}
		}
		return _methodCalls;
	}
	
	public List<Operator> getOperators(){
		if(_operators == null){
			if(_parsedNodes == null){
				parseNode();
			}
			_operators = new ArrayList<>();
			for(Node node : _parsedNodes){
				_operators.addAll(node.getOperators());
			}
		}
		return _operators;
	}
	
	private void parseNode(){
		for(ASTNode node : _nodes){
			_parsedNodes.add(process(node));
		}
	}
	
	
	/************************** visit start : Statement ***********************/
	private AssertStmt visit(AssertStatement node) {
		int start = _cunit.getLineNumber(node.getStartPosition());
		int end = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		AssertStmt assertStmt = new AssertStmt(start, end, node);
		return assertStmt;
	}
	
	private BreakStmt visit(BreakStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		BreakStmt breakStmt = new BreakStmt(startLine, endLine, node);
		return breakStmt;
	}
	
	private Blk visit(Block node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine  = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		Blk blk = new Blk(startLine, endLine, node);
		List<Stmt> stmts = new ArrayList<>();
		for(Object object : node.statements()){
			Stmt stmt = (Stmt) process((ASTNode) object);
			stmt.setParent(blk);
			stmts.add(stmt);
		}
		blk.setStatement(stmts);
		return blk;
	}
	
	private ConstructorInv visit(ConstructorInvocation node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ConstructorInv constructorInv = new ConstructorInv(startLine, endLine, node);
		List<Expr> arguments = new ArrayList<>();
		for(Object object : node.arguments()){
			Expr expr = (Expr) process((ASTNode) object);
			expr.setParent(constructorInv);
			arguments.add(expr);
		}
		constructorInv.setArguments(arguments);
		return constructorInv;
	}
	
	private ContinueStmt visit(ContinueStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ContinueStmt continueStmt = new ContinueStmt(startLine, endLine, node);
		if(node.getLabel() != null){
			continueStmt.setIdentifier(node.getLabel().getFullyQualifiedName());
		}
		return continueStmt;
	}
	
	private DoStmt visit(DoStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		DoStmt doStmt = new DoStmt(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(doStmt);
		doStmt.setExpression(expression);
		
		Stmt stmt = (Stmt) process(node.getBody());
		stmt.setParent(doStmt);
		doStmt.setBody(stmt);
		
		return doStmt;
	}
	
	private EmptyStmt visit(EmptyStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		EmptyStmt emptyStmt = new EmptyStmt(startLine, endLine, node);
		return emptyStmt;
	}
	
	private EnhancedForStmt visit(EnhancedForStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		EnhancedForStmt enhancedForStmt = new EnhancedForStmt(startLine, endLine, node);
		
		enhancedForStmt.setParameter(node.getParameter());
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(enhancedForStmt);
		enhancedForStmt.setExpression(expression);
		
		Stmt body = (Stmt) process(node.getBody());
		body.setParent(enhancedForStmt);
		enhancedForStmt.setBody(body);
		
		return enhancedForStmt;
	}
	
	private ExpressionStmt visit(ExpressionStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ExpressionStmt expressionStmt = new ExpressionStmt(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(expressionStmt);
		expressionStmt.setExpression(expression);
		
		return expressionStmt;
	}
	
	private ForStmt visit(ForStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ForStmt forStmt = new ForStmt(startLine, endLine, node);
		
		Expr condition = (Expr)process(node.getExpression());
		condition.setParent(forStmt);
		forStmt.setCondition(condition);
		
		List<Expr> initializers = new ArrayList<>();
		for(Object object : node.initializers()){
			Expr initializer = (Expr) process((ASTNode) object);
			initializer.setParent(forStmt);
			initializers.add(initializer);
		}
		forStmt.setInitializer(initializers);
		
		List<Expr> updaters = new ArrayList<>();
		for(Object object : node.updaters()){
			Expr update = (Expr)process((ASTNode) object);
			update.setParent(forStmt);
			updaters.add(update);
		}
		forStmt.setUpdaters(updaters);
		
		Stmt body = (Stmt) process(node.getBody());
		body.setParent(forStmt);
		forStmt.setBody(body);
		
		return forStmt;
	}
	
	private IfStmt visit(IfStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		IfStmt ifStmt = new IfStmt(startLine, endLine, node);
		
		Expr condition = (Expr)process(node.getExpression());
		condition.setParent(ifStmt);
		ifStmt.setCondition(condition);
		
		Stmt then = (Stmt)process(node.getThenStatement());
		then.setParent(ifStmt);
		ifStmt.setThen(then);
		
		if(node.getElseStatement() != null){
			Stmt els = (Stmt) process(node.getElseStatement());
			els.setParent(ifStmt);
			ifStmt.setElse(els);
		}
		
		return ifStmt;
	}
	
	private LabeledStmt visit(LabeledStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		LabeledStmt labeledStmt = new LabeledStmt(startLine, endLine, node);
		return labeledStmt;
	}
	
	private ReturnStmt visit(ReturnStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ReturnStmt returnStmt = new ReturnStmt(startLine, endLine, node);
		
		if(node.getExpression() != null){
			Expr expression = (Expr) process(node.getExpression());
			expression.setParent(returnStmt);
			returnStmt.setExpression(expression);
		}
		
		return returnStmt;
	}
	
	private SuperConstructorInv visit(SuperConstructorInvocation node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SuperConstructorInv superConstructorInv = new SuperConstructorInv(startLine, endLine, node);

		if(node.getExpression() != null){
			Expr expression = (Expr) process(node.getExpression());
			expression.setParent(superConstructorInv);
			superConstructorInv.setExpression(expression);
		}
		
		List<Expr> arguments = new ArrayList<>();
		for(Object object : node.arguments()){
			Expr arg = (Expr) process((ASTNode) object);
			arg.setParent(superConstructorInv);
			
		}
		superConstructorInv.setArguments(arguments);
		
		return superConstructorInv;
	}
	
	private SwCase visit(SwitchCase node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SwCase swCase = new SwCase(startLine, endLine, node);
		
		if(node.getExpression() != null){
			Expr expression = (Expr) process(node.getExpression());
			expression.setParent(swCase);
			swCase.setExpression(expression);
		}
		
		return swCase;
	}
	
	private SwitchStmt visit(SwitchStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SwitchStmt switchStmt = new SwitchStmt(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(switchStmt);
		switchStmt.setExpression(expression);
		
		SwCase lastSW = null;
		List<Stmt> statements = new ArrayList<>();
		for(Object object : node.statements()){
			Stmt stmt = (Stmt) process((ASTNode) object);
			stmt.setParent(switchStmt);
			if (stmt instanceof SwCase) {
				lastSW = (SwCase) stmt;
			} else if(lastSW != null){
				lastSW.addChild(stmt);
			}
			statements.add(stmt);
		}
		switchStmt.setStatements(statements);
		
		return switchStmt;
	}
	
	private SynchronizedStmt visit(SynchronizedStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SynchronizedStmt synchronizedStmt = new SynchronizedStmt(startLine, endLine, node);
		
		if(node.getExpression() != null){
			Expr expression = (Expr) process(node.getExpression());
			expression.setParent(synchronizedStmt);
			synchronizedStmt.setExpression(expression);
		}
		
		Blk blk = (Blk) process(node.getBody());
		blk.setParent(synchronizedStmt);
		synchronizedStmt.setBlock(blk);
		
		return synchronizedStmt;
	}
	
	private ThrowStmt visit(ThrowStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ThrowStmt throwStmt = new ThrowStmt(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(throwStmt);
		throwStmt.setExpression(expression);
		
		return throwStmt;
	}
	
	private TryStmt visit(TryStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		TryStmt tryStmt = new TryStmt(startLine, endLine, node);
		
		Blk blk = (Blk) process(node.getBody());
		blk.setParent(tryStmt);
		tryStmt.setBody(blk);
		
		return tryStmt;
	}
	
	private TypeDeclarationStmt visit(TypeDeclarationStatement node){
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		TypeDeclarationStmt typeDeclarationStmt = new TypeDeclarationStmt(startLine, endLine, node);
		return typeDeclarationStmt;
	}
	
	private VarDeclarationStmt visit(VariableDeclarationStatement node){
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		VarDeclarationStmt varDeclarationStmt = new VarDeclarationStmt(startLine, endLine, node);
		
		varDeclarationStmt.setDeclType(node.getType());
		
		List<Vdf> fragments = new ArrayList<>();
		for(Object object : node.fragments()){
			Vdf vdf = (Vdf) process((ASTNode) object);
			vdf.setParent(varDeclarationStmt);
			fragments.add(vdf);
		}
		varDeclarationStmt.setFragments(fragments);

		return varDeclarationStmt;
	}
	
	private WhileStmt visit(WhileStatement node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		WhileStmt whileStmt = new WhileStmt(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(whileStmt);
		whileStmt.setExpression(expression);
		
		Stmt body = (Stmt) process(node.getBody());
		body.setParent(whileStmt);
		whileStmt.setBody(body);
		
		return whileStmt;
	}
	/*********************** Visit Expression *********************************/
	private Comment visit(Annotation node){
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		Comment comment = new Comment(startLine, endLine, node);
		return comment;
	}
	
	private ArrayAcc visit(ArrayAccess node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ArrayAcc arrayAcc = new ArrayAcc(startLine, endLine, node);
		
		Expr array = (Expr) process(node.getArray());
		array.setParent(array);
		arrayAcc.setArray(array);
		
		Expr indexExpr = (Expr) process(node.getIndex());
		indexExpr.setParent(arrayAcc);
		arrayAcc.setIndex(indexExpr);
		
		ASTNode parent = node.getParent();
		Pair<String, String> classAndMethodName = NodeUtils.getTypeDecAndMethodDec(node);
		String nodeStr = node.toString();
		int index = nodeStr.indexOf("[");
		if(index >= 0){
			nodeStr = nodeStr.substring(0, index);
		}
		Type type = ProjectInfo.getVariableType(classAndMethodName.getFirst(), classAndMethodName.getSecond(), nodeStr);
		if(type != null){
			if(type instanceof ArrayType){
				ArrayType arrayType = (ArrayType) type;
				type = arrayType.getElementType();
			} else {
				System.out.println("ArrayAccess type error : not array type ! " + node.toString());
			}
		}
		arrayAcc.setType(type);
		
		return arrayAcc;
	}
	
	private ArrayCreate visit(ArrayCreation node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ArrayCreate arrayCreate = new ArrayCreate(startLine, endLine, node);
		
		arrayCreate.setArrayType(node.getType());
		
		List<Expr> dimension = new ArrayList<>();
		for(Object object : node.dimensions()){
			Expr dim = (Expr) process((ASTNode) object);
			dim.setParent(arrayCreate);
			dimension.add(dim);
		}
		arrayCreate.setDimension(dimension);
		
		if(node.getInitializer() != null){
			ArrayInitial arrayInitializer = (ArrayInitial) process(node.getInitializer());
			arrayInitializer.setParent(arrayCreate);
			arrayCreate.setInitializer(arrayInitializer);
		}
		
		return arrayCreate;
	}
	
	private ArrayInitial visit(ArrayInitializer node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ArrayInitial arrayInitial = new ArrayInitial(startLine, endLine, node);
		
		List<Expr> expressions = new ArrayList<>();
		for(Object object : node.expressions()){
			Expr expr = (Expr) process((ASTNode) object);
			expr.setParent(arrayInitial);
			expressions.add(expr);
		}
		arrayInitial.setExpressions(expressions);

		return arrayInitial;
	}
	
	private Assign visit(Assignment node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		Assign assign = new Assign(startLine, endLine, node);
		
		Expr lhs = (Expr) process(node.getLeftHandSide());
		lhs.setParent(assign);
		assign.setLeftHandSide(lhs);
		
		Expr rhs = (Expr) process(node.getRightHandSide());
		rhs.setParent(assign);
		assign.setRightHandSide(rhs);
		
		assign.setOperator(node.getOperator());
		
		return assign;
	}
	
	private BoolLiteral visit(BooleanLiteral node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		BoolLiteral literal = new BoolLiteral(startLine, endLine, node);
		literal.setValue(node.booleanValue());
		AST ast = AST.newAST(AST.JLS8);
		Type type = ast.newPrimitiveType(PrimitiveType.BOOLEAN);
		literal.setType(type);
		
		return literal;
	}
	
	private CastExpr visit(CastExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		CastExpr castExpr = new CastExpr(startLine, endLine, node);
		
		castExpr.setCastType(node.getType());
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(castExpr);
		castExpr.setExpression(expression);
		castExpr.setType(node.getType());

		return castExpr;
	}
	
	private CharLiteral visit(CharacterLiteral node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		CharLiteral charLiteral = new CharLiteral(startLine, endLine, node);
		
		charLiteral.setValue(node.charValue());
		
		AST ast = AST.newAST(AST.JLS8);
		Type type = ast.newPrimitiveType(PrimitiveType.CHAR);
		charLiteral.setType(type);
		
		return charLiteral;
	}
	
	private ClassInstanceCreate visit(ClassInstanceCreation node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ClassInstanceCreate classInstanceCreate = new ClassInstanceCreate(startLine, endLine, node);
		
		if(node.getExpression() != null){
			Expr expression = (Expr) process(node.getExpression());
			expression.setParent(classInstanceCreate);
			classInstanceCreate.setExpression(expression);
		}
		
		if(node.getAnonymousClassDeclaration() != null){
			AnonymousClassDecl anonymousClassDecl = (AnonymousClassDecl) process(node.getAnonymousClassDeclaration());
			anonymousClassDecl.setParent(classInstanceCreate);
			classInstanceCreate.setAnonymousClassDecl(anonymousClassDecl);
		}

		List<Expr> arguments = new ArrayList<>();
		for(Object object : node.arguments()){
			Expr arg = (Expr) process((ASTNode) object);
			arg.setParent(classInstanceCreate);
			arguments.add(arg);
		}
		classInstanceCreate.setArguments(arguments);
		
		classInstanceCreate.setClassType(node.getType());
		classInstanceCreate.setType(node.getType());
		
		return classInstanceCreate;
	}
	
	private AnonymousClassDecl visit(AnonymousClassDeclaration node){
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		AnonymousClassDecl anonymousClassDecl = new AnonymousClassDecl(startLine, endLine, node);
		return anonymousClassDecl;
	}
	
	private ConditionalExpr visit(ConditionalExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ConditionalExpr conditionalExpr = new ConditionalExpr(startLine, endLine, node);
		
		Expr condition = (Expr) process(node.getExpression());
		condition.setParent(conditionalExpr);
		conditionalExpr.setCondition(condition);
		
		Expr first = (Expr) process(node.getThenExpression());
		first.setParent(conditionalExpr);
		conditionalExpr.setFirst(first);
		
		Expr snd = (Expr) process(node.getElseExpression());
		snd.setParent(conditionalExpr);
		conditionalExpr.setSecond(snd);
		
		if(first.getType() != null){
			conditionalExpr.setType(first.getType());
		} else {
			conditionalExpr.setType(snd.getType());
		}
		
		return conditionalExpr;
	}
	
	private CreationRef visit(CreationReference node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		CreationRef creationRef = new CreationRef(startLine, endLine, node);
		return creationRef;
	}
	
	private ExpressionMethodRef visit(ExpressionMethodReference node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ExpressionMethodRef expressionMethodRef = new ExpressionMethodRef(startLine, endLine, node);
		return expressionMethodRef;
	}
	
	private FieldAcc visit(FieldAccess node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		FieldAcc fieldAcc = new FieldAcc(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(fieldAcc);
		fieldAcc.setExpression(expression);
		
		fieldAcc.setIdentifier(node.getName().getFullyQualifiedName());
		
		Pair<String, String> classAndMethodName = NodeUtils.getTypeDecAndMethodDec(node);
		Type type = ProjectInfo.getVariableType(classAndMethodName.getFirst(), classAndMethodName.getSecond(), node.getName().toString());
		fieldAcc.setType(type);
		
		return fieldAcc;
	}
	
	private InfixExpr visit(InfixExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		InfixExpr infixExpr = new InfixExpr(startLine, endLine, node);
		
		Expr lhs = (Expr) process(node.getLeftOperand());
		lhs.setParent(infixExpr);
		infixExpr.setLeftHandSide(lhs);
		
		Expr rhs = (Expr) process(node.getRightOperand());
		rhs.setParent(infixExpr);
		infixExpr.setRightHandSide(rhs);
		
		infixExpr.setOperator(node.getOperator());
		
		infixExpr.setType(NodeUtils.parseExprType(lhs, node.getOperator().toString(), rhs));
		
		return infixExpr;
	}
	
	private InstanceofExpr visit(InstanceofExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		InstanceofExpr instanceofExpr = new InstanceofExpr(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getLeftOperand());
		expression.setParent(expression);
		instanceofExpr.setExpression(expression);
		
		instanceofExpr.setInstanceType(node.getRightOperand());
		
		AST ast = AST.newAST(AST.JLS8);
		Type exprType = ast.newPrimitiveType(PrimitiveType.BOOLEAN);
		instanceofExpr.setType(exprType);
		
		return instanceofExpr;
	}
	
	private LambdaExpr visit(LambdaExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		LambdaExpr lambdaExpr = new LambdaExpr(startLine, endLine, node);
		return lambdaExpr;
	}
	
	private MethodInv visit(MethodInvocation node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		MethodInv methodInv = new MethodInv(startLine, endLine, node);
		
		Expr expression = null;
		if(node.getExpression() != null){
			expression = (Expr) process(node.getExpression());
			expression.setParent(methodInv);
			methodInv.setExpression(expression);
		}
		
		methodInv.setName(node.getName().getFullyQualifiedName());
		
		List<Expr> arguments = new ArrayList<>();
		for(Object object : node.arguments()){
			Expr expr = (Expr) process((ASTNode) object);
			expr.setParent(methodInv);
			arguments.add(expr);
		}
		methodInv.setArguments(arguments);
		
		String className = null;
		String methodName = node.getName().getFullyQualifiedName();
		if(expression != null){
			if(expression.getType() != null){
				className = expression.getType().toString();
			} else {
				LevelLogger.error("parse type error for method invocation !");
			}
		} else {
			Pair<String, String> classAndMethodName = NodeUtils.getTypeDecAndMethodDec(node);
			className = classAndMethodName.getFirst();
		}
		Type type = ProjectInfo.getMethodRetType(className, methodName);
		
		methodInv.setType(type);

		return methodInv;
	}

	private MethodRef visit(MethodReference node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		MethodRef methodRef = new MethodRef(startLine, endLine, node);
		return methodRef;
	}

	private Label visit(Name node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		Label expr = null;
		if(node instanceof SimpleName){
			SName sName = new SName(startLine, endLine, node);

			String name = node.getFullyQualifiedName();
			sName.setName(name);
			Pair<String, String> classAndMethodName = NodeUtils.getTypeDecAndMethodDec(node);
			Type type = ProjectInfo.getVariableType(classAndMethodName.getFirst(), classAndMethodName.getSecond(), node.toString());
			
			if(type == null){
				Pattern pattern = Pattern.compile("[A-Z][a-zA-Z_0-9]*");
				Matcher matcher = pattern.matcher(name);
				if(matcher.matches()){
					AST ast = AST.newAST(AST.JLS8);
					type = ast.newSimpleType(ast.newSimpleName(name));
				}
			}
			
			sName.setType(type);
			expr = sName;
		} else if(node instanceof QualifiedName){
			QualifiedName qualifiedName = (QualifiedName) node;
			QName qName = new QName(startLine, endLine, node);
			SName sname = (SName) process(qualifiedName.getName());
			sname.setParent(qName);
			Label label = (Label) process(qualifiedName.getQualifier());
			label.setParent(qName);
			qName.setName(label, sname);
			qName.setType(sname.getType());
			
			expr = qName;
		}
		return expr;
	}

	private NillLiteral visit(NullLiteral node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		NillLiteral nillLiteral = new NillLiteral(startLine, endLine, node);
		return nillLiteral;
	}

	private NumLiteral visit(NumberLiteral node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		String token = node.getToken();
		NumLiteral expr = null;
		try{
			Integer value = Integer.parseInt(token);
			IntLiteral literal = new IntLiteral(startLine, endLine, node);
			literal.setValue(value);
			AST ast = AST.newAST(AST.JLS8);
			Type type = ast.newPrimitiveType(PrimitiveType.INT);
			literal.setType(type);
			expr = literal;
		} catch (Exception e){}
		
		if(expr == null){
			try{
				long value = Long.parseLong(token);
				LongLiteral literal = new LongLiteral(startLine, endLine, node);
				literal.setValue(value);
				AST ast = AST.newAST(AST.JLS8);
				Type type = ast.newPrimitiveType(PrimitiveType.LONG);
				literal.setType(type);
				expr = literal;
			} catch (Exception e){}
		}
		
		if(expr == null){
			try{
				float value = Float.parseFloat(token);
				FloatLiteral literal = new FloatLiteral(startLine, endLine, node);
				literal.setValue(value);
				AST ast = AST.newAST(AST.JLS8);
				Type type = ast.newPrimitiveType(PrimitiveType.FLOAT);
				literal.setType(type);
				expr = literal;
			} catch (Exception e){}
		}
		
		if(expr == null){
			try{
				double value = Double.parseDouble(token);
				DoubleLiteral literal = new DoubleLiteral(startLine, endLine, node);
				literal.setValue(value);
				AST ast = AST.newAST(AST.JLS8);
				Type type = ast.newPrimitiveType(PrimitiveType.DOUBLE);
				literal.setType(type);
				expr = literal;
			} catch (Exception e){}
		}
		
		if(expr == null){
			// should be hexadecimal number or octal number 
			token = token.replace("X", "x");
			token = token.replace("F", "f");
			NumLiteral literal = new NumLiteral(startLine, endLine, node);
			literal.setValue(token);
			// simply set as int type
			AST ast = AST.newAST(AST.JLS8);
			Type type = ast.newPrimitiveType(PrimitiveType.INT);
			literal.setType(type);
			expr = literal;
		}
		
		return expr;
	}

	private ParenthesiszedExpr visit(ParenthesizedExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		
		ParenthesiszedExpr parenthesiszedExpr = new ParenthesiszedExpr(startLine, endLine, node);
		Expr expression = (Expr) process(node.getExpression());
		expression.setParent(parenthesiszedExpr);
		parenthesiszedExpr.setExpr(expression);
		parenthesiszedExpr.setType(expression.getType());
		
		return parenthesiszedExpr;
	}

	private PostfixExpr visit(PostfixExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		PostfixExpr postfixExpr = new PostfixExpr(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getOperand());
		expression.setParent(postfixExpr);
		postfixExpr.setExpression(expression);
		
		postfixExpr.setOperator(node.getOperator());
		
		Type exprType = NodeUtils.parseExprType(expression, node.getOperator().toString(), null);
		postfixExpr.setType(exprType);
		
		return postfixExpr;
	}

	private PrefixExpr visit(PrefixExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		PrefixExpr prefixExpr = new PrefixExpr(startLine, endLine, node);
		
		Expr expression = (Expr) process(node.getOperand());
		expression.setParent(prefixExpr);
		prefixExpr.setExpression(expression);
		
		prefixExpr.setOperator(node.getOperator());
		
		Type type = NodeUtils.parseExprType(null, node.getOperator().toString(), expression);
		prefixExpr.setType(type);
		
		return prefixExpr;
	}

	private StrLiteral visit(StringLiteral node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		StrLiteral literal = new StrLiteral(startLine, endLine, node);

		literal.setValue(node.getLiteralValue());
		
		AST ast = AST.newAST(AST.JLS8);
		Type type = ast.newSimpleType(ast.newSimpleName("String"));
		literal.setType(type);
		
		return literal;
	}

	private SuperFieldAcc visit(SuperFieldAccess node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SuperFieldAcc superFieldAcc = new SuperFieldAcc(startLine, endLine, node);
		
		superFieldAcc.setIdentifier(node.getName().getFullyQualifiedName());
		
		if(node.getQualifier() != null){
			Label name = (Label) process(node.getQualifier());
			name.setParent(superFieldAcc);
			superFieldAcc.setName(name);
		}
		
		Pair<String, String> pair = NodeUtils.getTypeDecAndMethodDec(node);
		Type exprType = ProjectInfo.getVariableType(pair.getFirst(), pair.getSecond(), node.getName().getFullyQualifiedName());
		superFieldAcc.setType(exprType);
		
		return superFieldAcc;
	}

	private SuperMethodInv visit(SuperMethodInvocation node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SuperMethodInv superMethodInv = new SuperMethodInv(startLine, endLine, node);
		
		superMethodInv.setName(node.getName().getFullyQualifiedName());
		
		if(node.getQualifier() != null){
			Label label = (Label) process(node.getQualifier());
			label.setParent(superMethodInv);
			superMethodInv.setLabel(label);
		}
		
		List<Expr> arguments = new ArrayList<>();
		for(Object object : node.arguments()){
			Expr expr = (Expr) process((ASTNode) object);
			expr.setParent(superMethodInv);
			arguments.add(expr);
		}
		superMethodInv.setArguments(arguments);
		
		Pair<String, String> pair = NodeUtils.getTypeDecAndMethodDec(node);
		Type type = ProjectInfo.getMethodRetType(pair.getFirst(), pair.getSecond());
		superMethodInv.setType(type);
		
		return null;
	}

	private SuperMethodRef visit(SuperMethodReference node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		SuperMethodRef superMethodRef = new SuperMethodRef(startLine, endLine, node);
		
		return superMethodRef;
	}
	
	private ThisExpr visit(ThisExpression node){
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		ThisExpr thisExpr = new ThisExpr(startLine, endLine, node);
		
		Pair<String, String> classAndMethodName = NodeUtils.getTypeDecAndMethodDec(node);
		Type type = ProjectInfo.getVariableType(classAndMethodName.getFirst(), classAndMethodName.getSecond(), "THIS");
		thisExpr.setType(type);
		
		return thisExpr;
	}

	private TyLiteral visit(TypeLiteral node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		TyLiteral tyLiteral = new TyLiteral(startLine, endLine, node);
		
		tyLiteral.setType(node.getType());
		
		return tyLiteral;
	}

	private TypeMethodRef visit(TypeMethodReference node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		TypeMethodRef typeMethodRef = new TypeMethodRef(startLine, endLine, node);
		return typeMethodRef;
	}

	private VarDeclarationExpr visit(VariableDeclarationExpression node) {
		int startLine = _cunit.getLineNumber(node.getStartPosition());
		int endLine = _cunit.getLineNumber(node.getStartPosition() + node.getLength());
		VarDeclarationExpr varDeclarationExpr = new VarDeclarationExpr(startLine, endLine, node);
		
		varDeclarationExpr.setDeclType(node.getType());
		
		List<Vdf> vdfs = new ArrayList<>();
		for(Object object : node.fragments()){
			Vdf vdf = (Vdf) process((ASTNode) object);
			vdf.setParent(varDeclarationExpr);
			vdfs.add(vdf);
		}
		varDeclarationExpr.setVarDeclFrags(vdfs);
		
		return varDeclarationExpr;
	}
	
	private Node process(ASTNode node){
		if(node == null){
			return null;
		}
		 if(node instanceof AssertStatement){
			 return visit((AssertStatement)node);
		 } else if(node instanceof Block){
			 return visit((Block)node);
		 } else if(node instanceof BreakStatement){
			 return visit((BreakStatement)node);
		 } else if(node instanceof ConstructorInvocation){
			 return visit((ConstructorInvocation)node);
		 } else if(node instanceof ContinueStatement){
			 return visit((ContinueStatement)node);
		 } else if(node instanceof DoStatement){
			 return visit((DoStatement)node);
		 } else if(node instanceof EmptyStatement){
			 return visit((EmptyStatement)node);
		 } else if(node instanceof EnhancedForStatement){
			 return visit((EnhancedForStatement)node);
		 } else if(node instanceof ExpressionStatement){
			 return visit((ExpressionStatement)node);
		 } else if(node instanceof ForStatement){
			 return visit((ForStatement)node);
		 } else if(node instanceof IfStatement){
			 return visit((IfStatement)node);
		 } else if(node instanceof LabeledStatement){
			 return visit((LabeledStatement)node);
		 } else if(node instanceof ReturnStatement){
			 return visit((ReturnStatement)node);
		 } else if(node instanceof SuperConstructorInvocation){
			 return visit((SuperConstructorInvocation)node);
		 } else if(node instanceof SwitchCase){
			 return visit((SwitchCase)node);
		 } else if(node instanceof SwitchStatement){
			 return visit((SwitchStatement)node);
		 } else if(node instanceof SynchronizedStatement){
			 return visit((SynchronizedStatement)node);
		 } else if(node instanceof ThrowStatement){
			 return visit((ThrowStatement)node);
		 } else if(node instanceof TryStatement){
			 return visit((TryStatement)node);
		 } else if(node instanceof TypeDeclarationStatement){
			 return visit((TypeDeclarationStatement)node);
		 } else if(node instanceof VariableDeclarationStatement){
			 return visit((VariableDeclarationStatement)node);
		 } else if(node instanceof WhileStatement){
			 return visit((WhileStatement)node);
		 } else if(node instanceof Annotation){
			 return visit((Annotation)node);
		 } else if(node instanceof ArrayAccess){
			 return visit((ArrayAccess)node);
		 } else if(node instanceof ArrayCreation){
			 return visit((ArrayCreation)node);
		 } else if(node instanceof ArrayInitializer){
			 return visit((ArrayInitializer)node);
		 } else if(node instanceof Assignment){
			 return visit((Assignment)node);
		 } else if(node instanceof BooleanLiteral){
			 return visit((BooleanLiteral)node);
		 } else if(node instanceof CastExpression){
			 return visit((CastExpression)node);
		 } else if(node instanceof CharacterLiteral){
			 return visit((CharacterLiteral)node);
		 } else if(node instanceof ClassInstanceCreation){
			 return visit((ClassInstanceCreation)node);
		 } else if(node instanceof ConditionalExpression){
			 return visit((ConditionalExpression)node);
		 } else if(node instanceof CreationReference){
			 return visit((CreationReference)node);
		 } else if(node instanceof ExpressionMethodReference){
			 return visit((ExpressionMethodReference)node);
		 } else if(node instanceof FieldAccess){
			 return visit((FieldAccess)node);
		 } else if(node instanceof InfixExpression){
			 return visit((InfixExpression)node);
		 } else if(node instanceof InstanceofExpression){
			 return visit((InstanceofExpression)node);
		 } else if(node instanceof LambdaExpression){
			 return visit((LambdaExpression)node);
		 } else if(node instanceof MethodInvocation){
			 return visit((MethodInvocation)node);
		 } else if(node instanceof MethodReference){
			 return visit((MethodReference)node);
		 } else if(node instanceof Name){
			 return visit((Name)node);
		 } else if(node instanceof NullLiteral){
			 return visit((NullLiteral)node);
		 } else if(node instanceof NumberLiteral){
			 return visit((NumberLiteral)node);
		 } else if(node instanceof ParenthesizedExpression){
			 return visit((ParenthesizedExpression)node);
		 } else if(node instanceof PostfixExpression){
			 return visit((PostfixExpression)node);
		 } else if(node instanceof PrefixExpression){
			 return visit((PrefixExpression)node);
		 } else if(node instanceof StringLiteral){
			 return visit((StringLiteral)node);
		 } else if(node instanceof SuperFieldAccess){
			 return visit((SuperFieldAccess)node);
		 } else if(node instanceof SuperMethodInvocation){
			 return visit((SuperMethodInvocation)node);
		 } else if(node instanceof SuperMethodReference){
			 return visit((SuperMethodReference)node);
		 } else if(node instanceof ThisExpression){
			 return visit((ThisExpression)node);
		 } else if(node instanceof TypeLiteral){
			 return visit((TypeLiteral)node);
		 } else if(node instanceof TypeMethodReference){
			 return visit((TypeMethodReference)node);
		 } else if(node instanceof VariableDeclarationExpression){
			 return visit((VariableDeclarationExpression)node);
		 } else if(node instanceof AnonymousClassDeclaration){
			 return visit((AnonymousClassDeclaration)node);
		 } else {
			 System.out.println("UNKNOWN ASTNode type : " + node.toString());
			 return null;
		 }
	}
	
	
}
