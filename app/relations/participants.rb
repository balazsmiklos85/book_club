# frozen_string_literal: true

module BookClub
  module Relation
    class Participants < ROM::Relation[:sql]
      schema :participants, infer: true do
        associations do
          belongs_to :events
        end
      end
    end
  end
end
