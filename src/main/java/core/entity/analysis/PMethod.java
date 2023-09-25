package core.entity.analysis;

import com.google.common.collect.Sets;
import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * client method
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tree"})
public class PMethod {
    private String className;
    private String methodName;
    private int startIndex;
    private int endIndex;
    private List<MethodParam> params;
    private List<String> localVariableDeclarations;
    private String methodModifier;
    private String pClassCid;
    private ParseTree tree;
    private List<CallMethod> callMethods;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PMethod p = (PMethod) o;
        return Objects.equals(className, p.className) &&
                Objects.equals(methodName, p.methodName) &&
                areListsEqual(params.stream().map(x -> x.getType().getText()).collect(Collectors.toList()),
                        p.params.stream().map(x -> x.getType().getText()).collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName);
    }


    private boolean areListsEqual(List<String> list1, List<String> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }

        Set<String> set1 = Sets.newHashSet(list1);
        Set<String> set2 = Sets.newHashSet(list2);

        return set1.equals(set2);
    }
}
