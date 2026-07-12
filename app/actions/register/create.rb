# frozen_string_literal: true

module BookClub
  module Actions
    module Register
      # Handles user registration submission.
      class Create < Action
        include Deps['operations.register']

        def handle(request, response)
          result = register.call request.params.to_h
          if result.success?
            response.session[:external_id] = result.value!
            response.redirect_to root_path, status: :see_other
          else
            response.flash[:error] = I18n.t 'book_club.flash.registration_failed'
            response.redirect_to register_path, status: :see_other
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
