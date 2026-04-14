# frozen_string_literal: true

require_relative '../types'

module BookClub
  module Structs
    # User struct representing a user entity.
    class User < DB::Struct
      attribute? :id, Types::String
      attribute? :name, Types::String
      attribute? :is_admin, Types::Bool
      attribute? :external_id, Types::Integer
      attribute? :user_passwords, Types::Array
    end
  end
end
