# frozen_string_literal: true

module BookClub
  module Operations
    # Registers a new user.
    class Register < Operation
      include Deps['repos.users', 'repos.emails', 'logger']

      def call(attrs)
        transaction do
          validated_attrs = step validate_input attrs
          user_id = step create_user validated_attrs
          step create_password user_id, validated_attrs[:password]
          step create_email validated_attrs[:email], user_id
          validated_attrs[:external_id]
        end
      end

      private

      def validate_input(attrs)
        result = Contracts::RegisterContract.new.call attrs
        return Failure result.errors.to_h unless result.success?

        Success result.to_h
      end

      def create_user(validated)
        user = users.insert(
          name: validated[:name],
          is_admin: false,
          external_id: validated[:external_id]
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
