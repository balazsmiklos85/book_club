# frozen_string_literal: true

require 'dry/validation'

module BookClub
  module Operations
    class Login < Operation
      include Dry::Validation::Macros
      include Deps['repos.email_repo']

      def call(email:, password:)
        record = email_repo.find_with_user_and_password(email)

        return Failure(:invalid_credentials) unless record

        user = record[:user]
        return Failure(:invalid_credentials) unless user

        password_data = user[:user_passwords].first
        return Failure(:invalid_credentials) unless password_data

        password_hash = password_data[:password_hash]
        hash_algorithm = password_data[:hash_algorithm]

        return Failure(:invalid_credentials) unless hash_algorithm == 'bcrypt'

        # Use BCrypt to verify the password
        bcrypt_password = BCrypt::Password.new(password_hash)

        if bcrypt_password == password
          external_id = user[:external_id]
          Success(external_id)
        else
          Failure(:invalid_credentials)
        end
      end
    end
  end
end
