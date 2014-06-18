module PHPMetrics

extend lang::php::m3::Core;
import lang::php::m3::Uses;
import lang::php::stats::Stats;

@metric{StaticNameResolutionHistogram}
@doc{Histogram counting type names that could be resolved to a certain number of declarations}
@friendlyName{StaticNameResolutionHistogram}
@appliesTo{php()}
map[int, int] getNameResolutionHistogram(rel[Language, loc, M3] m3s = ())
{
	models = { m | <php(), _, m> <- m3s };

	m3 = composeM3s(|project:///|, models);

	m3@uses = { <l, n> | <l, n> <- m3@uses, n.scheme in ["php+class", "php+interface", "php+trait"] };

	m3 = addPredefinedDeclarations(m3);

	useDecl = resolveUsesToPossibleDeclarations(propagateAliasesInUses(m3));
	
	return countNumPossibleDeclarations(useDecl);
}


private set[str] varClassMetrics = {
						"class consts with variable class name",
						"object creation with variable class name",
						"calls of static methods with variable targets",
						"fetches of static properties with variable targets"};
		
private	set[str] varVarMetrics = {
						"assignments into variable-variables",
						"assignments w/ops into variable-variables",
						"list assignments into variable-variables",
						"ref assignments into variable-variables",
						"fetches of properties with variable names"};
						//"uses of variable-variables (including the above)",

private	set[str] varFuncMetrics = {
						"calls of variable function names",
						"calls of variable method names",
						"calls of static methods with variable names",
						"fetches of static properties with variable names"};

private set[str] varArgsMetrics = {"var-args support functions"};

private set[str] varIncludeMetrics = {"includes with non-literal paths"};

private set[str] overloadMetrics = {
						"definitions of overloads: set",
						"definitions of overloads: get",
						"definitions of overloads: isset",
						"definitions of overloads: unset",
						"definitions of overloads: call",
						"definitions of overloads: callStatic"};

private set[str] varLabelMetrics = {
						"break with non-literal argument",
						"continue with non-literal argument"};	

@memo
private map[str, int] getCounts(rel[Language, loc, AST] asts)
{
	System sys = ( ast.file : ast.script  | <php(), _, ast> <- asts );
	return featureCounts(sys);
}

private int sumMetrics(rel[Language, loc, AST] asts, set[str] metricNames)
{
	counts = getCounts(asts);

	return sum([counts[n] | n <- metricNames]);
}

@metric{numVarVar}
@doc{number of variable variable references}
@friendlyName{numVarVar}
@appliesTo{php()}
public int getNumberOfVarVarUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, varVarMetrics);
}

@metric{numVarClass}
@doc{number of variable class references}
@friendlyName{numVarClass}
@appliesTo{php()}
public int getNumberOfVarClassUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, varClassMetrics);
}

@metric{numVarFunc}
@doc{number of variable accesses to functions or methods}
@friendlyName{numVarFunc}
@appliesTo{php()}
public int getNumberOfVarFuncUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, varFuncMetrics);
}

@metric{numVarArgs}
@doc{number of var args methods}
@friendlyName{numVarArgs}
@appliesTo{php()}
public int getNumberOfVarArgsUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, varArgsMetrics);
}

@metric{numVarIncludes}
@doc{number of variable includes}
@friendlyName{numVarIncludes}
@appliesTo{php()}
public int getNumberOfVarIncludeUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, varIncludeMetrics);
}

@metric{numOverloads}
@doc{number of overload uses}
@friendlyName{numVarOverloads}
@appliesTo{php()}
public int getNumberOfOverloadUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, overloadMetrics);
}

@metric{numVarLabels}
@doc{number of variable label references}
@friendlyName{numVarLabels}
@appliesTo{php()}
public int getNumberOfVarLabelUses(rel[Language, loc, AST] asts = ())
{
	 return sumMetrics(asts, varLabelMetrics);
}

@metric{numEvals}
@doc{number of calls to eval}
@friendlyName{numEvals}
@appliesTo{php()}
public int getNumberOfEvalCalls(rel[Language, loc, AST] asts = ())
{
	return (0 | it + 1 | <php(), _, ast> <- asts, /call(name(name(/eval/i)), _) <- ast);
}