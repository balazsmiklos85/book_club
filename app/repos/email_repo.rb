# frozen_string_literal: true

module BookClub
  module Repos
    # Repository for accessing email records with associated user data.
    class EmailRepo < Hanami::DB::Repo
      # Finds an email record by address and loads the associated user and password.
      #
      # @param email [String] the email address to search for
      # @return [Structs::Email, nil] the email struct with user data or nil if not found
      def find_with_user_and_password(email)
        emails
          .where(email_address: email)
          .combine(user: :user_passwords)
          .one
      end
    end
  end
end
