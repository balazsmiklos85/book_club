package hu.bmiklos.bc.controller.dto;

public class PasswordResetRequest {
  private String user_id;

  public String getUser_id() {
    return this.user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }
}
