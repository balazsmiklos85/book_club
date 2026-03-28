# frozen_string_literal: true

require_relative '../types'

module BookClub
  module Structs
    class UserPassword < DB::Struct
      attribute? :user_id, Types::String
      attribute? :password_hash, Types::String
      attribute? :salt, Types::String
      attribute? :hash_algorithm, Types::String
    end
  end
end
