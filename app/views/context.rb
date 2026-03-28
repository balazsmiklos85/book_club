# frozen_string_literal: true

module BookClub
  module Views
    class Context < Hanami::View::Context
      attr_reader :csrf_token

      def initialize(csrf_token: nil, **args)
        @csrf_token = csrf_token
        super(**args)
      end
    end
  end
end
