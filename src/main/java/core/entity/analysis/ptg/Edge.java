package core.entity.analysis.ptg;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edge {
    private Node source;
    private Node target;
    /**
     * localVariableDeclaration
     * assignment
     * methodInvocation
     * methodInvocation_lf_primary
     * methodInvocation_lfno_primary
     *
     * variableId
     */
    private String type;
    private ParseTree transfer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge p = (Edge) o;
        return Objects.equals(source, p.source) &&
                Objects.equals(target, p.target) &&
                Objects.equals(transfer, p.transfer)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, transfer);
    }
}
