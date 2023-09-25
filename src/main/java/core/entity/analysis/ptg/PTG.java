package core.entity.analysis.ptg;

import lombok.*;

import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PTG {
    private Set<Edge> edges;
}
