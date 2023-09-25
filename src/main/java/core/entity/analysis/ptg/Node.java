package core.entity.analysis.ptg;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"varTree"})
public class Node {
    private String var;
    /**
     * inner
     * callerParam
     * calleeParam
     */
    private String type;
    private ParseTree varTree;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node p = (Node) o;
        return Objects.equals(var, p.var) &&
                Objects.equals(type, p.type)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(var, type);
    }

}
