/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */
package cofix.core.parser.node.expr;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;

import com.sun.tracing.dtrace.ProviderAttributes;

import cofix.core.metric.Literal;
import cofix.core.metric.LoopStruct;
import cofix.core.metric.MethodCall;
import cofix.core.metric.Operator;
import cofix.core.metric.Variable;
import cofix.core.modify.Modification;
import cofix.core.parser.node.Node;
import cofix.core.parser.node.stmt.AnonymousClassDecl;

/**
 * @author Jiajun
 * @datae Jun 23, 2017
 */
public class ClassInstanceCreate extends Expr {

	private Expr _expression = null;
	private Type _classType = null;
	private List<Expr> _arguments = null;
	private AnonymousClassDecl _decl = null;
	
	private Expr _expression_replace = null;
	private List<Expr> _arguments_replace = null;
	
	/**
	 * ClassInstanceCreation:
     *   [ Expression . ]
     *       new [ < Type { , Type } > ]
     *       Type ( [ Expression { , Expression } ] )
     *       [ AnonymousClassDeclaration ]
	 */
	public ClassInstanceCreate(int startLine, int endLine, ASTNode node) {
		super(startLine, endLine, node);
	}
	
	public void setExpression(Expr expression){
		_expression = expression;
	}
	
	public void setClassType(Type classType){
		_classType = classType;
	}
	
	public void setArguments(List<Expr> arguments){
		_arguments = arguments;
	}
	
	public void setAnonymousClassDecl(AnonymousClassDecl decl){
		_decl = decl;
	}

	@Override
	public boolean match(Node node, Map<String, Type> allUsableVariables, List<Modification> modifications) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean adapt(Modification modification) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean restore(Modification modification) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean backup(Modification modification) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StringBuffer toSrcString() {
		StringBuffer stringBuffer = new StringBuffer();
		if(_expression != null){
			if(_expression_replace != null){
				stringBuffer.append(_expression_replace.toSrcString());
			} else {
				stringBuffer.append(_expression.toSrcString());
			}
			stringBuffer.append(".");
		}
		stringBuffer.append("new ");
		stringBuffer.append(_classType);
		stringBuffer.append("(");
		if(_arguments_replace != null){
			if(_arguments.size() > 0){
				stringBuffer.append(_arguments_replace.get(0).toSrcString());
				for(int i = 1; i < _arguments_replace.size(); i++){
					stringBuffer.append(",");
					stringBuffer.append(_arguments_replace.get(i).toSrcString());
				}
			}
		}else if(_arguments != null && _arguments.size() > 0){
			stringBuffer.append(_arguments.get(0).toSrcString());
			for(int i = 1; i < _arguments.size(); i++){
				stringBuffer.append(",");
				stringBuffer.append(_arguments.get(i).toSrcString());
			}
		}
		stringBuffer.append(")");
		if(_decl != null){
			stringBuffer.append(_decl.toSrcString());
		}
		return stringBuffer;
	}
	
	@Override
	public List<Literal> getLiterals() {
		List<Literal> list = new LinkedList<>();
		if(_expression != null){
			list.addAll(_expression.getLiterals());
		}
		for(Expr expr : _arguments){
			list.addAll(expr.getLiterals());
		}
		if(_decl != null){
			list.addAll(_decl.getLiterals());
		}
		return list;
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> list = new LinkedList<>();
		if(_expression != null){
			list.addAll(_expression.getVariables());
		}
		for(Expr expr : _arguments){
			list.addAll(expr.getVariables());
		}
		if(_decl != null){
			list.addAll(_decl.getVariables());
		}
		return list;
	}

	@Override
	public List<LoopStruct> getLoopStruct() {
		List<LoopStruct> list = new LinkedList<>();
		if(_decl != null){
			list.addAll(_decl.getLoopStruct());
		}
		return list;
	}

	@Override
	public List<MethodCall> getMethodCalls() {
		List<MethodCall> list = new LinkedList<>();
		MethodCall methodCall = new MethodCall(this);
		list.add(methodCall);
		if(_decl != null){
			list.addAll(_decl.getMethodCalls());
		}
		return list;
	}

	@Override
	public List<Operator> getOperators() {
		List<Operator> list = new LinkedList<>();
		if(_decl != null){
			list.addAll(_decl.getOperators());
		}
		return list;
	}
}