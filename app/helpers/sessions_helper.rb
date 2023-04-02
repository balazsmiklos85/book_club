# frozen_string_literal: true

# Sessions helper module. Contains methods for the session handling of the user.
module SessionsHelper
  def log_in(user)
    session[:user] = user
  end

  def logged_in?
    !session[:user].nil?
  end

  def log_out
    session.delete(:user)
  end
end
