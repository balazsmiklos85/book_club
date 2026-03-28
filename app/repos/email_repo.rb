# frozen_string_literal: true

module BookClub
  module Repos
    class EmailRepo < Hanami::DB::Repo
      def find_with_user_and_password(email)
        emails
          .where(email_address: email)
          .combine(users: :user_passwords)
          .one
      end
    end
  end
end
