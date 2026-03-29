# frozen_string_literal: true

module BookClub
  module Relations
    # ROM relation for accessing participant records.
    class Participants < ROM::Relation[:sql]
      schema :participants, infer: true do
        associations do
          belongs_to :events
        end
      end
    end
  end
end
