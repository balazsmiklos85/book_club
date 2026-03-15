# frozen_string_literal: true

module BookClub
  module Relation
    # Users relation class
    class Users < ROM::Relation[:sql]
      schema :users, infer: true

      associations do
        has_many :emails
        has_many :suggestions
        has_many :votes
      end
    end
  end
end
