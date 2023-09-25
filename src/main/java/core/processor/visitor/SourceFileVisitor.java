package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
public class SourceFileVisitor extends Java8BaseVisitor<RuleNode> {
    private String packageName = StringUtils.EMPTY;
    private List<String> importNameList = Lists.newArrayList();

    @Override
    public RuleNode visitPackageName(Java8Parser.PackageNameContext ctx) {
        if (StringUtils.isBlank(packageName)) {
            packageName = ctx.getText();
        }
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        String importDeclaration = ctx.getText()
                .replace("import", "")
                .replace(";", "")
                .trim();
        if (importDeclaration.startsWith("static")) {
            importDeclaration = importDeclaration.replaceFirst("static", "");
        }
        importNameList.add(importDeclaration);
        return visitChildren(ctx);
    }


}
