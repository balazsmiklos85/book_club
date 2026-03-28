# frozen_string_literal: true

module BookClub
  module Views
    module Login
      class Index < Hanami::View
        expose :csrf_token
      end
    end
  end
end
