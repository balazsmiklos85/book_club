# frozen_string_literal: true

module BookClub
  module Views
    # Displays the login form.
    module Login
      class Index < Hanami::View
        expose :csrf_token
      end
    end
  end
end
