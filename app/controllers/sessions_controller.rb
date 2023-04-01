class SessionsController < ApplicationController
  # GET /login
  # Displays the login form.
  def new
    render 'new'
  end

  # POST /login
  # Creates a new session, logging in the user.
  def create
    email = Email.find_by(email: params[:email])
    redirect_to '/' if email.nil?

    user = email.user
    if user.password && user.password.authenticate("#{params[:password]}#{user.password.salt}")
      log_in user
      redirect_to root_url
    else
      flash.now[:danger] = 'Invalid email/password combination'
      render 'new'
    end
  end

  # DELETE /logout
  # Destroys the current session, logging out the user.
  def destroy
    log_out
    redirect_to root_url
  end
end
