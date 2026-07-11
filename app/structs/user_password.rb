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

      # Hashes a plaintext password using BCrypt.
      #
      # @param plaintext [String] the password to hash
      # @return [String] the BCrypt hash
      def self.hash_password(plaintext)
        BCrypt::Password.create(plaintext).to_s
      end

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
