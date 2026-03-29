# frozen_string_literal: true

module BookClub
  # Defines the application routing configuration.
  class Routes < Hanami::Routes
    root to: 'leaderboard.index'
    get '/login', to: 'login.index'
    post '/session', to: 'session.create'
  end
end
