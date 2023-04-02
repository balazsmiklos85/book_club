# frozen_string_literal: true

# Controller for user handling.
class UsersController < ApplicationController
  # Shows the user creation form.
  def new
    @user = User.new
  end

  # Creates a new user.
  def create
    create_user
    if save_user
      log_in @user
      redirect_to root_path
    else
      render 'new'
    end
  end

  private

  def assert_email_is_valid(email)
    return if email =~ URI::MailTo::EMAIL_REGEXP

    flash.now[:danger] = 'Invalid email address provided.'
    render 'new'
  end

  def assert_password_is_matching(password, confirmation)
    return if password == confirmation

    flash.now[:danger] = 'The passwords do not match the confirmation.'
    render 'new'
  end

  def create_user
    user = params[:user]
    assert_email_is_valid user[:email]
    assert_password_is_matching user[:password], user[:password_confirmation]

    @user = User.new(name: user[:name], id: SecureRandom.uuid)
    random_salt = SecureRandom.hex 32
    @password = Password.new(password: user[:password], salt: random_salt, hash_algorithm: 'bcrypt', user: @user)
    @email = Email.new(email: user[:email], user: @user)
  end

  def save_user
    @user.save && @password.save && @email.save
  end
end
