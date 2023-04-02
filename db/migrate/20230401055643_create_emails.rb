# frozen_string_literal: true

# Database migration to create the emails table
class CreateEmails < ActiveRecord::Migration[7.0]
  def change
    create_table :emails, id: false do |t|
      t.string :email, null: false, primary_key: true
      t.references :user, null: false, foreign_key: true

      t.timestamps
    end
  end
end
