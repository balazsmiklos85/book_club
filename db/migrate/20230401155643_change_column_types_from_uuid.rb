# frozen_string_literal: true

# Database migration to change the column types from `uuid` to `string`. `uuid` type does not seem to be supported by
# the database and / or Rails, and the columns were referenced by `int` columns, which broke the foreign key
# relationships.
class ChangeColumnTypesFromUuid < ActiveRecord::Migration[7.0]
  def change
    change_column :users, :id, :string
    change_column :passwords, :user_id, :string
    change_column :emails, :user_id, :string
  end
end
