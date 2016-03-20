package com.tpgame.core.commands;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class CommandExecutionResult {
    private boolean result;
    private String message;

    public CommandExecutionResult(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public boolean isSuccessful() {
        return isResult();
    }

    protected boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
