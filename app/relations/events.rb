# frozen_string_literal: true

module BookClub
  module Relation
    class Events < ROM::Relation[:sql]
      schema :events, infer: true

      foreign_key :book_id

      associations { has_many :participants }
    end
  end
end
