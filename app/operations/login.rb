# frozen_string_literal: true

require 'dry/validation'

module BookClub
  module Operations
    # Authenticates the user
    class Login < Operation
      include Dry::Validation::Macros
      include Deps['repos.email_repo', 'logger']

      def call(email:, password:)
        user_external_id, stored_password = step load_user_data(email)
        step verify_password(stored_password, password, email)
        user_external_id
      end

      private

      def load_user_data(email)
        user = email_repo.find_with_user_and_password(email)
                         &.user
        if user.nil?
          logger.info "User not found: #{email}"
          return Failure :user_not_found
        end

        Success [user.external_id, user.user_passwords.first]
      rescue StandardError => e
        logger.error e
        Failure :user_not_found
      end

      def verify_password(stored_password, password, email)
        return Success(true) if stored_password.valid? password

        logger.info "Invalid password for #{email}"
        Failure :invalid_credentials
      rescue StandardError => e
        logger.error e
        Failure :unexpected_error
      end
    end
  end
end
