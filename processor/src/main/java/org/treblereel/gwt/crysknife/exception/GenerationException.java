//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.treblereel.gwt.crysknife.exception;

public class GenerationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String additionalFailureInfo = "";

    public GenerationException() {
    }

    public GenerationException(String msg) {
        super(msg);
    }

    public GenerationException(Throwable t) {
        super(t);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public void appendFailureInfo(String info) {
        this.additionalFailureInfo = this.additionalFailureInfo + "\n" + info;
    }

    public String getMessage() {
        return super.getMessage() + this.additionalFailureInfo;
    }
}
