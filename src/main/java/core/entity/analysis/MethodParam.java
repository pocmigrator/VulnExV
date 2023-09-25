package core.entity.analysis;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"type", "variableId"})
public class MethodParam {

    /**
     * reference, basic
     */
    private ParseTree type;
    private String typeText;
    private ParseTree variableId;
    private boolean isReferenceType;
}
