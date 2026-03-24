# frozen_string_literal: true

module BookClub
  module Relations
    # Email relation class
    class Emails < ROM::Relation[:sql]
      schema :emails, infer: true do
        associations do
          belongs_to :users
        end
      end
    end
  end
end
