# frozen_string_literal: true

module BookClub
  module Relations
    class UserPasswords < ROM::Relation[:sql]
      schema :user_password, infer: true, as: :user_passwords do
        associations do
          belongs_to :users
        end
      end
    end
  end
end
