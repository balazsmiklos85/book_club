# frozen_string_literal: true

module BookClub
  module Relation
    # Email relation class
    class Emails < ROM::Relation[:sql]
      schema :emails, infer: true
      foreign_key :user_id
    end
  end
end
