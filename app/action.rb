# frozen_string_literal: true

require 'hanami/action'
require 'dry/monads'

module BookClub
  # Base action class providing common functionality across all actions.
  class Action < Hanami::Action
    # Include Success and Failure monads for result handling.
    include Dry::Monads[:result]
  end
end
