package org.chromium.mojo.system;

public final class MojoResult {
    public static final int ABORTED = 10;
    public static final int ALREADY_EXISTS = 6;
    public static final int BUSY = 16;
    public static final int CANCELLED = 1;
    public static final int DATA_LOSS = 15;
    public static final int DEADLINE_EXCEEDED = 4;
    public static final int FAILED_PRECONDITION = 9;
    public static final int INTERNAL = 13;
    public static final int INVALID_ARGUMENT = 3;
    public static final int NOT_FOUND = 5;
    public static final int OK = 0;
    public static final int OUT_OF_RANGE = 11;
    public static final int PERMISSION_DENIED = 7;
    public static final int RESOURCE_EXHAUSTED = 8;
    public static final int SHOULD_WAIT = 17;
    public static final int UNAVAILABLE = 14;
    public static final int UNIMPLEMENTED = 12;
    public static final int UNKNOWN = 2;

    private MojoResult() {
    }

    public static String describe(int mCode) {
        switch (mCode) {
            case OK /*0*/:
                return "OK";
            case CANCELLED /*1*/:
                return "CANCELLED";
            case UNKNOWN /*2*/:
                return "UNKNOWN";
            case INVALID_ARGUMENT /*3*/:
                return "INVALID_ARGUMENT";
            case DEADLINE_EXCEEDED /*4*/:
                return "DEADLINE_EXCEEDED";
            case NOT_FOUND /*5*/:
                return "NOT_FOUND";
            case ALREADY_EXISTS /*6*/:
                return "ALREADY_EXISTS";
            case PERMISSION_DENIED /*7*/:
                return "PERMISSION_DENIED";
            case RESOURCE_EXHAUSTED /*8*/:
                return "RESOURCE_EXHAUSTED";
            case FAILED_PRECONDITION /*9*/:
                return "FAILED_PRECONDITION";
            case ABORTED /*10*/:
                return "ABORTED";
            case OUT_OF_RANGE /*11*/:
                return "OUT_OF_RANGE";
            case UNIMPLEMENTED /*12*/:
                return "UNIMPLEMENTED";
            case INTERNAL /*13*/:
                return "INTERNAL";
            case UNAVAILABLE /*14*/:
                return "UNAVAILABLE";
            case DATA_LOSS /*15*/:
                return "DATA_LOSS";
            case BUSY /*16*/:
                return "BUSY";
            case SHOULD_WAIT /*17*/:
                return "SHOULD_WAIT";
            default:
                return "UNKNOWN";
        }
    }
}
