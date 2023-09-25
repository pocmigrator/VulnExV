package core.entity.analysis;

import lombok.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"statementTree"})
public class RStatement {

    private int index;

    /**
     * localVariableDeclaration
     * assignment
     * methodInvocation
     * methodInvocation_lf_primary
     * methodInvocation_lfno_primary
     */
    private String type;
    private ParserRuleContext statementTree;
    private String codeLine;
    private Set<String> leftVars;
    private Set<String> rightVars;



}
