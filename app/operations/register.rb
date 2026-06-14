# frozen_string_literal: true

require 'dry/validation'
require 'bcrypt'

module BookClub
  module Operations
    # Registers a new user.
    class Register < Operation
      include Dry::Validation::Macros
      include Deps['repos.users', 'logger']

      def call(name:, email:, password:, confirm_password:, external_id:)
        step validate_password_match(password, confirm_password)
        # TODO: email.downcase should be a User domain concern
        step create_user(name: name, email: email.downcase, external_id: external_id)
        step create_password(external_id, password)
        # TODO: Email is not created and persisted for the User
        external_id
      end

      private

      def validate_password_match(password, confirm_password)
        return Failure :password_mismatch unless password == confirm_password

        Success true
      end

      # TODO: this should just create the User, another step should save it
      def create_user(name:, email:, external_id:)
        user_id = users.insert(
          # TODO: this should be a User domain concern
          id: SecureRandom.uuid,
          name: name,
          is_admin: false,
          external_id: external_id
        )
        # TODO: is the user areally already in the database if we have no user_id?
        return Failure :user_already_exists if user_id.nil?

        Success user_id
      rescue StandardError => e
        logger.error "Failed to create user: #{e.message}"
        Failure :user_creation_failed
      end

      # TODO: this should just create the UserPassword, another step should save it
      def create_password(user_id, password)
        # TODO: the details of the hashing should be a UserPassword domain concern
        password_hash = BCrypt::Password.create password
        users.user_passwords(user_id).insert(
          password_hash: password_hash.to_s,
          salt: '',
          hash_algorithm: 'bcrypt'
        )
        Success true
      rescue StandardError => e
        logger.error "Failed to create password for user: #{e.message}"
        Failure :password_creation_failed
      end
    end
  end
end
