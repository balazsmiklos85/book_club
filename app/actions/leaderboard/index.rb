# frozen_string_literal: true

module BookClub
  module Actions
    module Leaderboard
      # Displays the leaderboard home page.
      class Index < Action
        include BookClub::Actions::AuthenticatedAction

        # Handles rendering the leaderboard view.
        def handle(request, response); end
      end
    end
  end
end
