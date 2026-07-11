# frozen_string_literal: true

module BookClub
  module Actions
    module Register
      # Handles user registration submission.
      class Create < Action
        include Deps['operations.register']

        def handle(request, response)
          result = register.call(
            name: request.params[:name],
            email: request.params[:email],
            confirm_email: request.params[:confirm_email],
            password: request.params[:password],
            confirm_password: request.params[:confirm_password],
            external_id: request.params[:external_id].to_i
          )

          case result
          when Success
            response.session[:external_id] = result.value!
            response.redirect_to(root_path, status: :see_other)
          when Failure
            response.flash[:error] = I18n.t('book_club.flash.registration_failed')
            response.redirect_to(register_path, status: :see_other)
          end
        end

        private

        def register_path
          '/register'
        end

        def root_path
          '/'
        end
      end
    end
  end
end
