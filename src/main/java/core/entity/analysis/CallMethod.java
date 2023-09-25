package core.entity.analysis;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"paramList"})
public class CallMethod {
    private String className;
    private String methodName;
    private List<ParseTree> paramList;
    private boolean clientMethod;
}