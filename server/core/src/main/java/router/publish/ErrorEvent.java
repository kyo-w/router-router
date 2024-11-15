package router.publish;

public class ErrorEvent implements Error{
    private ErrorType errorType;
    private Long uniqId;
    private Exception exception;

    public ErrorEvent(ErrorType errorType, Long uniqId, Exception exception) {
        this.errorType = errorType;
        this.uniqId = uniqId;
        this.exception = exception;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public Long getUniqId() {
        return uniqId;
    }

    public void setUniqId(Long uniqId) {
        this.uniqId = uniqId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public ErrorType getType() {
        return errorType;
    }
}
