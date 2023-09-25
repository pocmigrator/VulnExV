package core.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskParam {
    private String projectFilepath;
    private String promptFilepath;

    private String vulKey;
    private String vulInput;
    private String vulDescription;
    private String vulFullyQualifiedClassName;
    private VulCode vulCode;
    private boolean needPt;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VulCode{
        private String className;
        private String methodName;
    }
}
