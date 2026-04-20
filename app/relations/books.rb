# frozen_string_literal: true

module BookClub
  module Relations
    # ROM relation for accessing book records.
    class Books < ROM::Relation[:sql]
      schema :books, infer: true do
        associations do
          has_many :suggestions
          has_many :votes
        end
      end
    end
  end
end
