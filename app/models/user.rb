class User < ApplicationRecord
  has_many :emails
  has_one :password
end
