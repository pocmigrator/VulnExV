package core.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import core.entity.analysis.*;
import core.entity.enums.NodeTypeEnum;
import core.entity.enums.RStatementTypeEnum;
import core.entity.analysis.ptg.PTG;
import core.entity.analysis.ptg.Edge;
import core.entity.analysis.ptg.Node;
import core.processor.rules.BasicTransferRule;
import core.processor.visitor.CallParamVisitor;
import core.processor.visitor.RStatementVarVisitor;
import core.processor.visitor.RStatementVisitor;
import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import utils.Log;
import utils.TaskParamReader;

import java.util.*;
import java.util.stream.Collectors;

public class ParameterTranslationProcessor {

    public BasicTransferRule processCallerCallee(PMethod caller, String calleeMethodName) {
        Log.info("caller: " + caller.toString());

        // get caller's parameter list
        List<MethodParam> callerParams = caller.getParams();

        // get related stmts
        List<RStatement> statements = parseCallerStatements(caller.getTree());

        // parse callee params
        CallParam calleeCallParam = parseCalleeParams(statements, calleeMethodName);
        if (calleeCallParam != null) {
            Log.info("calleeCallParam: " + calleeCallParam.toString());
        } else {
            Log.info("calleeCallParam == null ");
        }

        // filter related stmts
        PTG ptg = buildPTG(statements, calleeCallParam, callerParams);
        if (ptg != null) {
            Log.info("ptg: " + ptg.toString());
        } else {
            Log.info("ptg == null ");
        }

        // build param transfer path
        List<List<Node>> pathList = buildCalleeParamPathList(calleeCallParam, ptg);
        /**
         param-param: List<Node> -> rule1
         method-method: List<rule1> -> rule2
         path: List<rule2> -> final_rule
         */
        List<BasicTransferRule> basicTransferRules = parseBasicTransferRules(ptg, pathList);

        return parseBasicTransferRule(basicTransferRules);
    }

    private BasicTransferRule parseBasicTransferRule(List<BasicTransferRule> basicTransferRules) {
        Log.info("basicTransferRules: ");
        basicTransferRules.forEach(x -> Log.info(x.toString()));
        BasicTransferRule basicTransferRule = processBasicTransferRules(basicTransferRules);
        TaskParam taskParam = TaskParamReader.getTaskParam();
        if (!taskParam.isNeedPt() && (basicTransferRule == BasicTransferRule.NONE
                || basicTransferRule == BasicTransferRule.DATA_UPDATE)) {
            return BasicTransferRule.DIRECT;
        }

        return basicTransferRule;
    }

    @NotNull
    private List<BasicTransferRule> parseBasicTransferRules(PTG ptg, List<List<Node>> pathList) {
        List<BasicTransferRule> basicTransferRules = Lists.newArrayList();
        for (List<Node> path : pathList) {
            List<BasicTransferRule> paramTranslationRules = transferByRules(path, ptg);
            if (!paramTranslationRules.isEmpty()) {
                Log.info("paramTranslations: ");
                paramTranslationRules.forEach(x -> Log.info(x.toString()));
            } else {
                Log.info("paramTranslations is empty ");
            }
            BasicTransferRule basicTransferRule = processBasicTransferRules(paramTranslationRules);
            basicTransferRules.add(basicTransferRule);
        }
        return basicTransferRules;
    }

    public BasicTransferRule processBasicTransferRules(List<BasicTransferRule> paramTranslations) {
        if (paramTranslations == null || paramTranslations.isEmpty()) {
            return BasicTransferRule.NONE;
        }
        if (paramTranslations.contains(BasicTransferRule.NONE)) {
            return BasicTransferRule.NONE;
        }
        if (paramTranslations.contains(BasicTransferRule.DATA_UPDATE)) {
            return BasicTransferRule.DATA_UPDATE;
        }

        if (paramTranslations.contains(BasicTransferRule.TYPE_CHANGE)) {
            return BasicTransferRule.TYPE_CHANGE;
        }

        return BasicTransferRule.DIRECT;
    }

    /**
     * params -> rules
     */
    private List<BasicTransferRule> transferByRules(List<Node> path, PTG ptg) {
        if (path.size() < 2) {
            return Lists.newArrayList(BasicTransferRule.DIRECT);
        }
        Set<Edge> edges = ptg.getEdges();
        List<BasicTransferRule> basicTransferRules = Lists.newArrayList();
        for (int i = 0; i < path.size() - 1; i++) {
            Node sourceNode = path.get(i);
            Node targetNode = path.get(i + 1);

            Edge edge = edges.stream()
                    .filter(x -> Objects.equals(x.getSource(), sourceNode) && Objects.equals(x.getTarget(), targetNode))
                    .findFirst().orElse(null);
            if (edge == null) {
                continue;
            }

            // last param transfer
            if (i == path.size() - 2) {
                if (!StringUtils.equals(targetNode.getType(), NodeTypeEnum.callerParam.getCode())) {
                    basicTransferRules.add(BasicTransferRule.NONE);
                    continue;
                }
            }

            //TODO type change, data update
            if (StringUtils.equals(edge.getType(), RStatementTypeEnum.variableId.getCode())) {
                basicTransferRules.add(BasicTransferRule.DIRECT);
            } else {
                basicTransferRules.add(BasicTransferRule.DATA_UPDATE);
            }

        }
        return basicTransferRules;
    }

    private List<List<Node>> buildCalleeParamPathList(CallParam calleeCallParam, PTG ptg) {
        if (calleeCallParam == null) {
            return Lists.newArrayList();
        }
        Set<Edge> ptgEdges = ptg.getEdges();
        List<NodeTree> nodeTrees = Lists.newArrayList();
        for (String var : calleeCallParam.getExpressionNameList()) {
            NodeTree nodeTree = buildNodeTree(var, ptgEdges);
            if (nodeTree != null) {
                nodeTrees.add(nodeTree);
                Log.info("var:" + var);
                Log.info("nodeTree node: " + nodeTree.getNode());
//                Log.info("nodeTree parent: " + nodeTree.getParent());
//                Log.info("nodeTree children: " + nodeTree.getChildren());
            }
        }
        List<List<Node>> pathList = Lists.newArrayList();
        for (NodeTree nodeTree : nodeTrees) {
            pathList.addAll(buildPathList(nodeTree));
        }

        return pathList;
    }

    /**
     * p1-p2-p3
     * p2-p4
     */
    private List<List<Node>> buildPathList(NodeTree nodeTree) {
        List<NodeTree> leafNodes = Lists.newArrayList();
        Stack<NodeTree> stack = new Stack<>();
        stack.push(nodeTree);
        while (!stack.isEmpty()) {
            NodeTree pop = stack.pop();
            if (pop.getChildren() != null && !pop.getChildren().isEmpty()) {
                stack.addAll(pop.getChildren());
            } else {
                leafNodes.add(pop);
            }
        }

        List<List<Node>> list = Lists.newArrayList();
        for (NodeTree leaf : leafNodes) {
            List<Node> chain = Lists.newArrayList();
            chain.add(leaf.getNode());
            while (leaf.getParent() != null) {
                leaf = leaf.getParent();
                chain.add(leaf.getNode());
            }
            Collections.reverse(chain);
            list.add(chain);
        }

        return list;
    }

    private NodeTree buildNodeTree(String var, Set<Edge> ptgEdges) {
        Node varFirstNode = ptgEdges.stream()
                .map(Edge::getSource)
                .filter(source -> StringUtils.equals(source.getVar(), var))
                .findFirst().orElse(null);

        if (varFirstNode == null) {
            Log.info("varFirstNode is null");
            return null;
        }

        NodeTree nodeTree = new NodeTree();
        nodeTree.setNode(varFirstNode);
        nodeTree.setParent(null);

        Stack<NodeTree> stack = new Stack<>();
        stack.push(nodeTree);

        while (!stack.isEmpty()) {
            NodeTree pop = stack.pop();
            List<NodeTree> children = ptgEdges.stream()
                    .filter(x -> Objects.equals(x.getSource(), pop.getNode()))
//                    .filter(x -> StringUtils.equals(x.getSource().getVar(), pop.getNode().getVar()))
//                    .filter(x -> StringUtils.equals(x.getSource().getType(), pop.getNode().getType()))
                    .map(x -> NodeTree.builder()
                            .parent(pop)
                            .node(x.getTarget())
                            .build()
                    )
                    .collect(Collectors.toList());
            pop.setChildren(children);
            stack.addAll(children);
        }

        return nodeTree;
    }

    private CallParam parseCalleeParams(List<RStatement> statements, String calleeMethodName) {
        List<String> methodInvocationKeys = Lists.newArrayList(
                RStatementTypeEnum.methodInvocation.getCode(),
                RStatementTypeEnum.methodInvocation_lf_primary.getCode(),
                RStatementTypeEnum.methodInvocation_lfno_primary.getCode()
        );

        List<String> classInstanceCreationKeys = Lists.newArrayList(
                RStatementTypeEnum.classInstanceCreationExpression.getCode(),
                RStatementTypeEnum.classInstanceCreationExpression_lf_primary.getCode(),
                RStatementTypeEnum.classInstanceCreationExpression_lfno_primary.getCode()
        );


        RStatement rStatement = statements.stream()
                .filter(x -> x.getCodeLine().contains(calleeMethodName))
                .filter(x -> methodInvocationKeys.contains(x.getType()) || classInstanceCreationKeys.contains(x.getType()))
                .findFirst().orElse(null);
        if (rStatement == null) {
            return null;
        }

        CallParamVisitor callParamVisitor = new CallParamVisitor();
        callParamVisitor.visit(rStatement.getStatementTree());

        return CallParam.builder()
                .expressionList(callParamVisitor.getExpressionList())
                .expressionNameList(callParamVisitor.getExpressionNameList())
                .build();
    }

    private PTG buildPTG(List<RStatement> statements, CallParam callParam, List<MethodParam> callerParams) {
        Set<Edge> edges = Sets.newHashSet();

        if (callParam == null) {
            return null;
        }

        List<RStatement> reversedRStatements = statements.stream()
                .sorted(Comparator.comparing(RStatement::getIndex).reversed())
                .collect(Collectors.toList());
        for (String expressionName : callParam.getExpressionNameList()) {
            Set<String> searchKeys = Sets.newHashSet();
            searchKeys.add(expressionName);

            // inner
            for (RStatement rStatement : reversedRStatements) {
                Set<String> varKeys = Sets.newHashSet();
                varKeys.addAll(rStatement.getLeftVars());
                varKeys.addAll(rStatement.getRightVars());

                boolean empty = Sets.intersection(varKeys, searchKeys).isEmpty();
                if (empty) {
                    continue;
                }

                for (String leftVar : rStatement.getLeftVars()) {
                    for (String rightVar : rStatement.getRightVars()) {
                        String sourceType;
                        String targetType;

                        if (StringUtils.equals(leftVar, expressionName)) {
                            sourceType = NodeTypeEnum.calleeParam.getCode();
                            targetType = NodeTypeEnum.inner.getCode();
                        } else if (StringUtils.equals(rightVar, expressionName)) {
                            sourceType = NodeTypeEnum.inner.getCode();
                            targetType = NodeTypeEnum.calleeParam.getCode();
                        } else {
                            sourceType = NodeTypeEnum.inner.getCode();
                            targetType = NodeTypeEnum.inner.getCode();
                        }

                        Node source = Node.builder()
                                .type(sourceType)
                                .var(leftVar)
                                .build();
                        Node target = Node.builder()
                                .type(targetType)
                                .var(rightVar)
                                .build();
                        Edge edge = Edge.builder()
                                .source(source)
                                .target(target)
                                .type(rStatement.getType())
                                .transfer(rStatement.getStatementTree())
                                .build();
                        edges.add(edge);
                    }
                }
                searchKeys.addAll(varKeys);
            }

            // caller params
            for (MethodParam methodParam : callerParams) {
                String callerParamVar = CommonProcessor.formatCodeLine(methodParam.getVariableId()).trim();
                if (searchKeys.contains(callerParamVar)) {
                    String sourceType;
                    if (StringUtils.equals(expressionName, callerParamVar)) {
                        sourceType = NodeTypeEnum.calleeParam.getCode();
                    } else {
                        sourceType = NodeTypeEnum.inner.getCode();
                    }

                    Node source = Node.builder()
                            .type(sourceType)
                            .var(callerParamVar)
                            .build();
                    Node target = Node.builder()
                            .type(NodeTypeEnum.callerParam.getCode())
                            .var(callerParamVar)
                            .build();
                    Edge edge = Edge.builder()
                            .source(source)
                            .target(target)
                            .type(RStatementTypeEnum.variableId.getCode())
                            .transfer(methodParam.getVariableId())
                            .build();
                    edges.add(edge);
                }
            }
        }

        return PTG.builder().edges(edges).build();
    }

    private List<RStatement> parseCallerStatements(ParseTree tree) {
        RStatementVisitor rStatementVisitor = new RStatementVisitor();
        rStatementVisitor.visit(tree);

        rStatementVisitor.getRStatementList().forEach(r -> {
            Set<String> leftVars = Sets.newHashSet();
            Set<String> rightVars = Sets.newHashSet();

            RStatementVarVisitor rStatementVarVisitor = new RStatementVarVisitor();
            rStatementVarVisitor.visit(r.getStatementTree());
            Set<String> varKeys = rStatementVarVisitor.getVarKeyList();

            String codeLine = CommonProcessor.formatCodeLine(r.getStatementTree());
            if (codeLine.contains("=")) {
                String[] split = codeLine.split("=");
                varKeys.forEach(x -> {
                    if (split[0].contains(x)) {
                        leftVars.add(x);
                    } else {
                        rightVars.add(x);
                    }
                });
            }
            r.setCodeLine(codeLine);
            r.setLeftVars(leftVars);
            r.setRightVars(rightVars);
        });

        return rStatementVisitor.getRStatementList().stream()
                .sorted(Comparator.comparing(RStatement::getIndex))
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeTree {
        Node node;
        NodeTree parent;
        List<NodeTree> children;
    }


}
