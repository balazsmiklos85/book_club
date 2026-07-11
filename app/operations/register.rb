# frozen_string_literal: true

require 'dry/validation'

module BookClub
  module Operations
    # Registers a new user.
    class Register < Operation
      include Dry::Validation::Macros
      include Deps['repos.users', 'repos.emails', 'logger']

      def call(name:, email:, confirm_email:, password:, confirm_password:, external_id:)
        step validate_email_match(email, confirm_email)
        step validate_password_match(password, confirm_password)
        user_id = step create_user(name: name, email: email, external_id: external_id)
        step create_password(user_id, password)
        step create_email(email, user_id)
        external_id
      end

      private

      def validate_email_match(email, confirm_email)
        return Failure :email_mismatch unless email == confirm_email

        Success true
      end

      def validate_password_match(password, confirm_password)
        return Failure :password_mismatch unless password == confirm_password

        Success true
      end

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

      def create_password(user_id, password)
        users.user_passwords.insert(
          user_id: user_id,
          password_hash: Structs::UserPassword.hash_password(password)
        )
        Success true
      rescue StandardError => e
        logger.error "Failed to create password for user: #{e.message}"
        Failure :password_creation_failed
      end

      def create_email(email, user_id)
        emails.insert(email, user_id)
        Success true
      rescue StandardError => e
        logger.error "Failed to create email for user: #{e.message}"
        Failure :email_creation_failed
      end
    end
  end
end
