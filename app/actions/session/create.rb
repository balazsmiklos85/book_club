# frozen_string_literal: true

module BookClub
  module Actions
    module Session
      class Create < Action
        include Deps["operations.login"]

        def handle(request, response)
          result = login.(email: request.params[:email], password: request.params[:password])

          case result
          when Success
            response.session[:external_id] = result.value!
            
            response.redirect_to(root_path, status: :see_other)
          when Failure
            flash[:error] = 'Invalid email or password'
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
