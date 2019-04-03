package com.blockchain.larisa.common;

public enum MergeDepthEnum {
    STEP_0("step0"),
    STEP_1("step1"),
    STEP_2("step2"),
    STEP_3("step3"),
    STEP_4("step4"),
    STEP_5("step5"),
    STEP_6("step6"),
    STEP_7("step7"),
    STEP_8("step8"),
    STEP_9("step9"),
    STEP_10("step10"),
    STEP_11("step11"),
    ;

    MergeDepthEnum(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
