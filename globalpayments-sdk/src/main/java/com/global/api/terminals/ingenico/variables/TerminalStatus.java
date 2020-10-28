package com.global.api.terminals.ingenico.variables;

import java.util.HashMap;
import java.util.Map;

public enum TerminalStatus {
    NOT_READY(0), READY(1);

    private final int status;
    private final static Map map = new HashMap<Object, Object>();

    TerminalStatus(int status) {
        this.status = status;
    }

    static {
        for (TerminalStatus _status : TerminalStatus.values())
            map.put(_status.status, _status);
    }

    public static TerminalStatus getEnumName(int val) {
        return (TerminalStatus) map.get(val);
    }

    public int getTerminalStatus() {
        return this.status;
    }
}
