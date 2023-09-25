package core.entity.analysis.ptg;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Path {

     private PTG ptg;

    /**
     *
     * e1 - e2 ... eN
     */
    private List<Node> path;
}
