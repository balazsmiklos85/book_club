# frozen_string_literal: true

module BookClub
  module Repos
    # Repository for accessing email records.
    class Emails < Hanami::DB::Repo
      def insert(email_address, user_id)
        emails.insert(email_address: email_address, user_id: user_id)
      end
    end
  end
end
