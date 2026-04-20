# frozen_string_literal: true

module BookClub
  # Application settings loaded from environment variables.
  class Settings < Hanami::Settings
    setting :session_secret, constructor: Types::String
  end
end
