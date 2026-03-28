# frozen_string_literal: true

require_relative "../types"
require_relative "user"

module BookClub
  module Structs
    class Email < DB::Struct
      attribute? :email_address, Types::String
      attribute? :user_id, Types::String
      attribute? :user, Types::Nominal(BookClub::Structs::User).optional
    end
  end
end
