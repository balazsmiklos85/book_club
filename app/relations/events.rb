# frozen_string_literal: true

module BookClub
  module Relation
    class Events < ROM::Relation[:sql]
      schema :events, infer: true do
        associations do
          belongs_to :books
          has_many :participants
        end
      end
    end
  end
end
