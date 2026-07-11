# frozen_string_literal: true

require 'securerandom'

module BookClub
  # Custom types for the application.
  module Types
    include Dry.Types()

    # Email address type that normalizes input by downcasing.
    EmailAddress = Types::Strict::String.constructor(&:downcase)

    # UUID type with auto-generated default value.
    Uuid = Types::Strict::String.default { SecureRandom.uuid }
  end
end
