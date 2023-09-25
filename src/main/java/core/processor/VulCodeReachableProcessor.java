package core.processor;

import com.google.common.collect.Lists;
import core.entity.analysis.CallChainAnalysisResult;
import core.entity.analysis.PMethod;
import core.entity.analysis.TaskParam;
import core.processor.rules.BasicTransferRule;
import org.apache.commons.lang3.StringUtils;
import utils.JsonUtil;
import utils.Log;
import utils.TaskParamReader;

import java.util.List;
import java.util.stream.Collectors;

public class VulCodeReachableProcessor {
    private MethodCallChainProcessor methodCallChainProcessor = new MethodCallChainProcessor();
    private ParameterTranslationProcessor parameterTranslationProcessor = new ParameterTranslationProcessor();

    public List<CallChainAnalysisResult> processCallChains() {
        List<List<PMethod>> chains = methodCallChainProcessor.buildMethodCallChainList();

        System.out.println("=== chains start ===");
        chains.forEach(x -> {
            System.out.println("chain start");
            x.forEach(System.out::println);
            System.out.println("chain end");
        });
        System.out.println("=== chains end ===");

        List<CallChainAnalysisResult> callChainAnalysisResults = Lists.newArrayList();
        for (List<PMethod> chain : chains) {
            CallChainAnalysisResult callChainAnalysisResult = processMethodChain(chain);
            callChainAnalysisResults.add(callChainAnalysisResult);
        }

        for (CallChainAnalysisResult result : callChainAnalysisResults) {
            processCallChainAnalysisResults(result);
        }

        Log.info("processCallChains end");
        return callChainAnalysisResults.stream()
                .filter(CallChainAnalysisResult::isReachable).collect(Collectors.toList());
    }

    private void processCallChainAnalysisResults(CallChainAnalysisResult result) {
        BasicTransferRule transferRule = result.getTransferRule();
        if (StringUtils.equals(BasicTransferRule.NONE.getCode(), transferRule.getCode())) {
            Log.info("none: can  not reach vul code");
        }

        if (StringUtils.equals(BasicTransferRule.TYPE_CHANGE.getCode(), transferRule.getCode())) {
            result.setReachable(true);
            Log.info("type update: can   reach vul code");
        }

        if (StringUtils.equals(BasicTransferRule.DIRECT.getCode(), transferRule.getCode())) {
            result.setReachable(true);
            Log.info("DIRECT: can   reach vul code");
        }

        if (StringUtils.equals(BasicTransferRule.DATA_UPDATE.getCode(), transferRule.getCode())) {
            Log.info("data update: can  not reach vul code");
        }
    }

    public CallChainAnalysisResult processMethodChain(List<PMethod> chain) {
        List<BasicTransferRule> basicTransferRules = Lists.newArrayList();
        for (int i = 0; i < chain.size(); i++) {
            PMethod caller = chain.get(i);
            String calleeMethodName;
            if (i != chain.size() - 1) {
                calleeMethodName = chain.get(i + 1).getMethodName();
            } else {
                TaskParam taskParam = TaskParamReader.getTaskParam();
                TaskParam.VulCode vulCode = taskParam.getVulCode();
                calleeMethodName = vulCode.getMethodName();
            }

            BasicTransferRule basicTransferRule = parameterTranslationProcessor.processCallerCallee(caller, calleeMethodName);
            basicTransferRules.add(basicTransferRule);
        }

        Log.info("chain basicTransferRules: ");
        basicTransferRules.forEach(x -> Log.info(x.toString()));

        BasicTransferRule basicTransferRule = parameterTranslationProcessor.processBasicTransferRules(basicTransferRules);
        CallChainAnalysisResult result = new CallChainAnalysisResult();
        result.setChain(chain);
        result.setTransferRule(basicTransferRule);
        // default
        result.setReachable(false);

        return result;
    }

}
