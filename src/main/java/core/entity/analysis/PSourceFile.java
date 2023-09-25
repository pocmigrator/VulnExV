package core.entity.analysis;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tree"})
public class PSourceFile {
    private String filepath;
    private String packageName;
    private List<String> importList;
    private ParseTree tree;

    private List<PClass> pClasses;
}
