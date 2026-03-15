# frozen_string_literal: true

module BookClub
  module Relation
    class Participants < ROM::Relation[:sql]
      schema :participants, infer: true

      foreign_key :event_id
    end
  end
end
