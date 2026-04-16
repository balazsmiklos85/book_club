# frozen_string_literal: true

module BookClub
  module Views
    module Login
      # View that displays the login form.
      class New < Hanami::View
        expose :csrf_token
      end
    end
  end
end
