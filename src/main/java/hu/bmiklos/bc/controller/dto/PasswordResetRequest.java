package hu.bmiklos.bc.controller.dto;

public class PasswordResetRequest {
  private String user_id; // NOSONAR snake case needed for form data field resolution

  public String getUser_id() { // NOSONAR snake case needed for form data field resolution
    return this.user_id;
  }

  // snake case needed for form data field resoltion
  public void setUser_id(String user_id) { // NOSONAR
    this.user_id = user_id;
  }
}
