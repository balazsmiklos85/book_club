# frozen_string_literal: true

# Database migration to create the passwords table
class CreatePasswords < ActiveRecord::Migration[7.0]
  def change
    create_table :passwords do |t|
      t.references :user, foreign_key: true
      t.string :password_hash, null: false
      t.string :salt, null: false
      t.string :hash_algorithm, null: false

      t.timestamps
    end
  end
end
