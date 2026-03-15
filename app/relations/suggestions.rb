# frozen_string_literal: true

module BookClub
  module Relation
    # Suggestions relation class
    class Suggestions < ROM::Relation[:sql]
      schema :suggestions, infer: true do
        associations do
          belongs_to :users
          belongs_to :books
        end
      end
    end
  end
end
