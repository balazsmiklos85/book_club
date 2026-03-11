# frozen_string_literal: true

require 'hanami'

module BookClub
  # Main class of the book club application.
  class App < Hanami::App
    config.actions.sessions = :cookie, {
      key: 'book_club.session',
      secret: ENV.fetch('SESSION_SECRET')
    }
  end
end
