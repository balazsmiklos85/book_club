# frozen_string_literal: true

module BookClub
  module Relation
    class UserPasswords < ROM::Relation[:sql]
      schema :user_password, infer: true
    end
  end
end
