# frozen_string_literal: true

module BookClub
  module Relations
    class UserPasswords < ROM::Relation[:sql]
      schema :user_password, infer: true
    end
  end
end
