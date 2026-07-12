# frozen_string_literal: true

require 'dry/validation'

module BookClub
  module Contracts
    # Validates registration input: field types, email/password match, and basic strength rules.
    class RegisterContract < Dry::Validation::Contract
      params do
        required(:name).filled(:string)
        required(:email).filled(:string)
        required(:confirm_email).filled(:string)
        required(:password).filled(:string)
        required(:confirm_password).filled(:string)
        required(:external_id).filled
      end

      rule(:email).validate(:email_format)

      rule(:email) do
        key.failure('does not match confirmation') if values[:email] != values[:confirm_email]
      end

      rule(:password) do
        key.failure('does not match confirmation') if values[:password] != values[:confirm_password]
      end

      rule(:password) do
        key.failure('is too short') if values[:password] && values[:password].length < 8
      end

      register_macro(:email_format) do
        key.failure('is invalid') unless /\A[^@\s]+@[^@\s]+\.[^@\s]+\z/.match?(value)
      end
    end
  end
end
