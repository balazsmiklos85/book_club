# frozen_string_literal: true

module BookClub
  module Relation
    class Books < ROM::Relation[:sql]
      schema :books, infer: true

      associations do
        has_many :suggestions
        has_many :votes
      end
    end
  end
end
