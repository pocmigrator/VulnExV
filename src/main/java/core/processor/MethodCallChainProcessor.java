package core.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.entity.analysis.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import utils.Log;
import utils.TaskParamReader;

import java.util.*;
import java.util.stream.Collectors;


public class MethodCallChainProcessor {

    /**
     * eg:
     * m1 - m2 - m3(invoke vul(...))
     * m4 - m5(invoke vul(...))
     * m6 - m7 - m8(invoke vul(...))
     */
    public List<List<PMethod>> buildMethodCallChainList() {
        // m3, m5, m8
        List<PMethod> vulCodePMethodList = buildVulCodePMethodList();
        Map<PMethod, List<PMethod>> pMethodListMap = buildCallMethodMap(vulCodePMethodList);
        List<MethodCallTree> methodCallTrees = buildMethodCallTreeList(vulCodePMethodList, pMethodListMap);

        // m1 - m2 - m3, m4 - m5, m6 - m7 - m8
        List<List<PMethod>> list = Lists.newArrayList();
        for (MethodCallTree methodCallTree : methodCallTrees) {
            list.addAll(buildChainList(methodCallTree));
        }

        // filter chains by rules
        list = filterChainsByRules(list);

        Log.info("buildMethodCallChainList end");
        return list;
    }

    private List<List<PMethod>> filterChainsByRules(List<List<PMethod>> list) {
        List<List<PMethod>> lists = Lists.newArrayList();
        for (List<PMethod> pMethods : list) {
            PMethod pMethod = pMethods.get(0);
            if (pMethod.getMethodModifier().contains("@Test")) {
                pMethods.remove(pMethod);
            }
            if (pMethod.getMethodModifier().contains("private")) {
                pMethods.remove(pMethod);
            }
            if (!pMethods.isEmpty()) {
                lists.add(pMethods);
            }
        }

        return lists;
    }

    private List<PMethod> buildVulCodePMethodList() {
        TaskParam taskParam = TaskParamReader.getTaskParam();
        TaskParam.VulCode vulCode = taskParam.getVulCode();
        List<PMethod> vulCodeClientMethodList = Lists.newArrayList();

        for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
            for (PClass pClass : pSourceFile.getPClasses()) {
                for (PMethod pMethod : pClass.getPMethodList()) {
                    List<CallMethod> callLibMethodList = pMethod.getCallMethods().stream()
                            .filter(method -> !method.isClientMethod())
                            .collect(Collectors.toList());
                    for (CallMethod callMethod : callLibMethodList) {
                        if (StringUtils.equals(callMethod.getClassName(), vulCode.getClassName())
                                && StringUtils.equals(callMethod.getMethodName(), vulCode.getMethodName())) {
                            vulCodeClientMethodList.add(pMethod);
                        }
                        if (StringUtils.isBlank(vulCode.getClassName()) && StringUtils.equals(callMethod.getMethodName(), vulCode.getMethodName())) {
                            vulCodeClientMethodList.add(pMethod);
                        }
                    }
                }
            }
        }

        Log.info("buildVulCodePMethodList end");
        return vulCodeClientMethodList;
    }


    /**
     * vulCodePMethod: client method which calls vul code
     * build call map
     */
    private Map<PMethod, List<PMethod>> buildCallMethodMap(List<PMethod> vulCodePMethodList) {
        Map<PMethod, List<PMethod>> pMethodListMap = Maps.newHashMap();
        Set<PMethod> processedMethods = Sets.newHashSet();
        Stack<PMethod> pMethodStack = new Stack<>();
        pMethodStack.addAll(vulCodePMethodList);

        while (!pMethodStack.isEmpty()) {
            PMethod method = pMethodStack.pop();
            if (processedMethods.contains(method)) {
                continue;
            }
            // method: xml2Obj
            List<PMethod> callers = Lists.newArrayList();
            // all client methods
            for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
                for (PClass pClass : pSourceFile.getPClasses()) {
                    for (PMethod pMethod : pClass.getPMethodList()) {
                        // input:
                        // all client methods, method's callees
                        // methodA
                        // 找谁调用了methodA，并将其放到stack中
                        // 自底向上找调用关系(m1被哪些m调用，递归)
                        addToCallIfNeed(pMethod, method, callers);
                    }
                }
            }
            pMethodListMap.put(method, callers);
            pMethodStack.addAll(callers);
            processedMethods.addAll(callers);
        }
        Log.info("buildCallMethodMap end");
        return pMethodListMap;
    }

    /**
     * add pmethod to callMethods (pMethod)
     */
    private void addToCallIfNeed(PMethod pMethod, PMethod method, List<PMethod> callers) {
        for (CallMethod m : pMethod.getCallMethods()) {
            if (!m.isClientMethod()) {
                continue;
            }
//看看method有哪些pmethod在调用，现在有method的classname和methodname
//pmethod有调用列表（callmethod），如果在调用列表，通过classname和method能找到pmethod，那就存在调用
            if (method.equals(pMethod)) {
                continue;
            }
            String callClassName = preprocessName(m.getClassName());
            String className = preprocessName(method.getClassName());

            if (StringUtils.equals(m.getMethodName(), method.getMethodName())) {
                if (method.getParams() != null && m.getParamList() != null) {
                    if (method.getParams().size() != m.getParamList().size()) {
                        continue;
                    }
                }

                if (StringUtils.equals(callClassName, className)) {
                    callers.add(pMethod);
                    break;
                }
                // method's super class
                PClass methodClass = ProjectContext.pClassMap.get(method.getPClassCid());
                if (methodClass != null && methodClass.getSuperClassNames().contains(callClassName)) {
                    callers.add(pMethod);
                    break;
                }
            }
        }
    }

    private String preprocessName(String className) {
        if (className.contains("<")) {
            className = className.split("<")[0].trim();
        }
        return className;
    }

    /**
     * eg:
     * vulCodePMethodList: f1
     * pMethodListMap:
     * f1->f2,f3
     * f2->f4,f5,f6
     * f6->f7
     * return:
     * f1-f2-f4
     * f1-f2-f5
     * f1-f2-f6-f7
     * f1-f3
     */
    private List<List<PMethod>> buildChainList(MethodCallTree methodCallTree) {
        //1. no children node
        List<MethodCallTree> leafNodes = Lists.newArrayList();
        Stack<MethodCallTree> stack = new Stack<>();
        stack.push(methodCallTree);
        while (!stack.isEmpty()) {
            MethodCallTree pop = stack.pop();
            if (pop.getChildren() != null && !pop.getChildren().isEmpty()) {
                stack.addAll(pop.getChildren());
            } else {
                leafNodes.add(pop);
            }
        }

        List<List<PMethod>> list = Lists.newArrayList();
        for (MethodCallTree leaf : leafNodes) {
            List<PMethod> chain = Lists.newArrayList();
            chain.add(leaf.getMethod());
            while (leaf.getParent() != null) {
                leaf = leaf.getParent();
                chain.add(leaf.getMethod());
            }
            list.add(chain);
        }

        return list;
    }


    private List<MethodCallTree> buildMethodCallTreeList(List<PMethod> vulCodePMethodList, Map<PMethod, List<PMethod>> pMethodListMap) {
        List<MethodCallTree> methodCallTrees = Lists.newArrayList();
        for (PMethod pMethod : vulCodePMethodList) {
            MethodCallTree vulCodeMethodCallTree = new MethodCallTree();
            vulCodeMethodCallTree.setParent(null);
            vulCodeMethodCallTree.setMethod(pMethod);
            methodCallTrees.add(vulCodeMethodCallTree);

            Stack<MethodCallTree> stack = new Stack<>();
            stack.push(vulCodeMethodCallTree);
            while (!stack.isEmpty()) {
                MethodCallTree pop = stack.pop();
                List<MethodCallTree> callTrees = pMethodListMap.getOrDefault(pop.getMethod(), Lists.newArrayList()).stream()
                        .map(x -> MethodCallTree.builder()
                                .parent(pop)
                                .method(x)
                                .build())
                        .collect(Collectors.toList());
                pop.setChildren(callTrees);

                stack.addAll(callTrees);
            }
        }

        Log.info("buildMethodCallTreeList end");
        return methodCallTrees;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MethodCallTree {
        PMethod method;
        MethodCallTree parent;
        List<MethodCallTree> children;
    }
}


