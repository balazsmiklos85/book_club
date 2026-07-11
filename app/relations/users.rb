# frozen_string_literal: true

require_relative '../types'

module BookClub
  module Relations
    # ROM relation for accessing user records with associated data.
    class Users < ROM::Relation[:sql]
      schema :users, infer: true do
        attribute :id, Types::Uuid

        associations do
          has_many :emails
          has_many :suggestions
          has_many :user_passwords
          has_many :votes
        end
      end
    end
  end
end
