# frozen_string_literal: true

module BookClub
  module Relation
    # Suggestions relation class
    class Suggestions < ROM::Relation[:sql]
      schema :suggestions, infer: true

      foreign_key :user_id
      foreign_key :book_id
    end
  end
end
