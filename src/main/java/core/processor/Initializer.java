package core.processor;

import antlr.Java8Lexer;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.entity.analysis.PClass;
import core.entity.analysis.PMethod;
import core.entity.analysis.PSourceFile;
import core.entity.analysis.TaskParam;
import core.processor.visitor.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import utils.FileReadUtil;
import utils.GetFoldFileNames;
import utils.Log;
import utils.TaskParamReader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Initializer {

    private TaskParam taskParam;

    public Initializer(TaskParam taskParam) {
        this.taskParam = taskParam;
    }

    public void init() {
        initSourceFiles();

        initClasses();

        initClassMethods();

        initMethodCalls();
    }


    private void initSourceFiles() {
        String projectFilepath = taskParam.getProjectFilepath();
        List<String> filepathList = GetFoldFileNames.readFiles(projectFilepath);
        if (filepathList.isEmpty()) {
            Log.info("no files");
            return;
        }

        Set<PSourceFile> pSourceFiles = Sets.newHashSet();

        for (String filepath : filepathList) {
            try {
                List<String> strings = FileReadUtil.readFile(filepath);
                if (strings.size() >= 1200) {
                    Log.warn("over 1200 lines ");
                    continue;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.info("parsing file: "+filepath);
            CharStream inputStream = null;
            try {
                inputStream = CharStreams.fromFileName(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                CommonTokenStream commonTokenStream = new CommonTokenStream(new Java8Lexer(inputStream));
                Java8Parser parser = new Java8Parser(commonTokenStream);
                parser.removeErrorListeners();
                parser.addErrorListener(new ErrorListenerWithException());

                ParseTree parseTree = parser.compilationUnit();

                SourceFileVisitor sourceFileVisitor = new SourceFileVisitor();
                sourceFileVisitor.visit(parseTree);

                PSourceFile sourceFile = PSourceFile.builder()
                        .filepath(filepath)
                        .packageName(sourceFileVisitor.getPackageName())
                        .importList(sourceFileVisitor.getImportNameList())
                        .tree(parseTree)
                        .build();
                pSourceFiles.add(sourceFile);
            } catch (Exception e) {
                System.out.println("Error while parsing: " + e.getMessage());
            }
        }
        Log.info("parsing end");
        ProjectContext.pSourceFiles = pSourceFiles;
    }

    private void initClasses() {
        Map<String, PClass> pClassMap = Maps.newHashMap();
        for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
            ClassTreeVisitor classTreeVisitor = new ClassTreeVisitor();
            classTreeVisitor.visit(pSourceFile.getTree());
            List<PClass> pClassList = classTreeVisitor.getPClassList();
            fillClassTypeName(pSourceFile, pClassList);

            pSourceFile.setPClasses(pClassList);

            for (PClass pClass : pClassList) {
                pClassMap.put(pClass.getCid(), pClass);
            }
        }

        fillSuperClasses(pClassMap);

        ProjectContext.pClassMap = pClassMap;

        Log.info("init classes end");
    }

    private static void fillSuperClasses(Map<String, PClass> pClassMap) {
        List<PClass> pClasses = Lists.newArrayList(pClassMap.values());
        pClassMap.forEach((cid, pClass) -> {
            List<PClass> lists = Lists.newArrayList();
            List<PClass> collect = pClasses.stream()
                    .filter(x -> pClass.getSuperClassNames().contains(x.getClassName()))
                    .collect(Collectors.toList());

            Stack<PClass> stack = new Stack<>();
            Set<PClass> duplicates = Sets.newHashSet();
            stack.addAll(collect);
            duplicates.addAll(collect);

            while (!stack.isEmpty()) {
                PClass pop = stack.pop();
                List<PClass> superClasses = pClasses.stream()
                        .filter(x -> pop.getSuperClassNames().contains(x.getClassName()))
                        .collect(Collectors.toList());

                List<PClass> notDuplicatePClasses = superClasses.stream()
                        .filter(x -> !duplicates.contains(x))
                        .collect(Collectors.toList());

                duplicates.addAll(notDuplicatePClasses);
                stack.addAll(notDuplicatePClasses);
            }

            lists.addAll(duplicates);
            pClass.setSuperClasses(lists);
        });
    }

    private void initClassMethods() {
        for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
            for (PClass pClass : pSourceFile.getPClasses()) {
                MethodTreeVisitor methodTreeVisitor = new MethodTreeVisitor(pClass);
                methodTreeVisitor.visit(pClass.getTree());
                List<PMethod> pMethodList = methodTreeVisitor.getPMethodList();
                pClass.setPMethodList(pMethodList);
            }
        }

        Log.info("init class methods end");
    }

    private void initMethodCalls() {
        Set<String> clientClasses = Sets.newHashSet();
        Set<String> clientMethods = Sets.newHashSet();
        for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
            for (PClass pClass : pSourceFile.getPClasses()) {
                clientClasses.add(pClass.getClassName());
                for (PMethod pMethod : pClass.getPMethodList()) {
                    clientMethods.add(pMethod.getMethodName());
                }
            }
        }

        for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
            for (PClass pClass : pSourceFile.getPClasses()) {
                for (PMethod pMethod : pClass.getPMethodList()) {
                    // init method vars
                    LocalVariableTreeVisitor variableTreeVisitor = new LocalVariableTreeVisitor();
                    variableTreeVisitor.visit(pMethod.getTree());
                    List<String> localVariableDeclarations = variableTreeVisitor.getLocalVariableDeclarations();
                    pMethod.setLocalVariableDeclarations(localVariableDeclarations);

                    MethodCallTreeVisitor methodCallTreeVisitor = new MethodCallTreeVisitor(pSourceFile, pClass, pMethod);
                    methodCallTreeVisitor.visit(pMethod.getTree());

                    methodCallTreeVisitor.getCallMethods().forEach(callMethod -> {
                        if (callMethod == null) {
                            return;
                        }
                        boolean isClientMethod = true;
                        if (StringUtils.isBlank(callMethod.getClassName()) || StringUtils.isBlank(callMethod.getMethodName())) {
                            isClientMethod = false;
                        }
                        if (!clientClasses.contains(callMethod.getClassName())) {
                            isClientMethod = false;
                        }
                        if (!clientMethods.contains(callMethod.getMethodName())) {
                            isClientMethod = false;
                        }
                        callMethod.setClientMethod(isClientMethod);
                    });

                    pMethod.setCallMethods(methodCallTreeVisitor.getCallMethods());
                }
            }
        }

        Log.info("init method call end");
    }

    /**
     * util methods
     **/

    private void fillClassTypeName(PSourceFile pSourceFile, List<PClass> pClassList) {
//        String initTypeName = pSourceFile.getFilepath().substring(0, pSourceFile.getFilepath().lastIndexOf(File.separator));
        String initTypeName = pSourceFile.getPackageName();

        List<PClass> sortedPClassList = pClassList.stream()
                .sorted(Comparator.comparing(PClass::getStartIndex))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPClassList.size(); i++) {
            PClass pClass = sortedPClassList.get(i);
            String pathClassName;
            if (StringUtils.isBlank(initTypeName)) {
                pathClassName = pClass.getClassName();
            } else {
                pathClassName = initTypeName + "." + pClass.getClassName();
            }

            List<PClass> upClasses = Lists.newArrayList();
            for (PClass candidate : sortedPClassList) {
                if (candidate.getStartIndex() < pClass.getStartIndex()
                        && candidate.getEndIndex() > pClass.getEndIndex()) {
                    upClasses.add(candidate);
                }
            }

            if (upClasses.size() == 0) {
                pClass.setTypeName(pathClassName);
            } else {
                PClass fatherClass = upClasses.get(upClasses.size() - 1);
                pClass.setTypeName(fatherClass.getTypeName() + "$" + pClass.getClassName());
            }
        }


    }
}
