# frozen_string_literal: true

module BookClub
  module Relation
    class Votes < ROM::Relation[:sql]
      schema :votes, infer: true

      foreign_key :book_id
      foreign_key :user_id
    end
  end
end
