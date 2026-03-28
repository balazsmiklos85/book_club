# frozen_string_literal: true

require 'dry/validation'

module BookClub
  module Operations
    # Authenticates the user
    class Login < Operation
      include Dry::Validation::Macros
      include Deps['repos.email_repo']

      def call(email:, password:)
        user_external_id, stored_password = step load_user_data(email)
        step verify_password(stored_password, password)
        user_external_id
      end

      private

      def load_user_data(email)
        record = email_repo.find_with_user_and_password email
        return Failure(:user_not_found) if record.nil?

        user = record[:user]
        Success([user[:external_id], user[:user_passwords]])
      end

      def verify_password(stored_password, password)
        stored_password.check!(password)
        Success(true)
      rescue StandardError
        Failure(:invalid_credentials)
      end
    end
  end
end
