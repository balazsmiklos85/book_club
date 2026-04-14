# frozen_string_literal: true

require 'rom-factory'
require 'faker'
require 'bcrypt'

rom = Hanami.app['db.rom']
Factory = ROM::Factory.configure do |config|
  config.rom = rom
end

Factory.define(:user, relation: :users) do |f|
  f.id { SecureRandom.uuid }
  f.name 'Test User'
  f.is_admin false
  f.external_id { rand(1_000_000) }
  f.association(:emails, relation: :emails)
  f.association(:user_passwords, relation: :user_passwords)
end

Factory.define(:email, relation: :emails) do |f|
  f.email_address { Faker::Internet.unique.email }
end

Factory.define(:user_password, relation: :user_passwords) do |f|
  salt = 'fixed_salt_12345'
  f.salt salt
  f.password_hash { "password123#{salt}" }
  f.hash_algorithm 'plaintext'
end
