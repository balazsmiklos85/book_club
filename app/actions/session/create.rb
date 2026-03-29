# frozen_string_literal: true

module BookClub
  module Actions
    module Session
      # Creates a user session after successful authentication.
      class Create < Action
        include Deps['operations.login']

        # Handles the session creation request.
        #
        # @param request [Hanami::Action::Params] the incoming request
        # @param response [Hanami::Action::Response] the response object to modify
        def handle(request, response)
          result = login.call(email: request.params[:email], password: request.params[:password])

          case result
          when Success
            response.session[:external_id] = result.value!

            response.redirect_to(root_path, status: :see_other)
          when Failure
            response.flash[:error] = 'Invalid email or password'
            response.redirect_to(login_path, status: :see_other)
          end
        end

        private

        def login_path
          '/login'
        end

        def root_path
          '/'
        end
      end
    end
  end
end
