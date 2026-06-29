# frozen_string_literal: true

module BookClub
  # Custom types for the application.
  module Types
    include Dry.Types()

    # Email address type that normalizes input by downcasing.
    EmailAddress = Types::Strict::String.constructor(&:downcase)
  end
end
