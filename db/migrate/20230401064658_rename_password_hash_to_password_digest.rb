class RenamePasswordHashToPasswordDigest < ActiveRecord::Migration[7.0]
  def change
    rename_column :passwords, :password_hash, :password_digest
  end
end
