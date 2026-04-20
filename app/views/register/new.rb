# frozen_string_literal: true

module BookClub
  module Views
    module Register
      # View that displays the registration form.
      class New < Hanami::View
        expose :csrf_token
      end
    end
  end
end
