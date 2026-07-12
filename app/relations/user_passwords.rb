# frozen_string_literal: true

require_relative '../types'

module BookClub
  module Relations
    # ROM relation for accessing user password records.
    class UserPasswords < ROM::Relation[:sql]
      schema :user_password, infer: true, as: :user_passwords do
        attribute :salt, Types::String.default('')
        attribute :hash_algorithm, Types::String.default('bcrypt')

        associations do
          belongs_to :users
        end
      end
    end
  end
end
