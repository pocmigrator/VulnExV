package core.processor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.entity.analysis.PClass;
import core.entity.analysis.PSourceFile;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder
public class ProjectContext {
    public static Set<PSourceFile> pSourceFiles = Sets.newHashSet();

    public static Map<String, PClass> pClassMap = Maps.newHashMap();

}
