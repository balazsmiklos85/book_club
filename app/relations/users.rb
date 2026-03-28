# frozen_string_literal: true

module BookClub
  module Relations
    # Users relation class
    class Users < ROM::Relation[:sql]
      schema :users, infer: true do
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
