class CreateUsers < ActiveRecord::Migration[7.0]
  def change
    create_table :users, id: :uuid do |t|
      t.string :name, null: false
      t.boolean :is_admin, null: false, default: false

      t.timestamps
    end
  end
end
