package core.entity.analysis;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"expressionList"})
public class CallParam {
    private List<ParseTree> expressionList;

    private List<String> expressionNameList;
}
