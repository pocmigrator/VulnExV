package core.entity.analysis;

import core.processor.rules.BasicTransferRule;
import lombok.*;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"chain"})
public class CallChainAnalysisResult {
    private List<PMethod> chain;
    private BasicTransferRule transferRule;
    private boolean reachable;
}
