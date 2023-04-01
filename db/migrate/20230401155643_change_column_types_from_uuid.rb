class ChangeColumnTypesFromUuid < ActiveRecord::Migration[7.0]
  def change
    change_column :users, :id, :string
    change_column :passwords, :user_id, :string
    change_column :emails, :user_id, :string
  end
end
