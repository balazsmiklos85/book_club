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

  def create_user
    @user = User.new(name: params[:name])
    random_salt = SecureRandom.hex 32
    @password = Password.new(password: params[:password], salt: random_salt, hash_algorithm: 'bcrypt', user: @user)
    @email = Email.new(email: params[:email], user: @user)
  end

  def save_user
    @user.save && @password.save && @email.save
  end
end
