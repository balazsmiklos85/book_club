# frozen_string_literal: true

# rubocop:disable Metrics/BlockLength

ROM::SQL.migration do
  change do
    create_table :books do
      column :id, :uuid, null: false
      primary_key [:id]
      column :author, String, null: false
      column :title, String, null: false
      column :url, String, null: false
      column :recommender_external_id, Integer
      column :recommended_at, DateTime
      unique [:url], name: :books_url_key
    end

    create_table :emails do
      column :email_address, String, size: 255, null: false
      column :user_id, :uuid, null: false
      primary_key [:email_address]
    end

    create_table :events do
      column :id, :uuid, null: false
      primary_key [:id]
      column :book_id, :uuid, null: false
      column :host_id, :uuid
      column :host_external_id, Integer
      column :time, DateTime, null: false
    end

    create_table :participants do
      column :event_id, :uuid, null: false
      column :participant_external_id, Integer, null: false
      primary_key %i[event_id participant_external_id]
    end

    create_table :suggestions do
      column :id, :uuid, null: false
      primary_key [:id]
      column :book_id, :uuid, null: false
      column :user_id, :uuid, null: false
      column :creation_date, DateTime, null: false
      column :description, String, null: false
    end

    create_table :user_password do
      column :user_id, :uuid, null: false
      primary_key [:user_id]
      column :password_hash, String, size: 255, null: false
      column :salt, String, size: 255, null: false
      column :hash_algorithm, String, size: 255, null: false
    end

    create_table :users do
      column :id, :uuid, null: false
      primary_key [:id]
      column :name, String, size: 255
      column :is_admin, TrueClass, null: false
      column :external_id, Integer, null: false
      unique [:external_id], name: :users_external_id_key
    end

    create_table :votes do
      column :id, :uuid, null: false
      primary_key [:id]
      column :book_id, :uuid, null: false
      column :user_id, :uuid
      column :user_external_id, Integer
    end

    alter_table :emails do
      add_foreign_key [:user_id], :users, on_update: :cascade, on_delete: :cascade, name: :fk_email_user
    end

    alter_table :events do
      add_foreign_key [:book_id], :books, on_update: :cascade, on_delete: :cascade, name: :fk_events_books
    end

    alter_table :participants do
      add_foreign_key [:event_id], :events, on_update: :cascade, on_delete: :cascade, name: :fk_participants_events
    end

    alter_table :user_password do
      add_foreign_key [:user_id], :users, on_update: :cascade, on_delete: :cascade, name: :fk_password_user
    end

    alter_table :suggestions do
      add_foreign_key [:book_id], :books, on_update: :cascade, on_delete: :cascade, name: :fk_suggestions_book_id
      add_foreign_key [:user_id], :users, on_update: :cascade, on_delete: :cascade, name: :fk_suggestions_user_id
    end

    alter_table :votes do
      add_foreign_key [:book_id], :books, on_update: :cascade, on_delete: :cascade, name: :fk_votes_books
      add_foreign_key [:user_id], :users, on_update: :cascade, on_delete: :cascade, name: :fk_votes_users
    end
  end
end
# rubocop:enable Metrics/BlockLength
