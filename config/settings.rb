# frozen_string_literal: true

module BookClub
  class Settings < Hanami::Settings
    setting :session_secret, constructor: Types::String
  end
end
