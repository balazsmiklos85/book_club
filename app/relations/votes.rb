# frozen_string_literal: true

module BookClub
  module Relations
    class Votes < ROM::Relation[:sql]
      schema :votes, infer: true do
        associations do
          belongs_to :books
          belongs_to :users
        end
      end
    end
  end
end
