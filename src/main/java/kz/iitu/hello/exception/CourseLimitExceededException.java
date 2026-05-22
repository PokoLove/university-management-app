package kz.iitu.hello.exception;

public class CourseLimitExceededException extends RuntimeException {
    public CourseLimitExceededException(String message) {
        super(message);
    }
}
