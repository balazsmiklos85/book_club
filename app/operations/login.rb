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
        record = email_repo.find_with_user_and_password email
        user = record[:user]
        Success [user[:external_id], user[:user_passwords].first]
      rescue StandardError
        logger.info "Credentials not found for #{email}"
        Failure :user_not_found
      end

      def verify_password(stored_password, password, email)
        stored_password.check! password
        Success
      rescue StandardError
        logger.info "Invalid password for #{email}"
        Failure :invalid_credentials
      end
    end
  end
end
