package core.processor.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BasicTransferRule {
    DIRECT("direct"),
    TYPE_CHANGE("type_change"),
    DATA_UPDATE("data_update"),
    NONE("none"),
    ;
    private final String code;

}
