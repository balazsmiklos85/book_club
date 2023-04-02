# frozen_string_literal: true

# Database migration to rename the `password_hash` column to `password_digest`. Rails password checking expects the
# column to be named `*_digest`.
class RenamePasswordHashToPasswordDigest < ActiveRecord::Migration[7.0]
  def change
    rename_column :passwords, :password_hash, :password_digest
  end
end
