package core.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeTypeEnum {
    inner("inner"),
    callerParam("callerParam"),
    calleeParam("calleeParam"),
    ;

    private final String code;
}
