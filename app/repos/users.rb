# frozen_string_literal: true

module BookClub
  module Repos
    # Repository for accessing user records.
    class Users < Hanami::DB::Repo
      def insert(attributes)
        users.command(:create, result: :one).call(attributes)
      end

      def find_by_external_id(external_id)
        users.where(external_id: external_id).one
      end
    end
  end
end
