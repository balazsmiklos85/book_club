# frozen_string_literal: true

# Email model. Contains the email address of a user.
class Email < ApplicationRecord
  belongs_to :user
end
