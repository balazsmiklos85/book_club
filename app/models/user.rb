# frozen_string_literal: true

# User model. Contains the information for a user: its name, and whether it is an admin. Also references the password and emails of the user.
class User < ApplicationRecord
  has_many :emails
  has_one :password
end
