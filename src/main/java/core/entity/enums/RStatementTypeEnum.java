package core.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum RStatementTypeEnum{
    assignment("assignment"),
    localVariableDeclaration("localVariableDeclaration"),
    methodInvocation("methodInvocation"),
    methodInvocation_lf_primary("methodInvocation_lf_primary"),
    methodInvocation_lfno_primary("methodInvocation_lfno_primary"),

    classInstanceCreationExpression("classInstanceCreationExpression"),
    classInstanceCreationExpression_lf_primary("classInstanceCreationExpression_lf_primary"),
    classInstanceCreationExpression_lfno_primary("classInstanceCreationExpression_lfno_primary"),

    variableId("variableId"),
    ;
    private final String code;
}
