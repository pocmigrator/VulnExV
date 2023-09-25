package core.entity.validate;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptModel {
    private String entryMethodText;
    private String entryClassText;
    private String vulUnderMethodText;
    private String vulUnderClassText;
    private List<String> referenceParamTexts;
    private List<String> chainMethodSignatures;
    private String vulMethod;
    private String vulClass;
    private String vulFullyQualifiedClassName;
    private String pocInput;
    private String pocDescription;
    private String interceptor;
}
