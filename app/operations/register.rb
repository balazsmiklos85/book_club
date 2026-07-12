# frozen_string_literal: true

module BookClub
  module Operations
    # Registers a new user.
    class Register < Operation
      include Deps['repos.users', 'repos.emails', 'logger']

      def call(name:, email:, confirm_email:, password:, confirm_password:, external_id:)
        step validate_input(name:, email:, confirm_email:, password:, confirm_password:, external_id:)
        user_id = step create_user(name: name, external_id: external_id)
        step create_password(user_id, password)
        step create_email(email, user_id)
        external_id
      end

      private

      def validate_input(**input)
        result = Contracts::RegisterContract.new.call(input)
        return Failure result.errors.to_h unless result.success?

        Success result.to_h
      end

      def create_user(name:, external_id:)
        user = users.insert(
          name: name,
          is_admin: false,
          external_id: external_id
        )

        Success user.id
      rescue StandardError => e
        logger.error "Failed to create user: #{e.message}"
        Failure :user_creation_failed
      end

      def create_password(user_id, password)
        users.user_passwords.command(:create, result: :one).call(
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
