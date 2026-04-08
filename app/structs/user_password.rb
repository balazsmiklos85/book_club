# frozen_string_literal: true

require 'bcrypt'
require_relative '../types'

module BookClub
  module Structs
    # Represents a user password with hash verification capabilities.
    class UserPassword < DB::Struct
      attribute? :user_id, Types::String
      attribute? :password_hash, Types::String
      attribute? :salt, Types::String
      attribute? :hash_algorithm, Types::String

      # Verifies if the provided password matches the stored hash.
      #
      # @param password [String] the plaintext password to verify
      # @return [Boolean] true if the password is valid, false otherwise
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
