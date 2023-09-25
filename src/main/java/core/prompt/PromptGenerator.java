package core.prompt;

import com.google.common.collect.Lists;
import core.entity.analysis.*;
import core.entity.validate.PromptModel;
import core.entity.validate.PromptTemplate;
import core.processor.CommonProcessor;
import core.processor.ProjectContext;
import core.processor.VulCodeReachableProcessor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import utils.JsonUtil;
import utils.Log;
import utils.TaskParamReader;

import java.util.List;
import java.util.stream.Collectors;

public class PromptGenerator {
    private VulCodeReachableProcessor vulCodeReachableProcessor = new VulCodeReachableProcessor();

    public List<String> generatePrompts() {
        List<PromptModel> promptModels = buildPromptModels();

        Log.info("=== promptModel start ===");
        promptModels.forEach(x -> System.out.println("promptModel: " + JsonUtil.objectToJson(x)));
        Log.info("=== promptModel end ===");

        List<String> prompts = buildPrompts(promptModels);

        return prompts;
    }

    @NotNull
    private static List<String> buildPrompts(List<PromptModel> promptModels) {
        List<String> prompts = Lists.newArrayList();
        for (PromptModel promptModel : promptModels) {
            StringBuilder promptStringBuilder = new StringBuilder();
            promptStringBuilder.append(PromptTemplate.hint1).append("\n\n");
            promptStringBuilder.append(PromptTemplate.hint2).append("\n");
            promptStringBuilder.append(promptModel.getEntryMethodText()).append("\n\n");
            promptStringBuilder.append(PromptTemplate.hint3).append("\n");
            promptStringBuilder.append(promptModel.getEntryClassText()).append("\n\n");

            if (!promptModel.getReferenceParamTexts().isEmpty()) {
                promptStringBuilder.append(PromptTemplate.hint4).append("\n");
                for (String referenceParamText : promptModel.getReferenceParamTexts()) {
                    promptStringBuilder.append(referenceParamText).append("\n\n");
                }
            }

            if (promptModel.getChainMethodSignatures().size() > 1) {
                promptStringBuilder.append(PromptTemplate.hint5).append("\n");
                for (String chainMethod : promptModel.getChainMethodSignatures()) {
                    promptStringBuilder.append(chainMethod).append("\n");
                }
                promptStringBuilder.append(PromptTemplate.hint6).append("\n");
                promptStringBuilder.append(promptModel.getVulUnderMethodText()).append("\n\n");
                promptStringBuilder.append(PromptTemplate.hint7).append("\n");
                promptStringBuilder.append(promptModel.getVulUnderClassText()).append("\n\n");
            }

            promptStringBuilder.append(PromptTemplate.hint8).append("\n");
            promptStringBuilder.append("class name:").append(promptModel.getVulClass()).append("\n");
            promptStringBuilder.append("method name:").append(promptModel.getVulMethod()).append("\n\n");

            promptStringBuilder.append(PromptTemplate.hint9).append("\n");
            promptStringBuilder.append(promptModel.getPocInput()).append("\n\n");
            if (StringUtils.isNotBlank(promptModel.getPocDescription())) {
                promptStringBuilder.append(promptModel.getPocDescription()).append("\n\n");
            }

            promptStringBuilder.append(PromptTemplate.hint10).append("\n");
            promptStringBuilder.append(promptModel.getInterceptor()).append("\n\n");

            promptStringBuilder.append(PromptTemplate.hint11).append("\n");

            prompts.add(promptStringBuilder.toString());
        }
        return prompts;
    }

    public List<PromptModel> buildPromptModels() {
        List<PromptModel> promptModels = Lists.newArrayList();
        TaskParam taskParam = TaskParamReader.getTaskParam();

        List<CallChainAnalysisResult> callChainAnalysisResults = vulCodeReachableProcessor.processCallChains();

        System.out.println("=== callChainAnalysisResult start ===");
        callChainAnalysisResults.forEach(x -> System.out.println("callChainAnalysisResults: " + x));
        System.out.println("=== callChainAnalysisResult end ===");

        for (CallChainAnalysisResult result : callChainAnalysisResults) {
            PromptModel promptModel = buildPromptModel(taskParam, result);
            promptModels.add(promptModel);
        }

        return promptModels;
    }

    @NotNull
    private PromptModel buildPromptModel(TaskParam taskParam, CallChainAnalysisResult result) {
        PromptModel promptModel = new PromptModel();

        List<PMethod> chain = result.getChain();

        PMethod entry = chain.get(0);
        PMethod vul = null;
        if (chain.size() > 1) {
            vul = chain.get(chain.size() - 1);
        }

        // entry
        String entryMethodText = CommonProcessor.formatCodeLine(entry.getTree());
        promptModel.setEntryMethodText(entryMethodText);
        promptModel.setEntryClassText(entry.getClassName());

        // vul
        String vulMethodText;
        String vulClassText;
        if (vul == null) {
            vulMethodText = StringUtils.EMPTY;
            vulClassText = StringUtils.EMPTY;
        } else {
            vulMethodText = CommonProcessor.formatCodeLine(vul.getTree());
            vulClassText = vul.getClassName();
        }
        promptModel.setVulUnderMethodText(vulMethodText);
        promptModel.setVulUnderClassText(vulClassText);

        // reference param
        List<String> referenceParamTexts = referenceParamTexts(entry);
        promptModel.setReferenceParamTexts(referenceParamTexts);

        // chain methods
        List<String> chainMethodNames = chain.stream()
                .map(PMethod::getMethodName)
                .collect(Collectors.toList());
        promptModel.setChainMethodSignatures(chainMethodNames);

        promptModel.setVulClass(taskParam.getVulCode().getClassName());
        promptModel.setVulMethod(taskParam.getVulCode().getMethodName());
        promptModel.setVulFullyQualifiedClassName(taskParam.getVulFullyQualifiedClassName());
        String interceptor = "MethodCallInterceptor.interceptor(\n" +
                taskParam.getVulFullyQualifiedClassName() + ".class,"
                + "\"" + taskParam.getVulCode().getMethodName()
                + "\"" + ", new Object[]{input}\n" +
                ");";
        promptModel.setInterceptor(interceptor);

        // poc input
        promptModel.setPocInput(taskParam.getVulInput());
        promptModel.setPocDescription(taskParam.getVulDescription());
        return promptModel;
    }

    private List<String> referenceParamTexts(PMethod entry) {
        List<String> referenceTypes = entry.getParams().stream()
                .filter(MethodParam::isReferenceType)
                .map(x -> CommonProcessor.formatCodeLine(x.getType()).trim())
                .collect(Collectors.toList());

        List<PClass> pClasses = Lists.newArrayList();
        for (PSourceFile pSourceFile : ProjectContext.pSourceFiles) {
            List<PClass> subPClasses = pSourceFile.getPClasses().stream()
                    .filter(x -> referenceTypes.contains(x.getClassName()))
                    .collect(Collectors.toList());
            pClasses.addAll(subPClasses);
        }

        return pClasses.stream()
                .map(x -> CommonProcessor.formatCodeLine(x.getTree()))
                .collect(Collectors.toList());
    }

}
