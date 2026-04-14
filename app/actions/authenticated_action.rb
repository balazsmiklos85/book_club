# frozen_string_literal: true

module BookClub
  module Actions
    # Module that provides authentication protection to actions.
    # When included, it adds a before callback that redirects unauthenticated users to /login.
    module AuthenticatedAction
      def self.included(base)
        base.before :authenticate_user!
      end

      private

      def authenticate_user!(request, response)
        return if request.session[:external_id]

        response.flash[:error] = I18n.t('book_club.flash.login_required')
        response.redirect_to(login_path, status: :see_other)
        halt
      end

      def login_path
        '/login'
      end
    end
  end
end
