package hu.bmiklos.bc.controller.dto;

/**
 * @deprecated Use {@link hu.bmiklos.bc.web.dto.LoginRequest} from the Thymeleaf module instead.
 */
@Deprecated
public class LoginRequest {
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
