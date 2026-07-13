# frozen_string_literal: true

source 'https://rubygems.org'

gem 'hanami', '~> 2.3.0'
gem 'hanami-assets', '~> 2.3.0'
gem 'hanami-controller', '~> 2.3.0'
gem 'hanami-db', '~> 2.3.0'
gem 'hanami-router', '~> 2.3.0'
gem 'hanami-validations', '~> 2.3.0'
gem 'hanami-view', '~> 2.3.0'

gem 'bcrypt'
gem 'dry-operation', '>= 1.0.1'
gem 'dry-types', '~> 1.7'
gem 'pg'
gem 'puma'
gem 'rake'
# transient dependency needs >= 4.0.0, which as of 2026-06-16 translates to 7.2.0
# 7.2.0 conflicts with the systemwide available 7.0.3
gem 'rdoc', '7.0.3'

group :development do
  gem 'hanami-webconsole', '~> 2.3.0'
  gem 'rspec-rails'
  gem 'rubocop'
  gem 'rubocop-capybara'
  gem 'rubocop-rake'
  gem 'rubocop-rspec'
end

group :development, :test do
  gem 'dotenv'
end

group :cli, :development do
  gem 'hanami-reloader', '~> 2.3.0'
end

group :cli, :development, :test do
  gem 'hanami-rspec', '~> 2.3.0'
end

group :test do
  # Database
  gem 'database_cleaner-sequel'
  gem 'faker'
  gem 'rom-factory'

  # Web integration
  gem 'capybara'
  gem 'rack-test'
end
