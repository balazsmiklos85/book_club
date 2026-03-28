# frozen_string_literal: true

require 'bcrypt'
require_relative '../types'

module BookClub
  module Structs
    class UserPassword < DB::Struct
      attribute? :user_id, Types::String
      attribute? :password_hash, Types::String
      attribute? :salt, Types::String
      attribute? :hash_algorithm, Types::String

      def valid?(password)
        case hash_algorithm
        when 'bcrypt'
          BCrypt::Password.new(password_hash) == password + salt
        when 'plaintext'
          password_hash == password + salt
        else
          false
        end
      end
    end
  end
end
