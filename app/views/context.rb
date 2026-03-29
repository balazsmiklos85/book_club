# frozen_string_literal: true

module BookClub
  module Views
    # Extends the view context to include CSRF token support.
    class Context < Hanami::View::Context
      # The CSRF token for form security.
      attr_reader :csrf_token

      # Initializes a new view context instance with optional CSRF token.
      #
      # @param csrf_token [String, nil] the CSRF token to include in views
      # @param args [Hash] additional arguments passed to parent initializer
      def initialize(csrf_token: nil, **args)
        @csrf_token = csrf_token
        super(**args)
      end
    end
  end
end
