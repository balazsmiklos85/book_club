package hu.bmiklos.bc.controller.dto;

public class VoteRequest {
    private String bookId;
    private String method;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }    
}
