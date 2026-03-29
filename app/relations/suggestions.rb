# frozen_string_literal: true

module BookClub
  module Relations
    # ROM relation for accessing suggestion records.
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
